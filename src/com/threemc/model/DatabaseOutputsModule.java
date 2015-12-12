package com.threemc.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.threemc.data.Booking;
import com.threemc.data.Employee;
import com.threemc.data.Notice;
import com.threemc.data.Output;
import com.threemc.data.OutputsUpdate;
import com.threemc.view.CategoryGender;

public class DatabaseOutputsModule {

	private ArrayList<OutputsUpdate> dbOutputsUpdates;
	private ArrayList<Booking> dbBooking;
	private ArrayList<Booking> dbBookingA;
	private ArrayList<Output> dbOutput;
	private ArrayList<Employee> dbEmp;
	private Connection con;
	
	
	public DatabaseOutputsModule() {
		dbOutputsUpdates = new ArrayList<OutputsUpdate>();
		dbBooking = new ArrayList<Booking>();
		dbBookingA = new ArrayList<Booking>();
		dbOutput = new ArrayList<Output>();
		dbEmp = new ArrayList<Employee>();
	}
	
	// connect database
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
	
	public void addOutput(Output out) {
		dbOutput.clear();
		dbOutput.add(out);
	}
	
	public void addOutputUpdate(OutputsUpdate oud) {
		dbOutputsUpdates.clear();
		dbOutputsUpdates.add(oud);
	}
	
	// Save Outputs
	public void saveOutputs() throws SQLException {
		
		String checkSql = "SELECT COUNT(*) AS count FROM outputs WHERE output_id = ?";
		PreparedStatement checkStmt = con.prepareStatement(checkSql);

		String insertSql = "INSERT INTO " + "outputs(employee_id , event_id, "
				+ "client_id, output_name , output_desc, output_status) "
				+ "VALUES(?, ?, ?, ?, ?, ?)";
		PreparedStatement insertStmt = con.prepareStatement(insertSql);

		String updateSql = "UPDATE outputs SET employee_id = ? , "
				+ "event_id = ? , client_id = ? , "
				+ "output_name = ? , output_desc = ? , output_status = ? "
				+ "WHERE output_id  = ? ";
		PreparedStatement updateStmt = con.prepareStatement(updateSql);

		for (Output out: dbOutput) {
			int id = out.getId();
			int empid = out.getEmployee_id();
			int cid = out.getClient_id();
			int eid = out.getEvent_id();
			String oname = out.getOutputName();
			String odesc = out.getOutputDesc();
			String ostat = out.getOutputStat();
			
			checkStmt.setInt(1, id);
			ResultSet checkResult = checkStmt.executeQuery();
			checkResult.next();
			int count = checkResult.getInt(1);
			
			if (count == 0) {
				int col = 1;
				insertStmt.setInt(col++, empid);
				insertStmt.setInt(col++, eid);
				insertStmt.setInt(col++, cid);
				insertStmt.setString(col++, oname);
				insertStmt.setString(col++, odesc);
				insertStmt.setString(col++, ostat);
				insertStmt.executeUpdate();
			} else {
				int col = 1;
				updateStmt.setInt(col++, empid);
				updateStmt.setInt(col++, eid);
				updateStmt.setInt(col++, cid);
				updateStmt.setString(col++, oname);
				updateStmt.setString(col++, odesc);
				updateStmt.setString(col++, ostat);
				updateStmt.setInt(col++, id);
				updateStmt.executeUpdate();
			}
		}
		insertStmt.close();
		updateStmt.close();
		checkStmt.close();
	}
	
	// Save Outputs
	public void saveOutputsUpdate() throws SQLException {
		
		String checkSql = "SELECT COUNT(*) AS count FROM outputs_updates WHERE ou_id = ?";
		PreparedStatement checkStmt = con.prepareStatement(checkSql);

		String insertSql = "INSERT INTO " + "outputs_updates(output_id, employee_id, "
				+ "ou_desc, ou_date) "
				+ "VALUES(?, ?, ?, ?)";
		PreparedStatement insertStmt = con.prepareStatement(insertSql);

		String updateSql = "UPDATE outputs_updates SET employee_id = ? , "
				+ "output_id = ? , ou_desc = ? , ou_date = ? "
				+ "WHERE ou_id  = ? ";
		
		PreparedStatement updateStmt = con.prepareStatement(updateSql);

		for (OutputsUpdate out: dbOutputsUpdates) {
			
			int id = out.getId();
			int oid = out.getOutput_id();
			int empid = out.getEmployee_id();
			String odesc = out.getOuDesc();
			String odate = out.getOuDate();
			
			checkStmt.setInt(1, id);
			ResultSet checkResult = checkStmt.executeQuery();
			checkResult.next();
			int count = checkResult.getInt(1);
			
			if (count == 0) {
				int col = 1;
				insertStmt.setInt(col++, oid);
				insertStmt.setInt(col++, empid);
				insertStmt.setString(col++, odesc);
				insertStmt.setString(col++, odate);
				insertStmt.executeUpdate();
			} else {
				int col = 1;
				updateStmt.setInt(col++, oid);
				updateStmt.setInt(col++, empid);
				updateStmt.setString(col++, odesc);
				updateStmt.setString(col++, odate);
				updateStmt.setInt(col++, id);
				updateStmt.executeUpdate();
			}
		}
		insertStmt.close();
		updateStmt.close();
		checkStmt.close();
	}
	
	public void loadAllOutputs() throws SQLException {
		dbOutput.clear();
		String sql = "SELECT * FROM outputs";
		Statement loadStatement= null;
		ResultSet res = null;
		try {
			loadStatement = con.createStatement();
			res = loadStatement.executeQuery(sql);
			while (res.next()) {
				int id = res.getInt("output_id");
				int empid = res.getInt("employee_id");
				int eid = res.getInt("event_id");
				int cid = res.getInt("client_id");
				String oname = res.getString("output_name");
				String odesc = res.getString("output_desc");
				String ostat = res.getString("output_status");
				String eventDate = loadEventDateByEvent(eid);
				Employee emp = loadEmployeeByEmpId(empid);
				
				Output out = new Output(id, empid, eid, cid, oname, odesc, ostat);
				out.setEventDate(eventDate);
				out.setEmp(emp);
				dbOutput.add(out);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			loadStatement.close();
			res.close();
		}
	}
	
	public void loadAllOutputsById(String category, int id , String status) throws SQLException {
		dbOutput.clear();
		String sql = "";
		if(status.equals("All")) {
			if(category.equals("output")) {
				sql = "SELECT * FROM outputs WHERE output_id = " + id;
			} else if(category.equals("employee")) {
				sql = "SELECT * FROM outputs WHERE employee_id = " + id;
			} else if(category.equals("event")) {
				sql = "SELECT * FROM outputs WHERE event_id = " + id;
			} else if(category.equals("client")) {
				sql = "SELECT * FROM outputs WHERE client_id = " + id;
			}
		} else {
			if(category.equals("output")) {
				sql = "SELECT * FROM outputs WHERE output_id = " + id + " AND output_status = \"" + status + "\"";
			} else if(category.equals("employee")) {
				sql = "SELECT * FROM outputs WHERE employee_id = " + id + " AND output_status = \"" + status + "\"";
			} else if(category.equals("event")) {
				sql = "SELECT * FROM outputs WHERE event_id = " + id + " AND output_status = \"" + status + "\"";
			} else if(category.equals("client")) {
				sql = "SELECT * FROM outputs WHERE client_id = " + id + " AND output_status = \"" + status + "\"";
			}
		}
		
		Statement loadStatement = null;
		ResultSet res = null;
		try {
			loadStatement = con.createStatement();
			res = loadStatement.executeQuery(sql);
			while (res.next()) {
				int oid = res.getInt("output_id");
				int empid = res.getInt("employee_id");
				int eid = res.getInt("event_id");
				int cid = res.getInt("client_id");
				String oname = res.getString("output_name");
				String odesc = res.getString("output_desc");
				String ostat = res.getString("output_status");
				String eventDate = loadEventDateByEvent(eid);
				Employee emp = loadEmployeeByEmpId(empid);
				
				Output out = new Output(oid, empid, eid, cid, oname, odesc, ostat);
				out.setEventDate(eventDate);
				out.setEmp(emp);
				dbOutput.add(out);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			loadStatement.close();
			res.close();
		}
	}
	
	public void loadAllOutputsUpdateById(int output_id) throws SQLException {
		dbOutputsUpdates.clear();
		String sql = "SELECT * FROM outputs_updates WHERE output_id = " + output_id;
		Statement loadStatement= null;
		ResultSet res = null;
		try {
			loadStatement = con.createStatement();
			res = loadStatement.executeQuery(sql);
			while (res.next()) {
				int id = res.getInt("ou_id");
				int oid = res.getInt("output_id");
				int empid = res.getInt("employee_id");
				String odate = res.getString("ou_date");
				String odesc = res.getString("ou_desc");
				
				OutputsUpdate out = new OutputsUpdate(id, oid, empid, odesc, odate);
				dbOutputsUpdates.add(out);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			loadStatement.close();
			res.close();
		}
	}
	
	// loads all event
	public void loadAllBookingRecord(String status) throws SQLException {
		dbBookingA.clear();
		String sql = "";
		if(status.equals("All")) {
			sql = "SELECT DISTINCT e.event_id , "
					+ "e.client_id, e.event_title , "
					+ "e.event_venue, e.event_details, "
					+ "e.event_status, e.event_status2, "
					+ "e.event_type, e.event_date, "
					+ "e.event_time, e.event_guest,  e.event_paymentStatus ,"
					+ "CASE WHEN (SELECT DISTINCT " + "s.event_id as eventss "
					+ "FROM services_wanted AS s "
					+ "WHERE e.event_id = s.event_id) " + "IS NULL THEN 'false' "
					+ "ELSE 'true' END " + "AS HasServices FROM events AS e WHERE e.event_status = 'Open' ORDER BY "
							+ "e.event_title ASC";
		} else if(status.equals("Done") || status.equals("Ongoing") || status.equals("Cancel")) {
			sql = "SELECT DISTINCT e.event_id , "
					+ "e.client_id, e.event_title , "
					+ "e.event_venue, e.event_details, "
					+ "e.event_status, e.event_status2, "
					+ "e.event_type, e.event_date, "
					+ "e.event_time, e.event_guest,  e.event_paymentStatus ,"
					+ "CASE WHEN (SELECT DISTINCT " + "s.event_id as eventss "
					+ "FROM services_wanted AS s "
					+ "WHERE e.event_id = s.event_id) " + "IS NULL THEN 'false' "
					+ "ELSE 'true' END " + "AS HasServices FROM events AS e  WHERE e.event_status2 = '"+ status +"' ORDER BY "
							+ "e.event_title ASC";
		} else {
			sql = "SELECT DISTINCT e.event_id , "
					+ "e.client_id, e.event_title , "
					+ "e.event_venue, e.event_details,"
					+ "e.event_status, e.event_status2, "
					+ "e.event_type, e.event_date, "
					+ "e.event_time, e.event_guest,  e.event_paymentStatus ,"
					+ "CASE WHEN (SELECT DISTINCT " + "s.event_id as eventss "
					+ "FROM services_wanted AS s "
					+ "WHERE e.event_id = s.event_id) " + "IS NULL THEN 'false' "
					+ "ELSE 'true' END " + "AS HasServices FROM events AS e  WHERE e.event_status = '"+ status +"' ORDER BY "
							+ "e.event_title ASC";
		}
		Statement loadStatement = null;
		ResultSet res = null;
		try {
			loadStatement = con.createStatement();
			res = loadStatement.executeQuery(sql);
			while (res.next()) {
				int id = res.getInt("event_id");
				int client_id = res.getInt("client_id");
				String eventName = res.getString("event_title");
				String eventVenue = res.getString("event_venue");
				String eventDetails = res.getString("event_details");
				String eventType = res.getString("event_type");
				String eventDate = res.getString("event_date");
				String eventTime = res.getString("event_time");
				int eventGuest = res.getInt("event_guest");
				String eventStatus = res.getString("event_status");
				String eventStatus2 = res.getString("event_status2");
				boolean hsv = res.getBoolean("HasServices");
				String eventPaymentStatus = res.getString("event_paymentStatus");
				Booking book = new Booking(id, client_id, eventName,
					eventVenue, eventDetails, eventGuest, eventDate,
					eventTime, eventType);
				book.setHasServices(hsv);
				book.setEventStatus(eventStatus);
				book.setEventStatus2(eventStatus2);
				book.setEventPaymentStatus(eventPaymentStatus);
				dbBookingA.add(book);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			loadStatement.close();
			res.close();
		}
	}

	// loads all event by user id
	public void loadBookingRecordsByUserId(int idf) throws SQLException {
		dbBooking.clear();
		String sql = "SELECT DISTINCT e.*, CASE WHEN (SELECT DISTINCT s.event_id as eventss FROM services_wanted AS s WHERE e.event_id = s.event_id) IS NULL THEN 'false' ELSE 'true' END AS HasServices FROM outputs o, events e, users u WHERE o.event_id = e.event_id AND o.employee_id = u.employee_id AND u.user_id = " + idf;
		
//		if(status.equals("All")) {
//			sql = "SELECT DISTINCT e.event_id , "
//					+ "e.client_id, e.event_title , "
//					+ "e.event_venue, e.event_details, "
//					+ "e.event_status, e.event_status2, "
//					+ "e.event_type, e.event_date, "
//					+ "e.event_time, e.event_guest,  e.event_paymentStatus ,"
//					+ "CASE WHEN (SELECT DISTINCT " + "s.event_id as eventss "
//					+ "FROM services_wanted AS s "
//					+ "WHERE e.event_id = s.event_id) " + "IS NULL THEN 'false' "
//					+ "ELSE 'true' END " + "AS HasServices FROM events AS e ORDER BY "
//							+ "e.event_title ASC";
//		} else if(status.equals("Done") || status.equals("Ongoing") || status.equals("Cancel")) {
//			sql = "SELECT DISTINCT e.event_id , "
//					+ "e.client_id, e.event_title , "
//					+ "e.event_venue, e.event_details, "
//					+ "e.event_status, e.event_status2, "
//					+ "e.event_type, e.event_date, "
//					+ "e.event_time, e.event_guest,  e.event_paymentStatus ,"
//					+ "CASE WHEN (SELECT DISTINCT " + "s.event_id as eventss "
//					+ "FROM services_wanted AS s "
//					+ "WHERE e.event_id = s.event_id) " + "IS NULL THEN 'false' "
//					+ "ELSE 'true' END " + "AS HasServices FROM events AS e  WHERE e.event_status2 = '"+ status +"' ORDER BY "
//							+ "e.event_title ASC";
//		} else {
//			sql = "SELECT DISTINCT e.event_id , "
//					+ "e.client_id, e.event_title , "
//					+ "e.event_venue, e.event_details,"
//					+ "e.event_status, e.event_status2, "
//					+ "e.event_type, e.event_date, "
//					+ "e.event_time, e.event_guest,  e.event_paymentStatus ,"
//					+ "CASE WHEN (SELECT DISTINCT " + "s.event_id as eventss "
//					+ "FROM services_wanted AS s "
//					+ "WHERE e.event_id = s.event_id) " + "IS NULL THEN 'false' "
//					+ "ELSE 'true' END " + "AS HasServices FROM events AS e  WHERE e.event_status = '"+ status +"' ORDER BY "
//							+ "e.event_title ASC";
//		}
		Statement loadStatement = null;
		ResultSet res = null;
		try {
			loadStatement = con.createStatement();
			res = loadStatement.executeQuery(sql);
			while (res.next()) {
				int id = res.getInt("event_id");
				int client_id = res.getInt("client_id");
				String eventName = res.getString("event_title");
				String eventVenue = res.getString("event_venue");
				String eventDetails = res.getString("event_details");
				String eventType = res.getString("event_type");
				String eventDate = res.getString("event_date");
				String eventTime = res.getString("event_time");
				int eventGuest = res.getInt("event_guest");
				String eventStatus = res.getString("event_status");
				String eventStatus2 = res.getString("event_status2");
				boolean hsv = res.getBoolean("HasServices");
				String eventPaymentStatus = res.getString("event_paymentStatus");
				Booking book = new Booking(id, client_id, eventName,
					eventVenue, eventDetails, eventGuest, eventDate,
					eventTime, eventType);
				book.setHasServices(hsv);
				book.setEventStatus(eventStatus);
				book.setEventStatus2(eventStatus2);
				book.setEventPaymentStatus(eventPaymentStatus);
				dbBooking.add(book);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			loadStatement.close();
			res.close();
		}
	}

	// loads all event by event date
	public String loadEventDateByEvent(int event_id) throws SQLException {
		String sql = "SELECT `event_date` FROM `events` WHERE `event_id` = " + event_id;
		String eventDate = "";
		Statement loadStatement = null;
		ResultSet res = null;
		try {
			loadStatement = con.createStatement();
			res = loadStatement.executeQuery(sql);
			while (res.next()) {
				eventDate = res.getString("event_date");
			}
			return eventDate;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			loadStatement.close();
			res.close();
		}
		return eventDate;
	}

	public void loadAllEmployee() throws SQLException {
		dbEmp.clear();
		String sql = "SELECT e.`employee_id`, e.`position_id`, "
				+ "e.`employee_firstName`, e.`employee_middleName`, "
				+ "e.`employee_lastName`, e.`employee_gender`, "
				+ "e.`employee_address`, e.`employee_dateOfBirth`, "
				+ "e.`employee_dateOfEmployment`, e.`employee_contactno`, "
				+ "e.`employee_status`, (SELECT p.position_name FROM "
				+ "positions p WHERE e.position_id = p.position_id) "
				+ "AS position_name FROM `employees` e ORDER BY e.`employee_lastName`";
		Statement loadStatement = null;
		ResultSet res = null;
		try {
			loadStatement = con.createStatement();
			res = loadStatement.executeQuery(sql);
			while (res.next()) {
				int id = res.getInt("employee_id");
				int posid = res.getInt("position_id");
				String fname = res.getString("employee_firstName");
				String mname = res.getString("employee_middleName");
				String lname = res.getString("employee_lastName");
				String gender = res.getString("employee_gender");
				String address = res.getString("employee_address");
				String dob = res.getString("employee_dateOfBirth");
				String doe = res.getString("employee_dateOfEmployment");
				String cont = res.getString("employee_contactno");
				String stat = res.getString("employee_status");
				String pos = res.getString("position_name");
				
				Employee emp = new Employee(id, fname, mname, lname, dob, doe, pos, address, cont, CategoryGender.valueOf(gender));
				emp.setEmpPosId(posid);
				emp.setEmpStatus(stat);
				dbEmp.add(emp);
			}
		} catch (SQLException sqle) {
			throw new SQLException();
		} finally {
			loadStatement.close();
			res.close();
		}
	}
	
	public Employee loadEmployeeByEmpId(int empid) throws SQLException {
		Employee emp = null;
		String sql = "SELECT e.`employee_id`, e.`position_id`, "
				+ "e.`employee_firstName`, e.`employee_middleName`, "
				+ "e.`employee_lastName`, e.`employee_gender`, "
				+ "e.`employee_address`, e.`employee_dateOfBirth`, "
				+ "e.`employee_dateOfEmployment`, e.`employee_contactno`, "
				+ "e.`employee_status`, (SELECT p.position_name FROM "
				+ "positions p WHERE e.position_id = p.position_id) "
				+ "AS position_name FROM `employees` "
				+ "e WHERE e.`employee_id` = "+empid+" "
				+ "ORDER BY e.`employee_lastName` LIMIT 1";
		Statement loadStatement = null;
		ResultSet res = null;
		try {
			loadStatement = con.createStatement();
			res = loadStatement.executeQuery(sql);
			while (res.next()) {
				int id = res.getInt("employee_id");
				int posid = res.getInt("position_id");
				String fname = res.getString("employee_firstName");
				String mname = res.getString("employee_middleName");
				String lname = res.getString("employee_lastName");
				String gender = res.getString("employee_gender");
				String address = res.getString("employee_address");
				String dob = res.getString("employee_dateOfBirth");
				String doe = res.getString("employee_dateOfEmployment");
				String cont = res.getString("employee_contactno");
				String stat = res.getString("employee_status");
				String pos = res.getString("position_name");
				
				emp = new Employee(id, fname, mname, lname, dob, doe, pos, address, cont, CategoryGender.valueOf(gender));
				emp.setEmpPosId(posid);
				emp.setEmpStatus(stat);
			}
			return emp;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			loadStatement.close();
			res.close();
		}
		return emp;
	}
	
	public Notice loadLastNotice() throws SQLException {
		String sql = "SELECT * FROM notices ORDER BY notice_id DESC LIMIT 1";
		Statement loadStatement = null;
		ResultSet res = null;
		Notice not = null;
		try {
			loadStatement = con.createStatement();
			res = loadStatement.executeQuery(sql);
			while (res.next()) {
				int id = res.getInt("notice_id");
				String date = res.getString("notice_date");
				String desc = res.getString("notice_desc");

				 not = new Notice(id,date, desc);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			loadStatement.close();
			res.close();
		}
		return not;
	}

	public void updateNoticeNewToOld() throws SQLException {
		String updateSql = "UPDATE `notices` SET `notice_status` = ? WHERE `notices`.`notice_status` =  \"new\"";
		PreparedStatement updateStmt = con.prepareStatement(updateSql);
		int col = 1;
		updateStmt.setString(col++, "old");
		updateStmt.executeUpdate();
		updateStmt.close();
	}

	public int getNoticeNewCount() throws SQLException {
		String checkSql = "SELECT DISTINCT COUNT(*) as counts FROM notices n WHERE notice_status = \"new\"";
		PreparedStatement checkStmt = con.prepareStatement(checkSql);
		ResultSet checkResult = checkStmt.executeQuery();
		checkResult.next();
		int ss = checkResult.getInt("counts");
		return ss;
	}

	public ArrayList<Output> getOutputs() {
		return dbOutput;
	}

	public ArrayList<OutputsUpdate> getOutputUpdate() {
		return dbOutputsUpdates;
	}

	public ArrayList<Booking> getBookings() {
		return dbBooking;
	}

	public ArrayList<Booking> getBookingsAll() {
		return dbBookingA;
	}

	public ArrayList<Employee> getEmployees() {
		return dbEmp;
	}

	public void searchOutput(int employee_id, String value, String status) throws SQLException {
		String val = value.replace("'", "");
		dbOutput.clear();
		String sql = "";
		if(status.equals("All")) {
			sql = "SELECT * FROM `outputs` WHERE `output_name` LIKE '%" + val + "%' AND employee_id = " + employee_id;
		} else {
			sql = "SELECT * FROM `outputs` WHERE `output_name` LIKE '%" + val + "%' AND output_status = \"" + status +"\" AND employee_id = " + employee_id;
		}

		Statement loadStatement = null;
		ResultSet res = null;
		try {
			loadStatement = con.createStatement();
			res = loadStatement.executeQuery(sql);
			while (res.next()) {
				int oid = res.getInt("output_id");
				int empid = res.getInt("employee_id");
				int eid = res.getInt("event_id");
				int cid = res.getInt("client_id");
				String oname = res.getString("output_name");
				String odesc = res.getString("output_desc");
				String ostat = res.getString("output_status");
				String eventDate = loadEventDateByEvent(eid);
				Employee emp = loadEmployeeByEmpId(empid);

				Output out = new Output(oid, empid, eid, cid, oname, odesc, ostat);
				out.setEventDate(eventDate);
				out.setEmp(emp);
				dbOutput.add(out);
			}
		} catch (SQLException slqe) {
			throw new SQLException();
		} finally {
			loadStatement.close();
			res.close();
		}
	}

	public void updateStatus(int output_id, String status) throws SQLException {
		String updateSql = "UPDATE `outputs` SET `output_status` = ? WHERE `outputs`.`output_id` = ?;";
		PreparedStatement updateStmt = con.prepareStatement(updateSql);
		int col = 1;
		updateStmt.setString(col++, status);
		updateStmt.setInt(col++, output_id);
		updateStmt.executeUpdate();
		updateStmt.close();
	}
}