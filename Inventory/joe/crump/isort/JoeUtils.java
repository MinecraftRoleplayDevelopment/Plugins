/*     */ package joe.crump.isort;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.command.ConsoleCommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.SkullMeta;
/*     */ 
/*     */ public class JoeUtils
/*     */ {
/*  23 */   public static String joeDirectory = "plugins" + File.separator + "JoeInventorySort" + File.separator;
/*     */ 
/*  25 */   public static ConcurrentHashMap<String, Boolean> PlayerSortPreference = new ConcurrentHashMap();
/*     */ 
/*  56 */   public static ConcurrentHashMap<String, Boolean> loggedAlready = new ConcurrentHashMap();
/*     */ 
/*     */   public static void ConsoleMsg(String msg)
/*     */   {
/*     */     try
/*     */     {
/*  32 */       Bukkit.getConsoleSender().sendMessage(msg);
/*     */     }
/*     */     catch (Throwable exc)
/*     */     {
/*  36 */       System.out.println("JoeInventorySort: Failed to Write ConsoleMsg: " + msg);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void EnsureDirectory(String dirName)
/*     */   {
/*  42 */     File pDir = new File(dirName);
/*  43 */     if (pDir.isDirectory()) return;
/*     */ 
/*     */     try
/*     */     {
/*  47 */       System.out.println("Creating directory: " + dirName);
/*  48 */       pDir.mkdir();
/*     */     }
/*     */     catch (Throwable exc)
/*     */     {
/*  52 */       System.out.println("EnsureDirectory " + dirName + ": " + exc.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean ConsoleLogOnce(String msg)
/*     */   {
/*  59 */     Boolean hit = (Boolean)loggedAlready.get(msg);
/*  60 */     if (hit == null)
/*     */     {
/*  62 */       loggedAlready.put(msg, Boolean.valueOf(true));
/*  63 */       Bukkit.getConsoleSender().sendMessage(msg);
/*  64 */       return true;
/*     */     }
/*  66 */     return false;
/*     */   }
/*     */ 
/*     */   public static void ReportError(Throwable exc, String msg)
/*     */   {
/*  71 */     String errMsg = ChatColor.LIGHT_PURPLE + InventorySort.AppName + " -- Unexpected Error: " + ChatColor.YELLOW + exc.getMessage();
/*  72 */     if (msg != null) errMsg = errMsg + ChatColor.WHITE + ": " + msg;
/*  73 */     ConsoleMsg(errMsg);
/*     */   }
/*     */ 
/*     */   public static String NthString(long k)
/*     */   {
/*  78 */     if (k % 10L == 1L) return k + "st";
/*  79 */     if (k % 10L == 2L) return k + "nd";
/*  80 */     if (k % 10L == 3L) return k + "rd";
/*  81 */     return k + "th";
/*     */   }
/*     */ 
/*     */   public static String GetItemName(int id)
/*     */   {
/*  87 */     if (id == -1) return "Random";
/*  88 */     if (id == -2) return "Fireworks";
/*     */ 
/*  90 */     if (id == 356) return "redstone repeater";
/*  91 */     if (id == 52) return "pig spawner";
/*     */     try
/*     */     {
/*  94 */       String name = Material.getMaterial(id).toString().toLowerCase().replace('_', ' ');
/*  95 */       if (name.equals("mycel")) name = "mycelium";
/*  96 */       if (name.equals("diode"));
/*  96 */       return "redstone repeater";
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/*     */     }
/*     */ 
/* 102 */     return "Item ID " + id;
/*     */   }
/*     */ 
/*     */   public static String LocString(Location loc)
/*     */   {
/* 108 */     if (loc == null) return "NULL";
/* 109 */     return loc.getWorld().getName() + "(" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")";
/*     */   }
/*     */ 
/*     */   public static String ConcatArgs(String[] args, int startIdx)
/*     */   {
/* 115 */     StringBuilder sb = new StringBuilder();
/* 116 */     for (int i = startIdx; i < args.length; i++)
/*     */     {
/* 118 */       if (sb.length() > 0) sb.append(" ");
/* 119 */       sb.append(args[i]);
/*     */     }
/* 121 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String CircleText(String txt)
/*     */   {
/* 128 */     StringBuffer buf = new StringBuffer();
/*     */ 
/* 130 */     for (int i = 0; i < txt.length(); i++)
/*     */     {
/* 132 */       char ch = txt.charAt(i);
/* 133 */       if ((ch >= 'A') && (ch <= 'Z')) ch = (char)(9398 + (ch - 'A'));
/* 134 */       else if ((ch >= 'a') && (ch <= 'z')) ch = (char)(9424 + (ch - 'a'));
/* 135 */       else if ((ch >= '1') && (ch <= '9')) ch = (char)(9312 + (ch - '1'));
/* 136 */       else if (ch == '0') ch = '⓪';
/* 137 */       else if (ch == '_') ch = ' ';
/* 138 */       buf.append(ch);
/*     */     }
/* 140 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public static String StringReplace(String src, String key, String val)
/*     */   {
/* 145 */     int idx = src.indexOf(key);
/* 146 */     if (idx < 0) return src;
/* 147 */     return src.substring(0, idx) + val + src.substring(idx + key.length());
/*     */   }
/*     */ 
/*     */   public static String SpecialTranslate(String txt) {
/* 151 */     String res = txt;
/* 152 */     while (res.indexOf("{star1}") >= 0) res = StringReplace(res, "{star1}", "⚝");
/* 153 */     while (res.indexOf("{star2}") >= 0) res = StringReplace(res, "{star2}", "★");
/* 154 */     while (res.indexOf("{star3}") >= 0) res = StringReplace(res, "{star3}", "☆");
/* 155 */     while (res.indexOf("{space}") >= 0) res = StringReplace(res, "{space}", " ");
/* 156 */     while (res.indexOf("{_}") >= 0) res = StringReplace(res, "{_}", " ");
/*     */ 
/* 158 */     while (res.indexOf("{heart1}") >= 0) res = StringReplace(res, "{heart1}", "❤");
/* 159 */     while (res.indexOf("{heart2}") >= 0) res = StringReplace(res, "{heart2}", "♡");
/* 160 */     while (res.indexOf("{heart3}") >= 0) res = StringReplace(res, "{heart3}", "♥");
/* 161 */     while (res.indexOf("{cross1}") >= 0) res = StringReplace(res, "{cross1}", "✞");
/* 162 */     while (res.indexOf("{cross2}") >= 0) res = StringReplace(res, "{cross2}", "♱");
/* 163 */     while (res.indexOf("{cross3}") >= 0) res = StringReplace(res, "{cross3}", "♰");
/* 164 */     while (res.indexOf("{diamond1}") >= 0) res = StringReplace(res, "{diamond1}", "♦");
/* 165 */     while (res.indexOf("{diamond2}") >= 0) res = StringReplace(res, "{diamond2}", "♢");
/* 166 */     while (res.indexOf("{radio}") >= 0) res = StringReplace(res, "{radio}", "☢");
/* 167 */     while (res.indexOf("{bio}") >= 0) res = StringReplace(res, "{bio}", "☣");
/* 168 */     while (res.indexOf("{ankh}") >= 0) res = StringReplace(res, "{ankh}", "☥");
/* 169 */     while (res.indexOf("{peace}") >= 0) res = StringReplace(res, "{peace}", "☮");
/* 170 */     while (res.indexOf("{yinyang}") >= 0) res = StringReplace(res, "{yinyang}", "☯");
/* 171 */     while (res.indexOf("{male}") >= 0) res = StringReplace(res, "{male}", "♂");
/* 172 */     while (res.indexOf("{female}") >= 0) res = StringReplace(res, "{female}", "♀");
/* 173 */     while (res.indexOf("{aquarius}") >= 0) res = StringReplace(res, "{aquarius}", "♒");
/* 174 */     while (res.indexOf("{music1}") >= 0) res = StringReplace(res, "{music1}", "♩");
/* 175 */     while (res.indexOf("{music2}") >= 0) res = StringReplace(res, "{music2}", "♪");
/* 176 */     while (res.indexOf("{music3}") >= 0) res = StringReplace(res, "{music3}", "♫");
/* 177 */     while (res.indexOf("{music4}") >= 0) res = StringReplace(res, "{music4}", "♬");
/* 178 */     while (res.indexOf("{music5}") >= 0) res = StringReplace(res, "{music5}", "♭");
/* 179 */     while (res.indexOf("{anchor}") >= 0) res = StringReplace(res, "{anchor}", "⚓");
/* 180 */     while (res.indexOf("{atom}") >= 0) res = StringReplace(res, "{atom}", "⚛");
/* 181 */     while (res.indexOf("{bolt}") >= 0) res = StringReplace(res, "{bolt}", "⚡");
/* 182 */     while (res.indexOf("{plane}") >= 0) res = StringReplace(res, "{plane}", "✈");
/* 183 */     while (res.indexOf("{flower1}") >= 0) res = StringReplace(res, "{flower1}", "❀");
/* 184 */     while (res.indexOf("{flower2}") >= 0) res = StringReplace(res, "{flower2}", "❃");
/* 185 */     while (res.indexOf("{flower3}") >= 0) res = StringReplace(res, "{flower3}", "✼");
/*     */ 
/* 187 */     return res;
/*     */   }
/*     */ 
/*     */   public static boolean IsNumeric(String str)
/*     */   {
/*     */     try
/*     */     {
/* 195 */       d = Double.parseDouble(str);
/*     */     }
/*     */     catch (NumberFormatException nfe)
/*     */     {
/*     */       double d;
/* 199 */       return false;
/*     */     }
/* 201 */     return true;
/*     */   }
/*     */ 
/*     */   public static String RainbowString(String str)
/*     */   {
/* 206 */     return RainbowString(str, "");
/*     */   }
/*     */ 
/*     */   public static String RainbowString(String str, String ctl)
/*     */   {
/* 211 */     if (ctl.equalsIgnoreCase("x")) return str;
/*     */ 
/* 213 */     StringBuilder sb = new StringBuilder();
/* 214 */     int idx = 0;
/* 215 */     boolean useBold = ctl.indexOf('b') >= 0;
/* 216 */     boolean useItalics = ctl.indexOf('i') >= 0;
/* 217 */     boolean useUnderline = ctl.indexOf('u') >= 0;
/*     */ 
/* 219 */     for (int i = 0; i < str.length(); i++)
/*     */     {
/* 222 */       if (idx % 6 == 0) sb.append(ChatColor.RED);
/* 223 */       else if (idx % 6 == 1) sb.append(ChatColor.GOLD);
/* 224 */       else if (idx % 6 == 2) sb.append(ChatColor.YELLOW);
/* 225 */       else if (idx % 6 == 3) sb.append(ChatColor.GREEN);
/* 226 */       else if (idx % 6 == 4) sb.append(ChatColor.AQUA);
/* 227 */       else if (idx % 6 == 5) sb.append(ChatColor.LIGHT_PURPLE);
/*     */ 
/* 229 */       if (useBold) sb.append(ChatColor.BOLD);
/* 230 */       if (useItalics) sb.append(ChatColor.ITALIC);
/* 231 */       if (useUnderline) sb.append(ChatColor.UNDERLINE);
/*     */ 
/* 233 */       sb.append(str.charAt(i));
/*     */ 
/* 235 */       if (str.charAt(i) != ' ') idx++;
/*     */     }
/*     */ 
/* 238 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String GetSkullOwnerFromBlock(Block block)
/*     */   {
/* 243 */     String retValue = "";
/* 244 */     for (ItemStack is : block.getDrops())
/*     */     {
/* 246 */       SkullMeta sm = (SkullMeta)is.getItemMeta();
/* 247 */       if (sm != null)
/*     */       {
/* 249 */         retValue = sm.getOwner();
/* 250 */         if (retValue != null) break; retValue = "";
/* 251 */         break;
/*     */       }
/*     */     }
/* 254 */     return retValue;
/*     */   }
/*     */ 
/*     */   public static String GetSkullOwnerFromItemStack(ItemStack is)
/*     */   {
/* 259 */     if (is == null) return "";
/* 260 */     SkullMeta sm = (SkullMeta)is.getItemMeta();
/* 261 */     if (sm == null) return "";
/* 262 */     String headName = sm.getOwner();
/* 263 */     if (headName == null) return "";
/* 264 */     return headName;
/*     */   }
/*     */ 
/*     */   public static String GetDateString(Date dt)
/*     */   {
/* 270 */     return String.format("%d/%d/%4d", new Object[] { Integer.valueOf(dt.getMonth() + 1), Integer.valueOf(dt.getDate()), Integer.valueOf(dt.getYear() + 1900) });
/*     */   }
/*     */ 
/*     */   public static String GetDateString()
/*     */   {
/* 275 */     return GetDateString(new Date());
/*     */   }
/*     */ 
/*     */   public static String GetTimeString()
/*     */   {
/* 281 */     Date dt = new Date();
/* 282 */     int hr = dt.getHours();
/* 283 */     int min = dt.getMinutes();
/*     */ 
/* 285 */     if (hr < 12) return String.format("%02d:%02dam", new Object[] { Integer.valueOf(hr == 0 ? 12 : hr), Integer.valueOf(min) });
/* 286 */     hr -= 12;
/* 287 */     return String.format("%02d:%02dpm", new Object[] { Integer.valueOf(hr == 0 ? 12 : hr), Integer.valueOf(min) });
/*     */   }
/*     */ 
/*     */   public static String JustCharsAndDigits(String parm)
/*     */   {
/* 293 */     StringBuilder res = new StringBuilder();
/* 294 */     for (int i = 0; i < parm.length(); i++)
/*     */     {
/* 296 */       char ch = parm.charAt(i);
/* 297 */       if (IsCharLetterOrDigit(ch)) res.append(ch);
/*     */     }
/* 299 */     return res.toString();
/*     */   }
/*     */ 
/*     */   public static String TranslateColorString(String parm, boolean IsOp)
/*     */   {
/* 305 */     return TranslateColorString(parm, IsOp, false);
/*     */   }
/*     */ 
/*     */   public static String TranslateColorString(String parm, boolean IsOp, boolean fAllowSpaces)
/*     */   {
/* 311 */     if (IsOp) parm = SpecialTranslate(parm);
/*     */ 
/* 313 */     StringBuilder res = new StringBuilder();
/* 314 */     boolean pending = false;
/* 315 */     for (int i = 0; i < parm.length(); i++)
/*     */     {
/* 317 */       char ch = parm.charAt(i);
/* 318 */       if (ch == '&') { pending = true; }
/* 321 */       else if (pending)
/*     */       {
/* 323 */         pending = false;
/* 324 */         if ((ch == '0') && (IsOp)) res.append(ChatColor.BLACK);
/* 325 */         else if (ch == '1') res.append(ChatColor.DARK_BLUE);
/* 326 */         else if (ch == '2') res.append(ChatColor.DARK_GREEN);
/* 327 */         else if (ch == '3') res.append(ChatColor.DARK_AQUA);
/* 328 */         else if ((ch == '4') && (IsOp)) res.append(ChatColor.DARK_RED);
/* 329 */         else if (ch == '5') res.append(ChatColor.DARK_PURPLE);
/* 330 */         else if (ch == '6') res.append(ChatColor.GOLD);
/* 331 */         else if (ch == '7') res.append(ChatColor.GRAY);
/* 332 */         else if (ch == '8') res.append(ChatColor.DARK_GRAY);
/* 333 */         else if (ch == '9') res.append(ChatColor.BLUE);
/* 334 */         else if (ch == 'a') res.append(ChatColor.GREEN);
/* 335 */         else if (ch == 'b') res.append(ChatColor.AQUA);
/* 336 */         else if ((ch == 'c') && (IsOp)) res.append(ChatColor.RED);
/* 337 */         else if (ch == 'd') res.append(ChatColor.LIGHT_PURPLE);
/* 338 */         else if (ch == 'e') res.append(ChatColor.YELLOW);
/* 339 */         else if (ch == 'f') res.append(ChatColor.WHITE);
/* 340 */         else if (ch == 'l') res.append(ChatColor.BOLD);
/* 341 */         else if (ch == 'm') res.append(ChatColor.STRIKETHROUGH);
/* 342 */         else if ((ch == 'k') && (IsOp)) res.append(ChatColor.MAGIC);
/* 343 */         else if ((ch == 'n') && (IsOp)) res.append(ChatColor.UNDERLINE);
/* 344 */         else if (ch == 'o') res.append(ChatColor.ITALIC);
/*     */ 
/*     */       }
/* 348 */       else if (IsOp)
/*     */       {
/* 350 */         res.append(ch);
/*     */       }
/* 354 */       else if (IsCharLetterOrDigit(ch)) { res.append(ch);
/* 355 */       } else if ((fAllowSpaces) && (ch == ' ')) { res.append(ch); }
/*     */ 
/*     */ 
/*     */     }
/*     */ 
/* 360 */     return res.toString();
/*     */   }
/*     */ 
/*     */   public static boolean IsCharLetterOrDigit(char ch)
/*     */   {
/* 367 */     if (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))) return true;
/*     */ 
/* 369 */     if ((ch >= '0') && (ch <= '9')) return true;
/* 370 */     return false;
/*     */   }
/*     */ 
/*     */   public static void PlaySoundToPlayer(Player p, Sound snd)
/*     */   {
/* 377 */     p.playSound(p.getLocation(), snd, 1.0F, 1.0F);
/*     */   }
/*     */ 
/*     */   public static String TimeDeltaString(long ms)
/*     */   {
/* 383 */     int secs = (int)(ms / 1000L % 60L);
/* 384 */     int mins = (int)(ms / 1000L / 60L % 60L);
/* 385 */     int hours = (int)(ms / 1000L / 60L / 60L % 24L);
/* 386 */     int days = (int)(ms / 1000L / 60L / 60L / 24L);
/* 387 */     return String.format("%02dd %02dh %02dm %02ds", new Object[] { Integer.valueOf(days), Integer.valueOf(hours), Integer.valueOf(mins), Integer.valueOf(secs) });
/*     */   }
/*     */ 
/*     */   public static String TimeDeltaString_JustMinutesSecs(long ms) {
/* 391 */     int secs = (int)(ms / 1000L % 60L);
/* 392 */     int mins = (int)(ms / 1000L / 60L % 60L);
/* 393 */     return String.format("%02dm %02ds", new Object[] { Integer.valueOf(mins), Integer.valueOf(secs) });
/*     */   }
/*     */ 
/*     */   public static String TextAlign(String str, int padLen)
/*     */   {
/* 399 */     StringBuffer tgt = new StringBuffer();
/* 400 */     for (int i = 0; i < str.length(); i++)
/*     */     {
/* 402 */       char ch = str.charAt(i);
/*     */ 
/* 404 */       tgt.append(ch);
/*     */ 
/* 406 */       if (ch == 'i') tgt.append("..");
/* 407 */       else if (ch == 't') tgt.append(".");
/* 408 */       else if (ch == 'I') tgt.append(".");
/* 409 */       else if (ch == 'l') tgt.append(".");
/*     */     }
/* 411 */     for (int i = str.length(); i < padLen; i++) tgt.append("...");
/* 412 */     return tgt.toString();
/*     */   }
/*     */ 
/*     */   public static String TextAlignTrailer(String str, int padLen)
/*     */   {
/* 417 */     StringBuffer tgt = new StringBuffer();
/* 418 */     for (int i = 0; i < str.length(); i++)
/*     */     {
/* 420 */       char ch = str.charAt(i);
/* 421 */       if (ch == 'i') tgt.append("..");
/* 422 */       else if (ch == 't') tgt.append(".");
/* 423 */       else if (ch == 'I') tgt.append(".");
/* 424 */       else if (ch == 'l') tgt.append(".");
/* 425 */       else if (ch == ':') tgt.append("..");
/*     */     }
/*     */ 
/* 428 */     for (int i = str.length(); i < padLen; i++) tgt.append("...");
/* 429 */     return tgt.toString();
/*     */   }
/*     */ 
/*     */   public static void SavePreferences()
/*     */   {
/*     */     try
/*     */     {
/* 437 */       File file = new File(joeDirectory, "UserPrefs.dat");
/*     */ 
/* 439 */       FileOutputStream f = new FileOutputStream(file);
/* 440 */       ObjectOutputStream s = new ObjectOutputStream(f);
/*     */ 
/* 442 */       s.writeObject(PlayerSortPreference);
/* 443 */       s.close();
/*     */     }
/*     */     catch (Throwable exc) {
/* 446 */       System.out.println("**********************************************");
/* 447 */       System.out.println("SavePreferences: " + exc.toString());
/* 448 */       System.out.println("**********************************************");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void LoadPreferences()
/*     */   {
/*     */     try
/*     */     {
/* 456 */       File file = new File(joeDirectory, "UserPrefs.dat");
/* 457 */       FileInputStream f = new FileInputStream(file);
/* 458 */       ObjectInputStream s = new ObjectInputStream(f);
/* 459 */       PlayerSortPreference = (ConcurrentHashMap)s.readObject();
/* 460 */       s.close();
/*     */     }
/*     */     catch (Throwable exc)
/*     */     {
/* 464 */       ConsoleMsg("Starting New User Sorting Preference file...");
/* 465 */       PlayerSortPreference = new ConcurrentHashMap();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean DoesPlayerWantSorting(Player p)
/*     */   {
/* 471 */     if (p == null) return false;
/* 472 */     Boolean res = (Boolean)PlayerSortPreference.get(p.getName());
/* 473 */     if (res == null) return true;
/* 474 */     return false;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\JoeInventorySort.jar
 * Qualified Name:     joe.crump.isort.JoeUtils
 * JD-Core Version:    0.6.2
 */