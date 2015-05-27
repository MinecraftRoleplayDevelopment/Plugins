/*     */ package com.comphenix.net.sf.cglib.core;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassAdapter;
/*     */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.FieldVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.MethodAdapter;
/*     */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ClassEmitter extends ClassAdapter
/*     */ {
/*     */   private ClassInfo classInfo;
/*     */   private Map fieldInfo;
/*     */   private static int hookCounter;
/*     */   private MethodVisitor rawStaticInit;
/*     */   private CodeEmitter staticInit;
/*     */   private CodeEmitter staticHook;
/*     */   private Signature staticHookSig;
/*     */ 
/*     */   public ClassEmitter(ClassVisitor cv)
/*     */   {
/*  36 */     super(null);
/*  37 */     setTarget(cv);
/*     */   }
/*     */ 
/*     */   public ClassEmitter() {
/*  41 */     super(null);
/*     */   }
/*     */ 
/*     */   public void setTarget(ClassVisitor cv) {
/*  45 */     this.cv = cv;
/*  46 */     this.fieldInfo = new HashMap();
/*     */ 
/*  49 */     this.staticInit = (this.staticHook = null);
/*  50 */     this.staticHookSig = null;
/*     */   }
/*     */ 
/*     */   private static synchronized int getNextHook() {
/*  54 */     return ++hookCounter;
/*     */   }
/*     */ 
/*     */   public ClassInfo getClassInfo() {
/*  58 */     return this.classInfo;
/*     */   }
/*     */ 
/*     */   public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String source) {
/*  62 */     Type classType = Type.getType("L" + className.replace('.', '/') + ";");
/*  63 */     this.classInfo = new ClassInfo() { private final Type val$classType;
/*     */       private final Type val$superType;
/*     */       private final Type[] val$interfaces;
/*     */       private final int val$access;
/*     */ 
/*  65 */       public Type getType() { return this.val$classType; }
/*     */ 
/*     */       public Type getSuperType() {
/*  68 */         return this.val$superType != null ? this.val$superType : Constants.TYPE_OBJECT;
/*     */       }
/*     */       public Type[] getInterfaces() {
/*  71 */         return this.val$interfaces;
/*     */       }
/*     */       public int getModifiers() {
/*  74 */         return this.val$access;
/*     */       }
/*     */     };
/*  77 */     this.cv.visit(version, access, this.classInfo.getType().getInternalName(), null, this.classInfo.getSuperType().getInternalName(), TypeUtils.toInternalNames(interfaces));
/*     */ 
/*  83 */     if (source != null)
/*  84 */       this.cv.visitSource(source, null);
/*  85 */     init();
/*     */   }
/*     */ 
/*     */   public CodeEmitter getStaticHook() {
/*  89 */     if (TypeUtils.isInterface(getAccess())) {
/*  90 */       throw new IllegalStateException("static hook is invalid for this class");
/*     */     }
/*  92 */     if (this.staticHook == null) {
/*  93 */       this.staticHookSig = new Signature("CGLIB$STATICHOOK" + getNextHook(), "()V");
/*  94 */       this.staticHook = begin_method(8, this.staticHookSig, null);
/*     */ 
/*  97 */       if (this.staticInit != null) {
/*  98 */         this.staticInit.invoke_static_this(this.staticHookSig);
/*     */       }
/*     */     }
/* 101 */     return this.staticHook;
/*     */   }
/*     */ 
/*     */   protected void init() {
/*     */   }
/*     */ 
/*     */   public int getAccess() {
/* 108 */     return this.classInfo.getModifiers();
/*     */   }
/*     */ 
/*     */   public Type getClassType() {
/* 112 */     return this.classInfo.getType();
/*     */   }
/*     */ 
/*     */   public Type getSuperType() {
/* 116 */     return this.classInfo.getSuperType();
/*     */   }
/*     */ 
/*     */   public void end_class() {
/* 120 */     if ((this.staticHook != null) && (this.staticInit == null))
/*     */     {
/* 122 */       begin_static();
/*     */     }
/* 124 */     if (this.staticInit != null) {
/* 125 */       this.staticHook.return_value();
/* 126 */       this.staticHook.end_method();
/* 127 */       this.rawStaticInit.visitInsn(177);
/* 128 */       this.rawStaticInit.visitMaxs(0, 0);
/* 129 */       this.staticInit = (this.staticHook = null);
/* 130 */       this.staticHookSig = null;
/*     */     }
/* 132 */     this.cv.visitEnd();
/*     */   }
/*     */ 
/*     */   public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
/* 136 */     if (this.classInfo == null)
/* 137 */       throw new IllegalStateException("classInfo is null! " + this);
/* 138 */     MethodVisitor v = this.cv.visitMethod(access, sig.getName(), sig.getDescriptor(), null, TypeUtils.toInternalNames(exceptions));
/*     */ 
/* 143 */     if ((sig.equals(Constants.SIG_STATIC)) && (!TypeUtils.isInterface(getAccess()))) {
/* 144 */       this.rawStaticInit = v;
/* 145 */       MethodVisitor wrapped = new MethodAdapter(v) {
/*     */         public void visitMaxs(int maxStack, int maxLocals) {
/*     */         }
/*     */ 
/*     */         public void visitInsn(int insn) {
/* 150 */           if (insn != 177)
/* 151 */             super.visitInsn(insn);
/*     */         }
/*     */       };
/* 155 */       this.staticInit = new CodeEmitter(this, wrapped, access, sig, exceptions);
/* 156 */       if (this.staticHook == null)
/*     */       {
/* 158 */         getStaticHook();
/*     */       }
/* 160 */       else this.staticInit.invoke_static_this(this.staticHookSig);
/*     */ 
/* 162 */       return this.staticInit;
/* 163 */     }if (sig.equals(this.staticHookSig)) {
/* 164 */       return new CodeEmitter(this, v, access, sig, exceptions) {
/*     */         public boolean isStaticHook() {
/* 166 */           return true;
/*     */         }
/*     */       };
/*     */     }
/* 170 */     return new CodeEmitter(this, v, access, sig, exceptions);
/*     */   }
/*     */ 
/*     */   public CodeEmitter begin_static()
/*     */   {
/* 175 */     return begin_method(8, Constants.SIG_STATIC, null);
/*     */   }
/*     */ 
/*     */   public void declare_field(int access, String name, Type type, Object value) {
/* 179 */     FieldInfo existing = (FieldInfo)this.fieldInfo.get(name);
/* 180 */     FieldInfo info = new FieldInfo(access, name, type, value);
/* 181 */     if (existing != null) {
/* 182 */       if (!info.equals(existing))
/* 183 */         throw new IllegalArgumentException("Field \"" + name + "\" has been declared differently");
/*     */     }
/*     */     else {
/* 186 */       this.fieldInfo.put(name, info);
/* 187 */       this.cv.visitField(access, name, type.getDescriptor(), null, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean isFieldDeclared(String name)
/*     */   {
/* 193 */     return this.fieldInfo.get(name) != null;
/*     */   }
/*     */ 
/*     */   FieldInfo getFieldInfo(String name) {
/* 197 */     FieldInfo field = (FieldInfo)this.fieldInfo.get(name);
/* 198 */     if (field == null) {
/* 199 */       throw new IllegalArgumentException("Field " + name + " is not declared in " + getClassType().getClassName());
/*     */     }
/* 201 */     return field;
/*     */   }
/*     */ 
/*     */   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
/*     */   {
/* 246 */     begin_class(version, access, name.replace('/', '.'), TypeUtils.fromInternalName(superName), TypeUtils.fromInternalNames(interfaces), null);
/*     */   }
/*     */ 
/*     */   public void visitEnd()
/*     */   {
/* 255 */     end_class();
/*     */   }
/*     */ 
/*     */   public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
/*     */   {
/* 263 */     declare_field(access, name, Type.getType(desc), value);
/* 264 */     return null;
/*     */   }
/*     */ 
/*     */   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*     */   {
/* 272 */     return begin_method(access, new Signature(name, desc), TypeUtils.fromInternalNames(exceptions));
/*     */   }
/*     */ 
/*     */   static class FieldInfo
/*     */   {
/*     */     int access;
/*     */     String name;
/*     */     Type type;
/*     */     Object value;
/*     */ 
/*     */     public FieldInfo(int access, String name, Type type, Object value)
/*     */     {
/* 211 */       this.access = access;
/* 212 */       this.name = name;
/* 213 */       this.type = type;
/* 214 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 218 */       if (o == null)
/* 219 */         return false;
/* 220 */       if (!(o instanceof FieldInfo))
/* 221 */         return false;
/* 222 */       FieldInfo other = (FieldInfo)o;
/* 223 */       if ((this.access != other.access) || (!this.name.equals(other.name)) || (!this.type.equals(other.type)))
/*     */       {
/* 226 */         return false;
/*     */       }
/* 228 */       if (((this.value == null ? 1 : 0) ^ (other.value == null ? 1 : 0)) != 0)
/* 229 */         return false;
/* 230 */       if ((this.value != null) && (!this.value.equals(other.value)))
/* 231 */         return false;
/* 232 */       return true;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 236 */       return this.access ^ this.name.hashCode() ^ this.type.hashCode() ^ (this.value == null ? 0 : this.value.hashCode());
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.ClassEmitter
 * JD-Core Version:    0.6.2
 */