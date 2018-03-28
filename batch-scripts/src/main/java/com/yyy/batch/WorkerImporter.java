package com.yyy.batch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

@Service("worker")
public class WorkerImporter implements TableImporter {
    @Autowired
    DataSource ds;

    static Map<String, ColType> colTypes = new HashMap<String, ColType>();
    static {
    	colTypes.put("DOMAIN_ID", ColType.INTEGER);
    	colTypes.put("SEX", ColType.INTEGER);
        colTypes.put("HOME_ADDR", ColType.STRING);
        colTypes.put("ID_NO", ColType.STRING);
        colTypes.put("ID_TYPE", ColType.STRING);
        colTypes.put("NAME", ColType.STRING);
        colTypes.put("TYPES", ColType.STRING);
        colTypes.put("EMPLOYER", ColType.STRING);
        colTypes.put("PHONE_NUMS", ColType.STRING_ARRAY);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: WorkerImporter <csv_path>");
            System.exit(-1);
        }
        ApplicationContext ctx = new AnnotationConfigApplicationContext(BatchConfig.class);
        WorkerImporter script = ctx.getBean(WorkerImporter.class);
        script.execute(args[0]);
        System.exit(0);
    }

    public void execute(String csvFile) throws IOException, SQLException {
        Reader in = new InputStreamReader(new FileInputStream(csvFile), "UTF-8");
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        try {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            List<String> headers = null;
            for (CSVRecord record : records) {
                if (ps == null) {
                    headers = CSVUtils.toList(record);
                    String sql = "INSERT INTO WORKER(" + CSVUtils.toColumnNames(record) + ") VALUES (" + CSVUtils.genQuestionMarks(record.size()) + ")";
                    System.out.println(sql);
                    ps = con.prepareStatement(sql);
                } else {
                    System.out.println(record.toString());
                    for (int i = 0; i < record.size(); i++) {
                        ColType colType = colTypes.get(headers.get(i));
                        ps.setObject(i + 1, colType.parseString(record.get(i)));
                    }
                    ps.addBatch();
                }
            }
            int[] cnts = ps.executeBatch();
            ps.close();
            ps = null;
            System.out.println("Inserted:" + Arrays.toString(cnts));
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
            con.close();
        }

    }

}
