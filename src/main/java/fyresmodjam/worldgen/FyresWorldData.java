package fyresmodjam.worldgen;

import cpw.mods.fml.common.FMLCommonHandler;
import fyresmodjam.ModjamMod;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class FyresWorldData extends WorldSavedData {
   public static String[] validDisadvantages = new String[]{"Tougher Mobs", "Weak", "Explosive Traps", "Increased Mob Spawn", "Neverending Rain", "Neverending Night", "Permadeath"};
   public static String[] disadvantageDescriptions = new String[]{"Hostile enemies takes 25% less damage", "-25% melee damage", "Traps also trigger explosions when set off", "+33% hostile mob spawn rate", "Constantly rains", "Constant night", "Items dropped upon death are permanently lost"};
   public static String[] validTasks = new String[]{"Kill", "Burn"};
   public static String key = "FyresWorldData";
   public int[] potionValues = null;
   public int[] potionDurations = null;
   public int[][] mushroomColors = (int[][])null;
   public String currentDisadvantage = null;
   public String currentTask = null;
   public int currentTaskID = -1;
   public int currentTaskAmount = 0;
   public int progress = 0;
   public int tasksCompleted = 0;
   public int rewardLevels = -1;
   public boolean enderDragonKilled = false;
   public static Class[] validMobs = new Class[]{EntityDragon.class, EntityGhast.class, EntityWither.class};
   public static String[] validMobNames = new String[]{"Ender Dragon", "Ghast", "Wither"};
   public static int[][] mobNumbers = new int[][]{{1, 1}, {5, 15}, {1, 1}};
   public HashMap blessingByPlayer = new HashMap();
   public HashMap potionKnowledgeByPlayer = new HashMap();
   public HashMap killStatsByPlayer = new HashMap();
   public HashMap weaponStatsByPlayer = new HashMap();
   public HashMap craftingStatsByPlayer = new HashMap();
   public static ItemStack[] validItems;

   public FyresWorldData() {
      super(key);
   }

   public FyresWorldData(String key) {
      super(key);
   }

   public static FyresWorldData forWorld(World world) {
      MapStorage storage = world.perWorldStorage;
      FyresWorldData result = (FyresWorldData)storage.loadData(FyresWorldData.class, key);
      if (result == null) {
         result = new FyresWorldData();
         storage.setData(key, result);
         result.checkWorldData();
      }

      return result;
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      if (nbttagcompound.hasKey("values")) {
         this.potionValues = nbttagcompound.getIntArray("values");
      }

      if (nbttagcompound.hasKey("durations")) {
         this.potionDurations = nbttagcompound.getIntArray("durations");
      }

      if (nbttagcompound.hasKey("currentDisadvantage")) {
         this.currentDisadvantage = nbttagcompound.getString("currentDisadvantage");
      }

      if (nbttagcompound.hasKey("currentTask")) {
         this.currentTask = nbttagcompound.getString("currentTask");
      }

      if (nbttagcompound.hasKey("currentTaskID")) {
         this.currentTaskID = nbttagcompound.getInteger("currentTaskID");
      }

      if (nbttagcompound.hasKey("currentTaskAmount")) {
         this.currentTaskAmount = nbttagcompound.getInteger("currentTaskAmount");
      }

      if (nbttagcompound.hasKey("progress")) {
         this.progress = nbttagcompound.getInteger("progress");
      }

      if (nbttagcompound.hasKey("tasksCompleted")) {
         this.tasksCompleted = nbttagcompound.getInteger("tasksCompleted");
      }

      if (nbttagcompound.hasKey("enderDragonKilled")) {
         this.enderDragonKilled = nbttagcompound.getBoolean("enderDragonKilled");
      }

      if (nbttagcompound.hasKey("rewardLevels")) {
         this.rewardLevels = nbttagcompound.getInteger("rewardLevels");
      }

      this.mushroomColors = new int[13][];

      for(int i = 0; i < 13; ++i) {
         if (nbttagcompound.hasKey("mushroomColors_" + (i + 1))) {
            this.mushroomColors[i] = nbttagcompound.getIntArray("mushroomColors_" + (i + 1));
         }
      }

      if (nbttagcompound.hasKey("TempPlayerStats")) {
         NBTTagCompound tempStats = nbttagcompound.getCompoundTag("TempPlayerStats");
         Iterator i$ = tempStats.func_150296_c().iterator();

         while(i$.hasNext()) {
            Object o = i$.next();
            if (o != null && o instanceof NBTTagCompound) {
               NBTTagCompound player = (NBTTagCompound)o;
               this.blessingByPlayer.put(player.getString("Name"), player.getString("Blessing"));
               this.potionKnowledgeByPlayer.put(player.getString("Name"), player.getIntArray("PotionKnowledge"));
               if (player.hasKey("KillStats")) {
                  this.killStatsByPlayer.put(player.getString("Name"), player.getCompoundTag("KillStats"));
               }

               if (player.hasKey("WeaponStats")) {
                  this.weaponStatsByPlayer.put(player.getString("Name"), player.getCompoundTag("WeaponStats"));
               }

               if (player.hasKey("CraftingStats")) {
                  this.craftingStatsByPlayer.put(player.getString("Name"), player.getCompoundTag("CraftingStats"));
               }
            }
         }
      }

      this.checkWorldData();
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      this.checkWorldData();
      nbttagcompound.setIntArray("values", this.potionValues);
      nbttagcompound.setIntArray("durations", this.potionDurations);
      nbttagcompound.setString("currentDisadvantage", this.currentDisadvantage);
      nbttagcompound.setString("currentTask", this.currentTask);
      nbttagcompound.setInteger("currentTaskID", this.currentTaskID);
      nbttagcompound.setInteger("currentTaskAmount", this.currentTaskAmount);
      nbttagcompound.setInteger("progress", this.progress);
      nbttagcompound.setInteger("tasksCompleted", this.tasksCompleted);
      nbttagcompound.setBoolean("enderDragonKilled", this.enderDragonKilled);
      nbttagcompound.setInteger("rewardLevels", this.rewardLevels);

      for(int i = 0; i < 13; ++i) {
         nbttagcompound.setIntArray("mushroomColors_" + (i + 1), this.mushroomColors[i]);
      }

      if (!this.blessingByPlayer.isEmpty()) {
         NBTTagCompound tempPlayerStats = new NBTTagCompound();
         Iterator i$ = this.blessingByPlayer.keySet().iterator();

         while(i$.hasNext()) {
            String s = (String)i$.next();
            if (s != null) {
               NBTTagCompound player = new NBTTagCompound();
               player.setString("Name", s);
               player.setString("Blessing", (String)this.blessingByPlayer.get(s));
               player.setIntArray("PotionKnowledge", (int[])this.potionKnowledgeByPlayer.get(s));
               if (this.killStatsByPlayer.containsKey(s)) {
                  player.setTag("KillStats", (NBTBase)this.killStatsByPlayer.get(s));
               }

               if (this.weaponStatsByPlayer.containsKey(s)) {
                  player.setTag("WeaponStats", (NBTBase)this.weaponStatsByPlayer.get(s));
               }

               if (this.craftingStatsByPlayer.containsKey(s)) {
                  player.setTag("CraftingStats", (NBTBase)this.craftingStatsByPlayer.get(s));
               }

               tempPlayerStats.setTag(s, player);
            }
         }

         nbttagcompound.setTag("TempPlayerStats", tempPlayerStats);
      }

   }

   private void checkWorldData() {
      int i;
      int i2;
      int len$;
      if (this.potionValues == null) {
         this.potionValues = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

         for(i = 0; i < 12; ++i) {
            i2 = ModjamMod.r.nextInt(Potion.potionTypes.length);
            boolean stop = false;

            while(true) {
               while(Potion.potionTypes[i2] == null || !stop) {
                  stop = true;
                  i2 = ModjamMod.r.nextInt(Potion.potionTypes.length);

                  for(len$ = 0; len$ < 12; ++len$) {
                     if (this.potionValues[len$] == i2) {
                        stop = false;
                        break;
                     }
                  }
               }

               this.potionValues[i] = i2;
               break;
            }
         }
      } else {
         for(i = 0; i < 12; ++i) {
            if (Potion.potionTypes[this.potionValues[i]] == null) {
               for(i2 = ModjamMod.r.nextInt(Potion.potionTypes.length); Potion.potionTypes[i2] == null; i2 = ModjamMod.r.nextInt(Potion.potionTypes.length)) {
               }

               this.potionValues[i] = i2;
            }
         }
      }

      if (this.potionDurations == null) {
         this.potionDurations = new int[12];
      }

      for(i = 0; i < 12; ++i) {
         if (this.potionDurations[i] == 0) {
            this.potionDurations[i] = 5 + ModjamMod.r.nextInt(26);
         }
      }

      if (this.mushroomColors == null) {
         this.mushroomColors = new int[13][2];

         for(i = 0; i < 13; ++i) {
            this.mushroomColors[i][0] = Color.HSBtoRGB(ModjamMod.r.nextFloat(), ModjamMod.r.nextFloat(), ModjamMod.r.nextFloat());
            this.mushroomColors[i][1] = Color.HSBtoRGB(ModjamMod.r.nextFloat(), ModjamMod.r.nextFloat(), ModjamMod.r.nextFloat());
         }
      }

      boolean changeDisadvantage = this.currentDisadvantage == null;
      int i$;
      String s;
      boolean changeTask;
      String[] arr$;
      if (!changeDisadvantage) {
         changeTask = false;
         arr$ = validDisadvantages;
         len$ = arr$.length;

         for(i$ = 0; i$ < len$; ++i$) {
            s = arr$[i$];
            if (s.equals(this.currentDisadvantage)) {
               changeTask = true;
               break;
            }
         }

         changeDisadvantage = !changeTask && !this.currentDisadvantage.equals("None");
      }

      if (changeDisadvantage) {
         this.currentDisadvantage = validDisadvantages[ModjamMod.r.nextInt(validDisadvantages.length)];

         for(MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance(); server != null && server.isHardcore() && this.currentDisadvantage.equals("Permadeath"); this.currentDisadvantage = validDisadvantages[ModjamMod.r.nextInt(validDisadvantages.length)]) {
         }
      }

      if (this.currentTask == null) {
         this.giveNewTask();
      } else {
         changeTask = true;
         arr$ = validTasks;
         len$ = arr$.length;

         for(i$ = 0; i$ < len$; ++i$) {
            s = arr$[i$];
            if (s.equals(this.currentTask)) {
               changeTask = false;
               break;
            }
         }

         if (!changeTask && (this.currentTask == null || !this.currentTask.equals("Kill") || this.currentTaskID != 0 || !this.enderDragonKilled)) {
            if (this.currentTask.equals("Kill")) {
               this.currentTaskID %= validMobs.length;
            }
         } else {
            this.giveNewTask();
         }
      }

      if (this.rewardLevels == -1) {
         this.rewardLevels = 5 + ModjamMod.r.nextInt(6);
      }

   }

   public void giveNewTask() {
      this.progress = 0;
      this.currentTask = validTasks[ModjamMod.r.nextInt(validTasks.length)];
      if (this.currentTask.equals("Kill")) {
         this.currentTaskID = !this.enderDragonKilled ? ModjamMod.r.nextInt(validMobs.length) : 1 + ModjamMod.r.nextInt(validMobs.length - 1);
         this.currentTaskAmount = mobNumbers[this.currentTaskID][0] + ModjamMod.r.nextInt(mobNumbers[this.currentTaskID][1]);
      } else if (this.currentTask.equals("Burn")) {
         this.currentTaskID = ModjamMod.r.nextInt(validItems.length);
         if (validItems[this.currentTaskID].getItem() == Items.nether_star) {
            this.currentTaskAmount = 1;
         } else {
            this.currentTaskAmount = 5 + ModjamMod.r.nextInt(28);
         }

         if (validItems[this.currentTaskID].getItem() instanceof ItemBlock) {
            this.currentTaskAmount /= 4;
         }
      }

      this.rewardLevels = 5 + ModjamMod.r.nextInt(6);
      this.markDirty();
   }

   public String getDisadvantage() {
      return ModjamMod.disableDisadvantages ? "None" : this.currentDisadvantage;
   }

   static {
      validItems = new ItemStack[]{new ItemStack(Blocks.diamond_block), new ItemStack(Blocks.gold_block), new ItemStack(Blocks.emerald_block), new ItemStack(Blocks.lapis_block), new ItemStack(Items.diamond), new ItemStack(Items.emerald), new ItemStack(Items.gold_ingot), new ItemStack(Items.nether_star), new ItemStack(Items.ghast_tear)};
   }
}
