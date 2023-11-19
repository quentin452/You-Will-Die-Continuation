package fyresmodjam.tileentities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;

public class TileEntityPillar extends TileEntity {
   public static String[] validBlessings = new String[]{"Miner", "Lumberjack", "Warrior", "Hunter", "Swamp", "Thief", "Ninja", "Mechanic", "Alchemist", "Scout", "Guardian", "Vampire", "Inferno", "Diver", "Berserker", "Loner", "Paratrooper", "Porcupine"};
   public static String[] blessingDescriptions = new String[]{"+25% breaking speed on stone and iron blocks, and +20% damage with pickaxes", "+25% breaking speed on wooden blocks, and +15% damage with axes", "+20% melee damage", "+20% projectile damage", "Attacks will slow enemies", "Enemies have a chance to drop gold nuggets", "While sneaking you are invisble and attacks on enemies with full health do double damage", "@@§ePASSIVE - §oYou disarm traps 3x as often and have 2x the chance to salvage disarmed traps.@@§eACTIVE - §oOnce per day, you may disarm and salvage target trap for free", "All potions act like wildcard potions", "You can see traps without sneaking, but take 25% more damage from traps", "Take 20% less damage from all sources", "Heal 7% of damage dealt to enemies and, in direct sunlight, you take 20% more damage and deal 20% less damage", "You don't take fire damage, do +35% damage while on fire, and take damage when wet", "You can breathe underwater", "@@§ePASSIVE - §oKills are added to berserk counter. (10 max)@@§eACTIVE - §oTurn on/off berserk mode. While berserk mode is active, you do 30% more damage, and berserk counter ticks down every 2 seconds", "The lower your health, the higher your damage, to a maximum of +35%", "You don't take fall damage", "Melee attackers take receive damage"};
   public String blessing = null;

   public void updateEntity() {
      super.updateEntity();
      if (this.worldObj.isRemote) {
         this.spawnParticles();
         if (ModjamMod.pillarGlow) {
            this.worldObj.updateLightByType(EnumSkyBlock.Block, this.xCoord, this.yCoord, this.zCoord);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void spawnParticles() {
      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
      if (player != null && player.getEntityData().hasKey("Blessing") && player.getEntityData().getString("Blessing").equals(this.blessing)) {
         for(int i = 0; i < 2; ++i) {
            this.worldObj.spawnParticle("portal", (double)this.xCoord + ModjamMod.r.nextDouble(), (double)this.yCoord + ModjamMod.r.nextDouble() * 2.0D, (double)this.zCoord + ModjamMod.r.nextDouble(), (ModjamMod.r.nextDouble() - 0.5D) * 2.0D, -ModjamMod.r.nextDouble(), (ModjamMod.r.nextDouble() - 0.5D) * 2.0D);
         }
      }

   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeToNBT(par1NBTTagCompound);
      if (this.blessing == null) {
         this.blessing = validBlessings[ModjamMod.r.nextInt(validBlessings.length)];
      }

      par1NBTTagCompound.setString("Blessing", this.blessing);
   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readFromNBT(par1NBTTagCompound);
      this.blessing = par1NBTTagCompound.hasKey("Blessing") ? par1NBTTagCompound.getString("Blessing") : validBlessings[ModjamMod.r.nextInt(validBlessings.length)];
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
}
