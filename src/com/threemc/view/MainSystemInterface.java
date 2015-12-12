package com.threemc.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumnModel;

import com.threemc.controller.ControllerForOutputsModule;
import com.threemc.controller.ControllerForUser;
import com.threemc.data.Booking;
import com.threemc.data.Employee;
import com.threemc.data.Notice;
import com.threemc.data.Output;
import com.threemc.data.User;

public class MainSystemInterface extends JFrame {

	private JTabbedPane tabPane;

//	private JPanel panelTop;
	private JPanel panelOutputListTab;
	private JPanel panelOutsAssignTab;
	private JPanel panelTable;
	private JPanel panelTableA;
	private JPanel panelBot;
	private JPanel panelBotMan;
	private JPanel panelTtableTitle;
	private JPanel panelButton;
	private JPanel panelNoticeboard;

	private JLabel lblSearch;
	private JLabel lblByEvent;
	private JLabel lblTableTitle;
	private JLabel lblEvent;
	private JLabel lblOutputname;
	private JLabel lblOutputdesc;
	private JLabel lblEmp;
	private JLabel lblStats;

	private JComboBox<String> cboEvent;
	private JComboBox<String> cboEventTwo;
	private JComboBox<String> cboEmp;
	private JComboBox<String> cboStatusBy;

	private JTextField txtSearch;
	private JTextField txtOutputname;
	private JTextField txtOutputdesc;

	private JTextArea txtNoticeboard;

	private JButton btnLoadAll;
	private JButton btnRetrieve;
	private JButton btnUpdate;
	private JButton btnRefresh;
	private JButton btnSave;
	private JButton btnNew;
	private JButton btnRelease;

	private OutputTableModel model;
	private OutputTableModel modelA;
	private JTable table;
	private JTable tableA;

	private ControllerForOutputsModule controllero;
	private ControllerForUser controlleru;

	private ArrayList<Output> outputList;
	private ArrayList<Booking> bookingList;
	private ArrayList<Booking> bookingListA;
	private ArrayList<Employee> employeeList;

	private User user;
	private GridBagConstraints gc;
	private int outputId = 0;
	private Login log;
	private JMenuItem logout;
	private Timer timer;
	private Timer timerNotice;

	private Notice not;

	// TODO
	public MainSystemInterface() {
		setUILookAndFeel();
		set();
		initUI();
		layoutComponents();
		setJMenuBar(createMenuBar());

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					if (user != null) {
						if (user.getUserStatus().equals("Active")) {
							controlleru.connect();
							controlleru.updateUserStatus(user.getId(),
									"Inactive");
							controlleru.updateUserLogged(user.getId(), "true");
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		txtSearch.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent arg0) {
				search();
			}

			public void insertUpdate(DocumentEvent arg0) {
				search();
			}

			public void changedUpdate(DocumentEvent arg0) {
				search();
			}
		});

		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadFirstData();
			}
		});

		btnLoadAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadAllData();
			}
		});

		btnRetrieve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFirstData();
			}
		});

		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if (row != -1) {
					Output out = outputList.get(row);
					if (!out.getOutputStat().equals("Finished")) {
						if (!out.getOutputStat().equals("Released")) {
							OutputsUpdateForm ouf = new OutputsUpdateForm(
									MainSystemInterface.this,
									Dialog.ModalityType.APPLICATION_MODAL);
							ouf.setOutput(out);
							ouf.setOutputUpdate();
							ouf.setVisible(true);
						} else {
							String msg = "Already Released!";
							setMessage(msg, 0);
						}
					} else {
						String msg = "Item is ready for Pick-up";
						setMessage(msg, 2);
					}
				}
			}
		});

		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rowBooking = cboEventTwo.getSelectedIndex();
				int client_id = bookingListA.get(rowBooking).getClient_id();
				int event_id = bookingListA.get(rowBooking).getId();
				int rowEmp = cboEmp.getSelectedIndex();
				int emp_id = employeeList.get(rowEmp).getId();
				String outName = txtOutputname.getText();
				String outDesc = txtOutputdesc.getText();

				Output out = new Output(outputId, emp_id, event_id, client_id,
						outName, outDesc, "Pending");

				if (!outName.isEmpty()) {
					if (CustomPatternChecker
							.checkStringSomeCharsAllowed(outName)) {
						if (!outDesc.isEmpty()) {
							if (CustomPatternChecker
									.checkStringSomeCharsAllowed(outDesc)) {
								boolean b = true;
								for (int i = 0; i < outputList.size(); i++) {
									Output o = outputList.get(i);
									if (o.getOutputName().equalsIgnoreCase(
											outName)) {
										b = false;
									}
								}
								if (b) {
									try {
										controllero.connect();
										controllero.addOutput(out);
										controllero.saveOutputs();
										refreshBookingsFirst();
										refreshBookingsOutputList(1);
										loadFirstData();
										closeAssign();
										JOptionPane
										.showMessageDialog(
												MainSystemInterface.this,
												"Successfully saved Information",
												"Outputs",
												JOptionPane.INFORMATION_MESSAGE);
										txtOutputname.setText(cboEventTwo.getSelectedItem().toString()+ " - ");
										txtOutputdesc.setText("");
									} catch (Exception es) {
										es.printStackTrace();
									}
								} else {
									JOptionPane.showMessageDialog(
											MainSystemInterface.this,
											"Duplicate output name!", "Output",
											JOptionPane.INFORMATION_MESSAGE);
								}
							} else {
								JOptionPane
										.showMessageDialog(
												MainSystemInterface.this,
												"Don't leave empty fields and The only allowed special characters are as follows:\n"
														+ "\n\n ( ) - : @ & ' ! . ",
												"Bookings",
												JOptionPane.ERROR_MESSAGE);
							}
						} else {
							JOptionPane.showMessageDialog(
									MainSystemInterface.this,
									"Output description cannot be empty",
									"Outputs - Saving failed",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane
								.showMessageDialog(
										MainSystemInterface.this,
										"Don't leave empty fields and The only allowed special characters are as follows:\n"
												+ "\n\n ( ) - : @ & ' ! . ",
										"Bookings", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(MainSystemInterface.this,
							"Output name cannot be empty",
							"Outputs - Saving failed",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openAssign();
			}
		});
		
		btnRelease.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int row = table.getSelectedRow();
					if(row != -1) {
						if(outputList.get(row).getOutputStat().equals("Finished")) {
							if(controllero.connect().equals("ok")) {
								int oid = outputList.get(row).getId();
								controllero.updateStatus(oid, "Released");
								loadFirstData();
								JOptionPane.showMessageDialog(MainSystemInterface.this, "Successfully Updated Output Record", "Output - Update", JOptionPane.INFORMATION_MESSAGE);
							} else {
								System.out.println(controllero.connect());
							}
						} else {
							setMessage("Record is not yet set as Finished", 0);
						}
					} else {
						setMessage("Please select a record from the list", 0);
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				tableClicked();
			}
		});

		table.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				tableClicked();
			}
		});

		cboEvent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshBookingsOutputList(0);
			}
		});

		cboEventTwo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshBookingsOutputList(1);
				txtOutputname.setText(bookingListA.get(
						cboEventTwo.getSelectedIndex()).getEventName()
						+ " - ");
			}
		});

		cboStatusBy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFirstData();
			}
		});

		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
	}

	// TODO

	public void login() {
		try {
			if (user != null) {
				if (user.getUserStatus().equals("Active")) {
					controlleru.connect();
					controlleru.updateUserStatus(user.getId(), "Inactive");
					controlleru.updateUserLogged(user.getId(), "true");
					timer.stop();
					timerNotice.stop();
				}
			}

			log = new Login(MainSystemInterface.this,
					Dialog.ModalityType.APPLICATION_MODAL);
			log.setVisible(true);
			user = log.getUser();
			log.dispose();

			loadFirstData();
			refreshBookingsFirst();
			setIfUserIsManager();
			loadFirstNotice();
			logout.setText("Logout (" + user.getUserName() + ")");

			timer.start();
			timerNotice.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu accMenu = new JMenu("Account");
		logout = new JMenuItem("Log out");
		accMenu.addSeparator();
		accMenu.add(logout);
		menuBar.add(accMenu);
		return menuBar;
	}

	private void setIfUserIsManager() {
		if (user.getUserType().equals("user")) {
			tabPane.setEnabledAt(1, false);
			panelBotMan.setVisible(false);
		} else {
			try {
				tabPane.setEnabledAt(1, true);
				panelBotMan.setVisible(true);
				controllero.connect();
				controllero.loadAllEmployee();
				employeeList = controllero.getEmployees();
				DefaultComboBoxModel<String> asd = new DefaultComboBoxModel<String>();
				for (int i = 0; i < employeeList.size(); i++) {
					Employee emp = employeeList.get(i);
					asd.addElement(emp.getEmpLastName() + " , "
							+ emp.getEmpFirstName() + " "
							+ emp.getEmpMiddleName());
				}
				cboEmp.setModel(asd);

				gc.gridy++;
				gc.gridx = 0;
				gc.weighty = 0;
				gc.fill = GridBagConstraints.HORIZONTAL;

				panelBotMan.add(btnRetrieve);
				panelBotMan.add(btnLoadAll);
				panelBotMan.add(btnRelease);

				panelOutputListTab.add(panelBotMan, gc);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void loadAllData() {
		try {
			controllero.connect();
			controllero.loadAllOutputs();
			outputList = controllero.getOutputs();
			setDataList(outputList);
			refreshList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadFirstData() {
		try {
			String stat = cboStatusBy.getSelectedItem().toString();
			controllero.connect();
			controllero.loadAllOutputsById("employee", user.getEmployee_id(),
					stat);
			outputList = controllero.getOutputs();
			setDataList(outputList);
			refreshList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadFirstNotice() {
		try {
			if(controllero.connect().equals("ok")) {
				not = controllero.loadLastNotice();
				if(not != null) {
					StringBuffer msg = new StringBuffer();
					msg.append(not.getDesc());
					txtNoticeboard.setText(msg.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void refreshBookingsFirst() {
		try {
			controllero.connect();
			controllero.loadAllBookingRecord("All");
			bookingListA = controllero.getBookingsAll();
			
			controllero.loadBookingRecordsByUserId(user.getId());
			bookingList = controllero.getBookings();
			DefaultComboBoxModel<String> ee = new DefaultComboBoxModel<String>();
			for (int i = 0; i < bookingList.size(); i++) {
				Booking book = bookingList.get(i);
				ee.addElement(book.getEventName());
			}
			cboEvent.setModel(ee);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			DefaultComboBoxModel<String> ees = new DefaultComboBoxModel<String>();
			for (int i = 0; i < bookingListA.size(); i++) {
				Booking book = bookingListA.get(i);
				ees.addElement(book.getEventName());
			}
			cboEventTwo.setModel(ees);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void refreshBookingsOutputList(int tab) {
		try {
			if (tab == 0) {
				int id = bookingList.get(cboEvent.getSelectedIndex()).getId();
				String stat = cboStatusBy.getSelectedItem().toString();
				controllero.connect();
				controllero.loadAllOutputsById("event", id, stat);
				outputList = controllero.getOutputs();
				setDataList(outputList);
				refreshList();
			} else if (tab == 1) {
				int id = bookingListA.get(cboEventTwo.getSelectedIndex())
						.getId();
				controllero.connect();
				controllero.loadAllOutputsById("event", id, "All");
				outputList = controllero.getOutputs();
				setDataAssign(outputList);
				refreshAssign();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void search() {
		try {
			String stat = cboStatusBy.getSelectedItem().toString();
			int emp_id = user.getEmployee_id();
			controllero.connect();
			controllero.searchOutout(emp_id, txtSearch.getText().toString(),
					stat);
			outputList = controllero.getOutputs();
			setDataList(outputList);
			refreshList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void tableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			Output out = outputList.get(row);
			String msgg = "Outputs Table: " + out.getOutputName()
					+ " ; Status :" + out.getOutputStat();
			setMessage(msgg, 1);
			if (!out.getOutputStat().equals("Finished")) {
				if (!out.getOutputStat().equals("Released")) {

				} else {
					String msg = "Already Released!";
					setMessage(msg, 1);
				}
			} else {
				String msg = "Output is ready for Pick-up";
				setMessage(msg, 2);
			}
		}
	}

	private void setMessage(String msg, int cat) {
		lblTableTitle.setText(msg);
		if (cat == 1) {
			panelTtableTitle.setBackground(Color.WHITE);
		} else if (cat == 0) {
			panelTtableTitle.setBackground(CustomColor.notOkColorBackGround());
		} else if (cat == 2) {
			panelTtableTitle.setBackground(CustomColor.okColorBackGround());
		}
	}

	private void closeAssign() {
		cboEventTwo.setEnabled(false);
		txtOutputname.setEnabled(false);
		txtOutputdesc.setEnabled(false);
		cboEmp.setEnabled(false);
		btnSave.setEnabled(false);
		btnNew.setEnabled(true);
	}

	private void openAssign() {
		cboEventTwo.setEnabled(true);
		txtOutputname.setEnabled(true);
		txtOutputdesc.setEnabled(true);
		cboEmp.setEnabled(true);
		btnSave.setEnabled(true);
		btnNew.setEnabled(false);
	}

	private void setDataList(ArrayList<Output> db) {
		model.setData(db);
	}

	private void refreshList() {
		model.fireTableDataChanged();
	}

	private void setDataAssign(ArrayList<Output> db) {
		modelA.setData(db);
	}

	private void refreshAssign() {
		modelA.fireTableDataChanged();
	}

	// TODO
	private void initUI() {
		controllero = new ControllerForOutputsModule();
		controlleru = new ControllerForUser();
		Font f = new Font("Tahom", Font.PLAIN, 15);
		Font fbold = new Font("Tahom", Font.BOLD, 15);

		Dimension dim = getPreferredSize();
		dim.height = 100;

//		panelTop = new JPanel();
//		panelTop.setLayout(new FlowLayout());
//		panelTop.setPreferredSize(dim);

		panelOutputListTab = new JPanel();
		panelOutputListTab.setLayout(new GridBagLayout());
		panelOutputListTab.setBackground(CustomColor.bgColor());
		panelOutputListTab.setBorder(BorderFactory
				.createEmptyBorder(5, 5, 5, 5));

		panelOutsAssignTab = new JPanel();
		panelOutsAssignTab.setLayout(new GridBagLayout());
		panelOutsAssignTab.setBackground(CustomColor.bgColor());
		panelOutsAssignTab.setBorder(BorderFactory
				.createEmptyBorder(5, 5, 5, 5));

		panelTable = new JPanel();
		panelTable.setLayout(new BorderLayout());

		panelTableA = new JPanel();
		panelTableA.setLayout(new BorderLayout());

		panelNoticeboard = new JPanel();
		panelNoticeboard.setLayout(new GridBagLayout());
		panelNoticeboard.setBackground(CustomColor.bgColor());

		panelBot = new JPanel();
		panelBot.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelBot.setBackground(Color.WHITE);
		panelBot.setBorder(BorderFactory.createEtchedBorder());

		panelBotMan = new JPanel();
		panelBotMan.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelBotMan.setBackground(Color.WHITE);
		panelBotMan.setBorder(BorderFactory.createEtchedBorder());

		panelButton = new JPanel();
		panelButton.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelButton.setBackground(CustomColor.bgColor());

		panelTtableTitle = new JPanel();
		panelTtableTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelTtableTitle.setBackground(Color.WHITE);
		panelTtableTitle.setBorder(BorderFactory.createEtchedBorder());

		tabPane = new JTabbedPane();
		tabPane.add("Output Lists", panelOutputListTab);
		tabPane.add("Assign Output", panelOutsAssignTab);
		tabPane.add("Notice Board", panelNoticeboard);

		tabPane.setFont(fbold);

		lblOutputdesc = new JLabel("Output description: ");
		lblSearch = new JLabel("Search:");
		lblTableTitle = new JLabel("Outputs Table");
		lblOutputname = new JLabel("Output name: ");
		lblByEvent = new JLabel("By Event:");
		lblEmp = new JLabel("Assign to: ");
		lblEvent = new JLabel("Event: ");
		lblStats = new JLabel("By Status:");

		lblTableTitle.setFont(f);
		lblOutputname.setFont(f);
		lblOutputdesc.setFont(f);
		lblByEvent.setFont(f);
		lblSearch.setFont(f);
		lblEvent.setFont(f);
		lblEmp.setFont(f);
		lblStats.setFont(f);

		txtOutputname = new JTextField(20);
		txtOutputdesc = new JTextField(20);
		txtSearch = new JTextField(20);
		txtNoticeboard = new JTextArea();
		
		txtNoticeboard.setWrapStyleWord(true);
		txtNoticeboard.setEditable(false);

		txtSearch.setFont(f);
		txtOutputname.setFont(f);
		txtOutputdesc.setFont(f);
		txtNoticeboard.setFont(f);

		cboEvent = new JComboBox<String>();
		cboEvent.setFont(f);

		cboEventTwo = new JComboBox<String>();
		cboEmp = new JComboBox<String>();

		cboEventTwo.setFont(f);
		cboEmp.setFont(f);

		cboStatusBy = new JComboBox<String>();
		cboStatusBy.setFont(f);

		DefaultComboBoxModel<String> gg = new DefaultComboBoxModel<String>();
		gg.addElement("All");
		gg.addElement("Pending");
		gg.addElement("Ongoing");
		gg.addElement("Finished");
		gg.addElement("Released");

		cboStatusBy.setModel(gg);

		model = new OutputTableModel();
		table = new JTable(model);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.getTableHeader().setFont(fbold);
		table.setFont(f);
		table.setRowHeight(20);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(400);
		tcm.getColumn(1).setPreferredWidth(300);
		tcm.getColumn(2).setPreferredWidth(500);
		tcm.getColumn(3).setPreferredWidth(150);
		tcm.getColumn(4).setPreferredWidth(150);

		modelA = new OutputTableModel();
		tableA = new JTable(modelA);

		tableA.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableA.getTableHeader().setReorderingAllowed(false);
		tableA.getTableHeader().setResizingAllowed(true);
		tableA.getTableHeader().setFont(fbold);
		tableA.setFont(f);
		tableA.setRowHeight(20);
		tableA.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableColumnModel tcmA = tableA.getColumnModel();
		tcmA.getColumn(0).setPreferredWidth(400);
		tcmA.getColumn(1).setPreferredWidth(300);
		tcmA.getColumn(2).setPreferredWidth(500);
		tcmA.getColumn(3).setPreferredWidth(150);
		tcmA.getColumn(4).setPreferredWidth(150);

		btnLoadAll = new JButton("Load All");
		btnRefresh = new JButton("Refresh");
		btnRetrieve = new JButton("Fetch my pending outputs");
		btnUpdate = new JButton("Update");
		btnSave = new JButton("Save");
		btnNew = new JButton("Create New");
		btnRelease = new JButton("Set as Released");

		btnRefresh.setFont(f);

		closeAssign();

		timer = new Timer(2000, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(controlleru.connect().equals("ok")) {
						if (controlleru.checkUserStatus(user.getId()).equals(
								"Inactive")) {
							JOptionPane
									.showMessageDialog(
											MainSystemInterface.this,
											"Your session has Expired! Please Log in again.",
											"Session Expired",
											JOptionPane.ERROR_MESSAGE);
							login();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		timerNotice = new Timer(1000, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try {
					if(controllero.connect().equals("ok")) {
						if(controllero.getNoticeNewCount() > 0) {
							controllero.updateNoticeNewToOld();
							loadFirstNotice();
						}
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
	}

	// TODO
	private void layoutComponents() {
		gc = new GridBagConstraints();

		Insets inset = new Insets(5, 5, 5, 5);

		gc.weighty = 0;
		gc.insets = inset;

		gc.gridy = 0;
		gc.gridx = 0;
		gc.weightx = 0;
		gc.fill = GridBagConstraints.NONE;
		panelOutputListTab.add(lblSearch, gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		panelOutputListTab.add(txtSearch, gc);

		gc.gridx = 2;
		gc.weightx = 0;
		gc.fill = GridBagConstraints.NONE;
		panelOutputListTab.add(lblByEvent, gc);

		gc.gridx = 3;
		panelOutputListTab.add(cboEvent, gc);

		gc.gridx = 4;
		panelOutputListTab.add(lblStats, gc);

		gc.gridx = 5;
		panelOutputListTab.add(cboStatusBy, gc);

		gc.gridx = 6;
		panelOutputListTab.add(btnRefresh, gc);

		gc.gridy++;
		gc.gridx = 0;
		gc.gridwidth = 7;
		gc.weightx = 1;
		gc.fill = GridBagConstraints.BOTH;

		panelTtableTitle.add(lblTableTitle);

		panelOutputListTab.add(panelTtableTitle, gc);

		gc.gridy++;
		gc.gridx = 0;
		gc.weighty = 1;
		panelTable.add(new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		panelOutputListTab.add(panelTable, gc);

		gc.gridy++;
		gc.gridx = 0;
		gc.weighty = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;

		panelBot.add(btnUpdate);

		panelOutputListTab.add(panelBot, gc);

		GridBagConstraints gc2 = new GridBagConstraints();

		Insets inseta = new Insets(5, 5, 5, 5);

		gc2.weightx = 0;
		gc2.weighty = 0;
		gc2.fill = GridBagConstraints.NONE;
		gc2.insets = inseta;

		gc2.gridy = 0;
		gc2.gridx = 0;
		gc2.anchor = GridBagConstraints.LINE_END;
		panelOutsAssignTab.add(lblEvent, gc2);

		gc2.gridx = 1;
		gc2.anchor = GridBagConstraints.LINE_START;
		panelOutsAssignTab.add(cboEventTwo, gc2);

		gc2.gridy++;
		gc2.gridx = 0;
		gc2.weighty = 1;
		gc2.weightx = 1;
		gc2.gridwidth = 2;
		gc2.fill = GridBagConstraints.BOTH;
		panelTableA.add(new JScrollPane(tableA,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		panelOutsAssignTab.add(panelTableA, gc2);

		gc2.gridy++;
		gc2.gridx = 0;
		gc2.weighty = 0;
		gc2.weightx = 0;
		gc2.gridwidth = 1;
		gc2.fill = GridBagConstraints.NONE;
		gc2.anchor = GridBagConstraints.LINE_END;
		panelOutsAssignTab.add(lblOutputname, gc2);

		gc2.gridx = 1;
		gc2.fill = GridBagConstraints.HORIZONTAL;
		gc2.anchor = GridBagConstraints.LINE_START;
		panelOutsAssignTab.add(txtOutputname, gc2);

		gc2.gridy++;
		gc2.gridx = 0;
		gc2.fill = GridBagConstraints.NONE;
		gc2.anchor = GridBagConstraints.LINE_END;
		panelOutsAssignTab.add(lblOutputdesc, gc2);

		gc2.gridx = 1;
		gc2.fill = GridBagConstraints.HORIZONTAL;
		gc2.anchor = GridBagConstraints.LINE_START;
		panelOutsAssignTab.add(txtOutputdesc, gc2);

		gc2.gridy++;
		gc2.gridx = 0;
		gc2.fill = GridBagConstraints.NONE;
		gc2.anchor = GridBagConstraints.LINE_END;
		panelOutsAssignTab.add(lblEmp, gc2);

		gc2.gridx = 1;
		gc2.anchor = GridBagConstraints.LINE_START;
		panelOutsAssignTab.add(cboEmp, gc2);

		panelButton.add(btnNew);
		panelButton.add(btnSave);

		gc2.gridy++;
		gc2.gridx = 1;
		gc2.fill = GridBagConstraints.NONE;
		panelOutsAssignTab.add(panelButton, gc2);
		
		//

		GridBagConstraints gf = new GridBagConstraints();
		
		gf.insets = inset;
		gf.weightx = 1;
		gf.weighty = 1;
		gf.fill = GridBagConstraints.BOTH;
		gf.gridx = 0;
		gf.gridy = 0;
		
		panelNoticeboard.add(new JScrollPane(txtNoticeboard,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),gf);

		
		//
		add(tabPane, BorderLayout.CENTER);
	}

	private void setUILookAndFeel() {
		try {
			 UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			// UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		} catch (Exception e) {

		}
	}

	// TODO
	private void set() {
		setLayout(new BorderLayout());
		setSize(1100, 700);
		setMinimumSize(new Dimension(1100, 700));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Image img = new ImageIcon(getClass().getResource("/res/mcicon.png"))
				.getImage();
		setIconImage(img);
	}
}
