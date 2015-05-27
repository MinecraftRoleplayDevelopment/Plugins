/*     */ package com.comphenix.protocol.wrappers.nbt;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassReader;
/*     */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*     */ import com.comphenix.protocol.reflect.compiler.EmptyClassVisitor;
/*     */ import com.comphenix.protocol.reflect.compiler.EmptyMethodVisitor;
/*     */ import com.comphenix.protocol.utility.EnhancerFactory;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import org.bukkit.block.BlockState;
/*     */ 
/*     */ class TileEntityAccessor<T extends BlockState>
/*     */ {
/*  34 */   private static final TileEntityAccessor<BlockState> EMPTY_ACCESSOR = new TileEntityAccessor();
/*     */ 
/*  39 */   private static final ConcurrentMap<Class<?>, TileEntityAccessor<?>> cachedAccessors = Maps.newConcurrentMap();
/*     */   private FieldAccessor tileEntityField;
/*     */   private MethodAccessor readCompound;
/*     */   private MethodAccessor writeCompound;
/*     */   private boolean writeDetected;
/*     */   private boolean readDetected;
/*     */ 
/*     */   private TileEntityAccessor()
/*     */   {
/*     */   }
/*     */ 
/*     */   private TileEntityAccessor(FieldAccessor tileEntityField, T state)
/*     */   {
/*  61 */     if (tileEntityField != null) {
/*  62 */       this.tileEntityField = tileEntityField;
/*  63 */       Class type = tileEntityField.getField().getType();
/*     */       try
/*     */       {
/*  67 */         findMethodsUsingASM(type);
/*     */       }
/*     */       catch (IOException ex1) {
/*     */         try {
/*  71 */           findMethodUsingCGLib(state);
/*     */         } catch (Exception ex2) {
/*  73 */           throw new RuntimeException("Cannot find read/write methods in " + type, ex2);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  78 */       if (this.readCompound == null)
/*  79 */         throw new RuntimeException("Unable to find read method in " + type);
/*  80 */       if (this.writeCompound == null)
/*  81 */         throw new RuntimeException("Unable to find write method in " + type);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void findMethodsUsingASM(final Class<?> tileEntityClass)
/*     */     throws IOException
/*     */   {
/*  92 */     final Class nbtCompoundClass = MinecraftReflection.getNBTCompoundClass();
/*  93 */     ClassReader reader = new ClassReader(tileEntityClass.getCanonicalName());
/*     */ 
/*  95 */     final String tagCompoundName = getJarName(MinecraftReflection.getNBTCompoundClass());
/*  96 */     final String expectedDesc = "(L" + tagCompoundName + ";)V";
/*     */ 
/*  98 */     reader.accept(new EmptyClassVisitor()
/*     */     {
/*     */       public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
/* 101 */         final String methodName = name;
/*     */ 
/* 104 */         if (expectedDesc.equals(desc)) {
/* 105 */           return new EmptyMethodVisitor()
/*     */           {
/*     */             private int readMethods;
/*     */             private int writeMethods;
/*     */ 
/*     */             public void visitMethodInsn(int opcode, String owner, String name, String desc) {
/* 112 */               if ((opcode == 182) && (TileEntityAccessor.1.this.val$tagCompoundName.equals(owner)) && (desc.startsWith("(Ljava/lang/String")))
/*     */               {
/* 117 */                 if (desc.endsWith(")V"))
/* 118 */                   this.writeMethods += 1;
/*     */                 else
/* 120 */                   this.readMethods += 1;
/*     */               }
/*     */             }
/*     */ 
/*     */             public void visitEnd()
/*     */             {
/* 127 */               if (this.readMethods > this.writeMethods)
/* 128 */                 TileEntityAccessor.this.readCompound = Accessors.getMethodAccessor(TileEntityAccessor.1.this.val$tileEntityClass, methodName, new Class[] { TileEntityAccessor.1.this.val$nbtCompoundClass });
/* 129 */               else if (this.writeMethods > this.readMethods) {
/* 130 */                 TileEntityAccessor.this.writeCompound = Accessors.getMethodAccessor(TileEntityAccessor.1.this.val$tileEntityClass, methodName, new Class[] { TileEntityAccessor.1.this.val$nbtCompoundClass });
/*     */               }
/* 132 */               super.visitEnd();
/*     */             }
/*     */           };
/*     */         }
/* 136 */         return null;
/*     */       }
/*     */     }
/*     */     , 0);
/*     */   }
/*     */ 
/*     */   private void findMethodUsingCGLib(T blockState)
/*     */     throws IOException
/*     */   {
/* 147 */     Class nbtCompoundClass = MinecraftReflection.getNBTCompoundClass();
/*     */ 
/* 150 */     Enhancer enhancer = EnhancerFactory.getInstance().createEnhancer();
/* 151 */     enhancer.setSuperclass(nbtCompoundClass);
/* 152 */     enhancer.setCallback(new MethodInterceptor()
/*     */     {
/*     */       public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/* 155 */         if (method.getReturnType().equals(Void.TYPE))
/*     */         {
/* 157 */           TileEntityAccessor.this.writeDetected = true;
/*     */         }
/*     */         else {
/* 160 */           TileEntityAccessor.this.readDetected = true;
/*     */         }
/* 162 */         throw new RuntimeException("Stop execution.");
/*     */       }
/*     */     });
/* 165 */     Object compound = enhancer.create();
/* 166 */     Object tileEntity = this.tileEntityField.get(blockState);
/*     */ 
/* 169 */     for (Method method : FuzzyReflection.fromObject(tileEntity, true).getMethodListByParameters(Void.TYPE, new Class[] { nbtCompoundClass }))
/*     */     {
/*     */       try
/*     */       {
/* 173 */         this.readDetected = false;
/* 174 */         this.writeDetected = false;
/* 175 */         method.invoke(tileEntity, new Object[] { compound });
/*     */       }
/*     */       catch (Exception e) {
/* 178 */         if (this.readDetected)
/* 179 */           this.readCompound = Accessors.getMethodAccessor(method, true);
/* 180 */         if (this.writeDetected)
/* 181 */           this.writeCompound = Accessors.getMethodAccessor(method, true);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getJarName(Class<?> clazz)
/*     */   {
/* 192 */     return clazz.getCanonicalName().replace('.', '/');
/*     */   }
/*     */ 
/*     */   public NbtCompound readBlockState(T state)
/*     */   {
/* 201 */     NbtCompound output = NbtFactory.ofCompound("");
/* 202 */     Object tileEntity = this.tileEntityField.get(state);
/*     */ 
/* 205 */     this.writeCompound.invoke(tileEntity, new Object[] { NbtFactory.fromBase(output).getHandle() });
/* 206 */     return output;
/*     */   }
/*     */ 
/*     */   public void writeBlockState(T state, NbtCompound compound)
/*     */   {
/* 215 */     Object tileEntity = this.tileEntityField.get(state);
/*     */ 
/* 218 */     this.readCompound.invoke(tileEntity, new Object[] { NbtFactory.fromBase(compound).getHandle() });
/*     */   }
/*     */ 
/*     */   public static <T extends BlockState> TileEntityAccessor<T> getAccessor(T state)
/*     */   {
/* 228 */     Class craftBlockState = state.getClass();
/* 229 */     TileEntityAccessor accessor = (TileEntityAccessor)cachedAccessors.get(craftBlockState);
/*     */ 
/* 232 */     if (accessor == null) {
/* 233 */       TileEntityAccessor created = null;
/* 234 */       FieldAccessor field = null;
/*     */       try
/*     */       {
/* 237 */         field = Accessors.getFieldAccessor(craftBlockState, MinecraftReflection.getTileEntityClass(), true);
/*     */       } catch (Exception e) {
/* 239 */         created = EMPTY_ACCESSOR;
/*     */       }
/* 241 */       if (field != null) {
/* 242 */         created = new TileEntityAccessor(field, state);
/*     */       }
/* 244 */       accessor = (TileEntityAccessor)cachedAccessors.putIfAbsent(craftBlockState, created);
/*     */ 
/* 247 */       if (accessor == null) {
/* 248 */         accessor = created;
/*     */       }
/*     */     }
/* 251 */     return accessor != EMPTY_ACCESSOR ? accessor : null;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.TileEntityAccessor
 * JD-Core Version:    0.6.2
 */