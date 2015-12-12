package com.threemc.controller;

import java.sql.SQLException;
import java.util.ArrayList;

import com.threemc.data.User;
import com.threemc.model.DatabaseUser;

public class ControllerForUser {

	private DatabaseUser db;
	
	public ControllerForUser() {
		db = new DatabaseUser();
	}
	
	public String connect() throws Exception {
		return db.connect();
	}
	
	public void disconnect() throws Exception {
		db.disconnect();
	}
	
	public int checkUserAndPass(String username, String password) throws SQLException {
		return db.checkUserAndPass(username, password);
	}
	
	public String checkUserStatus(int user_id) throws SQLException {
		return db.checkUserStatus(user_id);
	}
	
	public ArrayList<User> getUser() {
		return db.getUser();
	}
	
	public void updateLastLogIn(int user_id, String date) throws SQLException {
		db.updateLastLogin(user_id, date);
	}
	
	public void updateUserStatus(int user_id , String status) throws SQLException {
		db.updateUserStatus(user_id, status);
	}
	
	public void updateUserLogged(int user_id, String status) throws SQLException {
		db.updateUserLogged(user_id, status);
	}
}
