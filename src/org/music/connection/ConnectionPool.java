package org.music.connection;

import java.sql.Connection;
import java.sql.DriverManager;

//import com.mysql.jdbc.Connection;

public class ConnectionPool {

    /**
     * IFI connection
     */
    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    // private static final String JDBC_DRIVER = "org.postgresql.Driver";
    // private static final String HOST =
    // "jdbc:postgresql://peter.ifi.uzh.ch:5401/musicbrainz_db";
    // private static final String USERNAME = "musicbrainz";
    // private static final String USERPASSWORD = "musicbrainz";
    private static BlockingQueue m_cons;
    private static final int POOL_SIZE = 10;

    /**
     * Local connection
     */
    private static final String HOST = "jdbc:postgresql://localhost:5432/car";
    private static final String USERNAME = "pei";
    private static final String USERPASSWORD = "pei";

    /**
     * Canada connection
     */
    // private static final String HOST =
    // "jdbc:mariadb://localhost:3306/music_for_abc";
    // private static final String USERNAME = "pei";
    // private static final String USERPASSWORD =
    // "BeiCh7getooquahQu2ooz8thieShouY6uj9quei1jaeL7aegaif2baegooDeu8ie";

    static {
        try {
            Class.forName(JDBC_DRIVER);
            m_cons = new BlockingQueue();
            for (int i = 0; i < POOL_SIZE; i++) {
                m_cons.put(DriverManager.getConnection(HOST, USERNAME, USERPASSWORD));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    public static Connection getConnection() throws Exception {
        return (Connection) m_cons.get();
    }

    public static void putConnection(java.sql.Connection con) throws Exception {
        m_cons.put(con);
    }


    public static final void main(String[] args) {
        try {
            Connection con = ConnectionPool.getConnection();
            //ConnectionPool.
            ConnectionPool.putConnection(con);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}