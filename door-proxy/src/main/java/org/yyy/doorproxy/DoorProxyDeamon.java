package org.yyy.doorproxy;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DoorProxyDeamon implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(DoorProxyDeamon.class);
	private static final int DEFAULT_PAUSE = 600; // Wait 1 hr
	private static final long MS_PER_SEC = 1000L; // Milliseconds in a second
	private static volatile Thread thrd; // start and stop are called from
											// different threads

	private final long pause; // How long to pause in service loop

	private final File stopFile;

	/**
	 *
	 * @param wait
	 *            seconds to wait in loop
	 * @param filename
	 *            optional filename - if non-null, run loop will stop when it
	 *            disappears
	 * @throws IOException
	 */
	private DoorProxyDeamon(long wait, File file) {
		pause = wait;
		stopFile = file;
	}

	/**
	 * Common entry point for start and stop service functions. To allow for use
	 * with Java mode, a temporary file is created by the start service, and a
	 * deleted by the stop service.
	 *
	 * @param args
	 *            [start [pause time] | stop]
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			printUsage();
		}
		log("DoorProxyDeamon called with params : " + Arrays.toString(args));
		String mode = args[0];
		switch (mode) {
		case "start":
			log("Starting ...");
			/*File f = createStopFile(getArg(args, 2));
			startThread(getArg(args, 1), f);*/
			start(args);
			break;
		case "stop":
			log("Stopping ...");
			//deleteStopFile(getArg(args, 1));
			stop(args);
			break;
		default:
			printUsage();
			break;
		}
		/*
		 * ConfigurableApplicationContext ctx = new
		 * AnnotationConfigApplicationContext(SpringConfig.class); try {
		 * DoorProxy proxy = ctx.getBean(DoorProxy.class); proxy.start(); }
		 * finally { ctx.close(); } System.exit(0);
		 */
	}

	/**
	 * Start the jvm version of the service, and waits for it to complete.
	 *
	 * @param args
	 *            optional, arg[0] = timeout (seconds)
	 */
	public static void start(String[] args) {
		startThread(getArg(args, 1), null);
		while (thrd.isAlive()) {
			try {
				thrd.join();
			} catch (InterruptedException ie) {
				// Ignored
			}
		}
	}

	/**
	 * Stop the JVM version of the service.
	 *
	 * @param args
	 *            ignored
	 */
	public static void stop(String[] args) {
		if (thrd != null) {
			log("Interrupting the thread");
			thrd.interrupt();
		} else {
			log("No thread to interrupt");
		}
	}

	private static void deleteStopFile(String fileName) {
		final File tmpFile = tmpFile(fileName);
		log("Deleting file: " + tmpFile.getPath());
		tmpFile.delete();
	}

	private static void startThread(String waitParam, File f) {
		long wait = DEFAULT_PAUSE;
		if (waitParam != null) {
			wait = Integer.valueOf(waitParam).intValue();
		}
		log("Starting the service thread, wait(seconds): " + wait);
		thrd = new Thread(new DoorProxyDeamon(wait * MS_PER_SEC, f), "DoorProxyDeamon");
		thrd.start();
	}

	private static void logSystemEnvironment() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null)
			log("Missing currentThread context ClassLoader");
		else
			log("Using context ClassLoader : " + cl.toString());
		log("Program environment: ");
		log("System properties: ");
		Properties ps = System.getProperties();
		TreeSet ts = new TreeSet(ps.keySet());
		for (Iterator i = ts.iterator(); i.hasNext();) {
			String n = (String) i.next();
			log(n + " ->  " + ps.get(n));
		}
	}

	private static void log(String msg) {
		System.out.println(msg);
		logger.info(msg);
	}

	private static void printUsage() {
		log("You must supply the argument 'start' or 'stop'");
	}

	private static File tmpFile(String filename) {
		return new File(System.getProperty("java.io.tmpdir"), filename != null ? filename : "DoorProxy.pid");
	}

	private static String getArg(String[] args, int argnum) {
		if (args.length > argnum) {
			return args[argnum];
		} else {
			return null;
		}
	}

	public static File createStopFile(String filename) throws IOException {
		File f = tmpFile(filename);
		log("Creating file: " + f.getPath());
		f.createNewFile();
		return f;
	}

	@Override
	public void run() {
		log("Started thread in " + System.getProperty("user.dir"));
		logSystemEnvironment();
		ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
		try {
			DoorProxy proxy = ctx.getBean(DoorProxy.class);
			proxy.start();

			while (stopFile == null || stopFile.exists()) {
				try {
					Thread.sleep(pause);
				} catch (InterruptedException e) {
					log("Exitting");
					break;
				}
			}
			proxy.stop();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ctx.close();
		}
	}
}
