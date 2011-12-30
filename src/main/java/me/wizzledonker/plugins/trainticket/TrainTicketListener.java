package me.wizzledonker.plugins.trainticket;

import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleListener;

/**
 *
 * @author Win
 */
public class TrainTicketListener extends VehicleListener{
    
    public static Trainticket plugin;
    
    public TrainTicketListener(Trainticket instance) {
        plugin = instance;
    }
    
    @Override
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
