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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.threemc.controller.ControllerForOutputsModule;
import com.threemc.data.Output;
import com.threemc.data.OutputsUpdate;

public class OutputsUpdateForm extends Dialog {

	private JTabbedPane tabPane;

	private JPanel panelFirst;
	private JPanel panelName;
	private JPanel panelButton;
	private JPanel panelCenter;
	private JPanel panelSecond;

	private JLabel lblName;
	private JLabel lblStatus;

	private JTextArea txtUpdate;
	private JTextArea txtUpdateList;

	private JComboBox<String> cboStatus;

	private JButton btnSave;
	private JButton btnCancel;
	private JButton btnClear;

	private Output out;

	private ControllerForOutputsModule controllero;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"MMMMM dd , yyyy hh:ss - EEEEE");

	public OutputsUpdateForm(final Window parent,
			final Dialog.ModalityType modal) {
		super(parent, "Outputs - Update", modal);
		set(parent);
		initUI();
		layoutComponent();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				OutputsUpdateForm.this.dispose();
			}
		});

		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtUpdate.setText("");
			}
		});

		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int output_id = out.getId();
				int employee_id = out.getEmployee_id();
				String desc = txtUpdate.getText().toString();
				String date = dateFormat.format(System.currentTimeMillis());
				String status = cboStatus.getSelectedItem().toString();

				if (!desc.isEmpty()) {
					if (CustomPatternChecker.checkStringSomeCharsAllowed(desc)) {
						OutputsUpdate outd = new OutputsUpdate(output_id,
								employee_id, desc, date);
						try {
							controllero.connect();
							controllero.addOutputUpdate(outd);
							controllero.saveOutputsUpdate();
							controllero.updateStatus(output_id, status);
							txtUpdate.setText("");
							JOptionPane.showMessageDialog(
									OutputsUpdateForm.this, "Success!",
									"Outputs - Update",
									JOptionPane.INFORMATION_MESSAGE);
							setOutputUpdate();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else {
						JOptionPane
								.showMessageDialog(
										OutputsUpdateForm.this,
										"Don't leave empty fields and The only allowed special characters are as follows:\n"
												+ "\n\n ( ) - : @ & ' ! . ",
										"Outputs - Update",
										JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(OutputsUpdateForm.this,
							"Empty Description!", "Outputs -Update",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	public void setOutput(Output out) {
		this.out = out;
		lblName.setText(out.getOutputName());
	}

	public void setOutputUpdate() {
		txtUpdateList.setText("");
		ArrayList<OutputsUpdate> outs;
		StringBuffer list = new StringBuffer();
		try {
			controllero.connect();
			controllero.loadAllOutputsUpdate(out.getId());
			outs = controllero.getOutputsUpdate();
			if (outs.size() >= 1) {
				for (OutputsUpdate ouh : outs) {
					list.append(ouh.getOuDate() + " " + ouh.getOuDesc()
							+ "\n\n");
				}
				txtUpdateList.setText(list.toString());
			} else {
				txtUpdateList.setText("No updates for this output yet");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO
	private void initUI() {
		controllero = new ControllerForOutputsModule();

		panelName = new JPanel();
		panelName.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelName.setBackground(Color.WHITE);
		panelName.setBorder(BorderFactory.createEtchedBorder());

		panelButton = new JPanel();
		panelButton.setLayout(new FlowLayout(FlowLayout.LEFT));

		panelButton.setBackground(Color.WHITE);
		panelButton.setBorder(BorderFactory.createEtchedBorder());

		panelCenter = new JPanel();
		panelCenter.setLayout(new GridBagLayout());
		panelCenter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelCenter.setBackground(CustomColor.bgColor());

		panelFirst = new JPanel();
		panelFirst.setLayout(new BorderLayout());

		panelSecond = new JPanel();
		panelSecond.setLayout(new BorderLayout());

		Font f = new Font("Tahoma", Font.PLAIN, 15);
		Font fbold = new Font("Tahoma", Font.BOLD, 15);

		lblName = new JLabel("...");
		lblName.setFont(f);

		lblStatus = new JLabel("Status: ");
		lblStatus.setFont(f);

		txtUpdate = new JTextArea();
		txtUpdate.setLineWrap(true);
		txtUpdate.setFont(f);

		txtUpdateList = new JTextArea();
		txtUpdateList.setLineWrap(true);
		txtUpdateList.setFont(f);
		txtUpdateList.setEditable(false);

		btnSave = new JButton("Save");
		btnCancel = new JButton("Cancel");
		btnClear = new JButton("Clear");

		btnSave.setFont(f);
		btnCancel.setFont(f);
		btnClear.setFont(f);

		cboStatus = new JComboBox<String>();
		cboStatus.setFont(f);

		DefaultComboBoxModel<String> asd = new DefaultComboBoxModel<String>();
		asd.addElement("Ongoing");
		asd.addElement("Finished");

		cboStatus.setModel(asd);

		panelName.add(lblName);

		tabPane = new JTabbedPane();
		tabPane.setFont(fbold);

		tabPane.add("Update Form", panelFirst);
		tabPane.add("Update History", panelSecond);
	}

	// TODO
	private void layoutComponent() {
		GridBagConstraints gc = new GridBagConstraints();
		Insets inset = new Insets(5, 5, 5, 5);

		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = inset;

		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 2;
		panelCenter.add(new JScrollPane(txtUpdate,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), gc);

		gc.weightx = 0;
		gc.weighty = 0;
		gc.fill = GridBagConstraints.NONE;

		gc.gridy++;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gc.anchor = GridBagConstraints.LINE_END;
		panelCenter.add(lblStatus, gc);

		gc.gridx = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		panelCenter.add(cboStatus, gc);

		panelButton.add(btnClear);
		panelButton.add(btnSave);
		panelButton.add(btnCancel);

		panelFirst.add(panelName, BorderLayout.NORTH);
		panelFirst.add(panelCenter, BorderLayout.CENTER);
		panelFirst.add(panelButton, BorderLayout.SOUTH);

		panelSecond.add(new JScrollPane(txtUpdateList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

		add(tabPane, BorderLayout.CENTER);
	}

	// TODO
	private void set(final Window parent) {
		setSize(600, 450);
		setLayout(new BorderLayout());
		setLocationRelativeTo(parent);
		Image img = new ImageIcon(getClass().getResource("/res/mcicon.png"))
				.getImage();
		setIconImage(img);
		setBackground(CustomColor.bgColor());
	}
}
