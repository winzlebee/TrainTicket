package me.wizzledonker.plugins.trainticket;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Trainticket extends JavaPlugin
{
TrainTicketListener signChestListener = new TrainTicketListener(this);
TrainTicketPlayerListener trainPlayerListener = new TrainTicketPlayerListener(this);
TrainTicketBlockListener trainBlockListener = new TrainTicketBlockListener(this);
TrainTicketLoginListener trainLoginListener = new TrainTicketLoginListener(this);
public static Economy economy = null;

public boolean messagesEnable = true;
public boolean dispenseMinecart = false;
public int ticketDataValue;
public String ticketName = "TrainTicket";
public String ticketLore1 = "Allows you to ride";
public String ticketLore2 = "In Minecarts/Boats";
private Set<Player> ticketSet = new HashSet();
public ItemStack is;
public ItemMeta ticket;

  public void onDisable()
  {
/*  42 */     System.out.println(this + " is now disabled!");
  }

  public void onEnable() {
/*  46 */     PluginManager pm = getServer().getPluginManager();
/*  47 */     if (setupEconomy().booleanValue())
/*  48 */       System.out.println(this + " has successfully linked with " + economy.getName() + ", via Vault");
    else {
/*  50 */       System.out.println(this + ": Vault economy not found, switching to gold ingots!");
    }

/*  53 */     pm.registerEvents(this.signChestListener, this);
/*  54 */     pm.registerEvents(this.trainPlayerListener, this);
/*  55 */     pm.registerEvents(this.trainBlockListener, this);
              pm.registerEvents(this.trainLoginListener, this);
/*  56 */     setupConfig();
			  is = new ItemStack(Material.getMaterial(ticketDataValue), 1);
			  ticket = is.getItemMeta();
			  ticket.setDisplayName(ticketName);
			  ArrayList<String> lore = new ArrayList<String>();
              lore.add(ticketLore1);
              lore.add(ticketLore2);
              ticket.setLore(lore);
              is.setItemMeta(ticket);
/*  58 */     System.out.println(this + " by wizzledonker loaded all events");
  }

  public void setupConfig()
  {
/*  63 */     if (!new File(getDataFolder(), "config.yml").exists()) {
/*  64 */       System.out.println(this + " generating config file :)");
/*  65 */       getConfig().addDefault("messages.enterMessage", "You entered a cart with a ticket!");
/*  66 */       getConfig().addDefault("messages.leaveMessage", "Hope you enjoyed your ride!");
/*  67 */       getConfig().addDefault("messages.deniedMessage", "Have you paid for that? / You need a ticket to enter that vehicle!");
/*  68 */       getConfig().addDefault("messages.enabled", Boolean.valueOf(true));
/*  69 */       getConfig().options().copyDefaults(true);
    }

/*  73 */     if (!getConfig().contains("messages.boothDenied")) {
/*  74 */       getConfig().set("messages.boothDenied", "Insufficient funds to buy a ticket!");
    }
/*  76 */     if (!getConfig().contains("messages.boothAccept")) {
/*  77 */       getConfig().set("messages.boothAccept", "You have bought a ticket for %price%");
    }
			 if (!getConfig().contains("booth.ticket_item")) {
/*  80 */       getConfig().set("booth.ticket_item", Integer.valueOf(339));
    }
/*  79 */     if (!getConfig().contains("booth.ticket_name")) {
/*  80 */       getConfig().set("booth.ticket_name", ticketName);
    }
			  if (!getConfig().contains("booth.ticket_lore1")) {
/*  80 */       getConfig().set("booth.ticket_lore1", ticketLore1);
    }
			  if (!getConfig().contains("booth.ticket_lore2")) {
                getConfig().set("booth.ticket_lore2", ticketLore2);
    }
/*  82 */     if (!getConfig().contains("booth.dispense_minecart")) {
/*  83 */       getConfig().set("booth.dispense_minecart", Boolean.valueOf(false));
    }

/*  86 */     saveConfig();

/*  89 */     this.messagesEnable = getConfig().getBoolean("messages.enabled", true);
/*  90 */     this.dispenseMinecart = getConfig().getBoolean("booth.dispense_minecart", false);
              this.ticketDataValue = getConfig().getInt("booth.ticket_item", 339);
/*  91 */     this.ticketName = getConfig().getString("booth.ticket_name", ticketName);
			  this.ticketLore1 = getConfig().getString("booth.ticket_lore1", ticketLore1);
			  this.ticketLore2 = getConfig().getString("booth.ticket_lore2", ticketLore2);
/*  93 */     System.out.println(this + " has finished loading the config file.");
  }

  public String handleMessages(Integer messageType)
  {
/*  98 */     if ((this.messagesEnable = false) != false) {
/*  99 */       return "";
    }
/* 101 */     switch (messageType.intValue()) {
    case 1:
/* 103 */       return getConfig().getString("messages.enterMessage");
    case 2:
/* 105 */       return getConfig().getString("messages.leaveMessage");
    case 3:
/* 107 */       return getConfig().getString("messages.deniedMessage");
    case 4:
/* 109 */       return getConfig().getString("messages.boothDenied");
    case 5:
/* 111 */       return getConfig().getString("messages.boothAccept");
    }
/* 113 */     return "";
  }
  public boolean hasTicket(Player player) {
	  /* 117 */     return this.ticketSet.contains(player);
	    }

	    public void setTicket(Player player, boolean enabled) {
	  /* 121 */     if ((hasTicket(player)) && (!enabled))
	  /* 122 */       this.ticketSet.remove(player);
	  /* 123 */     else if ((hasTicket(player)) && (enabled == true)) {
	  /* 124 */       return;
	      }
	  /* 126 */     if ((!hasTicket(player)) && (enabled == true))
	  /* 127 */       this.ticketSet.add(player);
	  /* 128 */     else if ((!hasTicket(player)) && (!enabled));
	    }
  public void buyTicket(Double price, Player player){
	  Player p = player;
    if (!isGoldIngot()) {
    	if (economy.getBalance(player.getName()) <= price.doubleValue()) {
    		player.sendMessage(ChatColor.RED + handleMessages(Integer.valueOf(4)));
    		return;
		 }
				
/* 144 */       economy.withdrawPlayer(player.getName(), price.doubleValue());
				if(InventoryCheck(p, is) == true)
				{
					setTicket(player, true);
					player.getInventory().addItem(is);
					player.updateInventory();
/* 146 */           player.sendMessage(ChatColor.GREEN + handleMessages(Integer.valueOf(5)).replace("%price%", new StringBuilder().append(ChatColor.WHITE).append(price.toString()).toString()));
				} else {
					player.sendMessage(ChatColor.RED + "Your inventory is full!");
					}
			}
    else {
       int goldAmount = goldCounter(player);
       int ticketPrice = (int)Math.ceil(price.doubleValue());
       if (goldAmount >= ticketPrice) {  
    	   if (InventoryCheck(p, is) == true){
    		  setTicket(player, true);
    		  takeGold(ticketPrice, player);
    	      player.getInventory().addItem(is);
    	      player.updateInventory();
    	      player.sendMessage(ChatColor.GREEN + handleMessages(Integer.valueOf(5)).replace("%price%", new StringBuilder().append(ChatColor.WHITE).append(price.toString()).toString()));   
    	   }else{
    		  player.sendMessage(ChatColor.RED + "Your inventory is full!");
    	   }
         
      } else {
         player.sendMessage(ChatColor.RED + handleMessages(Integer.valueOf(4)));
         return;
      }
    }
if (this.dispenseMinecart) {
    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.MINECART, 1));
   return;   
			}    
  }

  private boolean isGoldIngot() {
     return economy == null;
  }

  private Boolean setupEconomy()
  {
    Plugin vault = getServer().getPluginManager().getPlugin("Vault");
   if (vault == null) {
     return Boolean.valueOf(false);
    }
  RegisteredServiceProvider economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
  if (economyProvider != null) {
     economy = (Economy)economyProvider.getProvider();
    }

     return Boolean.valueOf(economy != null);
  }
  private boolean InventoryCheck(Player player, ItemStack is) {
		ItemStack itemToAdd = is;
		int freeSpace = 0;
		for (ItemStack i : player.getInventory()) {
			if (i == null) {
				freeSpace+=itemToAdd.getType().getMaxStackSize();
			} else if (i.getType() == itemToAdd.getType()) {
				freeSpace+=i.getType().getMaxStackSize() - i.getAmount();
			}
		}
		if (itemToAdd.getAmount() <= freeSpace) {
			return true;
		} else {
			return false;
			
//not enough space, tell the player and abort mission
}
}
  private int goldCounter(Player player){
	  int gold = 0;
	  for (ItemStack i : player.getInventory()) {
		  if ((i != null) && (i.getType() == Material.GOLD_INGOT)){
				int counter = 0;
				counter = i.getAmount();
				gold = gold + counter;
			}
			}
	  return gold;
		}
  private void takeGold(int Price, Player player){
	  int change;
	  int goldLeft = Price;
	  for (ItemStack i : player.getInventory().getContents()){
		  if ((i != null) && (i.getType() == Material.GOLD_INGOT)){
			  if (i.getAmount() >= goldLeft){
				  change = i.getAmount() - goldLeft;
				  if(change == 0)
				  {
					  player.sendMessage(ChatColor.RED + "If Works!");
					  player.getInventory().removeItem(i);
					  player.updateInventory();
					  return;
				  }
				  if(change != 0){
					  i.setAmount(change);
					  return;
				  }
			  }
			  if(i.getAmount() < goldLeft){
				goldLeft = goldLeft - i.getAmount();
				player.getInventory().removeItem(i);
				player.updateInventory();
			  	}
			  }
			  }
	  }
  }
  

  


/* Location:           C:\Users\DrkMatr\Desktop\TrainTicket-1.4.jar
 * Qualified Name:     me.wizzledonker.plugins.trainticket.Trainticket
 * JD-Core Version:    0.6.2
 */