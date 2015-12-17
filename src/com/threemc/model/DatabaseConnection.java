package com.threemc.model;

import java.awt.Dialog.ModalityType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import com.threemc.view.DatabaseSettings;
import com.threemc.view.PrefsListener;

public class DatabaseConnection {

	private static Connection con;
	
	private static String ip = "";
	private static String dbName = "";
	private static String dbUserName = "";
	private static String dbPassword = "";
	private static String dbPort = "";
	
	private static Preferences prefs;
	
	private static DatabaseSettings ds;

	public DatabaseConnection() {

	}
	
	public static void setDefaults(String ip, String dbName, String username, String password, int port) {
		DatabaseConnection.ip = ip;
		DatabaseConnection.dbName = dbName;
		DatabaseConnection.dbUserName = username;
		DatabaseConnection.dbPassword = password;
		DatabaseConnection.dbPort = ""+port;
	}

	public static Connection connect() throws Exception {
		if (con != null) {
			return con;
		} else {
			JOptionPane.showMessageDialog(null, "There is something wrong with your connection.\n\nPlease Input valid connection credentials for the database", "Connection Problems", JOptionPane.WARNING_MESSAGE);

			prefs = Preferences.userRoot().node("db");

			ds = new DatabaseSettings(null, ModalityType.APPLICATION_MODAL);
			ds.setPrefsListener(new PrefsListener() {
				public void preferenceSet(String ip, String dbName, String username, String password, int port) {
					prefs.put("dbname", dbName);
					prefs.put("ip", ip);
					prefs.put("username", username);
					prefs.put("password", password);
					prefs.putInt("port", 3306);
					ds.dispose();
				}
			});

			ip = prefs.get("ip", "localhost");
			dbName = prefs.get("dbname", "threemcqueens");
			dbUserName = prefs.get("username", "root");
			dbPassword = prefs.get("password", "");
			dbPort = prefs.get("port", ""+3306);

			ds.setDefaults(ip, dbName, dbUserName, dbPassword, Integer.parseInt(dbPort));
			DatabaseConnection.setDefaults(ip, dbName, dbUserName, dbPassword, Integer.parseInt(dbPort));
			ds.setVisible(true);
		}
			
		try {
//			String url = "jdbc:mysql://192.168.254.127:3306/" + dbName;
			Class.forName("com.mysql.jdbc.Driver");
			String connectionString = "jdbc:mysql://"+ ip + ":" + dbPort + "/" + dbName + "?user=" + dbUserName + "&password=" + dbPassword + "&useUnicode=true&characterEncoding=UTF-8";
			con = DriverManager.getConnection(connectionString);
		} catch (ClassNotFoundException e) {

		} catch (Exception e) {
			
		}
		return con;
	}

	public static Connection disconnect() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				System.out.println("can't close connection");
			}
		}
		return con;
	}
}