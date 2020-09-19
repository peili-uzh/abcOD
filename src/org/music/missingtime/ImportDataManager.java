package org.music.missingtime;

import org.music.connection.ConnectionPool;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.FileReader;
import java.sql.Connection;

public class ImportDataManager {

	public static void main(String[] args) throws Exception {
		Connection con = ConnectionPool.getConnection();

		if (!con.isClosed()) {
			System.err.println("Copying text data rows from stdin");

			CopyManager copyManager = new CopyManager((BaseConnection) con);
			FileReader fileReader = new FileReader("/Users/Pei/Documents/data/discogs/series_ground_truth.csv");

			StringBuilder sql = new StringBuilder();
			sql.append("COPY series_with_ground_truth FROM STDIN WITH(");
			sql.append(" ENCODING 'UTF-8' ");
			sql.append(", FORMAT csv ");
			sql.append(", DELIMITER ','");
			sql.append(", QUOTE '\"'");
			sql.append(")");

			copyManager.copyIn(sql.toString(), fileReader);

			System.err.println("Done!");
		}

		ConnectionPool.putConnection(con);
	}
}
