package org.efreak.bukkitmanager.addon.dropboxbackup;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.efreak.bukkitmanager.Bukkitmanager;
import org.efreak.bukkitmanager.Configuration;
import org.efreak.bukkitmanager.IOManager;
import org.efreak.bukkitmanager.util.BackupStorage;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

public class DropboxStorage extends BackupStorage {

	final static private String APP_KEY = "dzddfbah0zgoaxa";
	final static private String APP_SECRET = "s7zfbx7i40sana6";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	private static String KEY_TOKEN;
	private static String SECRET_TOKEN;
	private static AccessTokenPair accessToken = null;
	private static Configuration config;
	private static IOManager io;
	
	/* Setup Variables */
	private WebAuthInfo authInfo;
	
	
	private static DropboxAPI<WebAuthSession> mDBApi;
	
	static {
		config = Bukkitmanager.getConfiguration();
		io = Bukkitmanager.getIOManager();
	}
	
	public void init() throws Exception {
		io.sendConsole("Loading Dropbox Backup Storage");
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		KEY_TOKEN = config.getString("Autobackup.Dropbox.KeyToken");
		SECRET_TOKEN = config.getString("Autobackup.Dropbox.SecretToken");
		if (KEY_TOKEN != "" && SECRET_TOKEN != "") accessToken = new AccessTokenPair(KEY_TOKEN, SECRET_TOKEN);
		WebAuthSession session = new WebAuthSession(appKeys, ACCESS_TYPE);
		if (accessToken == null) {
			io.sendConsoleWarning("Can't connect to Dropbox. Please run '/bm dropbox setup' to allow Bukkitmanager fileuploading");
			return;
		}
		mDBApi = new DropboxAPI<WebAuthSession>(session);
		mDBApi.getSession().setAccessTokenPair(accessToken);
	}
	
	public void startSetup() {
		try {
			authInfo = mDBApi.getSession().getAuthInfo();
			io.sendConsole("Dropbox Login:");
			io.sendConsole("For using Dropbox you first need to authenticate Bukkitmanager");
			io.sendConsole("Step 1: Go to " + authInfo.url);
			io.sendConsole("Step 2: Allow access to this app");
			io.sendConsole("Step 3: Run /bm dropbox finish");
		} catch (DropboxException e) {
			e.printStackTrace();
		}
	}
	
	public void finishSetup() {
		try {
			accessToken = mDBApi.getSession().getAccessTokenPair();
			RequestTokenPair tokens = authInfo.requestTokenPair;
			mDBApi.getSession().retrieveWebAccessToken(tokens);
			accessToken = mDBApi.getSession().getAccessTokenPair();
			KEY_TOKEN = accessToken.key;
			SECRET_TOKEN = accessToken.secret;
			config.set("Autobackup.Dropbox.KeyToken", KEY_TOKEN);
			config.set("Autobackup.Dropbox.SecretToken", SECRET_TOKEN);
			io.sendConsole("Finished Dropbox Setup");
		} catch (DropboxException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean storeFile() {
		try {
			FileInputStream inputStream = new FileInputStream(backupFile);
			Entry newEntry = mDBApi.putFile("/testing.txt", inputStream, backupFile.length(), null, null);
	        System.out.println("Done. \nRevision of file: " + newEntry.rev);
		} catch (DropboxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}
