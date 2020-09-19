package org.music.car;

import org.music.connection.ConnectionPool;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Links {

    public ArrayList<Link> getLinks(String sql) throws Exception {
        Connection con = ConnectionPool.getConnection();
        ArrayList<Link> links = new ArrayList<Link>();

        if (!con.isClosed()) {
            Statement st = con.createStatement();
            System.out.println(sql);
            ResultSet result = st.executeQuery(sql);
            while (result.next()) {
                String url = result.getString("url");
                String brand = result.getString("brand");
                String year = result.getString("year");
                String model = result.getString("model");
                Link link = new Link(url, brand, year, model);
                if (!links.contains(link)) {
                    links.add(links.size(), link);
                }
            }

            st.close();
        }
        ConnectionPool.putConnection(con);
        return links;
    }

    public void importLink() throws Exception {
        Connection con = ConnectionPool.getConnection();

        if (!con.isClosed()) {
            System.out.println("Copying text data rows from stdin");

            CopyManager copyManager = new CopyManager((BaseConnection) con);
            FileReader fileReader = new FileReader("/Users/Pei/Documents/data/car/imported/porsche_Wealthport.csv");
            // FileReader fileReader = new
            // FileReader("/Users/Pei/Documents/data/car/link_with_brand.csv");

            StringBuilder sql = new StringBuilder();
            sql.append("COPY car.car_with_vin FROM STDIN WITH(");
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
}
