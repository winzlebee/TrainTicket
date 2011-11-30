package me.wizzledonker.plugins.trainticket;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Trainticket extends JavaPlugin {
    VehicleListener signChestListener = new TrainTicketListener(this);
    PlayerListener trainPlayerListener = new TrainTicketPlayerListener(this);
    BlockListener trainBlockListener = new TrainTicketBlockListener(this);
    public static Economy economy = null;
    public boolean messagesEnable = true;
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
            System.out.println(this + ": Error enabling economy via Vault! Disabling...");
            pm.disablePlugin(this);
            return;
        }
        
        pm.registerEvent(Type.VEHICLE_ENTER, signChestListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_INTERACT, trainPlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.SIGN_CHANGE, trainBlockListener, Priority.Normal, this);
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
           saveConfig();
        }
       messagesEnable = getConfig().getBoolean("messages.enabled");
    }
    
    public String handleMessages(Integer messageType) {
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
        ItemStack it = new ItemStack(Material.PAPER, 1);
        setTicket(player, true);
        if (!(economy.getBalance(player.getName()) > price)) {
            player.sendMessage(ChatColor.RED + "Insufficient funds to buy a ticket!");
            return;
        }
        economy.withdrawPlayer(player.getName(), price);
        player.setItemInHand(it);
        player.sendMessage(ChatColor.GREEN + "You have bought a ticket for " + ChatColor.WHITE + price.toString());
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
}
