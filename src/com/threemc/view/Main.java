package com.threemc.view;

import java.awt.Dialog.ModalityType;

import javax.swing.SwingWorker;

public class Main {

	private static ProgressbarMain prog;
	private static MainSystemInterface mainSys;

	public static void main(String[] args ) {

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
