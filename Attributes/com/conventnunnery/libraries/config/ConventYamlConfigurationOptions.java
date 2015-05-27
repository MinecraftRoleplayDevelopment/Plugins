/*    */ package com.conventnunnery.libraries.config;
/*    */ 
/*    */ import org.bukkit.configuration.file.YamlConfigurationOptions;
/*    */ 
/*    */ public class ConventYamlConfigurationOptions extends YamlConfigurationOptions
/*    */ {
/*  7 */   private boolean updateOnLoad = true;
/*  8 */   private boolean backupOnUpdate = false;
/*  9 */   private boolean createDefaultFile = true;
/*    */ 
/*    */   public ConventYamlConfigurationOptions(ConventYamlConfiguration configuration) {
/* 12 */     super(configuration);
/*    */   }
/*    */ 
/*    */   public boolean updateOnLoad() {
/* 16 */     return this.updateOnLoad;
/*    */   }
/*    */ 
/*    */   public void updateOnLoad(boolean updateOnLoad) {
/* 20 */     this.updateOnLoad = updateOnLoad;
/*    */   }
/*    */ 
/*    */   public boolean backupOnUpdate() {
/* 24 */     return this.backupOnUpdate;
/*    */   }
/*    */ 
/*    */   public void backupOnUpdate(boolean backupOnUpdate) {
/* 28 */     this.backupOnUpdate = backupOnUpdate;
/*    */   }
/*    */ 
/*    */   public boolean createDefaultFile() {
/* 32 */     return this.createDefaultFile;
/*    */   }
/*    */ 
/*    */   public void createDefaultFile(boolean createDefaultFile) {
/* 36 */     this.createDefaultFile = createDefaultFile;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     com.conventnunnery.libraries.config.ConventYamlConfigurationOptions
 * JD-Core Version:    0.6.2
 */