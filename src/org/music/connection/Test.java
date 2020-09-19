package org.music.connection;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Test {

    public static void main(String[] args) throws Exception {

        Connection con = ConnectionPool.getConnection();
        if (!con.isClosed()) {
            Statement st = con.createStatement();
            String delete = "select count(1) from artist";
            System.out.println(delete);
            ResultSet result = st.executeQuery(delete);

            while (result.next()) {
                String count = result.getString(1);
                System.out.println(count);
            }
            //st.execute(delete);


            st.close();
		
			/*
			PreparedStatement prest = null;
			int id = 0;
			String art = "1;2;3;";
			HashMap<Integer, String> list = new HashMap<Integer, String>();
			list.put(id, art);
			String insert = "insert into recording_artist_list (id, list) values (?, ?);";
			
			
			con.setAutoCommit(false);
			prest = con.prepareStatement(insert);
			
			
			for(Map.Entry<Integer, String> e: list.entrySet()){
				prest.setInt(1, e.getKey());
				prest.setString(2, e.getValue());
				prest.execute();
				con.commit();
				
			}
		
		if(prest!=null){
			prest.close();
		}
		con.setAutoCommit(true);
		*/
        }


        ConnectionPool.putConnection(con);
    }

}
