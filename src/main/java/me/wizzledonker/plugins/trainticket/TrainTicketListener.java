package me.wizzledonker.plugins.trainticket;
 
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
 
public class TrainTicketListener
implements Listener
{
public static Trainticket plugin;
 
public TrainTicketListener(Trainticket instance)
{
plugin = instance;
}

  
@EventHandler
public void onVehicleEnter(VehicleEnterEvent event) {
             if ((event.getVehicle().getType() == EntityType.HORSE)) {
                 return;             
             }
             if ((event.getVehicle().getType() == EntityType.PIG)) {
                 return;             
             }
             if ((event.getEntered() instanceof Player)) {
       	Player player = (Player)event.getEntered();
       	if (player.hasPermission("TrainTicket.exempt.cart")) {
         		return;
       		}
                if ((event.getVehicle().getType() == EntityType.BOAT) && player.hasPermission("TrainTicket.exempt.boat")) {
                		return;
                }
       	if (player.isInsideVehicle()) {
         		player.sendMessage(plugin.handleMessages(Integer.valueOf(2)));
         		return;
       		}
                              
        	     if (player.getItemInHand().getAmount() != 0)
        	    	 {
        	    	 	if((player.getInventory().getItemInHand().hasItemMeta() == true) && (player.getInventory().getItemInHand().getItemMeta().getDisplayName().equals(plugin.ticketName)) && (plugin.hasTicket(player) == true))
        	    	 		{
         				player.sendMessage(plugin.handleMessages(Integer.valueOf(1)));
         				if(player.getItemInHand().getAmount() == 1)
									{
										player.setItemInHand(new ItemStack(Material.AIR));
										plugin.setTicket(player, false);
									}
								else{
										player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
										plugin.setTicket(player, true);
									}
       				}
        	    	 	else{
							player.sendMessage(plugin.handleMessages(Integer.valueOf(3)));
							event.setCancelled(true);
        	    	 		}
        	    	 	}
				else {
						player.sendMessage(plugin.handleMessages(Integer.valueOf(3)));
						event.setCancelled(true);
       		 }
             }
}
}
