package org.mcstats;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import me.coder.combatindicator.Z;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class Metrics
{
  private static final int REVISION = 7;
  private static final String BASE_URL = "http://report.mcstats.org";
  private static final String REPORT_URL = "/plugin/%s";
  private static final int PING_INTERVAL = 15;
  private final Plugin plugin;
  private final Set graphs = Collections.synchronizedSet(new HashSet());
  private final YamlConfiguration configuration;
  private final File configurationFile;
  private final String guid;
  private final boolean debug;
  private final Object optOutLock = new Object();
  private volatile BukkitTask task = null;

  public Metrics(Plugin paramPlugin)
  {
    if (paramPlugin == null)
      throw new IllegalArgumentException("Plugin cannot be null");
    this.plugin = paramPlugin;
    this.configurationFile = getConfigFile();
    this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
    this.configuration.addDefault("opt-out", Boolean.valueOf(false));
    this.configuration.addDefault("guid", UUID.randomUUID().toString());
    this.configuration.addDefault("debug", Boolean.valueOf(false));
    if (this.configuration.get("guid", null) == null)
    {
      this.configuration.options().header("http://mcstats.org").copyDefaults(true);
      this.configuration.save(this.configurationFile);
    }
    this.guid = this.configuration.getString("guid");
    this.debug = this.configuration.getBoolean("debug", false);
  }

  public Metrics.Graph createGraph(String paramString)
  {
    if (paramString == null)
      throw new IllegalArgumentException("Graph name cannot be null");
    paramString = new Metrics.Graph(paramString, null);
    this.graphs.add(paramString);
    return paramString;
  }

  public void addGraph(Metrics.Graph paramGraph)
  {
    if (paramGraph == null)
      throw new IllegalArgumentException("Graph cannot be null");
    this.graphs.add(paramGraph);
  }

  public boolean start()
  {
    synchronized (this.optOutLock)
    {
      if (isOptOut())
        return false;
      if (this.task != null)
        return true;
      this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Metrics.1(this), 0L, 18000L);
      return true;
    }
  }

  public boolean isOptOut()
  {
    synchronized (this.optOutLock)
    {
      try
      {
        this.configuration.load(getConfigFile());
      }
      catch (IOException localIOException)
      {
        if (this.debug)
          Bukkit.getLogger().log(Level.INFO, "[Metrics] " + localIOException.getMessage());
        return true;
      }
      catch (InvalidConfigurationException localInvalidConfigurationException)
      {
        if (this.debug)
          Bukkit.getLogger().log(Level.INFO, "[Metrics] " + localInvalidConfigurationException.getMessage());
        return true;
      }
      return this.configuration.getBoolean("opt-out", false);
    }
  }

  public void enable()
  {
    synchronized (this.optOutLock)
    {
      if (isOptOut())
      {
        this.configuration.set("opt-out", Boolean.valueOf(false));
        this.configuration.save(this.configurationFile);
      }
      if (this.task == null)
        start();
      return;
    }
  }

  public void disable()
  {
    synchronized (this.optOutLock)
    {
      if (!isOptOut())
      {
        this.configuration.set("opt-out", Boolean.valueOf(true));
        this.configuration.save(this.configurationFile);
      }
      if (this.task != null)
      {
        this.task.cancel();
        this.task = null;
      }
      return;
    }
  }

  public File getConfigFile()
  {
    File localFile = this.plugin.getDataFolder().getParentFile();
    return new File(new File(localFile, "PluginMetrics"), "config.yml");
  }

  private void postPlugin(boolean arg1)
  {
    Object localObject3 = (localObject1 = this.plugin.getDescription()).getName();
    boolean bool = Bukkit.getServer().getOnlineMode();
    Object localObject1 = ((PluginDescriptionFile)localObject1).getVersion();
    Object localObject5 = Bukkit.getVersion();
    int j = Z.b();
    StringBuilder localStringBuilder;
    (localStringBuilder = new StringBuilder(1024)).append('{');
    appendJSONPair(localStringBuilder, "guid", this.guid);
    appendJSONPair(localStringBuilder, "plugin_version", (String)localObject1);
    appendJSONPair(localStringBuilder, "server_version", (String)localObject5);
    appendJSONPair(localStringBuilder, "players_online", Integer.toString(j));
    localObject1 = System.getProperty("os.name");
    localObject5 = System.getProperty("os.arch");
    Object localObject6 = System.getProperty("os.version");
    Object localObject7 = System.getProperty("java.version");
    int k = Runtime.getRuntime().availableProcessors();
    if (((String)localObject5).equals("amd64"))
      localObject5 = "x86_64";
    appendJSONPair(localStringBuilder, "osname", (String)localObject1);
    appendJSONPair(localStringBuilder, "osarch", (String)localObject5);
    appendJSONPair(localStringBuilder, "osversion", (String)localObject6);
    appendJSONPair(localStringBuilder, "cores", Integer.toString(k));
    appendJSONPair(localStringBuilder, "auth_mode", bool ? "1" : "0");
    appendJSONPair(localStringBuilder, "java_version", (String)localObject7);
    if (??? != 0)
      appendJSONPair(localStringBuilder, "ping", "1");
    if (this.graphs.size() > 0)
      synchronized (this.graphs)
      {
        localStringBuilder.append(',');
        localStringBuilder.append('"');
        localStringBuilder.append("graphs");
        localStringBuilder.append('"');
        localStringBuilder.append(':');
        localStringBuilder.append('{');
        int i = 1;
        localObject4 = this.graphs.iterator();
        while (((Iterator)localObject4).hasNext())
        {
          localObject5 = (Metrics.Graph)((Iterator)localObject4).next();
          (localObject6 = new StringBuilder()).append('{');
          localObject7 = ((Metrics.Graph)localObject5).getPlotters().iterator();
          while (((Iterator)localObject7).hasNext())
          {
            localObject8 = (Metrics.Plotter)((Iterator)localObject7).next();
            appendJSONPair((StringBuilder)localObject6, ((Metrics.Plotter)localObject8).getColumnName(), Integer.toString(((Metrics.Plotter)localObject8).getValue()));
          }
          ((StringBuilder)localObject6).append('}');
          if (i == 0)
            localStringBuilder.append(',');
          localStringBuilder.append(escapeJSON(((Metrics.Graph)localObject5).getName()));
          localStringBuilder.append(':');
          localStringBuilder.append((CharSequence)localObject6);
          i = 0;
        }
        localStringBuilder.append('}');
      }
    localStringBuilder.append('}');
    ??? = new URL("http://report.mcstats.org" + String.format("/plugin/%s", new Object[] { urlEncode((String)localObject3) }));
    URLConnection localURLConnection;
    if (isMineshafterPresent())
      localURLConnection = ???.openConnection(Proxy.NO_PROXY);
    else
      localURLConnection = ???.openConnection();
    Object localObject4 = localStringBuilder.toString().getBytes();
    localObject5 = gzip(localStringBuilder.toString());
    localURLConnection.addRequestProperty("User-Agent", "MCStats/7");
    localURLConnection.addRequestProperty("Content-Type", "application/json");
    localURLConnection.addRequestProperty("Content-Encoding", "gzip");
    localURLConnection.addRequestProperty("Content-Length", Integer.toString(localObject5.length));
    localURLConnection.addRequestProperty("Accept", "application/json");
    localURLConnection.addRequestProperty("Connection", "close");
    localURLConnection.setDoOutput(true);
    if (this.debug)
      System.out.println("[Metrics] Prepared request for " + (String)localObject3 + " uncompressed=" + localObject4.length + " compressed=" + localObject5.length);
    (localObject6 = localURLConnection.getOutputStream()).write((byte[])localObject5);
    ((OutputStream)localObject6).flush();
    Object localObject8 = (localObject7 = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream()))).readLine();
    ((OutputStream)localObject6).close();
    ((BufferedReader)localObject7).close();
    if ((localObject8 == null) || (((String)localObject8).startsWith("ERR")) || (((String)localObject8).startsWith("7")))
    {
      if (localObject8 == null)
        localObject8 = "null";
      else if (((String)localObject8).startsWith("7"))
        localObject8 = ((String)localObject8).substring(((String)localObject8).startsWith("7,") ? 2 : 1);
      throw new IOException((String)localObject8);
    }
    if ((((String)localObject8).equals("1")) || (((String)localObject8).contains("This is your first update this hour")))
      synchronized (this.graphs)
      {
        ??? = this.graphs.iterator();
        while (???.hasNext())
        {
          localObject3 = (localObject3 = (Metrics.Graph)???.next()).getPlotters().iterator();
          while (((Iterator)localObject3).hasNext())
            (localObject4 = (Metrics.Plotter)((Iterator)localObject3).next()).reset();
        }
        return;
      }
  }

  public static byte[] gzip(String paramString)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    GZIPOutputStream localGZIPOutputStream = null;
    try
    {
      (localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream)).write(paramString.getBytes("UTF-8"));
      try
      {
        localGZIPOutputStream.close();
      }
      catch (IOException localIOException1)
      {
      }
    }
    catch (IOException localIOException2)
    {
    }
    finally
    {
      if (localGZIPOutputStream != null)
        try
        {
          localGZIPOutputStream.close();
        }
        catch (IOException localIOException4)
        {
        }
    }
    return localByteArrayOutputStream.toByteArray();
  }

  private boolean isMineshafterPresent()
  {
    try
    {
      Class.forName("mineshafter.MineServer");
      return true;
    }
    catch (Exception localException)
    {
    }
    return false;
  }

  private static void appendJSONPair(StringBuilder paramStringBuilder, String paramString1, String paramString2)
  {
    int i = 0;
    try
    {
      if ((paramString2.equals("0")) || (!paramString2.endsWith("0")))
        i = 1;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      i = 0;
    }
    if (paramStringBuilder.charAt(paramStringBuilder.length() - 1) != '{')
      paramStringBuilder.append(',');
    paramStringBuilder.append(escapeJSON(paramString1));
    paramStringBuilder.append(':');
    if (i != 0)
    {
      paramStringBuilder.append(paramString2);
      return;
    }
    paramStringBuilder.append(escapeJSON(paramString2));
  }

  private static String escapeJSON(String paramString)
  {
    StringBuilder localStringBuilder;
    (localStringBuilder = new StringBuilder()).append('"');
    for (int i = 0; i < paramString.length(); i++)
    {
      char c;
      switch (c = paramString.charAt(i))
      {
      case '"':
      case '\\':
        localStringBuilder.append('\\');
        localStringBuilder.append(c);
        break;
      case '\b':
        localStringBuilder.append("\\b");
        break;
      case '\t':
        localStringBuilder.append("\\t");
        break;
      case '\n':
        localStringBuilder.append("\\n");
        break;
      case '\r':
        localStringBuilder.append("\\r");
        break;
      default:
        String str;
        if (c < ' ')
        {
          str = "000" + Integer.toHexString(c);
          localStringBuilder.append("\\u" + str.substring(str.length() - 4));
        }
        else
        {
          localStringBuilder.append(str);
        }
        break;
      }
    }
    localStringBuilder.append('"');
    return localStringBuilder.toString();
  }

  private static String urlEncode(String paramString)
  {
    return URLEncoder.encode(paramString, "UTF-8");
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     org.mcstats.Metrics
 * JD-Core Version:    0.6.2
 */