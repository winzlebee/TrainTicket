package me.wizzledonker.plugins.trainticket;
 
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
 
public class TrainTicketBlockListener
implements Listener
{
   public static Trainticket plugin;
 
   public TrainTicketBlockListener(Trainticket instance)
   {
     plugin = instance;
   }
 
   @EventHandler
   public void onSignChange(SignChangeEvent event) {
     Player player = event.getPlayer();
     if (!event.getLine(0).equalsIgnoreCase("[booth]")) {
       return;
     }
     if (!player.hasPermission("TrainTicket.booth.create")) {
       event.setLine(0, "The booth:");
       return;
     }
     if ((event.getLine(1).isEmpty()) || (event.getLine(2).isEmpty())) {
       player.sendMessage(ChatColor.DARK_RED + "Please fill in the missing values!");
       return;
     }
     event.setLine(0, ChatColor.GOLD + event.getLine(0));
   }
 }
