package me.wizzledonker.plugins.trainticket;
 

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.Listener;
public class TrainTicketLoginListener
implements Listener
{
public static Trainticket plugin;
 
public TrainTicketLoginListener(Trainticket instance)
{
plugin = instance;
}
@EventHandler
public void trainLoginListener(PlayerLoginEvent event) {    /*Fixes the error where TrainTickets cease to be TrainTickets if player logs off*/
	Player player = event.getPlayer();
	plugin.setTicket(player, true);
	}    
}
