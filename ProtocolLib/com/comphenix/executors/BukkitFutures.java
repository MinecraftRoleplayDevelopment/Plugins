/*     */ package com.comphenix.executors;
/*     */ 
/*     */ import com.google.common.util.concurrent.ListenableFuture;
/*     */ import com.google.common.util.concurrent.SettableFuture;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.bukkit.event.Event;
/*     */ import org.bukkit.event.EventException;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.HandlerList;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.plugin.EventExecutor;
/*     */ import org.bukkit.plugin.IllegalPluginAccessException;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.RegisteredListener;
/*     */ 
/*     */ public class BukkitFutures
/*     */ {
/*  21 */   private static Listener EMPTY_LISTENER = new Listener() { } ;
/*     */ 
/*     */   public static <TEvent extends Event> ListenableFuture<TEvent> nextEvent(Plugin plugin, Class<TEvent> eventClass)
/*     */   {
/*  29 */     return nextEvent(plugin, eventClass, EventPriority.NORMAL, false);
/*     */   }
/*     */ 
/*     */   public static <TEvent extends Event> ListenableFuture<TEvent> nextEvent(Plugin plugin, Class<TEvent> eventClass, EventPriority priority, boolean ignoreCancelled)
/*     */   {
/*  41 */     final HandlerList list = getHandlerList(eventClass);
/*  42 */     SettableFuture future = SettableFuture.create();
/*     */ 
/*  44 */     EventExecutor executor = new EventExecutor() {
/*  45 */       private final AtomicBoolean once = new AtomicBoolean();
/*     */ 
/*     */       public void execute(Listener listener, Event event)
/*     */         throws EventException
/*     */       {
/*  51 */         if ((!this.val$future.isCancelled()) && (!this.once.getAndSet(true)))
/*  52 */           this.val$future.set(event);
/*     */       }
/*     */     };
/*  56 */     RegisteredListener listener = new RegisteredListener(EMPTY_LISTENER, executor, priority, plugin, ignoreCancelled)
/*     */     {
/*     */       public void callEvent(Event event) throws EventException {
/*  59 */         super.callEvent(event);
/*  60 */         list.unregister(this);
/*     */       }
/*     */     };
/*  65 */     PluginDisabledListener.getListener(plugin).addFuture(future);
/*     */ 
/*  68 */     list.register(listener);
/*  69 */     return future;
/*     */   }
/*     */ 
/*     */   public static void registerEventExecutor(Plugin plugin, Class<? extends Event> eventClass, EventPriority priority, EventExecutor executor)
/*     */   {
/*  80 */     getHandlerList(eventClass).register(new RegisteredListener(EMPTY_LISTENER, executor, priority, plugin, false));
/*     */   }
/*     */ 
/*     */   private static HandlerList getHandlerList(Class<? extends Event> clazz)
/*     */   {
/*  92 */     while ((clazz.getSuperclass() != null) && (Event.class.isAssignableFrom(clazz.getSuperclass()))) {
/*     */       try {
/*  94 */         Method method = clazz.getDeclaredMethod("getHandlerList", new Class[0]);
/*  95 */         method.setAccessible(true);
/*  96 */         return (HandlerList)method.invoke(null, new Object[0]);
/*     */       }
/*     */       catch (NoSuchMethodException e) {
/*  99 */         clazz = clazz.getSuperclass().asSubclass(Event.class);
/*     */       } catch (Exception e) {
/* 101 */         throw new IllegalPluginAccessException(e.getMessage());
/*     */       }
/*     */     }
/* 104 */     throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName());
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.executors.BukkitFutures
 * JD-Core Version:    0.6.2
 */