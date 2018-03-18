package org.yyy.doorproxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.jar.JarFile;

public class Bootstrap extends JarLauncher {

    private static ClassLoader classLoader = null;
    private static Bootstrap bootstrap = null;

    protected void launch(String[] args, String mainClass, ClassLoader classLoader, String method) throws Exception {
    	Thread.currentThread().setContextClassLoader(classLoader);
    	Class<?> cls = classLoader.loadClass(mainClass);
        Method m = cls.getDeclaredMethod(method, args.getClass());
        m.invoke(null, (Object) args);
    }
    /*protected void launch(String[] args, String mainClass, ClassLoader classLoader, boolean wait)
            throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        Thread runnerThread = new Thread(() -> {
            try {
                createMainMethodRunner(mainClass, args, classLoader).run();
            }
            catch(Exception ex) {}
        });
        runnerThread.setContextClassLoader(classLoader);
        runnerThread.setName(Thread.currentThread().getName());
        runnerThread.start();
        if (wait == true) {
            runnerThread.join();
        }
    }*/

    
    public static void start (String []args) {
        bootstrap = new Bootstrap ();
        try {
            JarFile.registerUrlProtocolHandler();
            classLoader = bootstrap.createClassLoader(bootstrap.getClassPathArchives());
            //List<String> argList = new ArrayList<String>();
            //argList.add("start");
            //argList.addAll(Arrays.asList(args));
            //bootstrap.launch(argList.toArray(new String[0]), bootstrap.getMainClass(), classLoader, true);
            bootstrap.launch(args, bootstrap.getMainClass(), classLoader, "start");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
 
    public static void stop (String []args) {
        try {
            if (bootstrap != null) {
            	//List<String> argList = new ArrayList<String>();
                //argList.add("stop");
                //argList.addAll(Arrays.asList(args));
                //bootstrap.launch(argList.toArray(new String[0]), bootstrap.getMainClass(), classLoader, true);
                bootstrap.launch(args, bootstrap.getMainClass(), classLoader, "stop");
                bootstrap = null;
                classLoader = null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        String mode = args != null && args.length > 0 ? args[0] : null;
        if ("start".equals(mode)) {
            start(new String[0]);
        } else if ("stop".equals(mode)) {
            stop(new String[0]);
        }
    }

}
