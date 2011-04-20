package br.edu.ufcg.lsd.commune.functionaltests.xmpp.util;

import java.io.File;

public class XMPPServerUtil{
	
	private final String OS_NAME = System.getProperty("os.name");
	private final String OPENFIRE_HOME = "openfire" + File.separator + "bin";
	private final String OPENFIRE_DB = "openfire" + File.separator + "embedded-db";

	
	public void startServer(){
		try {  

			String cmd = getStartCommand();
			Runtime.getRuntime().exec(cmd);
			
		} catch (Exception e) {  
			e.printStackTrace();  
		}
	}
	
	public void stopServer() throws InterruptedException{
		try {  

			String cmd = getStopCommand();
			Runtime.getRuntime().exec(cmd);
			
		} catch (Exception e) {  
			e.printStackTrace();  
		
		}finally{
			removeDB();
		}
	}
	
	private void removeDB() {
		File db = new File(OPENFIRE_DB);
		deleteFile(db);
	}

	private String getStartCommand(){
		
		File cmd = new File(OPENFIRE_HOME);
		
		if(OS_NAME.contains("Windows")){
			return cmd.getAbsoluteFile() + File.separator + "openfired.exe";
		}
		return "bash " + cmd.getAbsolutePath() + File.separator + "openfire start";
		
	}
	
	private String getStopCommand(){
	
		if(OS_NAME.contains("Windows")){
			return "taskkill -im openfired.exe"; 
		}
		
		File cmd = new File(OPENFIRE_HOME);
		return "bash " + cmd.getAbsolutePath() + File.separator + "openfire stop";
	}
	
	private boolean deleteFile(File path) {
	    
		if( path.exists() ) {
	    	
	    	if (path.isDirectory()) {
		    
	    		File[] files = path.listFiles();
		        for(int i=0; i<files.length; i++) {
		           if(files[i].isDirectory()) {
		        	   deleteFile(files[i]);
		           } else {
		             files[i].delete();
		           }
		        }
	    	}
	    }
        return(path.delete());
	}
}