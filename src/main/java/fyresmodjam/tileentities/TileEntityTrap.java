package fyresmodjam.tileentities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.NewPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityTrap extends TileEntity {
   public static String[] settings = new String[]{"invisible to and damages all but player", "visible to all and damages all but player", "visible to all and only damages mobs", "decorative"};
   public String placedBy = null;
   public int setting = 0;

   public void updateEntity() {
      super.updateEntity();
      if (this.worldObj.isRemote) {
         this.spawnParticles();
      }

   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeToNBT(par1NBTTagCompound);
      if (this.placedBy != null) {
         par1NBTTagCompound.setString("PlacedBy", this.placedBy);
      }

      par1NBTTagCompound.setInteger("Setting", this.setting % settings.length);
   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("PlacedBy")) {
         this.placedBy = par1NBTTagCompound.getString("PlacedBy");
      } else {
         this.placedBy = null;
      }

      if (par1NBTTagCompound.hasKey("Setting")) {
         this.setting = par1NBTTagCompound.getInteger("Setting") % settings.length;
      }

   }

   public Packet getDescriptionPacket() {
      NBTTagCompound tag = new NBTTagCompound();
      this.writeToNBT(tag);
      return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag);
   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.func_148857_g());
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return INFINITE_EXTENT_AABB;
   }

   @SideOnly(Side.CLIENT)
   public double getMaxRenderDistanceSquared() {
      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
      return player == null || !player.getCommandSenderName().equals(this.placedBy) && this.setting == 0 ? 36.0D : 4096.0D;
   }

   @SideOnly(Side.CLIENT)
   public void spawnParticles() {
      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
      int type = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
      if (player != null && (!NewPacketHandler.trapsDisabled || this.placedBy != null) && (player.getCommandSenderName().equals(this.placedBy) || player.isSneaking() || this.setting != 0 || player.getEntityData().hasKey("Blessing") && player.getEntityData().getString("Blessing").equals("Scout")) && this.getDistanceFrom(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ) < (double)(player.getCommandSenderName().equals(this.placedBy) ? 4096.0F : 36.0F)) {
         if (type == 1) {
            if (ModjamMod.r.nextInt(5) == 0) {
               this.worldObj.spawnParticle("smoke", (double)((float)this.xCoord + 0.5F), (double)((float)this.yCoord + 0.175F), (double)((float)this.zCoord + 0.5F), (double)((ModjamMod.r.nextFloat() - 0.5F) / 16.0F), (double)(ModjamMod.r.nextFloat() / 16.0F), (double)((ModjamMod.r.nextFloat() - 0.5F) / 16.0F));
            }

            this.worldObj.spawnParticle("flame", (double)((float)this.xCoord + 0.5F), (double)((float)this.yCoord + 0.175F), (double)((float)this.zCoord + 0.5F), 0.0D, 0.0D, 0.0D);
         } else if (type == 2) {
            for(int i = 0; i < 3; ++i) {
               this.worldObj.spawnParticle("smoke", (double)((float)this.xCoord + 0.5F), (double)((float)this.yCoord + 0.175F), (double)((float)this.zCoord + 0.5F), (double)((ModjamMod.r.nextFloat() - 0.5F) / 16.0F), (double)(ModjamMod.r.nextFloat() / 16.0F), (double)((ModjamMod.r.nextFloat() - 0.5F) / 16.0F));
            }
         }
      }

   }
}
