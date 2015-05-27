/*     */ package com.comphenix.protocol.injector;
/*     */ 
/*     */ import com.google.common.collect.Sets;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginLoadOrder;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ class PluginVerifier
/*     */ {
/*  57 */   private static final Set<String> DYNAMIC_DEPENDENCY = Sets.newHashSet(new String[] { "mcore", "MassiveCore" });
/*     */ 
/*  62 */   private final Set<String> loadedAfter = new HashSet();
/*     */   private final Plugin dependency;
/*     */ 
/*     */   public PluginVerifier(Plugin dependency)
/*     */   {
/*  74 */     if (dependency == null) {
/*  75 */       throw new IllegalArgumentException("dependency cannot be NULL.");
/*     */     }
/*  77 */     if (safeConversion(dependency.getDescription().getLoadBefore()).size() > 0) {
/*  78 */       throw new IllegalArgumentException("dependency cannot have a load directives.");
/*     */     }
/*  80 */     this.dependency = dependency;
/*     */   }
/*     */ 
/*     */   private Plugin getPlugin(String pluginName)
/*     */   {
/*  90 */     Plugin plugin = getPluginOrDefault(pluginName);
/*     */ 
/*  93 */     if (plugin != null) {
/*  94 */       return plugin;
/*     */     }
/*  96 */     throw new PluginNotFoundException("Cannot find plugin " + pluginName);
/*     */   }
/*     */ 
/*     */   private Plugin getPluginOrDefault(String pluginName)
/*     */   {
/* 105 */     return this.dependency.getServer().getPluginManager().getPlugin(pluginName);
/*     */   }
/*     */ 
/*     */   public VerificationResult verify(String pluginName)
/*     */   {
/* 118 */     if (pluginName == null)
/* 119 */       throw new IllegalArgumentException("pluginName cannot be NULL.");
/* 120 */     return verify(getPlugin(pluginName));
/*     */   }
/*     */ 
/*     */   public VerificationResult verify(Plugin plugin)
/*     */   {
/* 133 */     if (plugin == null)
/* 134 */       throw new IllegalArgumentException("plugin cannot be NULL.");
/* 135 */     String name = plugin.getName();
/*     */ 
/* 138 */     if ((!this.dependency.equals(plugin)) && 
/* 139 */       (!this.loadedAfter.contains(name)) && (!DYNAMIC_DEPENDENCY.contains(name))) {
/* 140 */       if (verifyLoadOrder(this.dependency, plugin))
/*     */       {
/* 142 */         this.loadedAfter.add(plugin.getName());
/*     */       }
/* 144 */       else return VerificationResult.NO_DEPEND;
/*     */ 
/*     */     }
/*     */ 
/* 150 */     return VerificationResult.VALID;
/*     */   }
/*     */ 
/*     */   private boolean verifyLoadOrder(Plugin beforePlugin, Plugin afterPlugin)
/*     */   {
/* 164 */     if (hasDependency(afterPlugin, beforePlugin)) {
/* 165 */       return true;
/*     */     }
/*     */ 
/* 169 */     if ((beforePlugin.getDescription().getLoad() == PluginLoadOrder.STARTUP) && (afterPlugin.getDescription().getLoad() == PluginLoadOrder.POSTWORLD))
/*     */     {
/* 171 */       return true;
/*     */     }
/* 173 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean hasDependency(Plugin plugin, Plugin dependency)
/*     */   {
/* 183 */     return hasDependency(plugin, dependency, Sets.newHashSet());
/*     */   }
/*     */ 
/*     */   private Set<String> safeConversion(List<String> list)
/*     */   {
/* 194 */     if (list == null) {
/* 195 */       return Collections.emptySet();
/*     */     }
/* 197 */     return Sets.newHashSet(list);
/*     */   }
/*     */ 
/*     */   private boolean hasDependency(Plugin plugin, Plugin dependency, Set<String> checking)
/*     */   {
/* 202 */     Set childNames = Sets.union(safeConversion(plugin.getDescription().getDepend()), safeConversion(plugin.getDescription().getSoftDepend()));
/*     */ 
/* 208 */     if (!checking.add(plugin.getName())) {
/* 209 */       throw new IllegalStateException("Cycle detected in dependency graph: " + plugin);
/*     */     }
/*     */ 
/* 212 */     if (childNames.contains(dependency.getName())) {
/* 213 */       return true;
/*     */     }
/*     */ 
/* 217 */     for (String childName : childNames) {
/* 218 */       Plugin childPlugin = getPluginOrDefault(childName);
/*     */ 
/* 220 */       if ((childPlugin != null) && (hasDependency(childPlugin, dependency, checking))) {
/* 221 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 226 */     checking.remove(plugin.getName());
/*     */ 
/* 229 */     return false;
/*     */   }
/*     */ 
/*     */   public static enum VerificationResult
/*     */   {
/*  39 */     VALID, 
/*     */ 
/*  44 */     NO_DEPEND;
/*     */ 
/*     */     public boolean isValid()
/*     */     {
/*  50 */       return this == VALID;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class PluginNotFoundException extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 8956699101336877611L;
/*     */ 
/*     */     public PluginNotFoundException()
/*     */     {
/*     */     }
/*     */ 
/*     */     public PluginNotFoundException(String message)
/*     */     {
/*  34 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.PluginVerifier
 * JD-Core Version:    0.6.2
 */