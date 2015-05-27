/*    */ package se.ranzdo.bukkit.methodcommand;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import org.bukkit.command.PluginCommand;
/*    */ 
/*    */ public class RootCommand extends RegisteredCommand
/*    */ {
/*    */   private PluginCommand root;
/*    */ 
/*    */   RootCommand(PluginCommand root, CommandHandler handler)
/*    */   {
/* 12 */     super(root.getLabel(), handler, null);
/* 13 */     this.root = root;
/*    */   }
/*    */ 
/*    */   public PluginCommand getBukkitCommand() {
/* 17 */     return this.root;
/*    */   }
/*    */ 
/*    */   void set(Object methodInstance, Method method)
/*    */   {
/* 22 */     super.set(methodInstance, method);
/* 23 */     this.root.setDescription(getDescription());
/* 24 */     this.root.setUsage(getUsage());
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.RootCommand
 * JD-Core Version:    0.6.2
 */