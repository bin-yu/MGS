package com.yyy.batch;

import java.io.IOException;
import java.sql.SQLException;

public interface TableImporter {
    public void execute(String csvFile) throws IOException, SQLException;
}
