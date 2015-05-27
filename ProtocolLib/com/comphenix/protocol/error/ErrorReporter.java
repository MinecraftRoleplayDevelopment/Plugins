package com.comphenix.protocol.error;

import org.bukkit.plugin.Plugin;

public abstract interface ErrorReporter
{
  public abstract void reportMinimal(Plugin paramPlugin, String paramString, Throwable paramThrowable);

  public abstract void reportMinimal(Plugin paramPlugin, String paramString, Throwable paramThrowable, Object[] paramArrayOfObject);

  public abstract void reportDebug(Object paramObject, Report paramReport);

  public abstract void reportDebug(Object paramObject, Report.ReportBuilder paramReportBuilder);

  public abstract void reportWarning(Object paramObject, Report paramReport);

  public abstract void reportWarning(Object paramObject, Report.ReportBuilder paramReportBuilder);

  public abstract void reportDetailed(Object paramObject, Report paramReport);

  public abstract void reportDetailed(Object paramObject, Report.ReportBuilder paramReportBuilder);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.error.ErrorReporter
 * JD-Core Version:    0.6.2
 */