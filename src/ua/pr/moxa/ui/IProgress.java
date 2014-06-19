package ua.pr.moxa.ui;

public interface IProgress {
	String getFileName();
	String getBytes();
	double getProgress();	
	boolean isComplete();
	boolean isRunning();
	
	void setFileName(String fileName);
	void setBytes(String bytes);
	void setProgress(double progress); 
	void setComplete(boolean compl);
	void setRunning(boolean compl);
}
