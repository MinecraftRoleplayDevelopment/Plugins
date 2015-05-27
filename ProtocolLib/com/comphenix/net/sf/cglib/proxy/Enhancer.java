/*      */ package com.comphenix.net.sf.cglib.proxy;
/*      */ 
/*      */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*      */ import com.comphenix.net.sf.cglib.asm.Label;
/*      */ import com.comphenix.net.sf.cglib.asm.Type;
/*      */ import com.comphenix.net.sf.cglib.core.AbstractClassGenerator;
/*      */ import com.comphenix.net.sf.cglib.core.AbstractClassGenerator.Source;
/*      */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*      */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*      */ import com.comphenix.net.sf.cglib.core.CodeGenerationException;
/*      */ import com.comphenix.net.sf.cglib.core.CollectionUtils;
/*      */ import com.comphenix.net.sf.cglib.core.Constants;
/*      */ import com.comphenix.net.sf.cglib.core.DuplicatesPredicate;
/*      */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*      */ import com.comphenix.net.sf.cglib.core.KeyFactory;
/*      */ import com.comphenix.net.sf.cglib.core.Local;
/*      */ import com.comphenix.net.sf.cglib.core.MethodInfo;
/*      */ import com.comphenix.net.sf.cglib.core.MethodInfoTransformer;
/*      */ import com.comphenix.net.sf.cglib.core.MethodWrapper;
/*      */ import com.comphenix.net.sf.cglib.core.ObjectSwitchCallback;
/*      */ import com.comphenix.net.sf.cglib.core.ProcessSwitchCallback;
/*      */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*      */ import com.comphenix.net.sf.cglib.core.RejectModifierPredicate;
/*      */ import com.comphenix.net.sf.cglib.core.Signature;
/*      */ import com.comphenix.net.sf.cglib.core.Transformer;
/*      */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*      */ import com.comphenix.net.sf.cglib.core.VisibilityPredicate;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ 
/*      */ public class Enhancer extends AbstractClassGenerator
/*      */ {
/*   62 */   private static final CallbackFilter ALL_ZERO = new CallbackFilter() {
/*      */     public int accept(Method method) {
/*   64 */       return 0;
/*      */     }
/*   62 */   };
/*      */ 
/*   68 */   private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(Enhancer.class.getName());
/*   69 */   private static final EnhancerKey KEY_FACTORY = (EnhancerKey)KeyFactory.create(EnhancerKey.class);
/*      */   private static final String BOUND_FIELD = "CGLIB$BOUND";
/*      */   private static final String THREAD_CALLBACKS_FIELD = "CGLIB$THREAD_CALLBACKS";
/*      */   private static final String STATIC_CALLBACKS_FIELD = "CGLIB$STATIC_CALLBACKS";
/*      */   private static final String SET_THREAD_CALLBACKS_NAME = "CGLIB$SET_THREAD_CALLBACKS";
/*      */   private static final String SET_STATIC_CALLBACKS_NAME = "CGLIB$SET_STATIC_CALLBACKS";
/*      */   private static final String CONSTRUCTED_FIELD = "CGLIB$CONSTRUCTED";
/*   79 */   private static final Type FACTORY = TypeUtils.parseType("com.comphenix.net.sf.cglib.proxy.Factory");
/*      */ 
/*   81 */   private static final Type ILLEGAL_STATE_EXCEPTION = TypeUtils.parseType("IllegalStateException");
/*      */ 
/*   83 */   private static final Type ILLEGAL_ARGUMENT_EXCEPTION = TypeUtils.parseType("IllegalArgumentException");
/*      */ 
/*   85 */   private static final Type THREAD_LOCAL = TypeUtils.parseType("ThreadLocal");
/*      */ 
/*   87 */   private static final Type CALLBACK = TypeUtils.parseType("com.comphenix.net.sf.cglib.proxy.Callback");
/*      */ 
/*   89 */   private static final Type CALLBACK_ARRAY = Type.getType(new Callback[0].getClass());
/*      */ 
/*   91 */   private static final Signature CSTRUCT_NULL = TypeUtils.parseConstructor("");
/*      */ 
/*   93 */   private static final Signature SET_THREAD_CALLBACKS = new Signature("CGLIB$SET_THREAD_CALLBACKS", Type.VOID_TYPE, new Type[] { CALLBACK_ARRAY });
/*      */ 
/*   95 */   private static final Signature SET_STATIC_CALLBACKS = new Signature("CGLIB$SET_STATIC_CALLBACKS", Type.VOID_TYPE, new Type[] { CALLBACK_ARRAY });
/*      */ 
/*   97 */   private static final Signature NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[] { CALLBACK_ARRAY });
/*      */ 
/*   99 */   private static final Signature MULTIARG_NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[] { Constants.TYPE_CLASS_ARRAY, Constants.TYPE_OBJECT_ARRAY, CALLBACK_ARRAY });
/*      */ 
/*  105 */   private static final Signature SINGLE_NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[] { CALLBACK });
/*      */ 
/*  107 */   private static final Signature SET_CALLBACK = new Signature("setCallback", Type.VOID_TYPE, new Type[] { Type.INT_TYPE, CALLBACK });
/*      */ 
/*  109 */   private static final Signature GET_CALLBACK = new Signature("getCallback", CALLBACK, new Type[] { Type.INT_TYPE });
/*      */ 
/*  111 */   private static final Signature SET_CALLBACKS = new Signature("setCallbacks", Type.VOID_TYPE, new Type[] { CALLBACK_ARRAY });
/*      */ 
/*  113 */   private static final Signature GET_CALLBACKS = new Signature("getCallbacks", CALLBACK_ARRAY, new Type[0]);
/*      */ 
/*  115 */   private static final Signature THREAD_LOCAL_GET = TypeUtils.parseSignature("Object get()");
/*      */ 
/*  117 */   private static final Signature THREAD_LOCAL_SET = TypeUtils.parseSignature("void set(Object)");
/*      */ 
/*  119 */   private static final Signature BIND_CALLBACKS = TypeUtils.parseSignature("void CGLIB$BIND_CALLBACKS(Object)");
/*      */   private Class[] interfaces;
/*      */   private CallbackFilter filter;
/*      */   private Callback[] callbacks;
/*      */   private Type[] callbackTypes;
/*      */   private boolean classOnly;
/*      */   private Class superclass;
/*      */   private Class[] argumentTypes;
/*      */   private Object[] arguments;
/*  141 */   private boolean useFactory = true;
/*      */   private Long serialVersionUID;
/*  143 */   private boolean interceptDuringConstruction = true;
/*      */ 
/*      */   public Enhancer()
/*      */   {
/*  153 */     super(SOURCE);
/*      */   }
/*      */ 
/*      */   public void setSuperclass(Class superclass)
/*      */   {
/*  166 */     if ((superclass != null) && (superclass.isInterface()))
/*  167 */       setInterfaces(new Class[] { superclass });
/*  168 */     else if ((superclass != null) && (superclass.equals(Object.class)))
/*      */     {
/*  170 */       this.superclass = null;
/*      */     }
/*  172 */     else this.superclass = superclass;
/*      */   }
/*      */ 
/*      */   public void setInterfaces(Class[] interfaces)
/*      */   {
/*  183 */     this.interfaces = interfaces;
/*      */   }
/*      */ 
/*      */   public void setCallbackFilter(CallbackFilter filter)
/*      */   {
/*  195 */     this.filter = filter;
/*      */   }
/*      */ 
/*      */   public void setCallback(Callback callback)
/*      */   {
/*  206 */     setCallbacks(new Callback[] { callback });
/*      */   }
/*      */ 
/*      */   public void setCallbacks(Callback[] callbacks)
/*      */   {
/*  219 */     if ((callbacks != null) && (callbacks.length == 0)) {
/*  220 */       throw new IllegalArgumentException("Array cannot be empty");
/*      */     }
/*  222 */     this.callbacks = callbacks;
/*      */   }
/*      */ 
/*      */   public void setUseFactory(boolean useFactory)
/*      */   {
/*  235 */     this.useFactory = useFactory;
/*      */   }
/*      */ 
/*      */   public void setInterceptDuringConstruction(boolean interceptDuringConstruction)
/*      */   {
/*  245 */     this.interceptDuringConstruction = interceptDuringConstruction;
/*      */   }
/*      */ 
/*      */   public void setCallbackType(Class callbackType)
/*      */   {
/*  257 */     setCallbackTypes(new Class[] { callbackType });
/*      */   }
/*      */ 
/*      */   public void setCallbackTypes(Class[] callbackTypes)
/*      */   {
/*  270 */     if ((callbackTypes != null) && (callbackTypes.length == 0)) {
/*  271 */       throw new IllegalArgumentException("Array cannot be empty");
/*      */     }
/*  273 */     this.callbackTypes = CallbackInfo.determineTypes(callbackTypes);
/*      */   }
/*      */ 
/*      */   public Object create()
/*      */   {
/*  283 */     this.classOnly = false;
/*  284 */     this.argumentTypes = null;
/*  285 */     return createHelper();
/*      */   }
/*      */ 
/*      */   public Object create(Class[] argumentTypes, Object[] arguments)
/*      */   {
/*  298 */     this.classOnly = false;
/*  299 */     if ((argumentTypes == null) || (arguments == null) || (argumentTypes.length != arguments.length)) {
/*  300 */       throw new IllegalArgumentException("Arguments must be non-null and of equal length");
/*      */     }
/*  302 */     this.argumentTypes = argumentTypes;
/*  303 */     this.arguments = arguments;
/*  304 */     return createHelper();
/*      */   }
/*      */ 
/*      */   public Class createClass()
/*      */   {
/*  316 */     this.classOnly = true;
/*  317 */     return (Class)createHelper();
/*      */   }
/*      */ 
/*      */   public void setSerialVersionUID(Long sUID)
/*      */   {
/*  325 */     this.serialVersionUID = sUID;
/*      */   }
/*      */ 
/*      */   private void validate() {
/*  329 */     if ((this.classOnly ^ this.callbacks == null)) {
/*  330 */       if (this.classOnly) {
/*  331 */         throw new IllegalStateException("createClass does not accept callbacks");
/*      */       }
/*  333 */       throw new IllegalStateException("Callbacks are required");
/*      */     }
/*      */ 
/*  336 */     if ((this.classOnly) && (this.callbackTypes == null)) {
/*  337 */       throw new IllegalStateException("Callback types are required");
/*      */     }
/*  339 */     if ((this.callbacks != null) && (this.callbackTypes != null)) {
/*  340 */       if (this.callbacks.length != this.callbackTypes.length) {
/*  341 */         throw new IllegalStateException("Lengths of callback and callback types array must be the same");
/*      */       }
/*  343 */       Type[] check = CallbackInfo.determineTypes(this.callbacks);
/*  344 */       for (int i = 0; i < check.length; i++) {
/*  345 */         if (!check[i].equals(this.callbackTypes[i]))
/*  346 */           throw new IllegalStateException("Callback " + check[i] + " is not assignable to " + this.callbackTypes[i]);
/*      */       }
/*      */     }
/*  349 */     else if (this.callbacks != null) {
/*  350 */       this.callbackTypes = CallbackInfo.determineTypes(this.callbacks);
/*      */     }
/*  352 */     if (this.filter == null) {
/*  353 */       if (this.callbackTypes.length > 1) {
/*  354 */         throw new IllegalStateException("Multiple callback types possible but no filter specified");
/*      */       }
/*  356 */       this.filter = ALL_ZERO;
/*      */     }
/*  358 */     if (this.interfaces != null)
/*  359 */       for (int i = 0; i < this.interfaces.length; i++) {
/*  360 */         if (this.interfaces[i] == null) {
/*  361 */           throw new IllegalStateException("Interfaces cannot be null");
/*      */         }
/*  363 */         if (!this.interfaces[i].isInterface())
/*  364 */           throw new IllegalStateException(this.interfaces[i] + " is not an interface");
/*      */       }
/*      */   }
/*      */ 
/*      */   private Object createHelper()
/*      */   {
/*  371 */     validate();
/*  372 */     if (this.superclass != null)
/*  373 */       setNamePrefix(this.superclass.getName());
/*  374 */     else if (this.interfaces != null) {
/*  375 */       setNamePrefix(this.interfaces[ReflectUtils.findPackageProtected(this.interfaces)].getName());
/*      */     }
/*  377 */     return super.create(KEY_FACTORY.newInstance(this.superclass != null ? this.superclass.getName() : null, ReflectUtils.getNames(this.interfaces), this.filter, this.callbackTypes, this.useFactory, this.interceptDuringConstruction, this.serialVersionUID));
/*      */   }
/*      */ 
/*      */   protected ClassLoader getDefaultClassLoader()
/*      */   {
/*  387 */     if (this.superclass != null)
/*  388 */       return this.superclass.getClassLoader();
/*  389 */     if (this.interfaces != null) {
/*  390 */       return this.interfaces[0].getClassLoader();
/*      */     }
/*  392 */     return null;
/*      */   }
/*      */ 
/*      */   private Signature rename(Signature sig, int index)
/*      */   {
/*  397 */     return new Signature("CGLIB$" + sig.getName() + "$" + index, sig.getDescriptor());
/*      */   }
/*      */ 
/*      */   public static void getMethods(Class superclass, Class[] interfaces, List methods)
/*      */   {
/*  416 */     getMethods(superclass, interfaces, methods, null, null);
/*      */   }
/*      */ 
/*      */   private static void getMethods(Class superclass, Class[] interfaces, List methods, List interfaceMethods, Set forcePublic)
/*      */   {
/*  421 */     ReflectUtils.addAllMethods(superclass, methods);
/*  422 */     List target = interfaceMethods != null ? interfaceMethods : methods;
/*  423 */     if (interfaces != null) {
/*  424 */       for (int i = 0; i < interfaces.length; i++) {
/*  425 */         if (interfaces[i] != Factory.class) {
/*  426 */           ReflectUtils.addAllMethods(interfaces[i], target);
/*      */         }
/*      */       }
/*      */     }
/*  430 */     if (interfaceMethods != null) {
/*  431 */       if (forcePublic != null) {
/*  432 */         forcePublic.addAll(MethodWrapper.createSet(interfaceMethods));
/*      */       }
/*  434 */       methods.addAll(interfaceMethods);
/*      */     }
/*  436 */     CollectionUtils.filter(methods, new RejectModifierPredicate(8));
/*  437 */     CollectionUtils.filter(methods, new VisibilityPredicate(superclass, true));
/*  438 */     CollectionUtils.filter(methods, new DuplicatesPredicate());
/*  439 */     CollectionUtils.filter(methods, new RejectModifierPredicate(16));
/*      */   }
/*      */ 
/*      */   public void generateClass(ClassVisitor v) throws Exception {
/*  443 */     Class sc = this.superclass == null ? Object.class : this.superclass;
/*      */ 
/*  445 */     if (TypeUtils.isFinal(sc.getModifiers()))
/*  446 */       throw new IllegalArgumentException("Cannot subclass final class " + sc);
/*  447 */     List constructors = new ArrayList(Arrays.asList(sc.getDeclaredConstructors()));
/*  448 */     filterConstructors(sc, constructors);
/*      */ 
/*  453 */     List actualMethods = new ArrayList();
/*  454 */     List interfaceMethods = new ArrayList();
/*  455 */     Set forcePublic = new HashSet();
/*  456 */     getMethods(sc, this.interfaces, actualMethods, interfaceMethods, forcePublic);
/*      */ 
/*  458 */     List methods = CollectionUtils.transform(actualMethods, new Transformer() { private final Set val$forcePublic;
/*      */ 
/*  460 */       public Object transform(Object value) { Method method = (Method)value;
/*  461 */         int modifiers = 0x10 | method.getModifiers() & 0xFFFFFBFF & 0xFFFFFEFF & 0xFFFFFFDF;
/*      */ 
/*  466 */         if (this.val$forcePublic.contains(MethodWrapper.create(method))) {
/*  467 */           modifiers = modifiers & 0xFFFFFFFB | 0x1;
/*      */         }
/*  469 */         return ReflectUtils.getMethodInfo(method, modifiers);
/*      */       }
/*      */     });
/*  473 */     ClassEmitter e = new ClassEmitter(v);
/*  474 */     e.begin_class(46, 1, getClassName(), Type.getType(sc), this.useFactory ? TypeUtils.add(TypeUtils.getTypes(this.interfaces), FACTORY) : TypeUtils.getTypes(this.interfaces), "<generated>");
/*      */ 
/*  482 */     List constructorInfo = CollectionUtils.transform(constructors, MethodInfoTransformer.getInstance());
/*      */ 
/*  484 */     e.declare_field(2, "CGLIB$BOUND", Type.BOOLEAN_TYPE, null);
/*  485 */     if (!this.interceptDuringConstruction) {
/*  486 */       e.declare_field(2, "CGLIB$CONSTRUCTED", Type.BOOLEAN_TYPE, null);
/*      */     }
/*  488 */     e.declare_field(26, "CGLIB$THREAD_CALLBACKS", THREAD_LOCAL, null);
/*  489 */     e.declare_field(26, "CGLIB$STATIC_CALLBACKS", CALLBACK_ARRAY, null);
/*  490 */     if (this.serialVersionUID != null) {
/*  491 */       e.declare_field(26, "serialVersionUID", Type.LONG_TYPE, this.serialVersionUID);
/*      */     }
/*      */ 
/*  494 */     for (int i = 0; i < this.callbackTypes.length; i++) {
/*  495 */       e.declare_field(2, getCallbackField(i), this.callbackTypes[i], null);
/*      */     }
/*      */ 
/*  498 */     emitMethods(e, methods, actualMethods);
/*  499 */     emitConstructors(e, constructorInfo);
/*  500 */     emitSetThreadCallbacks(e);
/*  501 */     emitSetStaticCallbacks(e);
/*  502 */     emitBindCallbacks(e);
/*      */ 
/*  504 */     if (this.useFactory) {
/*  505 */       int[] keys = getCallbackKeys();
/*  506 */       emitNewInstanceCallbacks(e);
/*  507 */       emitNewInstanceCallback(e);
/*  508 */       emitNewInstanceMultiarg(e, constructorInfo);
/*  509 */       emitGetCallback(e, keys);
/*  510 */       emitSetCallback(e, keys);
/*  511 */       emitGetCallbacks(e);
/*  512 */       emitSetCallbacks(e);
/*      */     }
/*      */ 
/*  515 */     e.end_class();
/*      */   }
/*      */ 
/*      */   protected void filterConstructors(Class sc, List constructors)
/*      */   {
/*  529 */     CollectionUtils.filter(constructors, new VisibilityPredicate(sc, true));
/*  530 */     if (constructors.size() == 0)
/*  531 */       throw new IllegalArgumentException("No visible constructors in " + sc);
/*      */   }
/*      */ 
/*      */   protected Object firstInstance(Class type) throws Exception {
/*  535 */     if (this.classOnly) {
/*  536 */       return type;
/*      */     }
/*  538 */     return createUsingReflection(type);
/*      */   }
/*      */ 
/*      */   protected Object nextInstance(Object instance)
/*      */   {
/*  543 */     Class protoclass = (instance instanceof Class) ? (Class)instance : instance.getClass();
/*  544 */     if (this.classOnly)
/*  545 */       return protoclass;
/*  546 */     if ((instance instanceof Factory)) {
/*  547 */       if (this.argumentTypes != null) {
/*  548 */         return ((Factory)instance).newInstance(this.argumentTypes, this.arguments, this.callbacks);
/*      */       }
/*  550 */       return ((Factory)instance).newInstance(this.callbacks);
/*      */     }
/*      */ 
/*  553 */     return createUsingReflection(protoclass);
/*      */   }
/*      */ 
/*      */   public static void registerCallbacks(Class generatedClass, Callback[] callbacks)
/*      */   {
/*  581 */     setThreadCallbacks(generatedClass, callbacks);
/*      */   }
/*      */ 
/*      */   public static void registerStaticCallbacks(Class generatedClass, Callback[] callbacks)
/*      */   {
/*  594 */     setCallbacksHelper(generatedClass, callbacks, "CGLIB$SET_STATIC_CALLBACKS");
/*      */   }
/*      */ 
/*      */   public static boolean isEnhanced(Class type)
/*      */   {
/*      */     try
/*      */     {
/*  604 */       getCallbacksSetter(type, "CGLIB$SET_THREAD_CALLBACKS");
/*  605 */       return true; } catch (NoSuchMethodException e) {
/*      */     }
/*  607 */     return false;
/*      */   }
/*      */ 
/*      */   private static void setThreadCallbacks(Class type, Callback[] callbacks)
/*      */   {
/*  612 */     setCallbacksHelper(type, callbacks, "CGLIB$SET_THREAD_CALLBACKS");
/*      */   }
/*      */ 
/*      */   private static void setCallbacksHelper(Class type, Callback[] callbacks, String methodName)
/*      */   {
/*      */     try {
/*  618 */       Method setter = getCallbacksSetter(type, methodName);
/*  619 */       setter.invoke(null, new Object[] { callbacks });
/*      */     } catch (NoSuchMethodException e) {
/*  621 */       throw new IllegalArgumentException(type + " is not an enhanced class");
/*      */     } catch (IllegalAccessException e) {
/*  623 */       throw new CodeGenerationException(e);
/*      */     } catch (InvocationTargetException e) {
/*  625 */       throw new CodeGenerationException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Method getCallbacksSetter(Class type, String methodName) throws NoSuchMethodException {
/*  630 */     return type.getDeclaredMethod(methodName, new Class[] { new Callback[0].getClass() });
/*      */   }
/*      */ 
/*      */   private Object createUsingReflection(Class type) {
/*  634 */     setThreadCallbacks(type, this.callbacks);
/*      */     try
/*      */     {
/*      */       Object localObject1;
/*  637 */       if (this.argumentTypes != null)
/*      */       {
/*  639 */         return ReflectUtils.newInstance(type, this.argumentTypes, this.arguments);
/*      */       }
/*      */ 
/*  643 */       return ReflectUtils.newInstance(type);
/*      */     }
/*      */     finally
/*      */     {
/*  648 */       setThreadCallbacks(type, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Object create(Class type, Callback callback)
/*      */   {
/*  660 */     Enhancer e = new Enhancer();
/*  661 */     e.setSuperclass(type);
/*  662 */     e.setCallback(callback);
/*  663 */     return e.create();
/*      */   }
/*      */ 
/*      */   public static Object create(Class superclass, Class[] interfaces, Callback callback)
/*      */   {
/*  675 */     Enhancer e = new Enhancer();
/*  676 */     e.setSuperclass(superclass);
/*  677 */     e.setInterfaces(interfaces);
/*  678 */     e.setCallback(callback);
/*  679 */     return e.create();
/*      */   }
/*      */ 
/*      */   public static Object create(Class superclass, Class[] interfaces, CallbackFilter filter, Callback[] callbacks)
/*      */   {
/*  692 */     Enhancer e = new Enhancer();
/*  693 */     e.setSuperclass(superclass);
/*  694 */     e.setInterfaces(interfaces);
/*  695 */     e.setCallbackFilter(filter);
/*  696 */     e.setCallbacks(callbacks);
/*  697 */     return e.create();
/*      */   }
/*      */ 
/*      */   private void emitConstructors(ClassEmitter ce, List constructors) {
/*  701 */     boolean seenNull = false;
/*  702 */     for (Iterator it = constructors.iterator(); it.hasNext(); ) {
/*  703 */       MethodInfo constructor = (MethodInfo)it.next();
/*  704 */       CodeEmitter e = EmitUtils.begin_method(ce, constructor, 1);
/*  705 */       e.load_this();
/*  706 */       e.dup();
/*  707 */       e.load_args();
/*  708 */       Signature sig = constructor.getSignature();
/*  709 */       seenNull = (seenNull) || (sig.getDescriptor().equals("()V"));
/*  710 */       e.super_invoke_constructor(sig);
/*  711 */       e.invoke_static_this(BIND_CALLBACKS);
/*  712 */       if (!this.interceptDuringConstruction) {
/*  713 */         e.load_this();
/*  714 */         e.push(1);
/*  715 */         e.putfield("CGLIB$CONSTRUCTED");
/*      */       }
/*  717 */       e.return_value();
/*  718 */       e.end_method();
/*      */     }
/*  720 */     if ((!this.classOnly) && (!seenNull) && (this.arguments == null))
/*  721 */       throw new IllegalArgumentException("Superclass has no null constructors but no arguments were given");
/*      */   }
/*      */ 
/*      */   private int[] getCallbackKeys() {
/*  725 */     int[] keys = new int[this.callbackTypes.length];
/*  726 */     for (int i = 0; i < this.callbackTypes.length; i++) {
/*  727 */       keys[i] = i;
/*      */     }
/*  729 */     return keys;
/*      */   }
/*      */ 
/*      */   private void emitGetCallback(ClassEmitter ce, int[] keys) {
/*  733 */     CodeEmitter e = ce.begin_method(1, GET_CALLBACK, null);
/*  734 */     e.load_this();
/*  735 */     e.invoke_static_this(BIND_CALLBACKS);
/*  736 */     e.load_this();
/*  737 */     e.load_arg(0);
/*  738 */     e.process_switch(keys, new ProcessSwitchCallback() { private final CodeEmitter val$e;
/*      */ 
/*  740 */       public void processCase(int key, Label end) { this.val$e.getfield(Enhancer.getCallbackField(key));
/*  741 */         this.val$e.goTo(end); }
/*      */ 
/*      */       public void processDefault() {
/*  744 */         this.val$e.pop();
/*  745 */         this.val$e.aconst_null();
/*      */       }
/*      */     });
/*  748 */     e.return_value();
/*  749 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitSetCallback(ClassEmitter ce, int[] keys) {
/*  753 */     CodeEmitter e = ce.begin_method(1, SET_CALLBACK, null);
/*  754 */     e.load_arg(0);
/*  755 */     e.process_switch(keys, new ProcessSwitchCallback() { private final CodeEmitter val$e;
/*      */ 
/*  757 */       public void processCase(int key, Label end) { this.val$e.load_this();
/*  758 */         this.val$e.load_arg(1);
/*  759 */         this.val$e.checkcast(Enhancer.this.callbackTypes[key]);
/*  760 */         this.val$e.putfield(Enhancer.getCallbackField(key));
/*  761 */         this.val$e.goTo(end);
/*      */       }
/*      */ 
/*      */       public void processDefault()
/*      */       {
/*      */       }
/*      */     });
/*  767 */     e.return_value();
/*  768 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitSetCallbacks(ClassEmitter ce) {
/*  772 */     CodeEmitter e = ce.begin_method(1, SET_CALLBACKS, null);
/*  773 */     e.load_this();
/*  774 */     e.load_arg(0);
/*  775 */     for (int i = 0; i < this.callbackTypes.length; i++) {
/*  776 */       e.dup2();
/*  777 */       e.aaload(i);
/*  778 */       e.checkcast(this.callbackTypes[i]);
/*  779 */       e.putfield(getCallbackField(i));
/*      */     }
/*  781 */     e.return_value();
/*  782 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitGetCallbacks(ClassEmitter ce) {
/*  786 */     CodeEmitter e = ce.begin_method(1, GET_CALLBACKS, null);
/*  787 */     e.load_this();
/*  788 */     e.invoke_static_this(BIND_CALLBACKS);
/*  789 */     e.load_this();
/*  790 */     e.push(this.callbackTypes.length);
/*  791 */     e.newarray(CALLBACK);
/*  792 */     for (int i = 0; i < this.callbackTypes.length; i++) {
/*  793 */       e.dup();
/*  794 */       e.push(i);
/*  795 */       e.load_this();
/*  796 */       e.getfield(getCallbackField(i));
/*  797 */       e.aastore();
/*      */     }
/*  799 */     e.return_value();
/*  800 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitNewInstanceCallbacks(ClassEmitter ce) {
/*  804 */     CodeEmitter e = ce.begin_method(1, NEW_INSTANCE, null);
/*  805 */     e.load_arg(0);
/*  806 */     e.invoke_static_this(SET_THREAD_CALLBACKS);
/*  807 */     emitCommonNewInstance(e);
/*      */   }
/*      */ 
/*      */   private void emitCommonNewInstance(CodeEmitter e) {
/*  811 */     e.new_instance_this();
/*  812 */     e.dup();
/*  813 */     e.invoke_constructor_this();
/*  814 */     e.aconst_null();
/*  815 */     e.invoke_static_this(SET_THREAD_CALLBACKS);
/*  816 */     e.return_value();
/*  817 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitNewInstanceCallback(ClassEmitter ce) {
/*  821 */     CodeEmitter e = ce.begin_method(1, SINGLE_NEW_INSTANCE, null);
/*  822 */     switch (this.callbackTypes.length)
/*      */     {
/*      */     case 0:
/*  825 */       break;
/*      */     case 1:
/*  828 */       e.push(1);
/*  829 */       e.newarray(CALLBACK);
/*  830 */       e.dup();
/*  831 */       e.push(0);
/*  832 */       e.load_arg(0);
/*  833 */       e.aastore();
/*  834 */       e.invoke_static_this(SET_THREAD_CALLBACKS);
/*  835 */       break;
/*      */     default:
/*  837 */       e.throw_exception(ILLEGAL_STATE_EXCEPTION, "More than one callback object required");
/*      */     }
/*  839 */     emitCommonNewInstance(e);
/*      */   }
/*      */ 
/*      */   private void emitNewInstanceMultiarg(ClassEmitter ce, List constructors) {
/*  843 */     CodeEmitter e = ce.begin_method(1, MULTIARG_NEW_INSTANCE, null);
/*  844 */     e.load_arg(2);
/*  845 */     e.invoke_static_this(SET_THREAD_CALLBACKS);
/*  846 */     e.new_instance_this();
/*  847 */     e.dup();
/*  848 */     e.load_arg(0);
/*  849 */     EmitUtils.constructor_switch(e, constructors, new ObjectSwitchCallback() { private final CodeEmitter val$e;
/*      */ 
/*  851 */       public void processCase(Object key, Label end) { MethodInfo constructor = (MethodInfo)key;
/*  852 */         Type[] types = constructor.getSignature().getArgumentTypes();
/*  853 */         for (int i = 0; i < types.length; i++) {
/*  854 */           this.val$e.load_arg(1);
/*  855 */           this.val$e.push(i);
/*  856 */           this.val$e.aaload();
/*  857 */           this.val$e.unbox(types[i]);
/*      */         }
/*  859 */         this.val$e.invoke_constructor_this(constructor.getSignature());
/*  860 */         this.val$e.goTo(end); }
/*      */ 
/*      */       public void processDefault() {
/*  863 */         this.val$e.throw_exception(Enhancer.ILLEGAL_ARGUMENT_EXCEPTION, "Constructor not found");
/*      */       }
/*      */     });
/*  866 */     e.aconst_null();
/*  867 */     e.invoke_static_this(SET_THREAD_CALLBACKS);
/*  868 */     e.return_value();
/*  869 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitMethods(ClassEmitter ce, List methods, List actualMethods) {
/*  873 */     CallbackGenerator[] generators = CallbackInfo.getGenerators(this.callbackTypes);
/*      */ 
/*  875 */     Map groups = new HashMap();
/*  876 */     Map indexes = new HashMap();
/*  877 */     Map originalModifiers = new HashMap();
/*  878 */     Map positions = CollectionUtils.getIndexMap(methods);
/*  879 */     Map declToBridge = new HashMap();
/*      */ 
/*  881 */     Iterator it1 = methods.iterator();
/*  882 */     Iterator it2 = actualMethods != null ? actualMethods.iterator() : null;
/*      */ 
/*  884 */     while (it1.hasNext()) {
/*  885 */       MethodInfo method = (MethodInfo)it1.next();
/*  886 */       Method actualMethod = it2 != null ? (Method)it2.next() : null;
/*  887 */       int index = this.filter.accept(actualMethod);
/*  888 */       if (index >= this.callbackTypes.length) {
/*  889 */         throw new IllegalArgumentException("Callback filter returned an index that is too large: " + index);
/*      */       }
/*  891 */       originalModifiers.put(method, new Integer(actualMethod != null ? actualMethod.getModifiers() : method.getModifiers()));
/*  892 */       indexes.put(method, new Integer(index));
/*  893 */       List group = (List)groups.get(generators[index]);
/*  894 */       if (group == null) {
/*  895 */         groups.put(generators[index], group = new ArrayList(methods.size()));
/*      */       }
/*  897 */       group.add(method);
/*      */ 
/*  901 */       if (TypeUtils.isBridge(actualMethod.getModifiers())) {
/*  902 */         Set bridges = (Set)declToBridge.get(actualMethod.getDeclaringClass());
/*  903 */         if (bridges == null) {
/*  904 */           bridges = new HashSet();
/*  905 */           declToBridge.put(actualMethod.getDeclaringClass(), bridges);
/*      */         }
/*  907 */         bridges.add(method.getSignature());
/*      */       }
/*      */     }
/*      */ 
/*  911 */     Map bridgeToTarget = new BridgeMethodResolver(declToBridge).resolveAll();
/*      */ 
/*  913 */     Set seenGen = new HashSet();
/*  914 */     CodeEmitter se = ce.getStaticHook();
/*  915 */     se.new_instance(THREAD_LOCAL);
/*  916 */     se.dup();
/*  917 */     se.invoke_constructor(THREAD_LOCAL, CSTRUCT_NULL);
/*  918 */     se.putfield("CGLIB$THREAD_CALLBACKS");
/*      */ 
/*  920 */     Object[] state = new Object[1];
/*  921 */     CallbackGenerator.Context context = new CallbackGenerator.Context() { private final Map val$originalModifiers;
/*      */       private final Map val$indexes;
/*      */       private final Map val$positions;
/*      */       private final Map val$bridgeToTarget;
/*      */ 
/*  923 */       public ClassLoader getClassLoader() { return Enhancer.this.getClassLoader(); }
/*      */ 
/*      */       public int getOriginalModifiers(MethodInfo method) {
/*  926 */         return ((Integer)this.val$originalModifiers.get(method)).intValue();
/*      */       }
/*      */       public int getIndex(MethodInfo method) {
/*  929 */         return ((Integer)this.val$indexes.get(method)).intValue();
/*      */       }
/*      */       public void emitCallback(CodeEmitter e, int index) {
/*  932 */         Enhancer.this.emitCurrentCallback(e, index);
/*      */       }
/*      */       public Signature getImplSignature(MethodInfo method) {
/*  935 */         return Enhancer.this.rename(method.getSignature(), ((Integer)this.val$positions.get(method)).intValue());
/*      */       }
/*      */ 
/*      */       public void emitInvoke(CodeEmitter e, MethodInfo method)
/*      */       {
/*  942 */         Signature bridgeTarget = (Signature)this.val$bridgeToTarget.get(method.getSignature());
/*  943 */         if (bridgeTarget != null)
/*      */         {
/*  950 */           e.invoke_virtual_this(bridgeTarget);
/*      */ 
/*  952 */           Type retType = method.getSignature().getReturnType();
/*      */ 
/*  963 */           if (!retType.equals(bridgeTarget.getReturnType()))
/*  964 */             e.checkcast(retType);
/*      */         }
/*      */         else {
/*  967 */           e.super_invoke(method.getSignature());
/*      */         }
/*      */       }
/*      */ 
/*  971 */       public CodeEmitter beginMethod(ClassEmitter ce, MethodInfo method) { CodeEmitter e = EmitUtils.begin_method(ce, method);
/*  972 */         if ((!Enhancer.this.interceptDuringConstruction) && (!TypeUtils.isAbstract(method.getModifiers())))
/*      */         {
/*  974 */           Label constructed = e.make_label();
/*  975 */           e.load_this();
/*  976 */           e.getfield("CGLIB$CONSTRUCTED");
/*  977 */           e.if_jump(154, constructed);
/*  978 */           e.load_this();
/*  979 */           e.load_args();
/*  980 */           e.super_invoke();
/*  981 */           e.return_value();
/*  982 */           e.mark(constructed);
/*      */         }
/*  984 */         return e;
/*      */       }
/*      */     };
/*  987 */     for (int i = 0; i < this.callbackTypes.length; i++) {
/*  988 */       CallbackGenerator gen = generators[i];
/*  989 */       if (!seenGen.contains(gen)) {
/*  990 */         seenGen.add(gen);
/*  991 */         List fmethods = (List)groups.get(gen);
/*  992 */         if (fmethods != null) {
/*      */           try {
/*  994 */             gen.generate(ce, context, fmethods);
/*  995 */             gen.generateStatic(se, context, fmethods);
/*      */           } catch (RuntimeException x) {
/*  997 */             throw x;
/*      */           } catch (Exception x) {
/*  999 */             throw new CodeGenerationException(x);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1004 */     se.return_value();
/* 1005 */     se.end_method();
/*      */   }
/*      */ 
/*      */   private void emitSetThreadCallbacks(ClassEmitter ce) {
/* 1009 */     CodeEmitter e = ce.begin_method(9, SET_THREAD_CALLBACKS, null);
/*      */ 
/* 1012 */     e.getfield("CGLIB$THREAD_CALLBACKS");
/* 1013 */     e.load_arg(0);
/* 1014 */     e.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_SET);
/* 1015 */     e.return_value();
/* 1016 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitSetStaticCallbacks(ClassEmitter ce) {
/* 1020 */     CodeEmitter e = ce.begin_method(9, SET_STATIC_CALLBACKS, null);
/*      */ 
/* 1023 */     e.load_arg(0);
/* 1024 */     e.putfield("CGLIB$STATIC_CALLBACKS");
/* 1025 */     e.return_value();
/* 1026 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitCurrentCallback(CodeEmitter e, int index) {
/* 1030 */     e.load_this();
/* 1031 */     e.getfield(getCallbackField(index));
/* 1032 */     e.dup();
/* 1033 */     Label end = e.make_label();
/* 1034 */     e.ifnonnull(end);
/* 1035 */     e.pop();
/* 1036 */     e.load_this();
/* 1037 */     e.invoke_static_this(BIND_CALLBACKS);
/* 1038 */     e.load_this();
/* 1039 */     e.getfield(getCallbackField(index));
/* 1040 */     e.mark(end);
/*      */   }
/*      */ 
/*      */   private void emitBindCallbacks(ClassEmitter ce) {
/* 1044 */     CodeEmitter e = ce.begin_method(26, BIND_CALLBACKS, null);
/*      */ 
/* 1047 */     Local me = e.make_local();
/* 1048 */     e.load_arg(0);
/* 1049 */     e.checkcast_this();
/* 1050 */     e.store_local(me);
/*      */ 
/* 1052 */     Label end = e.make_label();
/* 1053 */     e.load_local(me);
/* 1054 */     e.getfield("CGLIB$BOUND");
/* 1055 */     e.if_jump(154, end);
/* 1056 */     e.load_local(me);
/* 1057 */     e.push(1);
/* 1058 */     e.putfield("CGLIB$BOUND");
/*      */ 
/* 1060 */     e.getfield("CGLIB$THREAD_CALLBACKS");
/* 1061 */     e.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_GET);
/* 1062 */     e.dup();
/* 1063 */     Label found_callback = e.make_label();
/* 1064 */     e.ifnonnull(found_callback);
/* 1065 */     e.pop();
/*      */ 
/* 1067 */     e.getfield("CGLIB$STATIC_CALLBACKS");
/* 1068 */     e.dup();
/* 1069 */     e.ifnonnull(found_callback);
/* 1070 */     e.pop();
/* 1071 */     e.goTo(end);
/*      */ 
/* 1073 */     e.mark(found_callback);
/* 1074 */     e.checkcast(CALLBACK_ARRAY);
/* 1075 */     e.load_local(me);
/* 1076 */     e.swap();
/* 1077 */     for (int i = this.callbackTypes.length - 1; i >= 0; i--) {
/* 1078 */       if (i != 0) {
/* 1079 */         e.dup2();
/*      */       }
/* 1081 */       e.aaload(i);
/* 1082 */       e.checkcast(this.callbackTypes[i]);
/* 1083 */       e.putfield(getCallbackField(i));
/*      */     }
/*      */ 
/* 1086 */     e.mark(end);
/* 1087 */     e.return_value();
/* 1088 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private static String getCallbackField(int index) {
/* 1092 */     return "CGLIB$CALLBACK_" + index;
/*      */   }
/*      */ 
/*      */   public static abstract interface EnhancerKey
/*      */   {
/*      */     public abstract Object newInstance(String paramString, String[] paramArrayOfString, CallbackFilter paramCallbackFilter, Type[] paramArrayOfType, boolean paramBoolean1, boolean paramBoolean2, Long paramLong);
/*      */   }
/*      */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.Enhancer
 * JD-Core Version:    0.6.2
 */