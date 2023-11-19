package fyresmodjam.tileentities;

import fyresmodjam.items.ItemCrystal;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCrystal extends TileEntity {
   public static Random random = new Random();

   public void updateEntity() {
      super.updateEntity();
      if (random.nextInt(4) == 0) {
         this.worldObj.spawnParticle(ItemCrystal.particleNames[this.getBlockMetadata() % ItemCrystal.particleNames.length], (double)((float)this.xCoord + random.nextFloat()), (double)((float)this.yCoord + random.nextFloat()), (double)((float)this.zCoord + random.nextFloat()), 0.0D, 0.0D, 0.0D);
      }

   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeToNBT(par1NBTTagCompound);
   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readFromNBT(par1NBTTagCompound);
   }

   public Packet getDescriptionPacket() {
      NBTTagCompound tag = new NBTTagCompound();
      this.writeToNBT(tag);
      return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag);
   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.func_148857_g());
   }
}
