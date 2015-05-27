/*    */ package net.milkbowl.vault.economy;
/*    */ 
/*    */ public class EconomyResponse
/*    */ {
/*    */   public final double amount;
/*    */   public final double balance;
/*    */   public final ResponseType type;
/*    */   public final String errorMessage;
/*    */ 
/*    */   public EconomyResponse(double amount, double balance, ResponseType type, String errorMessage)
/*    */   {
/* 71 */     this.amount = amount;
/* 72 */     this.balance = balance;
/* 73 */     this.type = type;
/* 74 */     this.errorMessage = errorMessage;
/*    */   }
/*    */ 
/*    */   public boolean transactionSuccess()
/*    */   {
/* 82 */     switch (1.$SwitchMap$net$milkbowl$vault$economy$EconomyResponse$ResponseType[this.type.ordinal()]) {
/*    */     case 1:
/* 84 */       return true;
/*    */     }
/* 86 */     return false;
/*    */   }
/*    */ 
/*    */   public static enum ResponseType
/*    */   {
/* 30 */     SUCCESS(1), 
/* 31 */     FAILURE(2), 
/* 32 */     NOT_IMPLEMENTED(3);
/*    */ 
/*    */     private int id;
/*    */ 
/*    */     private ResponseType(int id) {
/* 37 */       this.id = id;
/*    */     }
/*    */ 
/*    */     int getId() {
/* 41 */       return this.id;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.EconomyResponse
 * JD-Core Version:    0.6.2
 */