/*    */ package se.ranzdo.bukkit.methodcommand.handlers;
/*    */ 
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import se.ranzdo.bukkit.methodcommand.ArgumentHandler;
/*    */ import se.ranzdo.bukkit.methodcommand.CommandArgument;
/*    */ import se.ranzdo.bukkit.methodcommand.TransformError;
/*    */ 
/*    */ public class MaterialArgumentHandler extends ArgumentHandler<Material>
/*    */ {
/*    */   public MaterialArgumentHandler()
/*    */   {
/* 14 */     setMessage("parse_error", "The parameter [%p] is not a valid material.");
/* 15 */     setMessage("include_error", "There is no material named %1");
/* 16 */     setMessage("exclude_error", "There is no material named %1");
/*    */   }
/*    */ 
/*    */   public Material transform(CommandSender sender, CommandArgument argument, String value) throws TransformError
/*    */   {
/* 21 */     Material m = null;
/*    */     try {
/* 23 */       m = Material.getMaterial(Integer.parseInt(value));
/*    */     }
/*    */     catch (NumberFormatException e) {
/*    */     }
/* 27 */     if (m != null) {
/* 28 */       return m;
/*    */     }
/* 30 */     m = Material.getMaterial(value);
/*    */ 
/* 32 */     if (m != null) {
/* 33 */       return m;
/*    */     }
/*    */ 
/* 36 */     throw new TransformError(argument.getMessage("parse_error"));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.handlers.MaterialArgumentHandler
 * JD-Core Version:    0.6.2
 */