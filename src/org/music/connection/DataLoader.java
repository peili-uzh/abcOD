package org.music.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DataLoader {

    public HashMap<String, ArrayList<Double>> loadDataForABCDiscovery(String sql, int dataSize) throws Exception {
        Connection con = ConnectionPool.getConnection();
        HashMap<String, ArrayList<Double>> groupedYearSequence = new HashMap<>();

        int i = 0;
        if (!con.isClosed()) {
            Statement st = con.createStatement();
            System.out.println(sql);
            ResultSet result = st.executeQuery(sql);

            while (result.next()) {
                String groupId = result.getString("group_id");
                String year = result.getString("time");
                year = parseDateToYear(year);
//                String catalogNumber = result.getString("catno");
//                String catPrefix = catalogNumber.replaceAll("[0-9]", "");
//                System.out.println(catalogNumber+"\t"+catPrefix);
//                groupId = groupId + catPrefix;
//                System.out.println(catalogNumber+"\t"+catPrefix+"\t"+groupId);

                if (!year.equals("0") && i < dataSize) {
                    ArrayList<Double> yearSequence = new ArrayList<>();
                    if (groupedYearSequence.containsKey(groupId)) {
                        yearSequence = groupedYearSequence.get(groupId);
                    }
                    yearSequence.add(Double.valueOf(year));
                    groupedYearSequence.put(groupId, yearSequence);

                    i = i + 1;
                }
            }
            result.close();
            st.close();
        }

        ConnectionPool.putConnection(con);
        return groupedYearSequence;
    }

    public double[] loadDataForABDiscovery(String sql, int dataSize) throws Exception {
        Connection con = ConnectionPool.getConnection();
        double[] musicYearSequence = new double[dataSize];

        int i = 0;
        if (!con.isClosed()) {
            Statement st = con.createStatement();
//            System.out.println(sql);
            ResultSet result = st.executeQuery(sql);

            while (result.next()) {
                String year = result.getString("time");
                year = parseDateToYear(year);

                if (!year.equals("0") && i < dataSize) {
                    musicYearSequence[i] = Double.valueOf(year);
                    i = i + 1;
                }
            }
            result.close();
            st.close();
        }

        ConnectionPool.putConnection(con);

//        System.out.println("discogs label size \t" + musicYearSequence.length);
//        System.out.println("after loading Memory in MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));

        return musicYearSequence;
    }

    private String parseDateToYear(String year) {
        if (year != null) {
            if (year.equals("?") || year.equals("???") || year.equals("????") || year.equals("Unknown")
                    || year.equals("??") || year.equals("None") || year.equals("Not Known") || year.equals("")
                    || year.startsWith(" "))
                year = "0";
            if (year.length() > 4)
                year = year.trim().replace("-", "").substring(0, 4);
        } else {
            year = "0";
        }

        return year;
    }
}
