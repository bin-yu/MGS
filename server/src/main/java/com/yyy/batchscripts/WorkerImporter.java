package com.yyy.batchscripts;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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

@Service
public class WorkerImporter {
    @Autowired
    DataSource ds;

    static enum ColType {
        STRING {
            @Override
            Object parseString(String value) {
                return value;
            }
        },
        STRING_ARRAY {
            @Override
            Object parseString(String value) {
                String[] arr = value.split(",");
                try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
                    try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                        o.writeObject(arr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return b.toByteArray();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return new byte[0];
            }
        };
        abstract Object parseString(String value);
    }

    static Map<String, ColType> colTypes = new HashMap<String, ColType>();
    static {
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
                    headers = toList(record);
                    String sql = "INSERT INTO WORKER(" + toColumnNames(record) + ") VALUES (" + genQuestionMarks(record.size()) + ")";
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

    private List<String> toList(CSVRecord record) {
        List<String> rt = new ArrayList<String>(record.size());
        record.forEach(cell -> {
            rt.add(cell);
        });
        return rt;
    }
    private String toColumnNames(CSVRecord record) {
        StringBuilder sb = new StringBuilder();
        record.forEach(cell -> {
            sb.append(cell).append(',');
        });
        return sb.substring(0, sb.length() - 1);
    }

    private String genQuestionMarks(long cnt) {
        StringBuilder sb = new StringBuilder();
        while (cnt-- > 0) {
            sb.append('?').append(',');
        }
        return sb.substring(0, sb.length() - 1);
    }

}
