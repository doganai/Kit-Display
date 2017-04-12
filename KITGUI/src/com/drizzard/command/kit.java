package com.drizzard.command;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.drizzard.Kit;
import com.drizzard.Language;
import com.drizzard.main;
import com.earth2me.essentials.User;

public class kit implements CommandExecutor {
	private static int MAXPERPAGE = 36;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		onCommand(sender, label, args);
		return true;
	}
	
	public static void onCommand(CommandSender sender, String label, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(args.length == 0){
				if(player.hasPermission("essentials.kit")){
					player.openInventory(getMenuInventory(player));
				}else{
					sender.sendMessage(Language.No_Permissions.getMessage());
				}
			}else{
				String kitname = args[0];
				com.earth2me.essentials.Kit kit = main.getKit(kitname.toLowerCase());
				if(kit != null){
					if(sender.hasPermission("essentials.kits." + kitname.toLowerCase()))
						giveKit(player, kit);
					else
						sender.sendMessage(Language.No_Permissions.getMessage());
				}else{
					player.sendMessage(Language.DoesntExist_Kit.getMessage().replace("{kit}", args[0]));
				}
			}
		}else{
			sender.sendMessage(Language.NotA_Player.getMessage());
		}
	}
	
	
	public static void onInventoryClickEvent(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getInventory();
		ItemStack is = event.getCurrentItem();
		
		if(inv.getName() == getMenuInventory(player).getName())
		{
			event.setCancelled(true);
			if(is.getType() == Material.getMaterial(main.CONFIG_BLOCK1))
			{
				player.closeInventory();
				player.openInventory(getKitInventory(player));
				return;
			}
			if(is.getType() == Material.getMaterial(main.CONFIG_BLOCK2))
			{
				player.closeInventory();
				player.openInventory(getSpecInventory(player));
				return;
			}
					
		}
		if(inv.getTitle() != null && inv.getTitle() == main.CONFIG_INVTITLE && is != null && is.getType() != null && is.getType() != Material.AIR){
			event.setCancelled(true);
			com.earth2me.essentials.Kit kit = main.getKit(getKitAt(player, event.getSlot(), 1).getName());
			if(player.hasPermission("essentials.kits." + kit.getName()))
			{
				giveKit(player, kit);
				player.closeInventory();
				return;
			}
		}
		if(inv.getName() != null && inv.getTitle() == main.CONFIG_INVTITLE2 && is != null && is.getType() !=null && is.getType() != Material.AIR)
		{
			event.setCancelled(true);
			com.earth2me.essentials.Kit kit = main.getKit(getSpecKitAt(player, event.getSlot(), 1).getName());
			if(player.hasPermission("essentials.kits." + kit.getName()))
			{
				giveKit(player, kit);
				player.closeInventory();
				return;
			}
		}
	}

	public static void onInventoryDragEvent(InventoryDragEvent event){
		Inventory inv = event.getInventory();
		if(inv.getTitle() != null && inv.getTitle().startsWith(main.CONFIG_INVTITLE))
			event.setCancelled(true);
		if(inv.getTitle() != null && inv.getTitle().startsWith(main.CONFIG_INVTITLE2))
			event.setCancelled(true);
		if(inv.getTitle() != null && inv.getTitle().startsWith("Menu"))
			event.setCancelled(true);
	}

	public static void giveKit(Player player, com.earth2me.essentials.Kit kit){
		User user = main.es.getUser(player);
		
		// check, if he is allowed to
		try { kit.checkDelay(user); } catch (Exception e) { return; }
		
		player.sendMessage(Language.Giving.getMessage().replace("{kit}", main.firstCharCaps(kit.getName())));
		
		// give items
		try { kit.expandItems(user); } catch (Exception e) { }
		
		// add to the scheduler from essentials
		try { kit.setTime(user); } catch (Exception e) { e.printStackTrace(); }
	}
	
	public static Inventory getKitInventory(Player player){
		String time = "";
		ArrayList<Kit> kits = main.getKits(player);
		Inventory invBasic = Bukkit.createInventory(player, getInvSize(kits.size()), main.CONFIG_INVTITLE);
		
		for(Kit kit:kits){
			ItemStack is = kit.getIcon();
			ItemMeta im = is.getItemMeta();
			try {
			Long t = main.getKit(kit.getName()).getNextUse(main.es.getUser(player));
			time = t.toString();
	        
			} catch (Exception e) {
				// TODO Auto-generated catch block1	
				e.printStackTrace();
			}
			
			Date endDate = new Date(Long.parseLong(time));
			
			Date startDate = new Date(System.currentTimeMillis());
			
			long duration = endDate.getTime() - startDate.getTime();
			
			long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
	
			// name
			if(player.hasPermission("essentials.kits." + kit.getName()) && Float.parseFloat(time) > 0F)
			{
			im.setDisplayName(ChatColor.RED + Language.stringToChatColor(kit.getPrefix()) + main.firstCharCaps(kit.getName()) + ChatColor.WHITE + " --> COOLDOWN: " + String.valueOf(diffInSeconds) + "s");
			is = new ItemStack(Material.STAINED_GLASS_PANE,1, DyeColor.YELLOW.getData());
			}
			else if(player.hasPermission("essentials.kits." + kit.getName()) && Integer.parseInt(time) <= 0)
			{
			im.setDisplayName(ChatColor.RED + Language.stringToChatColor(kit.getPrefix()) + main.firstCharCaps(kit.getName()) + ChatColor.GREEN + " --> READY!");
			is = new ItemStack(Material.STAINED_GLASS_PANE,1, DyeColor.GREEN.getData());
			}
			else
			{
			im.setDisplayName(ChatColor.RED + Language.stringToChatColor(kit.getPrefix()) + main.firstCharCaps(kit.getName()) + ChatColor.RED + " --> LOCKED");
			is = new ItemStack(Material.STAINED_GLASS_PANE,1, DyeColor.RED.getData());
			}
			// lores
			List<String> lores = new ArrayList<String>();
			for(String lore:kit.getLores())
			{
            lores.add(ChatColor.GRAY + lore);
            im.setLore(lores);
			}
			
            is.setItemMeta(im);
			
			invBasic.addItem(is);
		}
		return invBasic;
	}
	
	public static Inventory getMenuInventory(Player player){
		Inventory menu = Bukkit.createInventory(player, getInvSize(2), "Menu");

	    ItemStack simplekit = new ItemStack(Material.getMaterial(main.CONFIG_BLOCK1));
			
	    ItemStack specialkit = new ItemStack(Material.getMaterial(main.CONFIG_BLOCK2));
		
		ItemMeta simplemeta = simplekit.getItemMeta();
		
		ItemMeta specialmeta = specialkit.getItemMeta();
		
		simplemeta.setDisplayName(ChatColor.GREEN + "Kits Normales");
		
		specialmeta.setDisplayName(ChatColor.RED + "Kits Especiales");
	    
		simplekit.setItemMeta(simplemeta);
		
		specialkit.setItemMeta(specialmeta);
		
		menu.setItem(3, simplekit);
		
		menu.setItem(5, specialkit);
		
		return menu;
		
	}
	
	public static Inventory getSpecInventory(Player player){		
		String time = "";
		ArrayList<Kit> kits = main.getSpecialKits(player);
		Inventory spec = Bukkit.createInventory(player, getInvSize(kits.size()), main.CONFIG_INVTITLE2);
		
		for(Kit kit:kits){
			ItemStack is = kit.getIcon();
			ItemMeta im = is.getItemMeta();
			try {
			Long t = main.getKit(kit.getName()).getNextUse(main.es.getUser(player));
			time = t.toString();
	        
			} catch (Exception e) {
				// TODO Auto-generated catch block1	
				e.printStackTrace();
			}
			
			Date endDate = new Date(Long.parseLong(time));
			
			Date startDate = new Date(System.currentTimeMillis());
			
			long duration = endDate.getTime() - startDate.getTime();
			
			long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
	
			// name
			if(player.hasPermission("essentials.kits." + kit.getName()) && Float.parseFloat(time) > 0F)
			{
			im.setDisplayName(ChatColor.RED + Language.stringToChatColor(kit.getPrefix()) + main.firstCharCaps(kit.getName()) + ChatColor.WHITE + " --> COOLDOWN: " + String.valueOf(diffInSeconds) + "s");
			is = new ItemStack(Material.STAINED_GLASS_PANE,1, DyeColor.YELLOW.getData());
			}
			else if(player.hasPermission("essentials.kits." + kit.getName()) && Integer.parseInt(time) <= 0)
			{
			im.setDisplayName(ChatColor.RED + Language.stringToChatColor(kit.getPrefix()) + main.firstCharCaps(kit.getName()) + ChatColor.GREEN + " --> READY!");
			is = new ItemStack(Material.STAINED_GLASS_PANE,1, DyeColor.GREEN.getData());
			}
			else
			{
			im.setDisplayName(ChatColor.RED + Language.stringToChatColor(kit.getPrefix()) + main.firstCharCaps(kit.getName()) + ChatColor.RED + " --> LOCKED");
			is = new ItemStack(Material.STAINED_GLASS_PANE,1, DyeColor.RED.getData());
			}
			// lores
			List<String> lores = new ArrayList<String>();
			for(String lore:kit.getLores())
			{
            lores.add(ChatColor.GRAY + lore);
            im.setLore(lores);
			}
            is.setItemMeta(im);
			
			spec.addItem(is);
		}
		
		return spec;
		
	}
	
	public static int getInvSize(int size){
		for(int i=1; i<=10; i++){
			if(size >= i*9-9 && size < i*9)
				return i*9;
		}
		return 10*9;
	}
	
	public static Kit getKitAt(Player player, int at, int page){
		List<Kit> kits = main.getKits(player);
		
		if(at > MAXPERPAGE)
			return null;
		
		int slot = (page - 1) * MAXPERPAGE + at;
		if(page > 1)
			slot--;
		
		if(slot < kits.size())
			return kits.get(slot);
		else
			return null;
	}
	public static Kit getSpecKitAt(Player player, int at, int page){
		List<Kit> kits = main.getSpecialKits(player);
		
		if(at > MAXPERPAGE)
			return null;
		
		int slot = (page - 1) * MAXPERPAGE + at;
		if(page > 1)
			slot--;
		
		if(slot < kits.size())
			return kits.get(slot);
		else
			return null;
	}
	
}
