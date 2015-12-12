package com.threemc.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.threemc.data.User;

public class DatabaseUser {

	private ArrayList<User> dbUsers;
	private Connection con;
	
	public DatabaseUser() {
		dbUsers = new ArrayList<User>();
	}
	
	public String connect() throws Exception {
		String msg = "";
		try {
			con = DatabaseConnection.connect();
			msg = "ok";
		} catch (Exception e) {
			msg = e.getMessage();
		}
		return msg;
	}
	
	public void disconnect() throws Exception {
		con = DatabaseConnection.disconnect();
	}
	
	public int checkUserAndPass(String username, String password) throws SQLException {
		dbUsers.clear();
		int ress = 0;
		String myQuery = "SELECT * FROM `users` WHERE `user_name` = \"" + username + "\" AND `user_password` = \"" + password + "\" LIMIT 1";
		Statement stmt = con.createStatement();
		ResultSet res = stmt.executeQuery(myQuery);
		try {
			if (res.next()) {
				int id = res.getInt("user_id");
				int empid = res.getInt("employee_id");
				String usernames = res.getString("user_name");
				String pass = res.getString("user_password");
				String llgin = res.getString("user_lastLogIn");
				String type = res.getString("user_type");
				String status = res.getString("user_status");
				User user = new User(id, empid, usernames, pass, llgin, type, status);
				dbUsers.add(user);
				ress = 1;
			} else {
				ress = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			stmt.close();
			res.close();
		}
		return ress;
	}
	
	public String checkUserStatus(int user_id) throws SQLException {
		String status = "";
		String myQuery = "SELECT user_status FROM `users` WHERE `user_id` = " + user_id + " LIMIT 1";
		Statement stmt = con.createStatement();
		ResultSet res = stmt.executeQuery(myQuery);
		try {
			if (res.next()) {
				status = res.getString("user_status");
			} else {
				status = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			stmt.close();
			res.close();
		}
		return status;
	}
	
	public ArrayList<User> getUser() {
		return dbUsers;
	}
	
	public void updateLastLogin(int user_id, String date) throws SQLException {
		String updateSql = "UPDATE `users` SET `user_lastLogIn` = ? WHERE `users`.`user_id` = ?;";
		PreparedStatement updateStmt = con.prepareStatement(updateSql);
		int col = 1;
		updateStmt.setString(col++, date);
		updateStmt.setInt(col++, user_id);
		updateStmt.executeUpdate();
		updateStmt.close();
	}
	
	public void updateUserStatus(int user_id, String status) throws SQLException {
		String updateSql = "UPDATE `users` SET `user_status` = ? WHERE `users`.`user_id` = ?;";
		PreparedStatement updateStmt = con.prepareStatement(updateSql);
		int col = 1;
		updateStmt.setString(col++, status);
		updateStmt.setInt(col++, user_id);
		updateStmt.executeUpdate();
		updateStmt.close();
	}
	
	public void updateUserLogged(int user_id, String status) throws SQLException {
		String updateSql = "UPDATE `users` SET `user_logged` = ? WHERE `users`.`user_id` = ?;";
		PreparedStatement updateStmt = con.prepareStatement(updateSql);
		int col = 1;
		updateStmt.setString(col++, status);
		updateStmt.setInt(col++, user_id);
		updateStmt.executeUpdate();
		updateStmt.close();
	}
}
