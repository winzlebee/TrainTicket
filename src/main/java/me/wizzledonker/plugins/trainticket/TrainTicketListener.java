package me.wizzledonker.plugins.trainticket;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

/**
 *
 * @author Win
 */
public class TrainTicketListener implements Listener{
    
    public static Trainticket plugin;
    
    public TrainTicketListener(Trainticket instance) {
        plugin = instance;
    }
    
    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            Player player = (Player) event.getEntered();
            if (player.hasPermission("TrainTicket.exempt")) {
                return;
            }
            if (player.isInsideVehicle()) {
                player.sendMessage(plugin.handleMessages(2));
                return;
            }
            if (plugin.isWorldGuard()) {
                RegionManager regionManager = plugin.worldGuard.getRegionManager(player.getWorld());
                ApplicableRegionSet set = regionManager.getApplicableRegions(player.getLocation());
                
                boolean ticket = false;
                for (ProtectedRegion region : set) {
                    if (plugin.worldGuardRegions.contains(region.getId())) {
                        ticket = true;
                    }
                }
                if (!ticket) return;
            }
            if (player.getItemInHand().getTypeId() == plugin.ticketDataValue && plugin.hasTicket(player)) {
                plugin.setTicket(player, false);
                player.sendMessage(plugin.handleMessages(1));
                player.setItemInHand(null);
            } else {
                player.sendMessage(plugin.handleMessages(3));
                event.setCancelled(true);
            }
        }
    }
}
