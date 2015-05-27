/*    */ package jp.mydns.dyukusi.itemsounds.listener;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import jp.mydns.dyukusi.itemsounds.ItemSounds;
/*    */ import jp.mydns.dyukusi.itemsounds.soundinfo.SoundInformation;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.Sound;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.entity.HumanEntity;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.inventory.InventoryClickEvent;
/*    */ import org.bukkit.event.inventory.InventoryType.SlotType;
/*    */ import org.bukkit.event.player.PlayerItemHeldEvent;
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ import org.bukkit.inventory.PlayerInventory;
/*    */ 
/*    */ public class InventoryListener
/*    */   implements Listener
/*    */ {
/*    */   ItemSounds plugin;
/*    */ 
/*    */   public InventoryListener(ItemSounds iss, HashMap<Material, SoundInformation> it_map)
/*    */   {
/* 24 */     this.plugin = iss;
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   void SelectItemEvent(InventoryClickEvent event) {
/* 29 */     Material item = event.getCursor().getType();
/* 30 */     HumanEntity he = event.getWhoClicked();
/*    */ 
/* 32 */     if ((he instanceof Player)) {
/* 33 */       Player player = (Player)he;
/*    */ 
/* 37 */       if (!item.equals(Material.AIR))
/*    */       {
/*    */         SoundInformation sinfo;
/*    */         SoundInformation sinfo;
/* 41 */         if (event.getSlotType().equals(InventoryType.SlotType.ARMOR))
/*    */         {
/*    */           SoundInformation sinfo;
/* 44 */           if (this.plugin.iscontain_equip_armor_sound(item)) {
/* 45 */             sinfo = this.plugin.get_equip_armor_sound_inf(item);
/*    */           }
/*    */           else
/*    */           {
/* 49 */             sinfo = this.plugin.get_default_equip_armor_sound_inf();
/*    */           }
/*    */         }
/*    */         else
/*    */         {
/*    */           SoundInformation sinfo;
/* 56 */           if (this.plugin.iscontain_put_sound(item)) {
/* 57 */             sinfo = this.plugin.get_put_sound_inf(item);
/*    */           }
/*    */           else
/*    */           {
/* 61 */             sinfo = this.plugin.get_default_item_sound_inf();
/*    */           }
/*    */ 
/*    */         }
/*    */ 
/* 66 */         player.playSound(player.getLocation(), sinfo.get_sound(), sinfo.get_volume(), sinfo.get_pitch());
/*    */       }
/*    */       else
/*    */       {
/* 71 */         player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   void SwitchItemEvent(PlayerItemHeldEvent event)
/*    */   {
/* 80 */     Player player = event.getPlayer();
/* 81 */     ItemStack item = player.getInventory().getItem(event.getNewSlot());
/*    */ 
/* 83 */     if (item != null) {
/* 84 */       Material material = item.getType();
/*    */       SoundInformation sinfo;
/*    */       SoundInformation sinfo;
/* 88 */       if (this.plugin.iscontain_handitem_sound(material)) {
/* 89 */         sinfo = this.plugin.get_handitem_sound_inf(material);
/*    */       }
/*    */       else
/*    */       {
/* 93 */         sinfo = this.plugin.get_default_handitem_sound_inf();
/*    */       }
/*    */ 
/* 96 */       player.getWorld().playSound(player.getLocation(), sinfo.get_sound(), sinfo.get_volume(), sinfo.get_pitch());
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemSounds.jar
 * Qualified Name:     jp.mydns.dyukusi.itemsounds.listener.InventoryListener
 * JD-Core Version:    0.6.2
 */