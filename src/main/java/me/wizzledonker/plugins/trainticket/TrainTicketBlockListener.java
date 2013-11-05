/*    */ package me.wizzledonker.plugins.trainticket;
/*    */ 
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.block.SignChangeEvent;
/*    */ 
/*    */ public class TrainTicketBlockListener
/*    */   implements Listener
/*    */ {
/*    */   public static Trainticket plugin;
/*    */ 
/*    */   public TrainTicketBlockListener(Trainticket instance)
/*    */   {
/* 21 */     plugin = instance;
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onSignChange(SignChangeEvent event) {
/* 26 */     Player player = event.getPlayer();
/* 27 */     if (!event.getLine(0).equalsIgnoreCase("[booth]")) {
/* 28 */       return;
/*    */     }
/* 30 */     if (!player.hasPermission("TrainTicket.booth.create")) {
/* 31 */       event.setLine(0, "The booth:");
/* 32 */       return;
/*    */     }
/* 34 */     if ((event.getLine(1).isEmpty()) || (event.getLine(2).isEmpty())) {
/* 35 */       player.sendMessage(ChatColor.DARK_RED + "Please fill in the missing values!");
/* 36 */       return;
/*    */     }
/* 38 */     event.setLine(0, ChatColor.GOLD + event.getLine(0));
/*    */   }
/*    */ }

/* Location:           C:\Users\DrkMatr\Desktop\TrainTicket-1.4.jar
 * Qualified Name:     me.wizzledonker.plugins.trainticket.TrainTicketBlockListener
 * JD-Core Version:    0.6.2
 */