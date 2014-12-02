package com.psd.rendering.openrender.oorender.module;

import com.psd.rendering.openrender.oorender.core.EParapherManager;
import com.psd.rendering.openrender.oorender.core.EParapherSettings;
import com.psd.rendering.openrender.oorender.util.JVMSettings;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class OODaemonManager {

	private static Logger  log = Logger.getLogger(OODaemonManager.class);

	public static String[] OODIRPATTERN    = { "ooo-", "openoffice", "OpenOffice.org " };
	public static String[] WININSTALLDIR   = { "C:\\Program Files\\", "C:\\Program Files\\OpenOffice.org 2.4\\program" };
	public static String[] MACINSTALLDIR   = { "/Applications/" };
	public static String[] NIXINSTALLDIR   = { "/usr/lib", "/lib", "/usr/local/lib" };
	public static String[] NIX64INSTALLDIR = { "/usr/lib64", "/lib64", "/usr/local/lib64", "/usr/lib", "/lib", "/usr/local/lib"};
	public static String[] BINDIR          = { "/usr/bin", "/bin", "/usr/local/bin" };

	private long    TIMETOWAITFORDAEMON = 2500L;
	
	private Process oofficeProcess;
	private EParapherSettings settings;
	
	//Singleton
	private static OODaemonManager singleton;
	public static OODaemonManager getInstance() {
		if (singleton == null)
			singleton = new OODaemonManager();
		return singleton;
	}
	public OODaemonManager() {
		oofficeProcess = null;
		settings=EParapherManager.getInstance().getSettings();
		return;
	}

	/**
	 * Test if OpenOffice binary exists and can be executed 
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	public boolean canExecuteOpenOfficeBinary() {
		boolean ret = true;
		File soffice = new File(getsofficefile());

		if (!soffice.exists()) {
			log.error("Open Office binary not found : " +soffice.getAbsolutePath());
			ret = false;
		}
		if (!soffice.isFile()) {
			log.error("Open Office binary isn't a file : " + soffice.getAbsolutePath());
			ret = false;			
		}
		if (!soffice.canExecute()) {
			log.error("Cannot execute soffice binary file");
			ret = false;
		}
		return ret;

	}

	/** Starts Local OpenOffice daemon
	 * 
	 * @return true if the daemon is correctly started
	 * @throws java.io.IOException I/O problem, another process is already listening on this TCP port?
	 * @throws InterruptedException
	 */
	@SuppressWarnings("static-access")
	public boolean start() throws IOException, InterruptedException {
		if (!settings.useLocalOpenOffice()) {
			log.warn("Must not call OpenOffice start Daemon : eParapher is not configured to use a local OpenOffice Daemon");
			return false;
		}
		if (canExecuteOpenOfficeBinary() && !isOpenOfficeRunning()) {
			Runtime r = Runtime.getRuntime();
			try {				

				//Build command line, exec directory and environment
				String cmd = getStartCmd();

				//Run the command
				log.debug("Starting OpenOffice Daemon : '"+cmd+"'");
				oofficeProcess = r.exec( cmd );
				
				Thread.currentThread().sleep(TIMETOWAITFORDAEMON);

				log.info("OpenOffice daemon started on " + settings.getOpenOfficeServerName() + " port "+settings.getOpenOfficeServerPort());
				return true;
			} catch (IOException e) {
				log.error("Error while starting OpenOffice Process : "+e.getLocalizedMessage(),e);
				throw e;
				
			} catch (InterruptedException e) {
				log.error("Error while starting OpenOffice Process : "+e.getLocalizedMessage(),e);
				throw e;
			}
		}
		return false;
	}
	public boolean stop() {

		if (oofficeProcess == null) {
			log.warn("Cannot stop OpenOffice daemon as it is not running");
			return false;
		}
		
		log.info("Stop OpenOffice daemon.");
		if (JVMSettings.isWindowsOS()) {
			try {
				if (JVMSettings.isWindowsVistaOS() || JVMSettings.isWindowsSevenOS())
					Runtime.getRuntime().exec("taskkill /F /IM soffice.exe /T");
				else Runtime.getRuntime().exec("tskill soffice");
				oofficeProcess = null;
				return true;
			} catch (IOException e) {
				log.error("Error while killing Open Office Process : "+e.getLocalizedMessage(),e);
			} 
		} else if ( JVMSettings.isSunOS() || JVMSettings.isLinuxOS()|| JVMSettings.isAIXOS() ) {
			try {
				unixKillOpenOffice();
				oofficeProcess = null;
				return true;
			} catch (IOException e) {
				log.error("Error while killing Open Office Process : "+e.getLocalizedMessage(),e);
			}
		} else {
			log.warn("Unsupported OS for OpenOffice, killing impossible");
	    }
		return false;
	}

	/**
	 * Test if the OpenOffice daemon is running
	 * Done with a simple tcp connect
	 * @return
	 */
	public boolean isOpenOfficeRunning() {
		if ( EParapherManager.getInstance().getSettings().useLocalOpenOffice()
		  && oofficeProcess == null)
			return false;
		try{
			  Socket connection = new Socket(settings.getOpenOfficeServerName(), settings.getOpenOfficeServerPort());
			  connection.close();
			  return true;
			}catch(Exception e){
			  log.warn("Could not connect or connection was interrupted on : '"+settings.getOpenOfficeServerName()+":"+settings.getOpenOfficeServerPort()+"'.");
			  return false;
			}
	}
	
    /**
    * Kill OpenOffice on Unix.
    */
    private static void unixKillOpenOffice() throws IOException
    {
       Runtime runtime = Runtime.getRuntime();
       String pid = getOpenOfficeProcessID();
       if (pid != null)
       {
          while (pid != null)
          {
             String[] killCmd = {"/bin/bash", "-c", "kill -9 "+pid};
             runtime.exec(killCmd);
	         // Is another OpenOffice prozess running?
             pid = getOpenOfficeProcessID();
          }
       }
    }

    /**
    * Get OpenOffice process id for Unix.
    */
    private static String getOpenOfficeProcessID() throws IOException {
       Runtime runtime = Runtime.getRuntime();
	   // Get process id
       String[] getPidCmd = {"/bin/bash", "-c", "ps -e|grep soffice|awk '{print $1}'"};
       Process getPidProcess = runtime.exec(getPidCmd);
	   // Read process id
       InputStreamReader isr = new InputStreamReader(getPidProcess.getInputStream());
       BufferedReader br = new BufferedReader(isr);
       return br.readLine();
    }

    private String getStartCmd() {
    	String cmd = getsofficefile() + " " + getsofficeParameters();
		return cmd;
	}

	private String getsofficefile() {
		String binary = "soffice";
		if (JVMSettings.isWindowsOS())
			binary += ".exe";
		return settings.getOpenOfficeBinaryPath() + File.separator + binary;
	}

	public void addOOLibPath() {
		// Add OpenOffice library path to java.library.path
		String env = System.getProperty("java.library.path");
		if (env.indexOf(settings.getOpenOfficeLibraryPath())<0) {
			env = env + File.pathSeparator + settings.getOpenOfficeLibraryPath();
			System.setProperty("java.library.path",env);
		}
	}
	
	public static String findOOBinaryPath() {
		String[] binDirs;
		String defaultValue;
		if (!JVMSettings.isWindowsOS()) {
			binDirs = BINDIR;
			for (int i = 0; i < binDirs.length; i++) {
				String dirtotest = binDirs[i];
				log.debug("scanning for OpenOffice binary in : " + dirtotest );
				File f = new File(dirtotest + File.separator + "soffice");
				if ( f!=null && f.exists() && f.canExecute() ) {
					log.info("Found OpenOffice binary in : " + dirtotest);
					return dirtotest;
				}
			}
			defaultValue = BINDIR[0];
		} else {
			binDirs = WININSTALLDIR;
			for (int i = 0; i < binDirs.length; i++) {
				String dirtoscan = binDirs[i];
				log.debug("Scanning for OpenOffice binary in : " + dirtoscan );
				File fDirToScan = new File(dirtoscan);
				if (fDirToScan!=null && fDirToScan.exists() && fDirToScan.canRead()) {
					File[] instDirs = fDirToScan.listFiles();
					for (File file : instDirs) {
						String path = file.getAbsolutePath() + File.separator + "program" + File.separator + "soffice.exe";
						log.debug("scanning for OpenOffice binary in : " + path );
						File ootest  = new File(path);
						if (ootest.exists() && ootest.canExecute()) {
							log.info("Found OpenOffice binary in : " + path);
							return path;
						}
					}
				} else {
					log.info("Cannot scan for OO binary in : " + dirtoscan );

				}
			}
			defaultValue = WININSTALLDIR[0];
		}
		log.info("OpenOffice binary not found.");
		return defaultValue;
	}

	public static String findOOLibPath() {

		String[] libDirs;
		String   oopath = null;
		String defaultValue;

		//Settings OS Paths
		if (JVMSettings.isWindowsOS()) {
			libDirs = WININSTALLDIR;
		} else {
			if (JVMSettings.isMacOS()) {
				libDirs = MACINSTALLDIR;
				//store.setDefault(PreferenceConstants.P_LOCALOOLIBPATH,     "/Applications/OpenOffice.org 2.4.app/");
			} else {
				//Test if 64bit *nix /usr/lib64
				if (System.getProperty("os.arch").equals("amd64"))
					libDirs = NIX64INSTALLDIR;
				else
					libDirs = NIXINSTALLDIR;
			}
		}
		log.info("Scanning for OpenOffice libraries...");
		for (int i = 0; i < libDirs.length; i++) {
			File dirtoscan = new File(libDirs[i]);
			log.debug("Scanning for OpenOffice libraries in : " + libDirs[i] );
			if (dirtoscan.isDirectory() && dirtoscan.canRead()) {
				String result = recursivescan(dirtoscan);
				if (result != null)
					oopath = result; 
			}
		}
		if (oopath==null) {
			log.warn("OpenOffice library path not found.");
			return libDirs[0];
		}
		else {
			log.info("Found OpenOffice path : " + oopath);
		}
		return oopath;
	}

	private static String recursivescan(File dirtoscan) {
			File[] dirContent = dirtoscan.listFiles();
			for ( int j = 0; j < dirContent.length; j++ ) {
				File file = dirContent[j];
				for( String name : OODIRPATTERN ) {
					//TODO : Compare versions of Open Office
					if ( file.getAbsolutePath().indexOf(name) > 0 )
						return file.getAbsolutePath();
				}
			}
		return null;
	}
	
	public String getsofficeParameters() {
		if (JVMSettings.isWindowsOS())
			return "-headless -nodefault -norestore -accept=\"socket,host="+settings.getOpenOfficeServerName()+",port="+settings.getOpenOfficeServerPort()+";urp;StarOffice.NamingService\"";
		else
			return "-headless -accept=socket,host="+settings.getOpenOfficeServerName()+",port="+settings.getOpenOfficeServerPort()+";urp;";
	}

}
