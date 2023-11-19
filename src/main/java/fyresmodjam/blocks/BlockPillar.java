package fyresmodjam.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.misc.EntityStatHelper;
import fyresmodjam.tileentities.TileEntityPillar;
import fyresmodjam.worldgen.WorldGenTrapsTowersAndMore;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPillar extends BlockContainer {
   public BlockPillar() {
      super(Material.rock);
      this.setLightOpacity(0);
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("fyresmodjam:pillar");
   }

   public int idDropped(int par1, Random par2Random, int par3) {
      return 0;
   }

   public int getDamageValue(World par1World, int par2, int par3, int par4) {
      int damage = 0;
      if (ModjamMod.showAllPillarsInCreative) {
         int meta = par1World.getBlockMetadata(par2, par3, par4);
         TileEntity te = par1World.getTileEntity(par2, par3 - (meta == 1 ? 1 : 0), par4);
         if (te != null && te instanceof TileEntityPillar) {
            for(int i = 0; i < TileEntityPillar.validBlessings.length; ++i) {
               if (TileEntityPillar.validBlessings[i].equals(((TileEntityPillar)te).blessing)) {
                  damage = i + 1;
                  break;
               }
            }
         }
      }

      return damage;
   }

   public ItemStack getPickBlock(MovingObjectPosition object, World par1World, int par2, int par3, int par4) {
      return new ItemStack(ModjamMod.itemPillar);
   }

   public boolean canHarvestBlock(EntityPlayer par1EntityPlayer, int par2) {
      return false;
   }

   public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
      if (par1World.getBlockMetadata(par2, par3, par4) % 2 == 1) {
         --par3;
      }

      TileEntity te = par1World.getTileEntity(par2, par3, par4);
      int i;
      if (par5EntityPlayer.getHeldItem() != null && par5EntityPlayer.getHeldItem().getItem() != null && par5EntityPlayer.getHeldItem().getItemDamage() == 1 && par5EntityPlayer.getHeldItem().getItem() == ModjamMod.sceptre) {
         if (!par1World.isRemote) {
            int damage = 0;
            if (te != null && te instanceof TileEntityPillar) {
               for(i = 0; i < TileEntityPillar.validBlessings.length; ++i) {
                  if (TileEntityPillar.validBlessings[i].equals(((TileEntityPillar)te).blessing)) {
                     damage = i + 1;
                     break;
                  }
               }
            }

            if (!par5EntityPlayer.capabilities.isCreativeMode) {
               --par5EntityPlayer.getHeldItem().stackSize;
            }

            par1World.spawnEntityInWorld(new EntityItem(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), new ItemStack(ModjamMod.itemPillar, 1, damage)));
            par1World.setBlockToAir(par2, par3, par4);
            NewPacketHandler.SEND_MESSAGE.sendToPlayer(par5EntityPlayer, "§e§oThe pillar deconstructs before you.");
         }
      } else if (te != null && te instanceof TileEntityPillar && (!par5EntityPlayer.getEntityData().hasKey("Blessing") || !par5EntityPlayer.getEntityData().getString("Blessing").equals(((TileEntityPillar)te).blessing))) {
         boolean skip = false;

         for(i = 0; i < par1World.loadedEntityList.size(); ++i) {
            Entity e = (Entity)par1World.loadedEntityList.get(i);
            if (e instanceof EntityMob) {
               double xDiff = (double)par2 - e.posX;
               double yDiff = (double)par3 - e.posY;
               double zDiff = (double)par4 - e.posZ;
               if (!(Math.abs(yDiff) > 4.0D)) {
                  double dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
                  if (dist <= 14.0D) {
                     skip = true;
                  }
               }
            }
         }

         if (!skip) {
            if (((TileEntityPillar)te).blessing != null) {
               EntityStatHelper.giveStat(par5EntityPlayer, "Blessing", ((TileEntityPillar)te).blessing);
               EntityStatHelper.giveStat(par5EntityPlayer, "BlessingActive", false);
               EntityStatHelper.giveStat(par5EntityPlayer, "BlessingCounter", 0);
               EntityStatHelper.giveStat(par5EntityPlayer, "BlessingCooldown", 0);
               if (!par1World.isRemote) {
                  par1World.playSoundAtEntity(par5EntityPlayer, "fyresmodjam:pillarActivated", 1.0F, 1.0F);
                  NewPacketHandler.SEND_MESSAGE.sendToPlayer(par5EntityPlayer, "Activated blessing of the " + ((TileEntityPillar)te).blessing + ".");
               }
            }
         } else if (!par1World.isRemote) {
            NewPacketHandler.SEND_MESSAGE.sendToPlayer(par5EntityPlayer, "§cCannot activate pillar with monsters nearby!");
         }
      }

      return true;
   }

   public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
      super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
      if (!par1World.isRemote && !WorldGenTrapsTowersAndMore.genning) {
         int i1 = par1World.getBlockMetadata(par2, par3, par4);
         if (i1 % 2 == 0) {
            if (par1World.isAirBlock(par2, par3 + 1, par4) || par1World.getBlock(par2, par3 + 1, par4) != par1World.getBlock(par2, par3, par4)) {
               par1World.setBlockToAir(par2, par3, par4);
            }
         } else if (par1World.isAirBlock(par2, par3 - 1, par4) || par1World.getBlock(par2, par3 - 1, par4) != par1World.getBlock(par2, par3, par4)) {
            par1World.setBlockToAir(par2, par3, par4);
         }
      }

   }

   public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
      return par3 >= 255 ? false : super.canPlaceBlockAt(par1World, par2, par3, par4) && super.canPlaceBlockAt(par1World, par2, par3 + 1, par4);
   }

   public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      if (l % 2 == 0) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return false;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean hasTileEntity(int metadata) {
      return metadata % 2 == 0;
   }

   public TileEntity createNewTileEntity(World world, int i) {
      return new TileEntityPillar();
   }

   public int getMobilityFlag() {
      return 2;
   }

   public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {
      if (par6EntityPlayer.capabilities.isCreativeMode && par5 >= 2 && par1World.getBlock(par2, par3 - 1, par4) == this) {
         par1World.setBlockToAir(par2, par3 - 1, par4);
      } else {
         super.onBlockHarvested(par1World, par2, par3, par4, par5, par6EntityPlayer);
      }

   }

   @SideOnly(Side.CLIENT)
   public int getLightValue(IBlockAccess world, int x, int y, int z) {
      Block block = world.getBlock(x, y, z);
      TileEntity te = world.getTileEntity(x, y, z);
      if (block != null && block != this) {
         return block.getLightValue(world, x, y, z);
      } else {
         if (ModjamMod.pillarGlow && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.isRemote) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player != null && te != null && te instanceof TileEntityPillar && ((TileEntityPillar)te).blessing != null && ((TileEntityPillar)te).blessing.equals(player.getEntityData().getString("Blessing"))) {
               return 4;
            }
         }

         return 0;
      }
   }
}
