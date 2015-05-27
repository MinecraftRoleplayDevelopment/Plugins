/*     */ package com.comphenix.protocol.wrappers.nbt.io;
/*     */ 
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtBase;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtCompound;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtFactory;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtList;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtType;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtVisitor;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.IntBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ 
/*     */ public class NbtConfigurationSerializer
/*     */ {
/*     */   public static final String TYPE_DELIMITER = "$";
/*  43 */   public static final NbtConfigurationSerializer DEFAULT = new NbtConfigurationSerializer();
/*     */   private String dataTypeDelimiter;
/*     */ 
/*     */   public NbtConfigurationSerializer()
/*     */   {
/*  51 */     this.dataTypeDelimiter = "$";
/*     */   }
/*     */ 
/*     */   public NbtConfigurationSerializer(String dataTypeDelimiter)
/*     */   {
/*  59 */     this.dataTypeDelimiter = dataTypeDelimiter;
/*     */   }
/*     */ 
/*     */   public String getDataTypeDelimiter()
/*     */   {
/*  67 */     return this.dataTypeDelimiter;
/*     */   }
/*     */ 
/*     */   public <TType> void serialize(NbtBase<TType> value, final ConfigurationSection destination)
/*     */   {
/*  76 */     value.accept(new NbtVisitor() {
/*  77 */       private ConfigurationSection current = destination;
/*     */       private List<Object> currentList;
/*  83 */       private Map<ConfigurationSection, Integer> workingIndex = Maps.newHashMap();
/*     */ 
/*     */       public boolean visitEnter(NbtCompound compound)
/*     */       {
/*  87 */         this.current = this.current.createSection(compound.getName());
/*  88 */         return true;
/*     */       }
/*     */ 
/*     */       public boolean visitEnter(NbtList<?> list)
/*     */       {
/*  93 */         Integer listIndex = getNextIndex();
/*  94 */         String name = getEncodedName(list, listIndex);
/*     */ 
/*  96 */         if (list.getElementType().isComposite())
/*     */         {
/*  98 */           this.current = this.current.createSection(name);
/*  99 */           this.workingIndex.put(this.current, Integer.valueOf(0));
/*     */         } else {
/* 101 */           this.currentList = Lists.newArrayList();
/* 102 */           this.current.set(name, this.currentList);
/*     */         }
/* 104 */         return true;
/*     */       }
/*     */ 
/*     */       public boolean visitLeave(NbtCompound compound)
/*     */       {
/* 109 */         this.current = this.current.getParent();
/* 110 */         return true;
/*     */       }
/*     */ 
/*     */       public boolean visitLeave(NbtList<?> list)
/*     */       {
/* 116 */         if (this.currentList != null)
/*     */         {
/* 118 */           this.currentList = null;
/*     */         }
/*     */         else {
/* 121 */           this.workingIndex.remove(this.current);
/* 122 */           this.current = this.current.getParent();
/*     */         }
/* 124 */         return true;
/*     */       }
/*     */ 
/*     */       public boolean visit(NbtBase<?> node)
/*     */       {
/* 130 */         if (this.currentList == null) {
/* 131 */           Integer listIndex = getNextIndex();
/* 132 */           String name = getEncodedName(node, listIndex);
/*     */ 
/* 135 */           this.current.set(name, NbtConfigurationSerializer.this.fromNodeValue(node));
/*     */         }
/*     */         else {
/* 138 */           this.currentList.add(NbtConfigurationSerializer.this.fromNodeValue(node));
/*     */         }
/* 140 */         return true;
/*     */       }
/*     */ 
/*     */       private Integer getNextIndex() {
/* 144 */         Integer listIndex = (Integer)this.workingIndex.get(this.current);
/*     */ 
/* 146 */         if (listIndex != null) {
/* 147 */           return (Integer)this.workingIndex.put(this.current, Integer.valueOf(listIndex.intValue() + 1));
/*     */         }
/* 149 */         return null;
/*     */       }
/*     */ 
/*     */       private String getEncodedName(NbtBase<?> node, Integer index)
/*     */       {
/* 154 */         if (index != null) {
/* 155 */           return index + NbtConfigurationSerializer.this.dataTypeDelimiter + node.getType().getRawID();
/*     */         }
/* 157 */         return node.getName() + NbtConfigurationSerializer.this.dataTypeDelimiter + node.getType().getRawID();
/*     */       }
/*     */ 
/*     */       private String getEncodedName(NbtList<?> node, Integer index) {
/* 161 */         if (index != null) {
/* 162 */           return index + NbtConfigurationSerializer.this.dataTypeDelimiter + node.getElementType().getRawID();
/*     */         }
/* 164 */         return node.getName() + NbtConfigurationSerializer.this.dataTypeDelimiter + node.getElementType().getRawID();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public <TType> NbtWrapper<TType> deserialize(ConfigurationSection root, String nodeName)
/*     */   {
/* 177 */     return readNode(root, nodeName);
/*     */   }
/*     */ 
/*     */   public NbtCompound deserializeCompound(YamlConfiguration root, String nodeName)
/*     */   {
/* 187 */     return (NbtCompound)readNode(root, nodeName);
/*     */   }
/*     */ 
/*     */   public <T> NbtList<T> deserializeList(YamlConfiguration root, String nodeName)
/*     */   {
/* 198 */     return (NbtList)readNode(root, nodeName);
/*     */   }
/*     */ 
/*     */   private NbtWrapper<?> readNode(ConfigurationSection parent, String name)
/*     */   {
/* 203 */     String[] decoded = getDecodedName(name);
/* 204 */     Object node = parent.get(name);
/* 205 */     NbtType type = NbtType.TAG_END;
/*     */ 
/* 208 */     if (node == null) {
/* 209 */       for (String key : parent.getKeys(false)) {
/* 210 */         decoded = getDecodedName(key);
/*     */ 
/* 213 */         if (decoded[0].equals(name)) {
/* 214 */           node = parent.get(decoded[0]);
/* 215 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 220 */       if (node == null) {
/* 221 */         throw new IllegalArgumentException("Unable to find node " + name + " in " + parent);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 226 */     if (decoded.length > 1) {
/* 227 */       type = NbtType.getTypeFromID(Integer.parseInt(decoded[1]));
/*     */     }
/*     */ 
/* 231 */     if ((node instanceof ConfigurationSection))
/*     */     {
/* 233 */       if (type != NbtType.TAG_END) {
/* 234 */         NbtList list = NbtFactory.ofList(decoded[0], new Object[0]);
/* 235 */         ConfigurationSection section = (ConfigurationSection)node;
/* 236 */         List sorted = sortSet(section.getKeys(false));
/*     */ 
/* 239 */         for (String key : sorted) {
/* 240 */           NbtBase base = readNode(section, key.toString());
/* 241 */           base.setName("");
/* 242 */           ((List)list.getValue()).add(base);
/*     */         }
/* 244 */         return (NbtWrapper)list;
/*     */       }
/*     */ 
/* 247 */       NbtCompound compound = NbtFactory.ofCompound(decoded[0]);
/* 248 */       ConfigurationSection section = (ConfigurationSection)node;
/*     */ 
/* 251 */       for (String key : section.getKeys(false))
/* 252 */         compound.put(readNode(section, key));
/* 253 */       return (NbtWrapper)compound;
/*     */     }
/*     */ 
/* 258 */     if (type == NbtType.TAG_END) {
/* 259 */       throw new IllegalArgumentException("Cannot find encoded type of " + decoded[0] + " in " + name);
/*     */     }
/*     */ 
/* 262 */     if ((node instanceof List)) {
/* 263 */       NbtList list = NbtFactory.ofList(decoded[0], new Object[0]);
/* 264 */       list.setElementType(type);
/*     */ 
/* 266 */       for (Iterator i$ = ((List)node).iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 267 */         list.addClosest(toNodeValue(value, type));
/*     */       }
/*     */ 
/* 271 */       return (NbtWrapper)list;
/*     */     }
/*     */ 
/* 275 */     return NbtFactory.ofWrapper(type, decoded[0], toNodeValue(node, type));
/*     */   }
/*     */ 
/*     */   private List<String> sortSet(Set<String> unsorted)
/*     */   {
/* 282 */     List sorted = new ArrayList(unsorted);
/*     */ 
/* 284 */     Collections.sort(sorted, new Comparator()
/*     */     {
/*     */       public int compare(String o1, String o2)
/*     */       {
/* 288 */         int index1 = Integer.parseInt(NbtConfigurationSerializer.getDecodedName(o1)[0]);
/* 289 */         int index2 = Integer.parseInt(NbtConfigurationSerializer.getDecodedName(o2)[0]);
/* 290 */         return Ints.compare(index1, index2);
/*     */       }
/*     */     });
/* 293 */     return sorted;
/*     */   }
/*     */ 
/*     */   private Object fromNodeValue(NbtBase<?> base)
/*     */   {
/* 298 */     if (base.getType() == NbtType.TAG_INT_ARRAY) {
/* 299 */       return toByteArray((int[])base.getValue());
/*     */     }
/* 301 */     return base.getValue();
/*     */   }
/*     */ 
/*     */   public Object toNodeValue(Object value, NbtType type)
/*     */   {
/* 306 */     if (type == NbtType.TAG_INT_ARRAY) {
/* 307 */       return toIntegerArray((byte[])value);
/*     */     }
/* 309 */     return value;
/*     */   }
/*     */ 
/*     */   private static byte[] toByteArray(int[] data)
/*     */   {
/* 318 */     ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
/* 319 */     IntBuffer intBuffer = byteBuffer.asIntBuffer();
/*     */ 
/* 321 */     intBuffer.put(data);
/* 322 */     return byteBuffer.array();
/*     */   }
/*     */ 
/*     */   private static int[] toIntegerArray(byte[] data)
/*     */   {
/* 333 */     IntBuffer source = ByteBuffer.wrap(data).asIntBuffer();
/* 334 */     IntBuffer copy = IntBuffer.allocate(source.capacity());
/*     */ 
/* 336 */     copy.put(source);
/* 337 */     return copy.array();
/*     */   }
/*     */ 
/*     */   private static String[] getDecodedName(String nodeName) {
/* 341 */     int delimiter = nodeName.lastIndexOf('$');
/*     */ 
/* 343 */     if (delimiter > 0) {
/* 344 */       return new String[] { nodeName.substring(0, delimiter), nodeName.substring(delimiter + 1) };
/*     */     }
/* 346 */     return new String[] { nodeName };
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.io.NbtConfigurationSerializer
 * JD-Core Version:    0.6.2
 */