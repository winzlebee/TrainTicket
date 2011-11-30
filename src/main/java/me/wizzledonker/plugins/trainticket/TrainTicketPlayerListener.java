/*
 * PlayerListener for right clicking ticket booth signs
 * Copyright WizzleDonker @ 28/11/11
 */
package me.wizzledonker.plugins.trainticket;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class TrainTicketPlayerListener extends PlayerListener {
    
    public static Trainticket plugin;
    
    public TrainTicketPlayerListener(Trainticket instance) {
        plugin = instance;
    }
   
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.hasPermission("TrainTicket.booth.use")) {
            return;
        }
        Block block = event.getClickedBlock();
        if (!(block.getType().equals(Material.SIGN_POST)) && !(block.getType().equals(Material.WALL_SIGN))) {
            return;
        }
        Sign sign = ((Sign) block.getState());
        if (!sign.getLine(0).equalsIgnoreCase(ChatColor.GOLD + "[booth]")) {
            return;
        }
        if (sign.getLine(1).isEmpty() || sign.getLine(2).isEmpty()) {
            player.sendMessage(ChatColor.RED + "That ticket booth is incomplete!");
            return;
        }
        plugin.buyTicket(Double.parseDouble(sign.getLine(2)), player);
    }
}
