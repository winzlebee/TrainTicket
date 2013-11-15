package me.wizzledonker.plugins.trainticket;
 
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
 
public class TrainTicketListener
implements Listener
{
public static Trainticket plugin;
 
public TrainTicketListener(Trainticket instance)
{
plugin = instance;
}
/*    */ 
/*    */   
@EventHandler

/*    */   public void onVehicleEnter(VehicleEnterEvent event) {
             if ((event.getVehicle().getType() == EntityType.HORSE)) {
                 return;             
             }
             if ((event.getVehicle().getType() == EntityType.PIG)) {
                 return;             
             }
             if ((event.getEntered() instanceof Player)) {
/* 23 */       	Player player = (Player)event.getEntered();
/* 24 */       	if (player.hasPermission("TrainTicket.exempt.cart")) {
/* 25 */         		return;
/*    */       		}
                if ((event.getVehicle().getType() == EntityType.BOAT) && player.hasPermission("TrainTicket.exempt.boat")) {
                		return;
                }
/* 27 */       	if (player.isInsideVehicle()) {
/* 28 */         		player.sendMessage(plugin.handleMessages(Integer.valueOf(2)));
/* 29 */         		return;
/*    */       		}
                              
        	     if (player.getItemInHand().getAmount() != 0)
        	    	 {
        	    	 	if((player.getInventory().getItemInHand().getItemMeta().getDisplayName().equals(plugin.ticketName)) && (plugin.hasTicket(player) == true))
        	    	 		{
/* 33 */         				player.sendMessage(plugin.handleMessages(Integer.valueOf(1)));
/* 34 */         				if(player.getItemInHand().getAmount() == 1)
									{
										player.setItemInHand(new ItemStack(Material.AIR));
										plugin.setTicket(player, false);
									}
								else{
										player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
										plugin.setTicket(player, true);
									}
/*    */       				}
        	    	 	else{
							player.sendMessage(plugin.handleMessages(Integer.valueOf(3)));
							event.setCancelled(true);
        	    	 		}
        	    	 	}
				else {
						player.sendMessage(plugin.handleMessages(Integer.valueOf(3)));
						event.setCancelled(true);
/*    */       		 }
             }
}
}
