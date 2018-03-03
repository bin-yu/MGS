package com.yyy.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

public class CSVUtils {

    public static List<String> toList(CSVRecord record) {
        List<String> rt = new ArrayList<String>(record.size());
        record.forEach(cell -> {
            rt.add(cell);
        });
        return rt;
    }

    public static String toColumnNames(CSVRecord record) {
        StringBuilder sb = new StringBuilder();
        record.forEach(cell -> {
            sb.append(cell).append(',');
        });
        return sb.substring(0, sb.length() - 1);
    }

    public static String genQuestionMarks(long cnt) {
        StringBuilder sb = new StringBuilder();
        while (cnt-- > 0) {
            sb.append('?').append(',');
        }
        return sb.substring(0, sb.length() - 1);
    }

}
