package me.coder.combatindicator;

import com.gmail.filoghost.holographicdisplays.nms.v1_8_R2.EntityNMSArmorStand;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import com.gmail.filoghost.holographicdisplays.object.line.CraftTextLine;
import org.bukkit.World;

final class W extends CraftTextLine
{
  public W(U paramU, CraftHologram paramCraftHologram, String paramString)
  {
    super(paramCraftHologram, paramString);
  }

  public final void spawn(World paramWorld, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: dload_2
    //   3: dload 4
    //   5: dload 6
    //   7: invokespecial 9	com/gmail/filoghost/holographicdisplays/object/line/CraftTextLine:spawn	(Lorg/bukkit/World;DDD)V
    //   10: aload_0
    //   11: dup
    //   12: astore_1
    //   13: invokevirtual 10	me/coder/combatindicator/W:getNmsNameble	()Lcom/gmail/filoghost/holographicdisplays/nms/interfaces/entity/NMSNameable;
    //   16: dup
    //   17: astore_1
    //   18: instanceof 2
    //   21: ifeq +12 -> 33
    //   24: aload_1
    //   25: ldc 1
    //   27: iconst_1
    //   28: invokestatic 11	me/coder/combatindicator/i:a	(Ljava/lang/Object;Ljava/lang/String;Z)V
    //   31: return
    //   32: pop
    //   33: return
    //
    // Exception table:
    //   from	to	target	type
    //   24	31	32	me/coder/combatindicator/ad
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.W
 * JD-Core Version:    0.6.2
 */