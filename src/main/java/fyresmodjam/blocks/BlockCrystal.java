package fyresmodjam.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.items.ItemCrystal;
import fyresmodjam.tileentities.TileEntityCrystal;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCrystal extends BlockContainer {
   public BlockCrystal() {
      super(Material.glass);
      this.setBlockBounds(0.25F, 0.1F, 0.25F, 0.75F, 0.9F, 0.75F);
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister par1IconRegister) {
      this.blockIcon = ((ItemCrystal)ModjamMod.crystalItem).texture;
   }

   public boolean canHarvestBlock(EntityPlayer player, int i) {
      return false;
   }

   public TileEntity createNewTileEntity(World world, int i) {
      return new TileEntityCrystal();
   }

   public boolean hasTileEntity(int meta) {
      return true;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockAccess blockAccess, int i, int i2, int i3, int i4) {
      return false;
   }

   public ItemStack getPickBlock(MovingObjectPosition object, World par1World, int par2, int par3, int par4) {
      return new ItemStack(ModjamMod.crystalItem);
   }

   public int getDamageValue(World world, int i, int i2, int i3) {
      return world.getBlockMetadata(i, i2, i3);
   }

   public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
      par1World.setBlockMetadataWithNotify(par2, par3, par4, par6ItemStack.getItemDamage(), 0);
   }

   public int getLightValue(IBlockAccess world, int x, int y, int z) {
      return 10;
   }
}
