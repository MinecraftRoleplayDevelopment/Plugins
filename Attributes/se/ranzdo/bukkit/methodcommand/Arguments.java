/*    */ package se.ranzdo.bukkit.methodcommand;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ public class Arguments
/*    */ {
/*    */   private List<String> arguments;
/* 12 */   private int argCounter = 0;
/*    */ 
/* 14 */   private Map<Flag, List<String>> flags = new HashMap();
/* 15 */   private Map<Flag, Integer> flagCounter = new HashMap();
/*    */ 
/*    */   public Arguments(String[] args, Map<String, Flag> flags) throws CommandError {
/* 18 */     List largs = new ArrayList(Arrays.asList(args));
/*    */ 
/* 20 */     for (Map.Entry entry : flags.entrySet()) {
/* 21 */       Flag flag = (Flag)entry.getValue();
/*    */ 
/* 23 */       int flagIndex = largs.indexOf("-" + flag.getIdentifier());
/* 24 */       if (flagIndex != -1)
/*    */       {
/* 27 */         largs.remove(flagIndex);
/*    */ 
/* 29 */         int endIndex = flag.getArguments().size() + flagIndex;
/*    */ 
/* 31 */         if (endIndex > largs.size()) {
/* 32 */           throw new CommandError("The flag -" + flag.getIdentifier() + " does not have the required parameters.");
/*    */         }
/* 34 */         this.flagCounter.put(flag, Integer.valueOf(0));
/*    */ 
/* 36 */         List flagArgs = new ArrayList();
/* 37 */         this.flags.put(flag, flagArgs);
/*    */ 
/* 39 */         for (int i = flagIndex; i < endIndex; i++) {
/* 40 */           flagArgs.add(largs.remove(flagIndex));
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 45 */     this.arguments = largs;
/*    */   }
/*    */ 
/*    */   public boolean flagExists(Flag flag) {
/* 49 */     return this.flags.get(flag) != null;
/*    */   }
/*    */ 
/*    */   public boolean hasNext() {
/* 53 */     return this.argCounter < size();
/*    */   }
/*    */ 
/*    */   public boolean hasNext(Flag flag) {
/* 57 */     Integer c = (Integer)this.flagCounter.get(flag);
/* 58 */     if (c == null) {
/* 59 */       return false;
/*    */     }
/* 61 */     return c.intValue() < size(flag);
/*    */   }
/*    */ 
/*    */   public String nextArgument() {
/* 65 */     String arg = (String)this.arguments.get(this.argCounter);
/* 66 */     this.argCounter += 1;
/* 67 */     return arg;
/*    */   }
/*    */ 
/*    */   public String nextFlagArgument(Flag flag) {
/* 71 */     List args = (List)this.flags.get(flag);
/*    */ 
/* 73 */     if (args == null) {
/* 74 */       return null;
/*    */     }
/* 76 */     return (String)args.get(((Integer)this.flagCounter.put(flag, Integer.valueOf(((Integer)this.flagCounter.get(flag)).intValue() + 1))).intValue());
/*    */   }
/*    */ 
/*    */   public int over() {
/* 80 */     return size() - this.argCounter;
/*    */   }
/*    */ 
/*    */   public int over(Flag flag) {
/* 84 */     return size(flag) - ((Integer)this.flagCounter.get(flag)).intValue();
/*    */   }
/*    */ 
/*    */   public int size() {
/* 88 */     return this.arguments.size();
/*    */   }
/*    */ 
/*    */   public int size(Flag flag) {
/* 92 */     List args = (List)this.flags.get(flag);
/*    */ 
/* 94 */     if (args == null) {
/* 95 */       return 0;
/*    */     }
/* 97 */     return args.size();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.Arguments
 * JD-Core Version:    0.6.2
 */