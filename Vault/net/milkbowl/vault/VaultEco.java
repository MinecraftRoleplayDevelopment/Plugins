/*     */ package net.milkbowl.vault;
/*     */ 
/*     */ import com.nijikokun.register.payment.Method;
/*     */ import com.nijikokun.register.payment.Method.MethodAccount;
/*     */ import com.nijikokun.register.payment.Method.MethodBankAccount;
/*     */ import java.util.List;
/*     */ import net.milkbowl.vault.economy.Economy;
/*     */ import net.milkbowl.vault.economy.EconomyResponse;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.RegisteredServiceProvider;
/*     */ import org.bukkit.plugin.ServicesManager;
/*     */ 
/*     */ public class VaultEco
/*     */   implements Method
/*     */ {
/*     */   private Vault vault;
/*     */   private Economy economy;
/*     */ 
/*     */   public Vault getPlugin()
/*     */   {
/*  33 */     return this.vault;
/*     */   }
/*     */ 
/*     */   public boolean createAccount(String name, Double amount)
/*     */   {
/*  39 */     if (!this.economy.createBank(name, "").transactionSuccess()) {
/*  40 */       return false;
/*     */     }
/*  42 */     return this.economy.bankDeposit(name, amount.doubleValue()).transactionSuccess();
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  46 */     return this.vault.getDescription().getName();
/*     */   }
/*     */ 
/*     */   public String getVersion() {
/*  50 */     return this.vault.getDescription().getVersion();
/*     */   }
/*     */ 
/*     */   public int fractionalDigits() {
/*  54 */     return this.economy.fractionalDigits();
/*     */   }
/*     */ 
/*     */   public String format(double amount) {
/*  58 */     return this.economy.format(amount);
/*     */   }
/*     */ 
/*     */   public boolean hasBanks() {
/*  62 */     return this.economy.hasBankSupport();
/*     */   }
/*     */ 
/*     */   public boolean hasBank(String bank) {
/*  66 */     return this.economy.getBanks().contains(bank);
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String name) {
/*  70 */     return this.economy.hasAccount(name);
/*     */   }
/*     */ 
/*     */   public boolean hasBankAccount(String bank, String name) {
/*  74 */     return (this.economy.isBankOwner(bank, name).transactionSuccess()) || (this.economy.isBankMember(bank, name).transactionSuccess());
/*     */   }
/*     */ 
/*     */   public boolean createAccount(String name) {
/*  78 */     return this.economy.createPlayerAccount(name);
/*     */   }
/*     */ 
/*     */   public Method.MethodAccount getAccount(String name) {
/*  82 */     if (!hasAccount(name)) {
/*  83 */       return null;
/*     */     }
/*     */ 
/*  86 */     return new VaultAccount(name, this.economy);
/*     */   }
/*     */ 
/*     */   public Method.MethodBankAccount getBankAccount(String bank, String name) {
/*  90 */     if (!hasBankAccount(bank, name)) {
/*  91 */       return null;
/*     */     }
/*     */ 
/*  94 */     return new VaultBankAccount(bank, this.economy);
/*     */   }
/*     */ 
/*     */   public boolean isCompatible(Plugin plugin) {
/*  98 */     return plugin instanceof Vault;
/*     */   }
/*     */ 
/*     */   public void setPlugin(Plugin plugin) {
/* 102 */     this.vault = ((Vault)plugin);
/* 103 */     RegisteredServiceProvider economyProvider = this.vault.getServer().getServicesManager().getRegistration(Economy.class);
/* 104 */     if (economyProvider != null)
/* 105 */       this.economy = ((Economy)economyProvider.getProvider());
/*     */   }
/*     */ 
/*     */   public class VaultBankAccount
/*     */     implements Method.MethodBankAccount
/*     */   {
/*     */     private final String bank;
/*     */     private final Economy economy;
/*     */ 
/*     */     public VaultBankAccount(String bank, Economy economy)
/*     */     {
/* 178 */       this.bank = bank;
/* 179 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     public String getBankName() {
/* 183 */       return this.bank;
/*     */     }
/*     */ 
/*     */     public int getBankId() {
/* 187 */       return -1;
/*     */     }
/*     */ 
/*     */     public double balance() {
/* 191 */       return this.economy.bankBalance(this.bank).balance;
/*     */     }
/*     */ 
/*     */     public boolean set(double amount) {
/* 195 */       if (!this.economy.bankWithdraw(this.bank, balance()).transactionSuccess()) {
/* 196 */         return false;
/*     */       }
/* 198 */       if (amount == 0.0D) {
/* 199 */         return true;
/*     */       }
/* 201 */       return this.economy.bankDeposit(this.bank, amount).transactionSuccess();
/*     */     }
/*     */ 
/*     */     public boolean add(double amount) {
/* 205 */       return this.economy.bankDeposit(this.bank, amount).transactionSuccess();
/*     */     }
/*     */ 
/*     */     public boolean subtract(double amount) {
/* 209 */       return this.economy.bankWithdraw(this.bank, amount).transactionSuccess();
/*     */     }
/*     */ 
/*     */     public boolean multiply(double amount) {
/* 213 */       double balance = balance();
/* 214 */       return set(balance * amount);
/*     */     }
/*     */ 
/*     */     public boolean divide(double amount) {
/* 218 */       double balance = balance();
/* 219 */       return set(balance / amount);
/*     */     }
/*     */ 
/*     */     public boolean hasEnough(double amount) {
/* 223 */       return balance() >= amount;
/*     */     }
/*     */ 
/*     */     public boolean hasOver(double amount) {
/* 227 */       return balance() > amount;
/*     */     }
/*     */ 
/*     */     public boolean hasUnder(double amount) {
/* 231 */       return balance() < amount;
/*     */     }
/*     */ 
/*     */     public boolean isNegative() {
/* 235 */       return balance() < 0.0D;
/*     */     }
/*     */ 
/*     */     public boolean remove() {
/* 239 */       return set(0.0D);
/*     */     }
/*     */   }
/*     */ 
/*     */   public class VaultAccount
/*     */     implements Method.MethodAccount
/*     */   {
/*     */     private final String name;
/*     */     private final Economy economy;
/*     */ 
/*     */     public VaultAccount(String name, Economy economy)
/*     */     {
/* 114 */       this.name = name;
/* 115 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     public double balance() {
/* 119 */       return this.economy.getBalance(this.name);
/*     */     }
/*     */ 
/*     */     public boolean set(double amount) {
/* 123 */       if (!this.economy.withdrawPlayer(this.name, balance()).transactionSuccess()) {
/* 124 */         return false;
/*     */       }
/*     */ 
/* 127 */       if (amount == 0.0D) {
/* 128 */         return true;
/*     */       }
/* 130 */       return this.economy.depositPlayer(this.name, amount).transactionSuccess();
/*     */     }
/*     */ 
/*     */     public boolean add(double amount) {
/* 134 */       return this.economy.depositPlayer(this.name, amount).transactionSuccess();
/*     */     }
/*     */ 
/*     */     public boolean subtract(double amount) {
/* 138 */       return this.economy.withdrawPlayer(this.name, amount).transactionSuccess();
/*     */     }
/*     */ 
/*     */     public boolean multiply(double amount) {
/* 142 */       double balance = balance();
/* 143 */       return set(balance * amount);
/*     */     }
/*     */ 
/*     */     public boolean divide(double amount) {
/* 147 */       double balance = balance();
/* 148 */       return set(balance / amount);
/*     */     }
/*     */ 
/*     */     public boolean hasEnough(double amount) {
/* 152 */       return balance() >= amount;
/*     */     }
/*     */ 
/*     */     public boolean hasOver(double amount) {
/* 156 */       return balance() > amount;
/*     */     }
/*     */ 
/*     */     public boolean hasUnder(double amount) {
/* 160 */       return balance() < amount;
/*     */     }
/*     */ 
/*     */     public boolean isNegative() {
/* 164 */       return balance() < 0.0D;
/*     */     }
/*     */ 
/*     */     public boolean remove() {
/* 168 */       return set(0.0D);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.VaultEco
 * JD-Core Version:    0.6.2
 */