package com.yyy.batch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

@Service("card")
public class CardImporter implements TableImporter {

    static Map<String, ColType> colTypes = new HashMap<String, ColType>();
    static {
        colTypes.put("DOOR_ID", ColType.INTEGER);
        colTypes.put("CARD_NO", ColType.INTEGER);
        colTypes.put("WORKER_ID", ColType.INTEGER);
    }
    @Autowired
    DataSource ds;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: WorkerImporter <csv_path>");
            System.exit(-1);
        }
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(BatchConfig.class);
        try {
            CardImporter script = ctx.getBean(CardImporter.class);
            script.execute(args[0]);
        } finally {
            ctx.close();
        }
        System.exit(0);
    }

    public void execute(String csvFile) throws IOException, SQLException {
        Reader in = new InputStreamReader(new FileInputStream(csvFile), "UTF-8");
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        try {
            Set<Long> doorIdSet = getDoorIds(con);
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            List<String> headers = null;
            int workerIdx = -1;
            int doorIdx = -1;
            boolean hasInsert = false;
            List<CSVRecord> skippedRecords = new ArrayList<CSVRecord>();
            for (CSVRecord record : records) {
                if (ps == null) {
                    headers = CSVUtils.toList(record);
                    workerIdx = getColumnIdxByName(record, "WORKER");
                    doorIdx = getColumnIdxByName(record, "DOOR_ID");
                    int questionMarksCnt = record.size();
                    String sql = "INSERT INTO CARD(" + toColumnNamesWithReplace(record, "WORKER", "WORKER_ID") + ") VALUES (" + CSVUtils.genQuestionMarks(questionMarksCnt) + ")";
                    System.out.println(sql);
                    ps = con.prepareStatement(sql);
                } else {
                    try {
                        System.out.println(record.toString());
                        for (int i = 0; i < record.size(); i++) {
                            String value = record.get(i);
                            Object parsedValue = null;
                            if (workerIdx == i) {
                                //replace worker name with worker id
                                parsedValue = getWorkerIdByName(con, value);
                                if (parsedValue == null) {
                                    throw new IllegalArgumentException("Invalid worker name : " + value);
                                }
                            } else {
                                ColType colType = colTypes.get(headers.get(i));
                                parsedValue = colType.parseString(value);
                                if (doorIdx == i) {
                                    //check door existence
                                    if (!doorIdSet.contains(parsedValue)) {
                                        throw new IllegalArgumentException("Invalid door id : " + value);
                                    }
                                }
                            }
                            ps.setObject(i + 1, parsedValue);
                        }
                        ps.addBatch();
                        hasInsert = true;
                    } catch (Exception e) {
                        System.out.println("Exception happened: " + e.getMessage());
                        skippedRecords.add(record);
                    }
                }
            }
            if (hasInsert) {
                int[] cnts = ps.executeBatch();
                ps.close();
                ps = null;
                System.out.println("Inserted:" + Arrays.toString(cnts));
            }
            System.out.println("Skipped records:");
            skippedRecords.forEach(record -> {
                System.out.println(record.toString());
            });
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

    private Set<Long> getDoorIds(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Set<Long> idSet = new HashSet<Long>();
        try {
            ps = con.prepareStatement("SELECT ID FROM DOOR");
            rs = ps.executeQuery();
            while (rs.next()) {
                idSet.add(rs.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("FAILDED TO load door id set : reason: " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
        return idSet;
    }

    private Object getWorkerIdByName(Connection con, String value) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Long workerId = null;
            ps = con.prepareStatement("SELECT ID FROM WORKER WHERE NAME = ?");
            ps.setString(1, value);
            rs = ps.executeQuery();
            if (rs.next()) {
                workerId = rs.getLong(1);
            }
            if (rs.next()) {
                System.out.println("Duplicate worker found for name : " + value);
                workerId = null;
            }
            return workerId;
        } catch (SQLException e) {
            System.out.println("FAILDED TO FIND WORKER " + value + " : reason: " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    private static int getColumnIdxByName(CSVRecord record, String colName) {
        for (int i = 0; i < record.size(); i++) {
            if (colName.equalsIgnoreCase(record.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static String toColumnNamesWithReplace(CSVRecord record, String oldName, String newName) {
        StringBuilder sb = new StringBuilder();
        record.forEach(cell -> {
            if (oldName.equalsIgnoreCase(cell)) {
                cell = newName;
            }
            sb.append(cell).append(',');
        });
        return sb.substring(0, sb.length() - 1);
    }
}
