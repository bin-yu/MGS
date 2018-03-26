package com.yyy.server.door.proxy.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.slf4j.LoggerFactory;

// 这里的参数型号，受调用它的函数决定。这里是受客户端socket.read调用
// Integer代表读了多少个字节
// ByteBuffer代表attachment
public abstract class AioReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AioReadHandler.class);
    private AsynchronousSocketChannel socket;
    private CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	private String remoteAddr = "Unknown";

    // 构造函数, 将通道注入
    public AioReadHandler(AsynchronousSocketChannel sc) {
        this.socket = sc;
        try {
			this.remoteAddr = socket.getRemoteAddress().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void completed(Integer i, ByteBuffer buf) {
        // 读到的字节数 > 0 
        if (i > 0) {
        	if(isRequestComplete(buf)){
				String request = parseRequest(buf);
				handleRequest(request);
        	}

            // 关键:  尽快发起下一次异步读
            // 第一个参数为读到哪里
            // 第二个参数为attachment
            // 第三个参数为CompletionHandler, 就是自己
            socket.read(buf, buf, this);

        }
        // 读失败, 就是客户端断开了
        else if (i == -1) {
            logInfo("Disconnected!");
            try {
				this.socket.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            buf = null;
        }
        // 读到0字节， 就不再发起异步读了!!!!
    }

	protected abstract void handleRequest(String request);

	private String parseRequest(ByteBuffer buf) {
		buf.flip(); // 进入读走模式
		try {
		    // 将buf数据转成字符串
		    String msg = decoder.decode(buf).toString();
		    logInfo("Message received : " + msg);
		    buf.compact(); // compact buf  --- 挑战ByteBuffer
		    return msg;
		} catch (CharacterCodingException e) {
		    throw new RuntimeException(e);
		}
	}
    static boolean isRequestComplete(ByteBuffer bb) {
        int p = bb.position() - 4;
        if (p < 0)
            return false;
        return (((bb.get(p + 0) == '\r') &&
                 (bb.get(p + 1) == '\n') &&
                 (bb.get(p + 2) == '\r') &&
                 (bb.get(p + 3) == '\n')));
    }
    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.out.println("cancelled");
    }

    private void logInfo(String msg) {
        logger.info("[" + this.remoteAddr + "] " + msg);
    }
}

