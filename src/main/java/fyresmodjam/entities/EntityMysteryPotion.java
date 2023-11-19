package fyresmodjam.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.CommonTickHandler;
import fyresmodjam.handlers.NewPacketHandler;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityMysteryPotion extends EntityThrowable {
   public EntityMysteryPotion(World par1World) {
      super(par1World);
   }

   public EntityMysteryPotion(World par1World, EntityLivingBase par2EntityLivingBase, int par3) {
      this(par1World, par2EntityLivingBase, (ItemStack)null);
   }

   public EntityMysteryPotion(World par1World, EntityLivingBase par2EntityLivingBase, ItemStack par4ItemStack) {
      super(par1World, par2EntityLivingBase);
      if (par4ItemStack != null) {
         int damage = par4ItemStack.getItemDamage();
         this.dataWatcher.updateObject(24, damage);
         if (damage % 13 < 12) {
            this.dataWatcher.updateObject(25, CommonTickHandler.worldData.potionDurations[damage % 13]);
         }
      }

   }

   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(24, 0);
      this.dataWatcher.addObject(25, 0);
   }

   @SideOnly(Side.CLIENT)
   public EntityMysteryPotion(World par1World, double par2, double par4, double par6, int par8) {
      this(par1World, par2, par4, par6, new ItemStack(Items.potionitem, 1, par8));
   }

   public EntityMysteryPotion(World par1World, double par2, double par4, double par6, ItemStack par4ItemStack) {
      super(par1World, par2, par4, par6);
      if (par4ItemStack != null) {
         int damage = par4ItemStack.getItemDamage();
         this.dataWatcher.updateObject(24, damage);
         this.dataWatcher.updateObject(25, CommonTickHandler.worldData.potionDurations[damage % 13]);
      }

   }

   protected float getGravityVelocity() {
      return 0.05F;
   }

   protected float func_70182_d() {
      return 0.5F;
   }

   protected float func_70183_g() {
      return -20.0F;
   }

   protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
      if (!this.worldObj.isRemote) {
         AxisAlignedBB axisalignedbb = this.boundingBox.expand(4.0D, 2.0D, 4.0D);
         List list1 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
         if (list1 != null && !list1.isEmpty()) {
            int type = this.getDataWatcher().getWatchableObjectInt(24) % 13;
            int j = type >= 12 ? 5 + ModjamMod.r.nextInt(26) : this.getDataWatcher().getWatchableObjectInt(25);
            int damage = false;
            int damage;
            if (type < 12 && (!this.getThrower().getEntityData().hasKey("Blessing") || !this.getThrower().getEntityData().getString("Blessing").equals("Alchemist"))) {
               damage = CommonTickHandler.worldData.potionValues[type];
            } else {
               for(damage = ModjamMod.r.nextInt(Potion.potionTypes.length); Potion.potionTypes[damage] == null; damage = ModjamMod.r.nextInt(Potion.potionTypes.length)) {
               }
            }

            Iterator iterator = list1.iterator();

            while(iterator.hasNext()) {
               EntityLivingBase entitylivingbase = (EntityLivingBase)iterator.next();
               double d0 = this.getDistanceSqToEntity(entitylivingbase);
               if (d0 < 16.0D) {
                  if (Potion.potionTypes[damage].isInstant()) {
                     Potion.potionTypes[damage].affectEntity(this.getThrower(), entitylivingbase, 1, 1.0D);
                  } else {
                     entitylivingbase.addPotionEffect(new PotionEffect(damage, j * 20, 1, false));
                  }
               }
            }

            if (this.getThrower() instanceof EntityPlayer) {
               EntityPlayer par3EntityPlayer = (EntityPlayer)this.getThrower();
               String name;
               Potion potion;
               if (type < 12 && (!this.getThrower().getEntityData().hasKey("Blessing") || !this.getThrower().getEntityData().getString("Blessing").equals("Alchemist"))) {
                  if (!par3EntityPlayer.getEntityData().hasKey("PotionKnowledge")) {
                     par3EntityPlayer.getEntityData().setIntArray("PotionKnowledge", new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
                  }

                  if (par3EntityPlayer.getEntityData().getIntArray("PotionKnowledge")[type] == -1) {
                     par3EntityPlayer.getEntityData().getIntArray("PotionKnowledge")[type] = 1;
                     NewPacketHandler.UPDATE_POTION_KNOWLEDGE.sendToPlayer(par3EntityPlayer, par3EntityPlayer.getEntityData().getIntArray("PotionKnowledge"));
                     potion = Potion.potionTypes[CommonTickHandler.worldData.potionValues[type]];
                     name = StatCollector.translateToLocal(potion.getName()) + " Potion";
                     if (!potion.isInstant()) {
                        int time = CommonTickHandler.worldData.potionDurations[type];
                        name = name + " (" + time + " seconds)";
                     }

                     NewPacketHandler.SEND_MESSAGE.sendToPlayer(par3EntityPlayer, "§oYou learnt Mystery Potion #" + (type + 1) + " was a " + name + "!");
                  }
               } else {
                  potion = Potion.potionTypes[damage];
                  name = StatCollector.translateToLocal(potion.getName()) + " Potion";
                  if (!potion.isInstant()) {
                     name = name + " (" + j + " seconds)";
                  }

                  NewPacketHandler.SEND_MESSAGE.sendToPlayer(par3EntityPlayer, "§oYou threw a " + name + ".");
               }
            }
         }

         this.worldObj.playAuxSFX(2002, (int)Math.round(this.posX), (int)Math.round(this.posY), (int)Math.round(this.posZ), 1);
         this.setDead();
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
   }
}
