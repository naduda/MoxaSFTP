package ua.pr.moxa.controller;

import ua.pr.moxa.sftp.SFTP;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ControllerMainFRM {
	@FXML 
	private TextField address;
	@FXML 
	private TextField user;
	@FXML 
	private TextField password;
	@SuppressWarnings("rawtypes")
	@FXML 
	private ComboBox remDir;
	@FXML 
	private TextField mask;
	@FXML 
	private TextField locDir;
	@FXML 
	private Text fileName;
	@FXML 
	private Text progressBytes;
	@FXML 
	private ProgressBar progressBar;
	@FXML 
	private Text timerS;
	
	private SFTP sftp;
	
	private static String DEFAULT_PORT = "22";
	
	@FXML 
	protected void getFiles(ActionEvent event) {
		sftp = new SFTP();
		
		final Task<Void> taskSFTP = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				String[] addressPort = address.getText().split(":");
				String port_ = "";
				if (addressPort.length == 1) {
					port_ = DEFAULT_PORT;
				} else {
					port_ = addressPort[1];
				}
				
				sftp.get(addressPort[0], port_, user.getText(), password.getText(), 
						remDir.getValue().toString(), mask.getText(), locDir.getText());
				return null;
			}			
		};
		
		final Thread threadSFTP = new Thread(taskSFTP);
		threadSFTP.start();
		
		final Task<Void> taskProgress = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				while (!sftp.isComplete()) {					
					updateProgress(sftp);
					Thread.sleep(1000);
				}
				fileName.setText("COMPLETED");
				return null;
			}
			
			int i = 0;
			String fileName_old = "";
			int countCompleted = 0;
			protected void updateProgress(SFTP sftp) {
				progressBar.setProgress(sftp.getProgress());
				progressBytes.setText(sftp.getBytes());
				fileName.setText(sftp.getFileName());
				
				if (!fileName_old.equals(sftp.getFileName())) {
					i = 0;
					if (sftp.getFileName() != null) fileName_old = sftp.getFileName();
					if ((sftp.getBytes() != null) && (sftp.getBytes().indexOf("/") > 0)) countCompleted++;
				}

				timerS.setText("   " + (i++) + " s; " + "(" + countCompleted + " files downloaded)");
			}
		};
	
		new Thread(taskProgress).start();
	}
	
	@FXML 
	protected void putFiles(ActionEvent event) {
		sftp = new SFTP();
		
		final Task<Void> taskSFTP = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				String[] addressPort = address.getText().split(":");
				String port_ = "";
				if (addressPort.length == 1) {
					port_ = DEFAULT_PORT;
				} else {
					port_ = addressPort[1];
				}
				
				sftp.put(addressPort[0], port_, user.getText(), password.getText(), 
						remDir.getValue().toString(), mask.getText(), locDir.getText());
				return null;
			}			
		};
		
		final Thread threadSFTP = new Thread(taskSFTP);
		threadSFTP.start();
		
		final Task<Void> taskProgress = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				while (!sftp.isComplete()) {					
					updateProgress(sftp);
					Thread.sleep(1000);
				}
				fileName.setText("COMPLETED");
				return null;
			}
			
			int i = 0;
			String fileName_old = "";
			int countCompleted = 0;
			protected void updateProgress(SFTP sftp) {
				progressBar.setProgress(sftp.getProgress());
				progressBytes.setText(sftp.getBytes());
				fileName.setText(sftp.getFileName());
				
				if (!fileName_old.equals(sftp.getFileName())) {
					i = 0;
					if (sftp.getFileName() != null) fileName_old = sftp.getFileName();
					if ((sftp.getBytes() != null) && (sftp.getBytes().indexOf("/") > 0)) countCompleted++;
				}

				timerS.setText("   " + (i++) + " s; " + "(" + countCompleted + " files downloaded)");
			}
		};
	
		new Thread(taskProgress).start();
	}
}
