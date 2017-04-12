package com.drizzard.config;

import java.util.Map.Entry;

import com.drizzard.Language;
import com.drizzard.main;

public class LanguageConfig {
	public static ConfigManager cm = new ConfigManager(main.plugin.getName(), "messages.yml", false);
	
	public static void load(){
		if(cm.exists()){
			cm.load();
			
			for(Entry<String, Object> entry:cm.getInside(0).entrySet()){
				String key = entry.getKey();
				String value = (String) entry.getValue();
				
				Language l = Language.getLanguage(key);
				if(l != null)
					Language.setTranslation(l, Language.stringToChatColor(value));
			}
			
		}
		
		save();
	}
	
	public static void save(){
		cm.clear();
		
		for(Language l:Language.values())
			cm.addConfig(l.name(), Language.chatColorToString(l.getMessage()));
		
		cm.save();
	}
}