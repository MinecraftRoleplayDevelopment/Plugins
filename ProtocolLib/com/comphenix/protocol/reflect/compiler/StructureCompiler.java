/*     */ package com.comphenix.protocol.reflect.compiler;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassWriter;
/*     */ import com.comphenix.net.sf.cglib.asm.FieldVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.primitives.Primitives;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public final class StructureCompiler
/*     */ {
/* 102 */   public static final ReportType REPORT_TOO_MANY_GENERATED_CLASSES = new ReportType("Generated too many classes (count: %s)");
/*     */   private static volatile Method defineMethod;
/* 138 */   private Map<StructureKey, Class> compiledCache = new ConcurrentHashMap();
/*     */   private ClassLoader loader;
/* 145 */   private static String PACKAGE_NAME = "com/comphenix/protocol/reflect/compiler";
/* 146 */   private static String SUPER_CLASS = "com/comphenix/protocol/reflect/StructureModifier";
/* 147 */   private static String COMPILED_CLASS = PACKAGE_NAME + "/CompiledStructureModifier";
/* 148 */   private static String FIELD_EXCEPTION_CLASS = "com/comphenix/protocol/reflect/FieldAccessException";
/*     */ 
/*     */   StructureCompiler(ClassLoader loader)
/*     */   {
/* 155 */     this.loader = loader;
/*     */   }
/*     */ 
/*     */   public <TField> boolean lookupClassLoader(StructureModifier<TField> source)
/*     */   {
/* 164 */     StructureKey key = new StructureKey(source);
/*     */ 
/* 167 */     if (this.compiledCache.containsKey(key)) {
/* 168 */       return true;
/*     */     }
/*     */     try
/*     */     {
/* 172 */       String className = getCompiledName(source);
/*     */ 
/* 175 */       Class before = this.loader.loadClass(PACKAGE_NAME.replace('/', '.') + "." + className);
/*     */ 
/* 177 */       if (before != null) {
/* 178 */         this.compiledCache.put(key, before);
/* 179 */         return true;
/*     */       }
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/*     */     }
/*     */ 
/* 186 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized <TField> StructureModifier<TField> compile(StructureModifier<TField> source)
/*     */   {
/* 201 */     if (!isAnyPublic(source.getFields())) {
/* 202 */       return source;
/*     */     }
/*     */ 
/* 205 */     StructureKey key = new StructureKey(source);
/* 206 */     Class compiledClass = (Class)this.compiledCache.get(key);
/*     */ 
/* 208 */     if (!this.compiledCache.containsKey(key)) {
/* 209 */       compiledClass = generateClass(source);
/* 210 */       this.compiledCache.put(key, compiledClass);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 215 */       return (StructureModifier)compiledClass.getConstructor(new Class[] { StructureModifier.class, StructureCompiler.class }).newInstance(new Object[] { source, this });
/*     */     }
/*     */     catch (OutOfMemoryError e)
/*     */     {
/* 220 */       ProtocolLibrary.getErrorReporter().reportWarning(this, Report.newBuilder(REPORT_TOO_MANY_GENERATED_CLASSES).messageParam(new Object[] { Integer.valueOf(this.compiledCache.size()) }));
/*     */ 
/* 223 */       throw e;
/*     */     } catch (IllegalArgumentException e) {
/* 225 */       throw new IllegalStateException("Used invalid parameters in instance creation", e);
/*     */     } catch (SecurityException e) {
/* 227 */       throw new RuntimeException("Security limitation!", e);
/*     */     } catch (InstantiationException e) {
/* 229 */       throw new RuntimeException("Error occured while instancing generated class.", e);
/*     */     } catch (IllegalAccessException e) {
/* 231 */       throw new RuntimeException("Security limitation! Cannot create instance of dynamic class.", e);
/*     */     } catch (InvocationTargetException e) {
/* 233 */       throw new RuntimeException("Error occured while instancing generated class.", e);
/*     */     } catch (NoSuchMethodException e) {
/* 235 */       throw new IllegalStateException("Cannot happen.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getSafeTypeName(Class<?> type)
/*     */   {
/* 245 */     return type.getCanonicalName().replace("[]", "Array").replace(".", "_");
/*     */   }
/*     */ 
/*     */   private String getCompiledName(StructureModifier<?> source)
/*     */   {
/* 254 */     Class targetType = source.getTargetType();
/*     */ 
/* 257 */     return "CompiledStructure$" + getSafeTypeName(targetType) + "$" + getSafeTypeName(source.getFieldType());
/*     */   }
/*     */ 
/*     */   private <TField> Class<?> generateClass(StructureModifier<TField> source)
/*     */   {
/* 269 */     ClassWriter cw = new ClassWriter(0);
/* 270 */     Class targetType = source.getTargetType();
/*     */ 
/* 272 */     String className = getCompiledName(source);
/* 273 */     String targetSignature = Type.getDescriptor(targetType);
/* 274 */     String targetName = targetType.getName().replace('.', '/');
/*     */ 
/* 277 */     cw.visit(50, 33, PACKAGE_NAME + "/" + className, null, COMPILED_CLASS, null);
/*     */ 
/* 280 */     createFields(cw, targetSignature);
/* 281 */     createConstructor(cw, className, targetSignature, targetName);
/* 282 */     createReadMethod(cw, className, source.getFields(), targetSignature, targetName);
/* 283 */     createWriteMethod(cw, className, source.getFields(), targetSignature, targetName);
/* 284 */     cw.visitEnd();
/*     */ 
/* 286 */     byte[] data = cw.toByteArray();
/*     */     try
/*     */     {
/* 290 */       if (defineMethod == null) {
/* 291 */         Method defined = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, [B.class, Integer.TYPE, Integer.TYPE });
/*     */ 
/* 295 */         defined.setAccessible(true);
/* 296 */         defineMethod = defined;
/*     */       }
/*     */ 
/* 300 */       return (Class)defineMethod.invoke(this.loader, new Object[] { null, data, Integer.valueOf(0), Integer.valueOf(data.length) });
/*     */     }
/*     */     catch (SecurityException e)
/*     */     {
/* 309 */       throw new RuntimeException("Cannot use reflection to dynamically load a class.", e);
/*     */     } catch (NoSuchMethodException e) {
/* 311 */       throw new IllegalStateException("Incompatible JVM.", e);
/*     */     } catch (IllegalArgumentException e) {
/* 313 */       throw new IllegalStateException("Cannot call defineMethod - wrong JVM?", e);
/*     */     } catch (IllegalAccessException e) {
/* 315 */       throw new RuntimeException("Security limitation! Cannot dynamically load class.", e);
/*     */     } catch (InvocationTargetException e) {
/* 317 */       throw new RuntimeException("Error occured in code generator.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isAnyPublic(List<Field> fields)
/*     */   {
/* 328 */     for (int i = 0; i < fields.size(); i++) {
/* 329 */       if (isPublic((Field)fields.get(i))) {
/* 330 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 334 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean isPublic(Field field) {
/* 338 */     return Modifier.isPublic(field.getModifiers());
/*     */   }
/*     */ 
/*     */   private boolean isNonFinal(Field field) {
/* 342 */     return !Modifier.isFinal(field.getModifiers());
/*     */   }
/*     */ 
/*     */   private void createFields(ClassWriter cw, String targetSignature) {
/* 346 */     FieldVisitor typedField = cw.visitField(2, "typedTarget", targetSignature, null, null);
/* 347 */     typedField.visitEnd();
/*     */   }
/*     */ 
/*     */   private void createWriteMethod(ClassWriter cw, String className, List<Field> fields, String targetSignature, String targetName)
/*     */   {
/* 352 */     String methodDescriptor = "(ILjava/lang/Object;)L" + SUPER_CLASS + ";";
/* 353 */     String methodSignature = "(ILjava/lang/Object;)L" + SUPER_CLASS + "<Ljava/lang/Object;>;";
/* 354 */     MethodVisitor mv = cw.visitMethod(4, "writeGenerated", methodDescriptor, methodSignature, new String[] { FIELD_EXCEPTION_CLASS });
/*     */ 
/* 356 */     BoxingHelper boxingHelper = new BoxingHelper(mv);
/*     */ 
/* 358 */     String generatedClassName = PACKAGE_NAME + "/" + className;
/*     */ 
/* 360 */     mv.visitCode();
/* 361 */     mv.visitVarInsn(25, 0);
/* 362 */     mv.visitFieldInsn(180, generatedClassName, "typedTarget", targetSignature);
/* 363 */     mv.visitVarInsn(58, 3);
/* 364 */     mv.visitVarInsn(21, 1);
/*     */ 
/* 367 */     Label[] labels = new Label[fields.size()];
/* 368 */     Label errorLabel = new Label();
/* 369 */     Label returnLabel = new Label();
/*     */ 
/* 372 */     for (int i = 0; i < fields.size(); i++) {
/* 373 */       labels[i] = new Label();
/*     */     }
/*     */ 
/* 376 */     mv.visitTableSwitchInsn(0, labels.length - 1, errorLabel, labels);
/*     */ 
/* 378 */     for (int i = 0; i < fields.size(); i++)
/*     */     {
/* 380 */       Field field = (Field)fields.get(i);
/* 381 */       Class outputType = field.getType();
/* 382 */       Class inputType = Primitives.wrap(outputType);
/* 383 */       String typeDescriptor = Type.getDescriptor(outputType);
/* 384 */       String inputPath = inputType.getName().replace('.', '/');
/*     */ 
/* 386 */       mv.visitLabel(labels[i]);
/*     */ 
/* 389 */       if (i == 0)
/* 390 */         mv.visitFrame(1, 1, new Object[] { targetName }, 0, null);
/*     */       else {
/* 392 */         mv.visitFrame(3, 0, null, 0, null);
/*     */       }
/*     */ 
/* 395 */       if ((isPublic(field)) && (isNonFinal(field))) {
/* 396 */         mv.visitVarInsn(25, 3);
/* 397 */         mv.visitVarInsn(25, 2);
/*     */ 
/* 399 */         if (!outputType.isPrimitive())
/* 400 */           mv.visitTypeInsn(192, inputPath);
/*     */         else {
/* 402 */           boxingHelper.unbox(Type.getType(outputType));
/*     */         }
/* 404 */         mv.visitFieldInsn(181, targetName, field.getName(), typeDescriptor);
/*     */       }
/*     */       else
/*     */       {
/* 408 */         mv.visitVarInsn(25, 0);
/* 409 */         mv.visitVarInsn(21, 1);
/* 410 */         mv.visitVarInsn(25, 2);
/* 411 */         mv.visitMethodInsn(182, generatedClassName, "writeReflected", "(ILjava/lang/Object;)V");
/*     */       }
/*     */ 
/* 414 */       mv.visitJumpInsn(167, returnLabel);
/*     */     }
/*     */ 
/* 417 */     mv.visitLabel(errorLabel);
/* 418 */     mv.visitFrame(3, 0, null, 0, null);
/* 419 */     mv.visitTypeInsn(187, FIELD_EXCEPTION_CLASS);
/* 420 */     mv.visitInsn(89);
/* 421 */     mv.visitTypeInsn(187, "java/lang/StringBuilder");
/* 422 */     mv.visitInsn(89);
/* 423 */     mv.visitLdcInsn("Invalid index ");
/* 424 */     mv.visitMethodInsn(183, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
/* 425 */     mv.visitVarInsn(21, 1);
/* 426 */     mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
/* 427 */     mv.visitMethodInsn(182, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
/* 428 */     mv.visitMethodInsn(183, FIELD_EXCEPTION_CLASS, "<init>", "(Ljava/lang/String;)V");
/* 429 */     mv.visitInsn(191);
/*     */ 
/* 431 */     mv.visitLabel(returnLabel);
/* 432 */     mv.visitFrame(3, 0, null, 0, null);
/* 433 */     mv.visitVarInsn(25, 0);
/* 434 */     mv.visitInsn(176);
/* 435 */     mv.visitMaxs(5, 4);
/* 436 */     mv.visitEnd();
/*     */   }
/*     */ 
/*     */   private void createReadMethod(ClassWriter cw, String className, List<Field> fields, String targetSignature, String targetName) {
/* 440 */     MethodVisitor mv = cw.visitMethod(4, "readGenerated", "(I)Ljava/lang/Object;", null, new String[] { "com/comphenix/protocol/reflect/FieldAccessException" });
/*     */ 
/* 442 */     BoxingHelper boxingHelper = new BoxingHelper(mv);
/*     */ 
/* 444 */     String generatedClassName = PACKAGE_NAME + "/" + className;
/*     */ 
/* 446 */     mv.visitCode();
/* 447 */     mv.visitVarInsn(25, 0);
/* 448 */     mv.visitFieldInsn(180, generatedClassName, "typedTarget", targetSignature);
/* 449 */     mv.visitVarInsn(58, 2);
/* 450 */     mv.visitVarInsn(21, 1);
/*     */ 
/* 453 */     Label[] labels = new Label[fields.size()];
/* 454 */     Label errorLabel = new Label();
/*     */ 
/* 457 */     for (int i = 0; i < fields.size(); i++) {
/* 458 */       labels[i] = new Label();
/*     */     }
/*     */ 
/* 461 */     mv.visitTableSwitchInsn(0, fields.size() - 1, errorLabel, labels);
/*     */ 
/* 463 */     for (int i = 0; i < fields.size(); i++)
/*     */     {
/* 465 */       Field field = (Field)fields.get(i);
/* 466 */       Class outputType = field.getType();
/* 467 */       String typeDescriptor = Type.getDescriptor(outputType);
/*     */ 
/* 469 */       mv.visitLabel(labels[i]);
/*     */ 
/* 472 */       if (i == 0)
/* 473 */         mv.visitFrame(1, 1, new Object[] { targetName }, 0, null);
/*     */       else {
/* 475 */         mv.visitFrame(3, 0, null, 0, null);
/*     */       }
/*     */ 
/* 478 */       if (isPublic(field)) {
/* 479 */         mv.visitVarInsn(25, 2);
/* 480 */         mv.visitFieldInsn(180, targetName, field.getName(), typeDescriptor);
/*     */ 
/* 482 */         boxingHelper.box(Type.getType(outputType));
/*     */       }
/*     */       else {
/* 485 */         mv.visitVarInsn(25, 0);
/* 486 */         mv.visitVarInsn(21, 1);
/* 487 */         mv.visitMethodInsn(182, generatedClassName, "readReflected", "(I)Ljava/lang/Object;");
/*     */       }
/*     */ 
/* 490 */       mv.visitInsn(176);
/*     */     }
/*     */ 
/* 493 */     mv.visitLabel(errorLabel);
/* 494 */     mv.visitFrame(3, 0, null, 0, null);
/* 495 */     mv.visitTypeInsn(187, FIELD_EXCEPTION_CLASS);
/* 496 */     mv.visitInsn(89);
/* 497 */     mv.visitTypeInsn(187, "java/lang/StringBuilder");
/* 498 */     mv.visitInsn(89);
/* 499 */     mv.visitLdcInsn("Invalid index ");
/* 500 */     mv.visitMethodInsn(183, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
/* 501 */     mv.visitVarInsn(21, 1);
/* 502 */     mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
/* 503 */     mv.visitMethodInsn(182, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
/* 504 */     mv.visitMethodInsn(183, FIELD_EXCEPTION_CLASS, "<init>", "(Ljava/lang/String;)V");
/* 505 */     mv.visitInsn(191);
/* 506 */     mv.visitMaxs(5, 3);
/* 507 */     mv.visitEnd();
/*     */   }
/*     */ 
/*     */   private void createConstructor(ClassWriter cw, String className, String targetSignature, String targetName) {
/* 511 */     MethodVisitor mv = cw.visitMethod(1, "<init>", "(L" + SUPER_CLASS + ";L" + PACKAGE_NAME + "/StructureCompiler;)V", "(L" + SUPER_CLASS + "<Ljava/lang/Object;>;L" + PACKAGE_NAME + "/StructureCompiler;)V", null);
/*     */ 
/* 514 */     String fullClassName = PACKAGE_NAME + "/" + className;
/*     */ 
/* 516 */     mv.visitCode();
/* 517 */     mv.visitVarInsn(25, 0);
/* 518 */     mv.visitMethodInsn(183, COMPILED_CLASS, "<init>", "()V");
/* 519 */     mv.visitVarInsn(25, 0);
/* 520 */     mv.visitVarInsn(25, 1);
/* 521 */     mv.visitMethodInsn(182, fullClassName, "initialize", "(L" + SUPER_CLASS + ";)V");
/* 522 */     mv.visitVarInsn(25, 0);
/* 523 */     mv.visitVarInsn(25, 1);
/* 524 */     mv.visitMethodInsn(182, SUPER_CLASS, "getTarget", "()Ljava/lang/Object;");
/* 525 */     mv.visitFieldInsn(181, fullClassName, "target", "Ljava/lang/Object;");
/* 526 */     mv.visitVarInsn(25, 0);
/* 527 */     mv.visitVarInsn(25, 0);
/* 528 */     mv.visitFieldInsn(180, fullClassName, "target", "Ljava/lang/Object;");
/* 529 */     mv.visitTypeInsn(192, targetName);
/* 530 */     mv.visitFieldInsn(181, fullClassName, "typedTarget", targetSignature);
/* 531 */     mv.visitVarInsn(25, 0);
/* 532 */     mv.visitVarInsn(25, 2);
/* 533 */     mv.visitFieldInsn(181, fullClassName, "compiler", "L" + PACKAGE_NAME + "/StructureCompiler;");
/* 534 */     mv.visitInsn(177);
/* 535 */     mv.visitMaxs(2, 3);
/* 536 */     mv.visitEnd();
/*     */   }
/*     */ 
/*     */   static class StructureKey
/*     */   {
/*     */     private Class targetType;
/*     */     private Class fieldType;
/*     */ 
/*     */     public StructureKey(StructureModifier<?> source)
/*     */     {
/* 111 */       this(source.getTargetType(), source.getFieldType());
/*     */     }
/*     */ 
/*     */     public StructureKey(Class targetType, Class fieldType) {
/* 115 */       this.targetType = targetType;
/* 116 */       this.fieldType = fieldType;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 121 */       return Objects.hashCode(new Object[] { this.targetType, this.fieldType });
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 126 */       if ((obj instanceof StructureKey)) {
/* 127 */         StructureKey other = (StructureKey)obj;
/* 128 */         return (Objects.equal(this.targetType, other.targetType)) && (Objects.equal(this.fieldType, other.fieldType));
/*     */       }
/*     */ 
/* 131 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.compiler.StructureCompiler
 * JD-Core Version:    0.6.2
 */