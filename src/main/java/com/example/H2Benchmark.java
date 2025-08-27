package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.h2.tools.Server;

public class H2Benchmark {

    private static final String JDBC_URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final int OPERATION_COUNT = 100000;
    private static final int INITIAL_ROWS = 1000000;

    public static void main(String[] args) throws Exception {
        Server server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists").start();
        System.out.println("H2 server started on port 9092.");

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            System.out.println("Connected to H2 database.");

            // Recreate the table for a clean benchmark
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS benchmark");
                stmt.execute("CREATE TABLE benchmark (id INT PRIMARY KEY, name VARCHAR(255), col1 VARCHAR(255), col2 VARCHAR(255), col3 VARCHAR(255), col4 VARCHAR(255), col5 VARCHAR(255), col6 VARCHAR(255), col7 VARCHAR(255), col8 VARCHAR(255), col9 VARCHAR(255), col10 VARCHAR(255))");
            }

            // Initial load of 1M rows
            System.out.println("\n--- Starting Initial Data Load ---");
            initialLoad(connection, INITIAL_ROWS);
            System.out.println("--- Initial Data Load Complete ---");


            // Benchmark INSERTs
            long insertTime = benchmarkInserts(connection, INITIAL_ROWS);

            // Benchmark MERGEs
            long mergeTime = benchmarkMerges(connection, INITIAL_ROWS + OPERATION_COUNT);

            System.out.println("\n--- Benchmark Comparison (on existing data) ---");
            System.out.printf("Operations per test: %d\n", OPERATION_COUNT);
            System.out.printf("Time taken for INSERTs: %d ms\n", insertTime);
            System.out.printf("Time taken for MERGEs: %d ms\n", mergeTime);

        } finally {
            server.stop();
            System.out.println("H2 server stopped.");
        }
    }

    private static void initialLoad(Connection connection, int rowCount) throws Exception {
        String sql = "INSERT INTO benchmark (id, name, col1, col2, col3, col4, col5, col6, col7, col8, col9, col10) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < rowCount; i++) {
                pstmt.setInt(1, i);
                pstmt.setString(2, "User" + i);
                for (int j = 1; j <= 10; j++) {
                    pstmt.setString(j + 2, "Value" + j);
                }
                pstmt.executeUpdate();
            }
        }
    }

    private static long benchmarkInserts(Connection connection, int startId) throws Exception {
        System.out.println("\nRunning INSERT benchmark...");
        long startTime = System.currentTimeMillis();
        String sql = "INSERT INTO benchmark (id, name, col1, col2, col3, col4, col5, col6, col7, col8, col9, col10) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < OPERATION_COUNT; i++) {
                pstmt.setInt(1, startId + i);
                pstmt.setString(2, "User" + (startId + i));
                for (int j = 1; j <= 10; j++) {
                    pstmt.setString(j + 2, "Value" + j);
                }
                pstmt.executeUpdate();
            }
        }
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private static long benchmarkMerges(Connection connection, int startId) throws Exception {
        System.out.println("\nRunning MERGE benchmark...");
        long startTime = System.currentTimeMillis();
        String sql = "MERGE INTO benchmark (id, name, col1, col2, col3, col4, col5, col6, col7, col8, col9, col10) KEY(id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < OPERATION_COUNT; i++) {
                pstmt.setInt(1, startId + i);
                pstmt.setString(2, "User" + (startId + i));
                for (int j = 1; j <= 10; j++) {
                    pstmt.setString(j + 2, "Value" + j);
                }
                pstmt.executeUpdate();
            }
        }
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
