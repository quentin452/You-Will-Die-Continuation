package fyresmodjam.misc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CreativeTabModjamMod extends CreativeTabs {
   public CreativeTabModjamMod(int par1, String par2Str) {
      super(par1, par2Str);
   }

   @SideOnly(Side.CLIENT)
   public ItemStack getIconItemStack() {
      return new ItemStack(ModjamMod.itemTrap, 1, 0);
   }

   public String getTranslatedTabLabel() {
      return "The \"You Will Die\" Mod";
   }

   public void displayAllReleventItems(List par1List) {
      try {
         ArrayList list = new ArrayList();
         Field[] arr$ = ModjamMod.class.getFields();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Field f = arr$[i$];
            if (f.getType() == Item.class) {
               Item item = (Item)f.get(ModjamMod.instance);
               if (item != null && item.getCreativeTab() != null) {
                  item.getSubItems(item, this, list);
               }
            } else if (f.getType() == Block.class) {
               Block block = (Block)f.get(ModjamMod.instance);
               if (block != null && block.getCreativeTabToDisplayOn() != null) {
                  block.getSubBlocks(block.getItem((World)null, 0, 0, 0), this, list);
               }
            }
         }

         Iterator i$ = list.iterator();

         while(i$.hasNext()) {
            ItemStack i = (ItemStack)i$.next();
            if (i != null && i.getItem() != null && i.getItem().getIconIndex(i) != null) {
               par1List.add(i);
            }
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }

   }

   public Item getTabIconItem() {
      return ModjamMod.itemTrap;
   }
}
