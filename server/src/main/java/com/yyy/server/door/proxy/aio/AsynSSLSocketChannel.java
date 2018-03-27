package com.yyy.server.door.proxy.aio;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsynSSLSocketChannel extends AsynchronousSocketChannel {
	private static Logger logger = LoggerFactory.getLogger(AioAcceptHandler.class);
	private SSLContext sslContext;
	private AsynchronousSocketChannel socket;
	private SSLEngine sslEngine;
	private ByteBuffer myAppData;
	private ByteBuffer myNetData;
	private ByteBuffer peerAppData;
	private ByteBuffer peerNetData;
	private String remoteAddr;
	private boolean shutdown;

	public AsynSSLSocketChannel(SSLContext sslContext, AsynchronousSocketChannel socket) throws Exception {
		super(null);
		this.sslContext = sslContext;
		this.socket = socket;
		this.remoteAddr = socket.getRemoteAddress().toString();
		logInfo("Initiating SSL connection...");
		createSSLEngine();
		createBuffers();
		doHandShake();
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	private void createSSLEngine() throws UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, IOException, Exception {
		sslEngine = sslContext.createSSLEngine();
		sslEngine.setUseClientMode(false);// work in a server mode
		sslEngine.setNeedClientAuth(true);// need client authentication
	}

	private void createBuffers() {
		// Create byte buffers to use for holding application and encoded data
		SSLSession session = sslEngine.getSession();
		myAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
		myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
		peerAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
		peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());
	}

	private void doHandShake() throws Exception {
		sslEngine.beginHandshake();// explicitly begin the handshake
		HandshakeStatus hsStatus = sslEngine.getHandshakeStatus();
		boolean handshakeDone = false;
		while (!handshakeDone) {
			SSLEngineResult res = null;
			switch (hsStatus) {
			case FINISHED:
				// the status become FINISHED only when the ssl handshake is
				// finished
				// but we still need to send data, so do nothing here
				logInfo("Handshake done. hsStatus = " + hsStatus);
				handshakeDone = true;
				break;
			case NEED_TASK:
				// do the delegate task if there is some extra work such as
				// checking the keystore during the handshake
				hsStatus = doTask();
				break;
			case NEED_UNWRAP:
				// unwrap means unwrap the ssl packet to get ssl handshake
				// information
				if (peerNetData.position() == 0) {
					int cnt = sslEngine.isInboundDone() ? -1 : socket.read(peerNetData).get();
					logDebug("Bytes received:" + cnt);
				}
				hsStatus = sslUnwrap();
				break;
			case NEED_WRAP:
				// wrap means wrap the app packet into an ssl packet to add ssl
				// handshake information
				res = sslWrap(true);
				flush();
				hsStatus = res.getHandshakeStatus();
				break;
			case NOT_HANDSHAKING:
				// now it is not in a handshake or say byebye status. here it
				// means handshake is over and ready for ssl talk
				logInfo("Handshake done. hsStatus = " + hsStatus);
				handshakeDone = true;
				break;
			}
		}
	}

	private SSLEngineResult sslWrap(boolean checkStatus) throws IOException {
		// Empty the local network packet buffer.
		myNetData.clear();
		myAppData.flip();
		SSLEngineResult res = sslEngine.wrap(myAppData, myNetData);
		myAppData.compact();
		if (checkStatus)
			handleErrorStatus(res);
		return res;
	}

	private HandshakeStatus sslUnwrap() throws SSLException, Exception {
		peerNetData.flip();
		SSLEngineResult res = sslEngine.unwrap(peerNetData, peerAppData);
		peerNetData.compact();
		handleErrorStatus(res);
		return res.getHandshakeStatus();
	}

	private void handleErrorStatus(SSLEngineResult result) throws IOException {
		switch (result.getStatus()) {
		case BUFFER_UNDERFLOW:
		case BUFFER_OVERFLOW:
		case CLOSED:
			if (sslEngine.isOutboundDone()) {
				this.socket.shutdownOutput();// stop sending
			}
			throw new IOException("SSL Engine handling error, status : " + result.getStatus());
		default:
			break;
		}
	}

	private void flush() throws InterruptedException, ExecutionException {
		myNetData.flip();
		int countOut = 0;
		int count;

		while (myNetData.hasRemaining()) {
			count = socket.write(myNetData).get();
			countOut += count;
		}
		logDebug("Bytes sent : " + countOut);
		myNetData.compact();
	}

	private HandshakeStatus doTask() {
		Runnable runnable;
		while ((runnable = sslEngine.getDelegatedTask()) != null) {
			logDebug("running delegated task...");
			runnable.run();
		}
		return sslEngine.getHandshakeStatus();
	}

	/*
	 * Begin the shutdown process. <P> Close out the SSLEngine if not already
	 * done so, then wrap our outgoing close_notify message and try to send it
	 * on. <P> Return true when we're done passing the shutdown messsages.
	 */
	boolean shutdown() throws Exception {

		if (!shutdown) {
			sslEngine.closeOutbound();
			shutdown = true;
		}
		// flush the unsent data
		flush();

		/*
		 * By RFC 2616, we can "fire and forget" our close_notify message, so
		 * that's what we'll do here.
		 */
		SSLEngineResult result = sslWrap(false);
		if (result.getStatus() != Status.CLOSED) {
			throw new SSLException("Improper close state");
		}

		flush();

		return (result.getHandshakeStatus() != HandshakeStatus.NEED_WRAP);
	}

	private void logInfo(String msg) {
		logger.info("[" + getRemoteAddr() + "] " + msg);
	}
	private void logDebug(String msg) {
		logger.debug("[" + getRemoteAddr() + "] " + msg);
	}

	private void logWarn(String msg, Throwable e) {
		logger.warn("[" + getRemoteAddr() + "] " + msg, e);
	}

	@Override
	public void close() throws IOException {
		try {
			this.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.socket.close();
	}

	@Override
	public boolean isOpen() {
		return this.socket.isOpen();
	}

	@Override
	public <T> T getOption(SocketOption<T> name) throws IOException {
		return this.socket.getOption(name);
	}

	@Override
	public Set<SocketOption<?>> supportedOptions() {
		return this.socket.supportedOptions();
	}

	@Override
	public AsynchronousSocketChannel bind(SocketAddress local) throws IOException {
		throw new RuntimeException("Not implemented yet!");
	}

	@Override
	public <T> AsynchronousSocketChannel setOption(SocketOption<T> name, T value) throws IOException {
		return this.socket.setOption(name, value);
	}

	@Override
	public AsynchronousSocketChannel shutdownInput() throws IOException {
		return this.socket.shutdownInput();
	}

	@Override
	public AsynchronousSocketChannel shutdownOutput() throws IOException {
		return this.socket.shutdownOutput();
	}

	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		return this.socket.getRemoteAddress();
	}

	@Override
	public <A> void connect(SocketAddress remote, A attachment, CompletionHandler<Void, ? super A> handler) {
		throw new RuntimeException("Not implemented yet!");
	}

	@Override
	public Future<Void> connect(SocketAddress remote) {
		throw new RuntimeException("Not implemented yet!");
	}

	@Override
	public <A> void read(ByteBuffer dst, long timeout, TimeUnit unit, A attachment,
			CompletionHandler<Integer, ? super A> handler) {
		if (sslEngine.isInboundDone()) {
			// We can skip the read operation as the SSLEngine is closed,
			// instead, propagate EOF one level up
			logInfo("Read reaches the end because SSLEngine is closed.");
			handler.completed(-1, attachment);
			return;
		}

		peerAppData.clear();

		// Read from the channel
		socket.read(peerNetData, attachment, new CompletionHandler<Integer, A>() {
			@Override
			public void completed(Integer cnt, A attachment) {
				SSLEngineResult result;
				try {
					if (cnt == -1) {
						sslEngine.closeInbound();
						handler.completed(-1, attachment);
						return;
					}
					peerNetData.flip();
					result = sslEngine.unwrap(peerNetData, peerAppData);
					peerNetData.compact();
					// Process the engineResult.Status
					handleErrorStatus(result);
					peerAppData.flip();
					dst.put(peerAppData);
					handler.completed(dst.limit(), attachment);
				} catch (Exception e) {
					logWarn("SSL Exception while reading data from the socket", e);
					handler.failed(e, attachment);
				}
			}

			@Override
			public void failed(Throwable t, A attachment) {
				logWarn("Failed to read data from the socket", t);
				handler.failed(t, attachment);
			}
		});
	}

	@Override
	public Future<Integer> read(ByteBuffer dst) {
		peerAppData.clear();

		Future<Integer> inFuture = socket.read(peerNetData);
		return new Future<Integer>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return inFuture.cancel(mayInterruptIfRunning);
			}

			@Override
			public boolean isCancelled() {
				return inFuture.isCancelled();
			}

			@Override
			public boolean isDone() {
				return inFuture.isDone();
			}

			@Override
			public Integer get() throws InterruptedException, ExecutionException {
				Integer cnt = inFuture.get();
				return unwrapData(dst, cnt);
			}

			private Integer unwrapData(ByteBuffer dst, Integer cnt) {
				try {
					if (cnt == -1) {
						sslEngine.closeInbound();
						return cnt;
					}
					peerNetData.flip();
					SSLEngineResult result;
					result = sslEngine.unwrap(peerNetData, peerAppData);

					peerNetData.compact();
					// Process the engineResult.Status
					handleErrorStatus(result);
					peerAppData.flip();
					cnt = peerAppData.limit();
					dst.put(peerAppData);
					return cnt;
				} catch (IOException e) {
					logWarn("Failed to unwrap the data",e);
					return -1;
				}
			}

			@Override
			public Integer get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				Integer cnt = inFuture.get(timeout, unit);
				return unwrapData(dst, cnt);
			}

		};
	}

	@Override
	public <A> void read(ByteBuffer[] dsts, int offset, int length, long timeout, TimeUnit unit, A attachment,
			CompletionHandler<Long, ? super A> handler) {
		throw new RuntimeException("Not implemented yet!");
	}

	@Override
	public <A> void write(ByteBuffer buffer, long timeout, TimeUnit unit, A attachment,
			CompletionHandler<Integer, ? super A> handler) {
		myAppData.put(buffer);
		try {
			this.sslWrap(true);
			myNetData.flip();

			if (myNetData.hasRemaining()) {
				socket.write(myNetData, timeout, unit, attachment, handler);
				myNetData.compact();
			}
		} catch (IOException e) {
			handler.failed(e, attachment);
		}
	}

	@Override
	public Future<Integer> write(ByteBuffer buffer) {
		myAppData.put(buffer);
		try {
			this.sslWrap(true);
			myNetData.flip();

			if (myNetData.hasRemaining()) {
				Future<Integer> future = socket.write(myNetData);
				myNetData.compact();
				return future;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new CompletedFuture<Integer>(0);
	}

	@Override
	public <A> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment,
			CompletionHandler<Long, ? super A> handler) {
		throw new RuntimeException("Not implemented yet!");
	}

	@Override
	public SocketAddress getLocalAddress() throws IOException {
		return this.socket.getLocalAddress();
	}
}
