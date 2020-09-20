package org.music.abcod;

import org.music.connection.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class YearProcess {

	public static void main(String[] args) throws Exception {
		String dbName = args[0];//"discogs_release_1409";
		HashMap<Integer, Integer> yearMap = new HashMap<Integer, Integer>();
		yearMap = getYears(dbName);

		insertYear(dbName, yearMap);
	}

	/*
	 * get date value from discogs_release tables
	 */


	private static HashMap<Integer, Integer> getYears(String dbName) throws Exception {
		// TODO Auto-generated method stub
		HashMap<Integer, Integer> yearMap = new HashMap<Integer, Integer>();

		Connection con = ConnectionPool.getConnection();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			String sql = "select id, date from " + dbName;
			ResultSet result = st.executeQuery(sql);

			while (result.next()) {
				int id = result.getInt(1);
				String date = result.getString(2);

				int year = 0;
				if (!date.equals("")) {
					String substr = date.substring(0, 1);
					if (substr.matches("[0-9]"))
						year = Integer.parseInt(date.substring(0, 4));
				}
				//System.out.println(i+"\t"+id+"\t"+date+"\t"+year);
				yearMap.put(id, year);
				i++;
			}
			System.out.println("total # records \t" + i);
			st.close();
		}
		ConnectionPool.putConnection(con);
		return yearMap;
	}

	/*
	 * insert year into discogs_release tables
	 */

	private static void insertYear(String dbName,
								   HashMap<Integer, Integer> yearMap) throws Exception {
		// TODO Auto-generated method stub

		Connection con = ConnectionPool.getConnection();
		if (!con.isClosed()) {
			String insert = "update " + dbName + " set year=? where id =?";
			PreparedStatement st = con.prepareStatement(insert);
			//con.setAutoCommit(false);

			int i = 0;
			for (Map.Entry<Integer, Integer> e : yearMap.entrySet()) {
				int id = e.getKey();
				int year = e.getValue();
				st.setInt(2, id);
				st.setInt(1, year);
				st.addBatch();
				i++;
				if (i % 100 == 0) {
					System.out.println(i + "\t" + id + "\t" + year);
					st.executeBatch();
				}
			}
			st.executeBatch();

			st.close();

		}
		ConnectionPool.putConnection(con);
	}


}
