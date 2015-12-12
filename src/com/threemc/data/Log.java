package com.threemc.data;
/**
 * @author Rashmier Ynawat
 * @since July 3, 2015
 * @version 1.0
 */
public class Log {
	
	private int id;
	private int user_id;
	private String logTitle;
	private String logDesc;
	private String logDate;
	
	public Log(int user_id, String logTitle, String logDesc, String logDate) {
		
		this.user_id = user_id;
		this.logTitle = logTitle;
		this.logDesc = logDesc;
		this.logDate = logDate;
	}
	
	public Log(int id, int user_id, String logTitle, String logDesc, String logDate) {
		
		this(user_id, logTitle, logDesc, logDate);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getLogTitle() {
		return logTitle;
	}

	public void setLogTitle(String logTitle) {
		this.logTitle = logTitle;
	}

	public String getLogDesc() {
		return logDesc;
	}

	public void setLogDesc(String logDesc) {
		this.logDesc = logDesc;
	}

	public String getLogDate() {
		return logDate;
	}

	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}
}
