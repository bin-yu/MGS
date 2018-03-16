package org.yyy.doorproxy;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;

import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.JarFile;

public class Bootstrap extends JarLauncher {

    private static ClassLoader classLoader = null;
    private static Bootstrap bootstrap = null;

    protected void launch(String[] args, String mainClass, ClassLoader classLoader, String method) throws Exception {
        Class<?> cls = classLoader.loadClass(mainClass);
        Method m = cls.getDeclaredMethod(method, args.getClass());
        m.invoke(null, (Object) args);
    }

    public Bootstrap() {
        super();
    }

    public Bootstrap(Archive archive) {
        super(archive);
    }

    public static void start(String[] args) {
        try {
            String PATH_PROXY_JAR = System.getProperty("proxyjar.path", "./target/door-proxy.jar");
            JarFile.registerUrlProtocolHandler();
            JarFileArchive proxyjar = new JarFileArchive(new File(PATH_PROXY_JAR));
            bootstrap = new Bootstrap(proxyjar);
            classLoader = bootstrap.createClassLoader(Collections.singletonList(proxyjar));
            bootstrap.launch(args, bootstrap.getMainClass(), classLoader, "start");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void stop(String[] args) {
        try {
            if (bootstrap != null) {
                bootstrap.launch(args, bootstrap.getMainClass(), classLoader, "stop");
                bootstrap = null;
                classLoader = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        String mode = args != null && args.length > 0 ? args[0] : null;
        if ("start".equals(mode)) {
            Bootstrap.start(args);
        } else if ("stop".equals(mode)) {
            Bootstrap.stop(args);
        }
    }

}
