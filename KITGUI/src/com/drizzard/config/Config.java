package com.drizzard.config;

import com.drizzard.Language;
import com.drizzard.main;

public class Config {
	public static ConfigManager manager = new ConfigManager("DrizzardKits", "config.yml");
	
	public static void load(){
		manager.load();
		String version = manager.getConfigString("config-version");
		String invtitle = manager.getConfigString("inv-title");
		String invtitle2 = manager.getConfigString("inv-title2");
		boolnull firstcharcaps = manager.getConfigBoolean("first-char-caps");
		boolnull incl_kits = manager.getConfigBoolean("includecmd-kits");
		String menu_block1 = manager.getConfigString("menu-block-1");
		String menu_block2 = manager.getConfigString("menu-block-2");
		String special_list = manager.getConfigString("special-kits");
		
		if(invtitle != null)
			main.CONFIG_INVTITLE = Language.stringToChatColor(invtitle);
		if(invtitle2 != null)
			main.CONFIG_INVTITLE2 = Language.stringToChatColor(invtitle2);
		if(firstcharcaps != boolnull.NULL)
			main.CONFIG_FIRSTCHARCAPS = firstcharcaps.toBoolean();
		if(incl_kits != boolnull.NULL)
			main.CONFIG_INCLCMD_KITS = incl_kits.toBoolean();
		if(menu_block1 != null)
		{
			main.CONFIG_BLOCK1 = Language.stringToChatColor(menu_block1); 
		}
		if(menu_block2 != null)
		{
			main.CONFIG_BLOCK2 = Language.stringToChatColor(menu_block2); 
		}
		if(special_list != null)
		{
			main.CONFIG_SPECIAL = Language.stringToChatColor(special_list);
		}
		
		if(version == null || version != null && !version.equals(main.getVersion()))
			save();
	}
	
	public static void save(){
		manager.clear();
		manager.addComment("Don't change this");
		manager.addConfig("config-version", main.getVersion());
		
		manager.addEmptyLine();
		
		manager.addComment("Set the title from the inventory");
		manager.addConfig("inv-title", Language.chatColorToString(main.CONFIG_INVTITLE));
		
		manager.addEmptyLine();
		
		manager.addComment("Set the title from the inventory2");
		manager.addConfig("inv-title2", Language.chatColorToString(main.CONFIG_INVTITLE2));
		
		manager.addEmptyLine();
		
		manager.addComment("If it's enabled, the first character in the name of the kit is in caps");
		manager.addConfig("first-char-caps", main.CONFIG_FIRSTCHARCAPS);
		
		manager.addEmptyLine();
		
		manager.addComment("If it's enabled, /kits will open the GUI too");
		manager.addConfig("includecmd-kits", main.CONFIG_INCLCMD_KITS);
		
		manager.addEmptyLine();
		
		manager.addComment("First Block For Menu");
		manager.addComment("EXAMPLE: menu-block-1: BONE_BLOCK");
		manager.addComment("BLOCK NAMES HERE: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");
		manager.addConfig("menu-block-1", main.CONFIG_BLOCK1);
		
		manager.addEmptyLine();
		
		manager.addComment("Second Block For Menu");
		manager.addConfig("menu-block-2", main.CONFIG_BLOCK2);
		
		manager.addEmptyLine();
		
		manager.addComment("Special-Kits INV");
		manager.addComment("EXAMPLE: : special-kits: vip,vip2,vip3,etc");
		manager.addConfig("special-kits", main.CONFIG_SPECIAL);
		
		manager.save();
	}
}
