package com.yyy.batch;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BatchScriptsApplication {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: <importerName> <csv_path>");
            System.exit(-1);
        }
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(BatchConfig.class);
        try {
            TableImporter script = (TableImporter) ctx.getBean(args[0]);
            if (script == null) {
                System.out.println("Table importer " + args[0] + " not found!!");
            } else {
                script.execute(args[1]);
            }
        } finally {
            ctx.close();
        }
        System.exit(0);
    }
}
