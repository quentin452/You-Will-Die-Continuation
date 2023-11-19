package fyresmodjam.misc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.CommonTickHandler;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.worldgen.FyresWorldData;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity.EnumEntitySize;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class EntityStatHelper {
   public static String[] knowledge = new String[]{"Clueless", "Novice", "Competent", "Talented", "Expert", "Professional", "Master", "Legendary"};
   public static int[] killCount = new int[]{0, 10, 25, 50, 100, 250, 500, 1000};
   public static float[] damageBonus = new float[]{0.0F, 0.01F, 0.025F, 0.05F, 0.075F, 0.1F, 0.15F, 0.2F};
   public static String[] damageBonusString = new String[]{"0", "1", "2.5", "5", "7.5", "10", "15", "20"};
   public static HashMap statTrackersByClass = new HashMap();
   public static ArrayList genericTrackers = new ArrayList();
   public static ArrayList temp = new ArrayList();
   public static boolean b = false;

   public static String getUnalteredName(Entity entity) {
      String s = EntityList.getEntityString(entity);
      if (s == null) {
         s = "generic";
      }

      return StatCollector.translateToLocal("entity." + s + ".name");
   }

   public static String getUnalteredItemName(Item item) {
      return StatCollector.translateToLocal(item.getUnlocalizedName() + ".name");
   }

   public static void addStatTracker(EntityStatHelper.EntityStatTracker statTracker) {
      if (statTracker.classes != null) {
         Class[] arr$ = statTracker.classes;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Class c = arr$[i$];
            statTrackersByClass.put(c, statTracker);
         }

         if (statTracker.instanceAllowed) {
            genericTrackers.add(statTracker);
         }
      }

   }

   public static Entity giveStat(Entity entity, String name, Object value) {
      if (entity != null && name != null && value != null) {
         entity.getEntityData().setString(name, value.toString());
      }

      return entity;
   }

   public static Entity setName(EntityLiving entity, String name) {
      entity.setCustomNameTag(name);
      return entity;
   }

   public static String getStat(Entity entity, String name) {
      String s = null;
      if (entity.getEntityData() != null && entity.getEntityData().hasKey(name)) {
         s = entity.getEntityData().getString(name);
      }

      return s;
   }

   public static boolean hasStat(Entity entity, String name) {
      return entity.getEntityData() != null && entity.getEntityData().hasKey(name);
   }

   @SubscribeEvent
   public void entityJoinWorld(EntityJoinWorldEvent event) {
      if (!event.world.isRemote) {
         processEntity(event.entity, ModjamMod.r);
         boolean isClone = true;
         isClone = event.entity.getEntityData().hasKey("isClone") ? event.entity.getEntityData().getBoolean("isClone") : false;
         if (CommonTickHandler.worldData != null && CommonTickHandler.worldData.getDisadvantage().equals("Increased Mob Spawn") && event.entity instanceof EntityMob && !(event.entity instanceof EntityDragon) && !isClone && ModjamMod.r.nextInt(3) == 0) {
            event.entity.getEntityData().setBoolean("isClone", true);
            Entity entityNew = null;

            try {
               Constructor[] constructors = event.entity.getClass().getConstructors();

               for(int i = 0; i < constructors.length; ++i) {
                  Class[] parameters = constructors[i].getParameterTypes();
                  if (parameters.length == 1 && parameters[0].equals(World.class)) {
                     entityNew = (Entity)event.entity.getClass().getConstructors()[i].newInstance(event.world);
                  }
               }
            } catch (Exception var7) {
               var7.printStackTrace();
            }

            if (entityNew != null) {
               entityNew.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, event.entity.rotationPitch);
               entityNew.getEntityData().setBoolean("isClone", true);
               entityNew.dimension = event.entity.dimension;
               CommonTickHandler.addLater.add(entityNew);
            }
         }

         if (event.entity instanceof EntityPlayer) {
            if (!event.entity.getEntityData().hasKey("Blessing") && CommonTickHandler.worldData.blessingByPlayer.containsKey(event.entity.getCommandSenderName())) {
               event.entity.getEntityData().setString("Blessing", (String)CommonTickHandler.worldData.blessingByPlayer.get(event.entity.getCommandSenderName()));
               NewPacketHandler.UPDATE_BLESSING.sendToPlayer((EntityPlayer)event.entity, event.entity.getEntityData().getString("Blessing"));
               CommonTickHandler.worldData.blessingByPlayer.remove(event.entity.getCommandSenderName());
               CommonTickHandler.worldData.markDirty();
            }

            if (!event.entity.getEntityData().hasKey("PotionKnowledge") && CommonTickHandler.worldData.potionKnowledgeByPlayer.containsKey(event.entity.getCommandSenderName())) {
               event.entity.getEntityData().setIntArray("PotionKnowledge", (int[])CommonTickHandler.worldData.potionKnowledgeByPlayer.get(event.entity.getCommandSenderName()));
               NewPacketHandler.UPDATE_POTION_KNOWLEDGE.sendToPlayer((EntityPlayer)event.entity, event.entity.getEntityData().getIntArray("PotionKnowledge"));
               CommonTickHandler.worldData.potionKnowledgeByPlayer.remove(event.entity.getCommandSenderName());
               CommonTickHandler.worldData.markDirty();
            }

            if (!event.entity.getEntityData().hasKey("KillStats") && CommonTickHandler.worldData.killStatsByPlayer.containsKey(event.entity.getCommandSenderName())) {
               event.entity.getEntityData().setTag("KillStats", (NBTBase)CommonTickHandler.worldData.killStatsByPlayer.get(event.entity.getCommandSenderName()));
               CommonTickHandler.worldData.killStatsByPlayer.remove(event.entity.getCommandSenderName());
               CommonTickHandler.worldData.markDirty();
            }

            if (!event.entity.getEntityData().hasKey("WeaponStats") && CommonTickHandler.worldData.weaponStatsByPlayer.containsKey(event.entity.getCommandSenderName())) {
               event.entity.getEntityData().setTag("WeaponStats", (NBTBase)CommonTickHandler.worldData.weaponStatsByPlayer.get(event.entity.getCommandSenderName()));
               CommonTickHandler.worldData.killStatsByPlayer.remove(event.entity.getCommandSenderName());
               CommonTickHandler.worldData.markDirty();
            }

            if (!event.entity.getEntityData().hasKey("CraftingStats") && CommonTickHandler.worldData.craftingStatsByPlayer.containsKey(event.entity.getCommandSenderName())) {
               event.entity.getEntityData().setTag("CraftingStats", (NBTBase)CommonTickHandler.worldData.craftingStatsByPlayer.get(event.entity.getCommandSenderName()));
               CommonTickHandler.worldData.craftingStatsByPlayer.remove(event.entity.getCommandSenderName());
               CommonTickHandler.worldData.markDirty();
            }
         }
      }

   }

   public static void processEntity(Entity entity, Random r) {
      if (entity != null) {
         temp.clear();
         if (statTrackersByClass.containsKey(entity.getClass())) {
            temp.add(statTrackersByClass.get(entity.getClass()));
         }

         Iterator i$ = genericTrackers.iterator();

         while(true) {
            while(true) {
               EntityStatHelper.EntityStatTracker e;
               do {
                  if (!i$.hasNext()) {
                     if (!temp.isEmpty()) {
                        String processed = getStat(entity, "processed");
                        if (processed == null || processed.equals("false")) {
                           giveStat(entity, "processed", "true");
                           Iterator i$ = temp.iterator();

                           while(i$.hasNext()) {
                              EntityStatHelper.EntityStatTracker statTracker = (EntityStatHelper.EntityStatTracker)i$.next();

                              EntityStatHelper.EntityStat s;
                              for(Iterator i$ = statTracker.stats.iterator(); i$.hasNext(); s.modifyEntity(entity)) {
                                 s = (EntityStatHelper.EntityStat)i$.next();
                                 giveStat(entity, s.name, s.getNewValue(r).toString());
                                 if (entity instanceof EntityLiving) {
                                    setName((EntityLiving)entity, s.getAlteredEntityName((EntityLiving)entity));
                                 }
                              }
                           }
                        }
                     }

                     return;
                  }

                  e = (EntityStatHelper.EntityStatTracker)i$.next();
               } while(temp.contains(e));

               Class[] arr$ = e.classes;
               int len$ = arr$.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  Class c = arr$[i$];
                  if (c.isAssignableFrom(entity.getClass())) {
                     temp.add(e);
                     break;
                  }
               }
            }
         }
      }
   }

   public void register() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void livingDeath(LivingDeathEvent event) {
      if (!event.entity.worldObj.isRemote) {
         String name1;
         if (event.entity.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
            if (event.entity instanceof EntityLivingBase && event.source != null && event.source.getEntity() != null && event.source.getEntity().getEntityData().hasKey("Blessing")) {
               name1 = event.source.getEntity().getEntityData().getString("Blessing");
               if (name1.equals("Thief") && ModjamMod.r.nextInt(20) == 0 && !event.entity.worldObj.isRemote) {
                  event.entity.dropItem(Items.gold_nugget, 1);
               }
            }

            int level = 0;
            if (event.entity.getEntityData().hasKey("Level")) {
               level = Integer.parseInt(event.entity.getEntityData().getString("Level"));
            }

            if (ModjamMod.r.nextInt(30) == 0 || level == 5) {
               event.entity.entityDropItem(new ItemStack(ModjamMod.mysteryPotion, 1, ModjamMod.r.nextInt(13)), event.entity.height / 2.0F);
            }
         }

         if (event.entity instanceof EntityLivingBase && event.source != null && event.source.getEntity() != null && event.source.getEntity().getEntityData().hasKey("Blessing")) {
            name1 = event.source.getEntity().getEntityData().getString("Blessing");
            if (name1.equals("Berserker")) {
               if (!hasStat(event.source.getEntity(), "BlessingCounter")) {
                  giveStat(event.source.getEntity(), "BlessingCounter", 0);
               }

               giveStat(event.source.getEntity(), "BlessingCounter", Math.min(10, Integer.parseInt(getStat(event.source.getEntity(), "BlessingCounter")) + 1));
            }
         }

         if (CommonTickHandler.worldData.currentTask.equals("Kill") && FyresWorldData.validMobs[CommonTickHandler.worldData.currentTaskID].isAssignableFrom(event.entity.getClass())) {
            ++CommonTickHandler.worldData.progress;
            name1 = CommonTickHandler.worldData.currentTask.equals("Kill") ? FyresWorldData.validMobNames[CommonTickHandler.worldData.currentTaskID] : FyresWorldData.validItems[CommonTickHandler.worldData.currentTaskID].getDisplayName();
            if (name1.contains("Block")) {
               if (name1.contains("Block")) {
                  name1 = name1.replace("Block", "Blocks").replace("block", "blocks");
               }
            } else {
               name1 = name1 + "s";
            }

            NewPacketHandler.SEND_MESSAGE.sendToAllPlayers("§fCurrent Goal Progress: " + CommonTickHandler.worldData.progress + "/" + CommonTickHandler.worldData.currentTaskAmount + " " + name1 + " " + CommonTickHandler.worldData.currentTask + "ed.");
            if (CommonTickHandler.worldData.progress >= CommonTickHandler.worldData.currentTaskAmount) {
               CommonTickHandler.worldData.progress = 0;
               ++CommonTickHandler.worldData.tasksCompleted;
               NewPacketHandler.LEVEL_UP.sendToAllPlayers(CommonTickHandler.worldData.rewardLevels);
               if (!CommonTickHandler.worldData.enderDragonKilled && event.entity instanceof EntityDragon) {
                  CommonTickHandler.worldData.enderDragonKilled = true;
               }

               CommonTickHandler.worldData.giveNewTask();
               NewPacketHandler.SEND_MESSAGE.sendToAllPlayers("§eA world goal has been completed!" + (!CommonTickHandler.worldData.getDisadvantage().equals("None") ? " World disadvantage has been lifted!" : ""));
               NewPacketHandler.SEND_MESSAGE.sendToAllPlayers("§eA new world goal has been set: " + CommonTickHandler.worldData.currentTask + " " + CommonTickHandler.worldData.currentTaskAmount + " " + (CommonTickHandler.worldData.currentTask.equals("Kill") ? FyresWorldData.validMobNames[CommonTickHandler.worldData.currentTaskID] : FyresWorldData.validItems[CommonTickHandler.worldData.currentTaskID].getDisplayName()) + "s. (" + CommonTickHandler.worldData.progress + " " + CommonTickHandler.worldData.currentTask + "ed)");
               CommonTickHandler.worldData.currentDisadvantage = "None";
            }

            NewPacketHandler.UPDATE_WORLD_DATA.sendToAllPlayers(CommonTickHandler.worldData.potionValues, CommonTickHandler.worldData.potionDurations, CommonTickHandler.worldData.getDisadvantage(), CommonTickHandler.worldData.currentTask, CommonTickHandler.worldData.currentTaskID, CommonTickHandler.worldData.currentTaskAmount, CommonTickHandler.worldData.progress, CommonTickHandler.worldData.tasksCompleted, CommonTickHandler.worldData.enderDragonKilled, ModjamMod.spawnTraps, CommonTickHandler.worldData.rewardLevels, CommonTickHandler.worldData.mushroomColors);
            CommonTickHandler.worldData.markDirty();
         }

         if (!CommonTickHandler.worldData.enderDragonKilled && event.entity instanceof EntityDragon) {
            CommonTickHandler.worldData.enderDragonKilled = true;
            NewPacketHandler.UPDATE_WORLD_DATA.sendToAllPlayers(CommonTickHandler.worldData.potionValues, CommonTickHandler.worldData.potionDurations, CommonTickHandler.worldData.getDisadvantage(), CommonTickHandler.worldData.currentTask, CommonTickHandler.worldData.currentTaskID, CommonTickHandler.worldData.currentTaskAmount, CommonTickHandler.worldData.progress, CommonTickHandler.worldData.tasksCompleted, CommonTickHandler.worldData.enderDragonKilled, ModjamMod.spawnTraps, CommonTickHandler.worldData.rewardLevels, CommonTickHandler.worldData.mushroomColors);
            CommonTickHandler.worldData.markDirty();
         }
      }

      EntityPlayer player;
      if (event.entity instanceof EntityPlayer) {
         player = (EntityPlayer)event.entity;
         player.triggerAchievement(ModjamMod.losingIsFun);
         CommonTickHandler.worldData.blessingByPlayer.put(player.getCommandSenderName(), player.getEntityData().getString("Blessing"));
         CommonTickHandler.worldData.potionKnowledgeByPlayer.put(player.getCommandSenderName(), player.getEntityData().getIntArray("PotionKnowledge"));
         if (player.getEntityData() != null && player.getEntityData().hasKey("KillStats")) {
            CommonTickHandler.worldData.killStatsByPlayer.put(player.getCommandSenderName(), player.getEntityData().getCompoundTag("KillStats"));
         }

         if (player.getEntityData() != null && player.getEntityData().hasKey("WeaponStats")) {
            CommonTickHandler.worldData.weaponStatsByPlayer.put(player.getCommandSenderName(), player.getEntityData().getCompoundTag("WeaponStats"));
         }

         if (player.getEntityData() != null && player.getEntityData().hasKey("CraftingStats")) {
            CommonTickHandler.worldData.craftingStatsByPlayer.put(player.getCommandSenderName(), player.getEntityData().getCompoundTag("CraftingStats"));
         }
      } else if (event.source != null && event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer) {
         player = (EntityPlayer)event.source.getEntity();
         String mob = getUnalteredName(event.entity);
         if (!player.getEntityData().hasKey("KillStats")) {
            player.getEntityData().setTag("KillStats", new NBTTagCompound());
         }

         NBTTagCompound killStats = player.getEntityData().getCompoundTag("KillStats");
         if (!killStats.hasKey(mob)) {
            killStats.setInteger(mob, 0);
            if (!killStats.hasKey("TrackedMobList")) {
               killStats.setString("TrackedMobList", mob);
            } else {
               killStats.setString("TrackedMobList", killStats.getString("TrackedMobList") + ";" + mob);
            }
         }

         killStats.setInteger(mob, killStats.getInteger(mob) + 1);
         int count;
         if (ModjamMod.enableMobKillStats) {
            int count;
            for(count = 0; count < knowledge.length; ++count) {
               if (killCount[count] == killStats.getInteger(mob)) {
                  NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§o§3You've become a " + knowledge[count].toLowerCase() + " " + mob.toLowerCase() + " slayer! (+" + damageBonusString[count] + "% damage against " + mob.toLowerCase() + "s.)" + (count < knowledge.length - 1 ? " " + (killCount[count + 1] - killCount[count]) + " " + mob.toLowerCase() + " kills to next rank." : ""));
                  break;
               }
            }

            count = 0;
            if (killStats.hasKey("TrackedMobList") && killStats.getString("TrackedMobList") != null && killStats.getString("TrackedMobList").length() > 0) {
               String[] arr$ = killStats.getString("TrackedMobList").split(";");
               count = arr$.length;

               for(int i$ = 0; i$ < count; ++i$) {
                  String object = arr$[i$];
                  if (killStats.hasKey(object) && killStats.getInteger(object) >= killCount[2]) {
                     ++count;
                  }
               }
            }

            if (count >= 5) {
               player.triggerAchievement(ModjamMod.theHunt);
            }
         }

         String weapon = "misc";
         if (player.getHeldItem() == null) {
            weapon = "fist";
         } else if (player.getHeldItem().getItem() != null && player.getHeldItem().getItem() instanceof ItemSword || player.getHeldItem().getItem() instanceof ItemBow || player.getHeldItem().getItem() instanceof ItemAxe) {
            weapon = getUnalteredItemName(player.getHeldItem().getItem());
         }

         if (!player.getEntityData().hasKey("WeaponStats")) {
            player.getEntityData().setTag("WeaponStats", new NBTTagCompound());
         }

         NBTTagCompound weaponStats = player.getEntityData().getCompoundTag("WeaponStats");
         if (!weaponStats.hasKey(weapon)) {
            weaponStats.setInteger(weapon, 0);
            if (!weaponStats.hasKey("TrackedItemList")) {
               weaponStats.setString("TrackedItemList", weapon);
            } else {
               weaponStats.setString("TrackedItemList", weaponStats.getString("TrackedItemList") + ";" + weapon);
            }
         }

         weaponStats.setInteger(weapon, weaponStats.getInteger(weapon) + 1);
         if (ModjamMod.enableWeaponKillStats) {
            for(count = 0; count < knowledge.length; ++count) {
               if (killCount[count] * 2 == weaponStats.getInteger(weapon)) {
                  NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§o§3You've become a " + knowledge[count].toLowerCase() + " " + weapon.toLowerCase() + " user! (+" + damageBonusString[count] + "% damage with " + weapon.toLowerCase() + "s.)" + (count < knowledge.length - 1 ? " " + (killCount[count + 1] * 2 - killCount[count] * 2) + " " + weapon.toLowerCase() + " kills to next rank." : ""));
                  break;
               }
            }

            count = 0;
            if (weaponStats.hasKey("TrackedItemList") && weaponStats.getString("TrackedItemList") != null && weaponStats.getString("TrackedItemList").length() > 0) {
               String[] arr$ = weaponStats.getString("TrackedItemList").split(";");
               int len$ = arr$.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  String object = arr$[i$];
                  if (weaponStats.hasKey(object) && weaponStats.getInteger(object) >= killCount[1] * 2) {
                     ++count;
                  }
               }
            }

            if (count >= 10) {
               player.triggerAchievement(ModjamMod.jackOfAllTrades);
            }
         }
      }

   }

   public static void setEntitySize(Entity entity, float par1, float par2) {
      float f2;
      if (par1 != entity.width || par2 != entity.height) {
         f2 = entity.width;
         entity.width = par1;
         entity.height = par2;
         entity.boundingBox.maxX = entity.boundingBox.minX + (double)entity.width;
         entity.boundingBox.maxZ = entity.boundingBox.minZ + (double)entity.width;
         entity.boundingBox.maxY = entity.boundingBox.minY + (double)entity.height;
      }

      f2 = par1 % 2.0F;
      if ((double)f2 < 0.375D) {
         entity.myEntitySize = EnumEntitySize.SIZE_1;
      } else if ((double)f2 < 0.75D) {
         entity.myEntitySize = EnumEntitySize.SIZE_2;
      } else if ((double)f2 < 1.0D) {
         entity.myEntitySize = EnumEntitySize.SIZE_3;
      } else if ((double)f2 < 1.375D) {
         entity.myEntitySize = EnumEntitySize.SIZE_4;
      } else if ((double)f2 < 1.75D) {
         entity.myEntitySize = EnumEntitySize.SIZE_5;
      } else {
         entity.myEntitySize = EnumEntitySize.SIZE_6;
      }

   }

   public static class EntityStat {
      public String name;
      public String value;

      public EntityStat(String name, String value) {
         this.name = name;
         this.value = value;
      }

      public Object getNewValue(Random r) {
         return this.value;
      }

      public String getAlteredEntityName(EntityLiving entity) {
         return entity.getCommandSenderName();
      }

      public void modifyEntity(Entity entity) {
      }
   }

   public static class EntityStatTracker {
      public Class[] classes;
      public boolean instanceAllowed;
      public ArrayList stats;

      public EntityStatTracker(Class[] classes, boolean instancesAllowed) {
         this.instanceAllowed = false;
         this.stats = new ArrayList();
         this.classes = classes;
         this.instanceAllowed = instancesAllowed;
      }

      public EntityStatTracker(Class c, boolean instancesAllowed) {
         this(new Class[]{c}, instancesAllowed);
      }

      public void addStat(EntityStatHelper.EntityStat stat) {
         if (!this.stats.contains(stat)) {
            this.stats.add(stat);
         }

      }
   }
}
