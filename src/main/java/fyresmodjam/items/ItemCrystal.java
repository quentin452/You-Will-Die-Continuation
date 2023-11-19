package fyresmodjam.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemCrystal extends ItemBlock {
   public IIcon texture;
   public static String[] names = new String[]{"Shining", "Void", "Firey"};
   public static String[] particleNames = new String[]{"spell", "portal", "flame"};
   public static Color[] colors = new Color[]{new Color(255, 255, 173), new Color(33, 0, 73), new Color(255, 55, 0)};

   public ItemCrystal() {
      super(ModjamMod.crystal);
      this.hasSubtypes = true;
      this.setMaxStackSize(1);
   }

   public void getSubItems(Item id, CreativeTabs creativeTab, List list) {
      for(int i = 0; i < names.length; ++i) {
         list.add(new ItemStack(id, 1, i));
      }

   }

   public String getItemDisplayName(ItemStack itemStack) {
      return names[itemStack.getItemDamage() % names.length] + " Crystal";
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
      this.texture = iconRegister.registerIcon("fyresmodjam:crystal_item");
      this.itemIcon = this.texture;
   }

   @SideOnly(Side.CLIENT)
   public int getColorFromItemStack(ItemStack itemStack, int i) {
      return colors[itemStack.getItemDamage() % colors.length].getRGB();
   }

   @SideOnly(Side.CLIENT)
   public int getSpriteNumber() {
      return 1;
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int par1) {
      return this.itemIcon;
   }

   public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
      return par1ItemStack;
   }

   @SideOnly(Side.CLIENT)
   public CreativeTabs getCreativeTab() {
      return CreativeTabs.tabMaterials;
   }
}
