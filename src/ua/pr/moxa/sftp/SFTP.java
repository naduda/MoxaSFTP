package ua.pr.moxa.sftp;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Vector;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import ua.pr.moxa.ui.IProgress;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTP implements IProgress {
	private String fileName;
	private String bytes;
	private double progress;
	public static boolean completed;
	private boolean running;
	
	public void get(String address, String port, String user, String passw, 
			String remDir, String mask, String locDir) {
		
		JSch jsch = new JSch();
	    Session session = null;
	    ChannelSftp sftpChannel = null;
	    
	    try {
	    	setRunning(true);
	    	setComplete(false);   	
	    	setFileName("connecting... ");

	        session = jsch.getSession(user, address, Integer.valueOf(port));
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.setPassword(passw);
	        session.connect();
	        setFileName("connected");
	        
	        Channel channel = session.openChannel("sftp");
	        channel.connect();
	        sftpChannel = (ChannelSftp) channel;

	        String path = "";
	        
	        if ("log".equals(remDir)) {
	        	path = "/home/powersys/log";
	        	sftpChannel.cd(path);
	        	@SuppressWarnings("unchecked")
				Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*"); 
	        	long oldDate = 0;
	        	String ffileName = "";
	        	for (ChannelSftp.LsEntry listEntry : list) {
	        		if (listEntry.getAttrs().getATime() > oldDate) {
	        			ffileName= listEntry.getFilename();
	        			oldDate = listEntry.getAttrs().getATime();
	        		}
	        	}
	        	path = path + "/" + ffileName;
	        	System.out.println(path);
	        }
	        if ("database".equals(remDir)) {
	        	path = "/home/powersys/database";
	        	mask = "*";
	        }
	        setFileName("cd .....");
	        sftpChannel.cd(path);
	        setFileName(path);
	        @SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list = sftpChannel.ls(mask); 

	        System.out.println(list.size() + " files");
	        for (ChannelSftp.LsEntry listEntry : list) {
	        	System.out.println(listEntry.getFilename());
	        	setFileName(listEntry.getFilename());
	        	long fileSize = listEntry.getAttrs().getSize();
	        	setBytes("0/" + fileSize);
	        	
	        	try (InputStream in = sftpChannel.get(listEntry.getFilename());
	        			FileOutputStream fOut = new FileOutputStream(locDir + "/" + listEntry.getFilename())) {
	        		
	        		byte[] buffer = new byte[256];

	                int length;
	                int bytesProgress = 0;
	                
	                while ((length = in.read(buffer)) > 0) {
	                	fOut.write(buffer, 0, length);
	                	bytesProgress = bytesProgress + length;
	                	if (bytesProgress > fileSize) bytesProgress = (int) fileSize;
	                	setBytes(bytesProgress + "/" + fileSize);
	                	setProgress(bytesProgress/fileSize);
	                }
	                setBytes("0/" + fileSize);
	                setProgress(0);
	            } catch (Exception e) {
	            	e.printStackTrace();
	        	}
	        }
	    } catch (JSchException | SftpException e) {
	        e.printStackTrace(); 
	    } finally {
	    	sftpChannel.exit();
	        session.disconnect();
	        setComplete(true);
	        setRunning(false);    
	    }
	}
	
	public void FileDownload(InputStream in, String locPath, long fileSize) {
    	try (FileOutputStream fOut = new FileOutputStream(locPath)) {
    		
    		byte[] buffer = new byte[100];

            int length;
            int bytesProgress = 0;
            while ((length = in.read(buffer)) > 0) {
            	fOut.write(buffer, 0, length);
            	bytesProgress = bytesProgress + length;
            	if (bytesProgress > fileSize) bytesProgress = (int) fileSize;
            	setBytes(bytesProgress + "/" + fileSize);
            	setProgress(bytesProgress/fileSize);
            }
        } catch (Exception e) {
        	e.printStackTrace();
    	}
	}
	
	public void put (String address, String port, String user, String passw, 
			String remDir, final String mask, final String locDir) {
		
		JSch jsch = new JSch();
	    Session session = null;
	    ChannelSftp sftpChannel = null;
	    
	    try {
	    	setRunning(true);
	    	setComplete(false);   	
	    	setFileName("connecting... ");

	        session = jsch.getSession(user, address, Integer.valueOf(port));
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.setPassword(passw);
	        session.connect();
	        setFileName("connected");
	        
	        Channel channel = session.openChannel("sftp");
	        channel.connect();
	        sftpChannel = (ChannelSftp) channel;

	        String path = "";
	        
	        if ("log".equals(remDir)) {
	        	path = "/home/powersys/log";
	        	sftpChannel.cd(path);
	        	@SuppressWarnings("unchecked")
				Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*"); 
	        	long oldDate = 0;
	        	String ffileName = "";
	        	for (ChannelSftp.LsEntry listEntry : list) {
	        		if (listEntry.getAttrs().getATime() > oldDate) {
	        			ffileName = listEntry.getFilename();
	        			oldDate = listEntry.getAttrs().getATime();
	        		}
	        	}
	        	path = path + "/" + ffileName;
	        	System.out.println(path);
	        }
	        if ("./".equals(remDir)) {
	        	path = "/home/powersys";
	        }
	        if ("database".equals(remDir)) {
	        	path = "/home/powersys/database";
	        }

	        File folder = new File(locDir);
	        FileFilter fileFilter = new WildcardFileFilter(mask);
	        File[] listOfFiles = folder.listFiles(fileFilter);

            for (int i = 0; i < listOfFiles.length; i++) {
            	if (listOfFiles[i].isFile()) {
            		System.out.println(locDir + "/" + listOfFiles[i].getName() + "       -       " + path + "/" + listOfFiles[i].getName());
            		setFileName(listOfFiles[i].getName());

            		sftpChannel.put(locDir + "/" + listOfFiles[i].getName(), path + "/" + listOfFiles[i].getName());
            		System.out.println("put");
            	}
            }           
	        
	    } catch (JSchException | SftpException e) {
	        e.printStackTrace(); 
	    } finally {
	    	sftpChannel.exit();
	        session.disconnect();
	        setComplete(true);
	        setRunning(false);    
	    }
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getBytes() {
		return bytes;
	}

	@Override
	public double getProgress() {
		return progress;
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void setBytes(String bytes) {
		this.bytes = bytes;
	}

	@Override
	public void setProgress(double progress) {
		this.progress = progress;
	}

	@Override
	public boolean isComplete() {
		return completed;
	}

	@Override
	public void setComplete(boolean compl) {
		completed = compl;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void setRunning(boolean r) {
		running = r;
	}
}
