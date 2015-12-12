package com.threemc.controller;

import java.sql.SQLException;
import java.util.ArrayList;

import com.threemc.data.Booking;
import com.threemc.data.Employee;
import com.threemc.data.Notice;
import com.threemc.data.Output;
import com.threemc.data.OutputsUpdate;
import com.threemc.model.DatabaseOutputsModule;

public class ControllerForOutputsModule {

	private DatabaseOutputsModule db;
	
	public ControllerForOutputsModule() {
		db = new DatabaseOutputsModule();
	}
	
	public String connect() throws Exception {
		return db.connect();
	}
	
	public void disconnect() throws Exception {
		db.disconnect();
	}
	
	public void addOutput(Output out) {
		db.addOutput(out);
	}
	
	public void addOutputUpdate(OutputsUpdate ou) {
		db.addOutputUpdate(ou);
	}
	
	public void saveOutputs() throws SQLException {
		db.saveOutputs();
	}
	
	public void saveOutputsUpdate() throws SQLException {
		db.saveOutputsUpdate();
	}
	
	public void loadAllOutputs() throws SQLException {
		db.loadAllOutputs();
	}
	
	public void loadAllOutputsById(String cat, int id, String stat) throws SQLException {
		db.loadAllOutputsById(cat, id , stat);
	}
	
	public void loadAllOutputsUpdate(int output_id) throws SQLException {
		db.loadAllOutputsUpdateById(output_id);
	}
	
	public void loadBookingRecordsByUserId(int id) throws SQLException {
		db.loadBookingRecordsByUserId(id);
	}
	
	public void loadAllBookingRecord(String status) throws SQLException {
		db.loadAllBookingRecord(status);
	}
	
	public void loadAllEmployee() throws SQLException {
		db.loadAllEmployee();
	}
	
	public Notice loadLastNotice() throws SQLException {
		return db.loadLastNotice();
	}
	
	public void updateNoticeNewToOld() throws SQLException {
		db.updateNoticeNewToOld();
	}
	
	public int getNoticeNewCount() throws SQLException {
		return db.getNoticeNewCount();
	}
	
	public ArrayList<Output> getOutputs() {
		return db.getOutputs();
	}
	
	public ArrayList<OutputsUpdate> getOutputsUpdate() {
		return db.getOutputUpdate();
	}
	
	public ArrayList<Booking> getBookings() {
		return db.getBookings();
	}
	
	public ArrayList<Booking> getBookingsAll() {
		return db.getBookingsAll();
	}
	
	public ArrayList<Employee> getEmployees() {
		return db.getEmployees();
	}
	
	public void searchOutout(int emp, String val, String stat) throws SQLException {
		db.searchOutput(emp, val, stat);
	}
	
	public void updateStatus(int output_id, String status) throws SQLException {
		db.updateStatus(output_id, status);
	}
}
