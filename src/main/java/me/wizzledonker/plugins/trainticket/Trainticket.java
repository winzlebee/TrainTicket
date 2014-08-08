 
 /*
 * TrainTicket source code, validated by WizzleDonker
 * on 30/12/11, copyright has been validated for this
 * source code. Everything under this repository
 * owns the same license.
 * 
 * Licensed GBPL v1.6+
 */


package me.wizzledonker.plugins.trainticket;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Trainticket extends JavaPlugin {
    TrainTicketListener signChestListener = new TrainTicketListener(this);
    TrainTicketPlayerListener trainPlayerListener = new TrainTicketPlayerListener(this);
    TrainTicketBlockListener trainBlockListener = new TrainTicketBlockListener(this);
    public static Economy economy = null;
    public WorldGuardPlugin worldGuard = null;
    
    public boolean messagesEnable = true;
    public boolean dispenseMinecart = false;
    public int ticketDataValue;
    public List<String> worldGuardRegions = null;
    
    private Set<Player> ticketSet = new HashSet<Player>();
    
    public void onDisable() {
        // TODO: Place any custom disable code here.
        System.out.println(this + " is now disabled!");
    }

    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        if (setupEconomy()) {
            System.out.println(this + " has successfully linked with " + economy.getName() + ", via Vault");
        } else {
            System.out.println(this + ": Vault economy not found, switching to gold ingots!");
        }
        
        worldGuard = getWorldGuard();
        if (worldGuard != null) {
            System.out.println(this + ": Successfully linked with WorldGuard");
        }
        
        pm.registerEvents(signChestListener, this);
        pm.registerEvents(trainPlayerListener, this);
        pm.registerEvents(trainBlockListener, this);
        setupConfig();
        
        System.out.println(this + " by wizzledonker loaded all events");

    }
    
    public void setupConfig() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
           System.out.println(this + " generating config file :)");
           getConfig().addDefault("messages.enterMessage", "You entered a cart with a ticket!");
           getConfig().addDefault("messages.leaveMessage", "Hope you enjoyed your ride!");
           getConfig().addDefault("messages.deniedMessage", "Have you paid for that? / You need a ticket to enter that vehicle!");
           getConfig().addDefault("messages.enabled", true);
           getConfig().options().copyDefaults(true);
        }
        
        //Adding the things I added in later, sloppy but needed. Will change later.
        if (!getConfig().contains("messages.boothDenied")) {
            getConfig().set("messages.boothDenied", "Insufficient funds to buy a ticket!");
        }
        if (!getConfig().contains("messages.boothAccept")) {
            getConfig().set("messages.boothAccept", "You have bought a ticket for %price%");
        }
        if (!getConfig().contains("booth.ticket_item_id")) {
            getConfig().set("booth.ticket_item_id", 339);
        }
        if (!getConfig().contains("booth.dispense_minecart")) {
            getConfig().set("booth.dispense_minecart", false);
        }
        if (!getConfig().contains("worldguard_regions")) {
            List<String> listOfStrings = Arrays.asList("trainstation", "road", "city");
            getConfig().set("worldguard_regions", listOfStrings);
        }
        
        saveConfig();
        
        //Set some variables to be used later
        messagesEnable = getConfig().getBoolean("messages.enabled", true);
        dispenseMinecart = getConfig().getBoolean("booth.dispense_minecart", false);
        ticketDataValue = getConfig().getInt("booth.ticket_item_id", 339);
        worldGuardRegions = getConfig().getStringList("worldguard_regions");
        
        System.out.println(this + " has finished loading the config file.");
    }
    
    public String handleMessages(Integer messageType) {
        //Cleaner way to get all the messages I need
        if (messagesEnable = false) {
            return "";
        }
        switch (messageType) {
            case 1:
                return getConfig().getString("messages.enterMessage");
            case 2:
                return getConfig().getString("messages.leaveMessage");
            case 3:
                return getConfig().getString("messages.deniedMessage");
            case 4:
                return getConfig().getString("messages.boothDenied");
            case 5:
                return getConfig().getString("messages.boothAccept");
        }
        return "";
    }
    
    public boolean hasTicket(Player player) {
        return ticketSet.contains(player);
    }
    
    public void setTicket(Player player, boolean enabled) {
        if (hasTicket(player) && enabled == false) {
            ticketSet.remove(player);
        } else if (hasTicket(player) && enabled == true) {
            return;
        }
        if (!hasTicket(player) && enabled == true) {
            ticketSet.add(player);
        } else if (!hasTicket(player) && enabled == false) {
            return;
        }
    }
    
    public void buyTicket(Double price, Player player) {
        //All messages are taken from method (String) handleMessages()
        
        if (hasTicket(player)) return;
        
        ItemStack it = new ItemStack(ticketDataValue, 1);

        if (!isGoldIngot()) {
            if (!(economy.getBalance(player.getName()) > price)) {
                player.sendMessage(ChatColor.RED + handleMessages(4));
                return;
            }
            setTicket(player, true);
            economy.withdrawPlayer(player.getName(), price);
            player.setItemInHand(it);
            player.sendMessage(ChatColor.GREEN + handleMessages(5).replace("%price%", ChatColor.WHITE + price.toString()));
        } else {
            //Process the payment as gold ingots
            Inventory inv = player.getInventory();
            ItemStack gold = new ItemStack(Material.GOLD_INGOT, ((int) Math.ceil(price)));
            if (inv.contains(gold)) {
                inv.remove(gold);
                setTicket(player, true);
                player.setItemInHand(it);
                player.sendMessage(ChatColor.GREEN + handleMessages(5).replace("%price%", ChatColor.WHITE + price.toString()));
            } else {
                player.sendMessage(ChatColor.RED + handleMessages(4));
                return;
            }
        }
        //Dispense the player a minecart if the option is enabled
        if (dispenseMinecart) {
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.MINECART, 1));
            return;
        }
    }
    
    private boolean isGoldIngot() {
        return (economy == null);
    }
    
    public boolean isWorldGuard() {
        return (economy != null);
    }
    
    private Boolean setupEconomy() {
        //Sets up Vault to be used with the plugin
        Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
        if (vault == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    
    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }
}
