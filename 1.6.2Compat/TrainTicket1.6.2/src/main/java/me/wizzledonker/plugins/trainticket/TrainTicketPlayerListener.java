/*    */ package me.wizzledonker.plugins.trainticket;
/*    */ 
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.block.Block;
/*    */ import org.bukkit.block.Sign;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.block.Action;
/*    */ import org.bukkit.event.player.PlayerInteractEvent;
/*    */ 
/*    */ public class TrainTicketPlayerListener
/*    */   implements Listener
/*    */ {
/*    */   public static Trainticket plugin;
/*    */ 
/*    */   public TrainTicketPlayerListener(Trainticket instance)
/*    */   {
/* 22 */     plugin = instance;
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onPlayerInteract(PlayerInteractEvent event) {
/* 27 */     Action action = event.getAction();
/* 28 */     if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
/* 29 */       return;
/*    */     }
/* 31 */     Player player = event.getPlayer();
/* 32 */     if (!player.hasPermission("TrainTicket.booth.use")) {
/* 33 */       return;
/*    */     }
/* 35 */     Block block = event.getClickedBlock();
/* 36 */     if ((!block.getType().equals(Material.SIGN_POST)) && (!block.getType().equals(Material.WALL_SIGN))) {
/* 37 */       return;
/*    */     }
/* 39 */     Sign sign = (Sign)block.getState();
/* 40 */     if (!sign.getLine(0).equalsIgnoreCase(ChatColor.GOLD + "[booth]")) {
/* 41 */       return;
/*    */     }
/* 43 */     if ((sign.getLine(1).isEmpty()) || (sign.getLine(2).isEmpty())) {
/* 44 */       player.sendMessage(ChatColor.RED + "That ticket booth is incomplete!");
/* 45 */       return;
/*    */     }
/* 47 */     plugin.buyTicket(Double.valueOf(Double.parseDouble(sign.getLine(2))), player);
/*    */   }
/*    */ }

/* Location:           C:\Users\DrkMatr\Desktop\TrainTicket-1.4.jar
 * Qualified Name:     me.wizzledonker.plugins.trainticket.TrainTicketPlayerListener
 * JD-Core Version:    0.6.2
 */