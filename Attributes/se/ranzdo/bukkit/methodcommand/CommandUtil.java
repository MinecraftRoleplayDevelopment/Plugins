/*    */ package se.ranzdo.bukkit.methodcommand;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ public class CommandUtil
/*    */ {
/* 11 */   private static Pattern verifyArgumentsPattern = Pattern.compile("^(.*?)\\[(.*?)\\]$");
/*    */ 
/* 13 */   public static String escapeArgumentVariable(String var) { if (var == null) {
/* 14 */       return null;
/*    */     }
/* 16 */     if (var.matches("^\\\\*\\?.*$")) {
/* 17 */       return "\\" + var;
/*    */     }
/* 19 */     return var; }
/*    */ 
/*    */   public static Map<String, String[]> parseVerifiers(String verifiers)
/*    */   {
/* 23 */     Map map = new LinkedHashMap();
/*    */ 
/* 25 */     if (verifiers.equals("")) {
/* 26 */       return map;
/*    */     }
/* 28 */     String[] arguments = verifiers.split("\\|");
/*    */ 
/* 30 */     for (String arg : arguments) {
/* 31 */       Matcher matcher = verifyArgumentsPattern.matcher(arg);
/* 32 */       if (!matcher.matches()) {
/* 33 */         throw new IllegalArgumentException("The argrument \"" + arg + "\" is in invalid form.");
/*    */       }
/* 35 */       List parameters = new ArrayList();
/*    */ 
/* 37 */       String sparameters = matcher.group(2);
/* 38 */       if (sparameters != null) {
/* 39 */         for (String parameter : sparameters.split(",")) {
/* 40 */           parameters.add(parameter.trim());
/*    */         }
/*    */       }
/* 43 */       String argName = matcher.group(1).trim();
/*    */ 
/* 45 */       map.put(argName, parameters.toArray(new String[0]));
/*    */     }
/*    */ 
/* 48 */     return map;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.CommandUtil
 * JD-Core Version:    0.6.2
 */