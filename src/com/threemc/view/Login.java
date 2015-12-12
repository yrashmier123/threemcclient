package com.threemc.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import com.threemc.controller.ControllerForUser;
import com.threemc.data.User;

public class Login extends Dialog {

	private JPanel panelTop;
	private JPanel panelCenter;
	private JPanel panelButton;

	private JLabel lblUser;
	private JLabel lblPass;

	private JTextField txtUser;
	private JPasswordField txtPass;

	private JButton btnLogin;
	private JButton btnCancel;

	private ControllerForUser controller;

	private User usera;

	private SimpleDateFormat format = new SimpleDateFormat(
			"MMMMM dd , yyyy hh:ss - EEEEE");
	
	private ProgressbarDialog prog;

	public Login(final Window parent, Dialog.ModalityType modal) {
		super(parent, "Login", modal);
		set(parent);
		initUI();
		layoutComponents();
		
		prog = new ProgressbarDialog(Login.this, ModalityType.APPLICATION_MODAL);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				Login.this.dispose();
				System.exit(0);
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});

		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String user = txtUser.getText().toString().trim();
					char[] pass = txtPass.getPassword();
					String passa = new String(pass);
					Pattern pattern = Pattern.compile("[^a-z0-9]",
							Pattern.CASE_INSENSITIVE);
					Matcher matcheru = pattern.matcher(user);
					boolean tofu = matcheru.find();
					Matcher matcherp = pattern.matcher(passa.toString());
					boolean tofp = matcherp.find();
					if (!tofu) {
						if (!tofp) {
							controller.connect();
							if (controller.checkUserAndPass(user, passa) == 1) {
								usera = controller.getUser().get(0);
								if (!usera.getUserStatus().equals("Active")) {
									log();
								} else {
									int okcancel = JOptionPane
											.showConfirmDialog(
													Login.this,
													"Your account is Already Logged in!\n\nDo you you want to Continue?",
													"Error Logging in!",
													JOptionPane.YES_NO_OPTION,
													JOptionPane.ERROR_MESSAGE);
									if (okcancel == JOptionPane.YES_OPTION) {
										log();
									}
								}
							} else {
								JOptionPane
								.showMessageDialog(
										Login.this,
										"Incorrect Username and password!",
										"Log in failed!",
										JOptionPane.ERROR_MESSAGE);
							}
						} else {
							JOptionPane
									.showMessageDialog(
											Login.this,
											"Special Characters are not allowed in Password",
											"Log in failed!",
											JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane
								.showMessageDialog(
										Login.this,
										"Special Characters are not allowed in Username",
										"Log in failed!",
										JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void log() throws SQLException {
		final String date = format.format(System.currentTimeMillis());
		prog.setIndeterminate(true);
		prog.setVisible(true);
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				int id = usera.getId();
				controller.updateLastLogIn(id, date);
				controller.updateUserStatus(id, "Active");
				controller.updateUserLogged(id, "true");
				return null;
			}
			protected void done() {
				usera.setUserStatus("Active");
				prog.dispose();
				JOptionPane.showMessageDialog(Login.this, "You have successfully Logged in!", "Welcome!", JOptionPane.INFORMATION_MESSAGE);
				Login.this.setVisible(false);
			}
		};
		worker.execute();
	}

	public User getUser() {
		return usera;
	}

	// TODO
	private void layoutComponents() {
		GridBagConstraints gc = new GridBagConstraints();

		Insets inset = new Insets(5, 5, 5, 5);

		gc.weighty = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = inset;

		gc.gridy = 0;
		gc.gridx = 0;
		gc.weightx = 0;
		panelCenter.add(lblUser, gc);

		gc.gridx = 1;
		gc.weightx = 1;
		panelCenter.add(txtUser, gc);

		gc.gridy++;
		gc.gridx = 0;
		gc.weightx = 0;
		panelCenter.add(lblPass, gc);

		gc.gridx = 1;
		gc.weightx = 1;
		panelCenter.add(txtPass, gc);

		gc.gridy++;
		gc.gridx = 1;
		gc.weighty = 1;
		gc.weightx = 0;
		gc.fill = GridBagConstraints.NONE;

		panelButton.add(btnLogin);
		panelButton.add(btnCancel);

		panelCenter.add(panelButton, gc);

		add(panelTop, BorderLayout.NORTH);
		add(panelCenter, BorderLayout.CENTER);
	}

	// TODO
	private void initUI() {
		controller = new ControllerForUser();
		Font f = new Font("Tahoma", Font.PLAIN, 15);

		panelTop = new JPanel();
		panelTop.setLayout(new FlowLayout());
		panelTop.setBackground(CustomColor.bgColor());

		panelCenter = new JPanel();
		panelCenter.setLayout(new GridBagLayout());
		panelCenter.setBackground(CustomColor.bgColor());

		panelButton = new JPanel();
		panelButton.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelButton.setBorder(BorderFactory.createEtchedBorder());
		panelButton.setBackground(Color.WHITE);

		lblUser = new JLabel("Username: ");
		lblPass = new JLabel("Password: ");

		lblUser.setFont(f);
		lblPass.setFont(f);

		txtUser = new JTextField();
		txtPass = new JPasswordField();

		txtUser.setFont(f);
		txtPass.setFont(f);

		btnLogin = new JButton("Log in");
		btnCancel = new JButton("Cancel");

		btnLogin.setFont(f);
		btnCancel.setFont(f);
	}

	// TODO
	private void set(final Window parent) {
		setResizable(false);
		setSize(400, 200);
		setLocationRelativeTo(parent);
		setLayout(new BorderLayout());
		Image img = new ImageIcon(getClass().getResource("/res/mcicon.png"))
				.getImage();
		setIconImage(img);
	}
}
