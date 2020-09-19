package org.music.car;

import org.music.connection.ConnectionPool;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.Connection;

public class Importer {

    public static void main(String[] args) throws Exception {

        for (int i = 1; i <= 1; i++) {

            String file = "/Users/Pei/Downloads/bugs/RD-4506/selected_attribute.csv";

            String table = "kb.relations";
            specialImportData(file, table);
        }

    }

    public static void exportData(String fileName, String query) throws Exception {
        Connection con = ConnectionPool.getConnection();

        if (!con.isClosed()) {
            System.out.println("Copying text data rows to stdout");

            CopyManager copyManager = new CopyManager((BaseConnection) con);
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(new File(fileName)), "UTF8"));

            StringBuilder sql = new StringBuilder();
            sql.append("COPY (" + query + ") to STDOUT WITH(");
            sql.append(" ENCODING 'UTF-8' ");
            sql.append(", FORMAT csv ");
            sql.append(", DELIMITER ','");
            sql.append(", QUOTE '\"'");
            sql.append(", HEADER");
            sql.append(")");

            System.out.println(sql.toString());

            // copyManager.copyOut(sql.toString(), writer);
            System.out.println("Done!");
            fileWriter.close();
        }

        ConnectionPool.putConnection(con);
    }

    public static void specialImportData(String fileName, String tableName) throws Exception {
        Connection con = ConnectionPool.getConnection();

        if (!con.isClosed()) {
            System.out.println("Copying text data rows from stdin");

            CopyManager copyManager = new CopyManager((BaseConnection) con);
            FileReader fileReader = new FileReader(fileName);
            // FileReader fileReader = new
            // FileReader("/Users/Pei/Documents/data/car/link_with_brand.csv");

            StringBuilder sql = new StringBuilder();
            sql.append("COPY " + tableName + " FROM STDIN WITH(");
            sql.append(" ENCODING 'UTF-8' ");
            sql.append(", FORMAT csv ");
            sql.append(", DELIMITER ','");
            sql.append(", QUOTE '\"'");
            sql.append(")");

            copyManager.copyIn(sql.toString(), fileReader);

            System.out.println("Done!");
        }

        ConnectionPool.putConnection(con);
    }

    /*
     * public static void importData(String fileName, String tableName) throws
     * Exception { Connection con = ConnectionPool.getConnection();
     *
     * if (!con.isClosed()) {
     * System.out.println("Copying text data rows from stdin");
     *
     * CopyManager copyManager = new CopyManager((BaseConnection) con);
     * FileReader fileReader = new FileReader(fileName); // FileReader
     * fileReader = new //
     * FileReader("/Users/Pei/Documents/data/car/link_with_brand.csv");
     *
     * StringBuilder sql = new StringBuilder(); sql.append("COPY " + tableName +
     * " FROM STDIN WITH("); sql.append(" ENCODING 'UTF-8' ");
     * sql.append(", FORMAT csv "); sql.append(", DELIMITER ','");
     * sql.append(", QUOTE '\"'"); sql.append(")");
     *
     * copyManager.copyIn(sql.toString(), fileReader);
     *
     * System.out.println("Done!"); }
     *
     * ConnectionPool.putConnection(con); }
     */
}
