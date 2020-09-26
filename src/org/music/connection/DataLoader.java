package org.music.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataLoader {

    public double[] loadMusicData(String sql, int dataSize) throws Exception {
        Connection con = ConnectionPool.getConnection();
//        System.out.println("before loading Memory in MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));
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
