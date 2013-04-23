package org.efreak.bukkitmanager.addon.dropboxbackup;

import org.efreak.bukkitmanager.addons.BukkitmanagerAddon;
import org.efreak.bukkitmanager.commands.BmCommandExecutor;
import org.efreak.bukkitmanager.util.BackupHelper;

public class DropboxBackup extends BukkitmanagerAddon {

	public static DropboxStorage storage;
	
	@Override
	public void onLoad() {
		super.onLoad();
		name = "DropboxBackup";
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		//Setup Config
		config.update("Autobackup.Dropbox.Enabled", false);
		config.update("Autobackup.Dropbox.KeyToken", "");
		config.update("Autobackup.Dropbox.SecretToken", "");
		config.update("Autobackup.Dropbox.Path", "backups");
		config.save();
		//Add Language File Entries
		
		//Register Backup Storage
		storage = new DropboxStorage();
		try {
			storage.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		BackupHelper.registerBackupStorage(storage);
		
		//Register Commands
		BmCommandExecutor.registerCommand(new DropboxCmd());
		
	}
}
