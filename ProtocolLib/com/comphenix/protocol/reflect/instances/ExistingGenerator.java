/*     */ package com.comphenix.protocol.reflect.instances;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class ExistingGenerator
/*     */   implements InstanceProvider
/*     */ {
/*  84 */   private Node root = new Node(null, null, 0);
/*     */ 
/*     */   public static ExistingGenerator fromObjectFields(Object object)
/*     */   {
/*  99 */     if (object == null) {
/* 100 */       throw new IllegalArgumentException("Object cannot be NULL.");
/*     */     }
/* 102 */     return fromObjectFields(object, object.getClass());
/*     */   }
/*     */ 
/*     */   public static ExistingGenerator fromObjectFields(Object object, Class<?> type)
/*     */   {
/* 115 */     ExistingGenerator generator = new ExistingGenerator();
/*     */ 
/* 118 */     if (object == null)
/* 119 */       throw new IllegalArgumentException("Object cannot be NULL.");
/* 120 */     if (type == null)
/* 121 */       throw new IllegalArgumentException("Type cannot be NULL.");
/* 122 */     if (!type.isAssignableFrom(object.getClass())) {
/* 123 */       throw new IllegalArgumentException("Type must be a superclass or be the same type.");
/*     */     }
/*     */ 
/* 126 */     for (Field field : FuzzyReflection.fromClass(type, true).getFields()) {
/*     */       try {
/* 128 */         Object value = FieldUtils.readField(field, object, true);
/*     */ 
/* 131 */         if (value != null) {
/* 132 */           generator.addObject(field.getType(), value);
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/* 139 */     return generator;
/*     */   }
/*     */ 
/*     */   public static ExistingGenerator fromObjectArray(Object[] values)
/*     */   {
/* 148 */     ExistingGenerator generator = new ExistingGenerator();
/*     */ 
/* 150 */     for (Object value : values) {
/* 151 */       generator.addObject(value);
/*     */     }
/* 153 */     return generator;
/*     */   }
/*     */ 
/*     */   private void addObject(Object value) {
/* 157 */     if (value == null) {
/* 158 */       throw new IllegalArgumentException("Value cannot be NULL.");
/*     */     }
/* 160 */     addObject(value.getClass(), value);
/*     */   }
/*     */ 
/*     */   private void addObject(Class<?> type, Object value) {
/* 164 */     Node node = getLeafNode(this.root, type, false);
/*     */ 
/* 167 */     node.setValue(value);
/*     */   }
/*     */ 
/*     */   private Node getLeafNode(Node start, Class<?> type, boolean readOnly) {
/* 171 */     Class[] path = getHierachy(type);
/* 172 */     Node current = start;
/*     */ 
/* 174 */     for (int i = 0; i < path.length; i++) {
/* 175 */       Node next = getNext(current, path[i], readOnly);
/*     */ 
/* 178 */       if ((next == null) && (readOnly)) {
/* 179 */         current = null;
/* 180 */         break;
/*     */       }
/*     */ 
/* 183 */       current = next;
/*     */     }
/*     */ 
/* 187 */     return current;
/*     */   }
/*     */ 
/*     */   private Node getNext(Node current, Class<?> clazz, boolean readOnly) {
/* 191 */     Node next = current.getChild(clazz);
/*     */ 
/* 194 */     if ((next == null) && (!readOnly)) {
/* 195 */       next = current.addChild(new Node(clazz, null, current.getLevel() + 1));
/*     */     }
/*     */ 
/* 199 */     if ((next != null) && (!readOnly) && (!clazz.isInterface())) {
/* 200 */       for (Class clazzInterface : clazz.getInterfaces()) {
/* 201 */         getLeafNode(this.root, clazzInterface, readOnly).addChild(next);
/*     */       }
/*     */     }
/* 204 */     return next;
/*     */   }
/*     */ 
/*     */   private Node getLowestLeaf(Node current) {
/* 208 */     Node candidate = current;
/*     */ 
/* 211 */     for (Node child : current.getChildren()) {
/* 212 */       Node subtree = getLowestLeaf(child);
/*     */ 
/* 215 */       if ((subtree.getValue() != null) && (candidate.getLevel() < subtree.getLevel())) {
/* 216 */         candidate = subtree;
/*     */       }
/*     */     }
/*     */ 
/* 220 */     return candidate;
/*     */   }
/*     */ 
/*     */   private Class<?>[] getHierachy(Class<?> type) {
/* 224 */     LinkedList levels = Lists.newLinkedList();
/*     */ 
/* 227 */     for (; type != null; type = type.getSuperclass()) {
/* 228 */       levels.addFirst(type);
/*     */     }
/*     */ 
/* 231 */     return (Class[])levels.toArray(new Class[0]);
/*     */   }
/*     */ 
/*     */   public Object create(@Nullable Class<?> type)
/*     */   {
/* 237 */     Node node = getLeafNode(this.root, type, true);
/*     */ 
/* 240 */     if (node != null) {
/* 241 */       node = getLowestLeaf(node);
/*     */     }
/*     */ 
/* 245 */     if (node != null) {
/* 246 */       return node.getValue();
/*     */     }
/* 248 */     return null;
/*     */   }
/*     */ 
/*     */   private static final class Node
/*     */   {
/*     */     private Map<Class<?>, Node> children;
/*     */     private Class<?> key;
/*     */     private Object value;
/*     */     private int level;
/*     */ 
/*     */     public Node(Class<?> key, Object value, int level)
/*     */     {
/*  51 */       this.children = new HashMap();
/*  52 */       this.key = key;
/*  53 */       this.value = value;
/*  54 */       this.level = level;
/*     */     }
/*     */ 
/*     */     public Node addChild(Node node) {
/*  58 */       this.children.put(node.key, node);
/*  59 */       return node;
/*     */     }
/*     */ 
/*     */     public int getLevel() {
/*  63 */       return this.level;
/*     */     }
/*     */ 
/*     */     public Collection<Node> getChildren() {
/*  67 */       return this.children.values();
/*     */     }
/*     */ 
/*     */     public Object getValue() {
/*  71 */       return this.value;
/*     */     }
/*     */ 
/*     */     public void setValue(Object value) {
/*  75 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public Node getChild(Class<?> clazz) {
/*  79 */       return (Node)this.children.get(clazz);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.instances.ExistingGenerator
 * JD-Core Version:    0.6.2
 */