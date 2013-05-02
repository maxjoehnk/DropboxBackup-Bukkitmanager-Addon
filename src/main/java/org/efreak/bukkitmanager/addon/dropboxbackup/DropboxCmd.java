package org.efreak.bukkitmanager.addon.dropboxbackup;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.efreak.bukkitmanager.commands.Command;
import org.efreak.bukkitmanager.commands.CommandCategory;

public class DropboxCmd extends Command {
	
	public DropboxCmd() {
		super("dropbox", "Dropbox.Setup", "bm.dropbox.setup", new ArrayList<String>(), CommandCategory.GENERAL);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) io.sendError(sender, "Please run this command from console");
		if (args.length < 0) io.sendFewArgs(sender, "/bm dropbox");
		else if (args.length > 1) io.sendManyArgs(sender, "/bm dropbox");
		else {
			if (args.length == 0 || args[0].equalsIgnoreCase("setup")) DropboxBackup.storage.startSetup();
			else if (args[0].equalsIgnoreCase("finish")) DropboxBackup.storage.finishSetup();
		}
		return true;
	}
}
