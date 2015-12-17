package com.threemc.view;

import java.awt.Dialog.ModalityType;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.threemc.model.DatabaseConnection;

public class Main {

	private static ProgressbarMain prog;
	private static MainSystemInterface mainSys;
	
	private static String ip = "";
	private static String dbName = "";
	private static String dbUserName = "";
	private static String dbPassword = "";
	private static String dbPort = "";
	
	private static Preferences prefs;
	
	private static DatabaseSettings ds;

	public static void main(String[] args ) {
		
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

		ip = prefs.get("ip", "locahost");
		dbName = prefs.get("dbname", "threemcqueens");
		dbUserName = prefs.get("username", "root");
		dbPassword = prefs.get("password", "");
		dbPort = prefs.get("port", ""+3306);

		ds.setDefaults(ip, dbName, dbUserName, dbPassword, Integer.parseInt(dbPort));
		DatabaseConnection.setDefaults(ip, dbName, dbUserName, dbPassword, Integer.parseInt(dbPort));
		ds.setVisible(true);

		prog = new ProgressbarMain(mainSys, ModalityType.APPLICATION_MODAL);
		prog.setIndeterminate(true);
		prog.setVisible(true);
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				mainSys = new MainSystemInterface();
				return null;
			}

			protected void done() {
				mainSys.setVisible(true);
				prog.dispose();
				mainSys.login();
			}
		};
		worker.execute();
		
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				new MainSystemInterface();
//			}
//		});
	}
}
