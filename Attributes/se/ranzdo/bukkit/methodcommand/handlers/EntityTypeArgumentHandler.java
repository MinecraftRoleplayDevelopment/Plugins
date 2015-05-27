/*    */ package se.ranzdo.bukkit.methodcommand.handlers;
/*    */ 
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.EntityType;
/*    */ import se.ranzdo.bukkit.methodcommand.ArgumentHandler;
/*    */ import se.ranzdo.bukkit.methodcommand.CommandArgument;
/*    */ import se.ranzdo.bukkit.methodcommand.TransformError;
/*    */ 
/*    */ public class EntityTypeArgumentHandler extends ArgumentHandler<EntityType>
/*    */ {
/*    */   public EntityTypeArgumentHandler()
/*    */   {
/* 13 */     setMessage("parse_error", "There is no entity named %1");
/* 14 */     setMessage("include_error", "There is no entity named %1");
/* 15 */     setMessage("exclude_error", "There is no entity named %1");
/*    */   }
/*    */ 
/*    */   public EntityType transform(CommandSender sender, CommandArgument argument, String value)
/*    */     throws TransformError
/*    */   {
/*    */     // Byte code:
/*    */     //   0: aload_3
/*    */     //   1: invokestatic 7	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*    */     //   4: invokestatic 8	org/bukkit/entity/EntityType:fromId	(I)Lorg/bukkit/entity/EntityType;
/*    */     //   7: areturn
/*    */     //   8: astore 4
/*    */     //   10: aload_3
/*    */     //   11: invokestatic 10	org/bukkit/entity/EntityType:fromName	(Ljava/lang/String;)Lorg/bukkit/entity/EntityType;
/*    */     //   14: astore 4
/*    */     //   16: aload 4
/*    */     //   18: ifnull +6 -> 24
/*    */     //   21: aload 4
/*    */     //   23: areturn
/*    */     //   24: new 11	se/ranzdo/bukkit/methodcommand/TransformError
/*    */     //   27: dup
/*    */     //   28: aload_2
/*    */     //   29: ldc 2
/*    */     //   31: iconst_1
/*    */     //   32: anewarray 12	java/lang/String
/*    */     //   35: dup
/*    */     //   36: iconst_0
/*    */     //   37: aload_3
/*    */     //   38: aastore
/*    */     //   39: invokevirtual 13	se/ranzdo/bukkit/methodcommand/CommandArgument:getMessage	(Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
/*    */     //   42: invokespecial 14	se/ranzdo/bukkit/methodcommand/TransformError:<init>	(Ljava/lang/String;)V
/*    */     //   45: athrow
/*    */     //
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   0	7	8	java/lang/NumberFormatException
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.handlers.EntityTypeArgumentHandler
 * JD-Core Version:    0.6.2
 */