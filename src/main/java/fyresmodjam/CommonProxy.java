package fyresmodjam;

import net.minecraftforge.common.config.Configuration;

public class CommonProxy {
   public void register() {
   }

   public void sendPlayerMessage(String message) {
   }

   public void loadFromConfig(Configuration config) {
      ModjamMod.pillarGlow = config.get("general", "pillar_glow", ModjamMod.pillarGlow).getBoolean(ModjamMod.pillarGlow);
      ModjamMod.pillarGenChance = config.get("general", "pillar_gen_difficulty", ModjamMod.pillarGenChance).getInt();
      ModjamMod.maxPillarsPerChunk = config.get("general", "max_pillars_per_chunk", ModjamMod.maxPillarsPerChunk).getInt();
      ModjamMod.towerGenChance = config.get("general", "tower_gen_difficulty", ModjamMod.towerGenChance).getInt();
      ModjamMod.trapGenChance = config.get("general", "trap_gen_difficulty", ModjamMod.trapGenChance).getInt();
      ModjamMod.mushroomReplaceChance = config.get("general", "mushroom_replace_difficulty", ModjamMod.mushroomReplaceChance).getInt();
      ModjamMod.spawnTraps = !config.get("general", "disable_traps", !ModjamMod.spawnTraps).getBoolean(!ModjamMod.spawnTraps);
      ModjamMod.spawnTowers = config.get("general", "spawn_towers", ModjamMod.spawnTowers).getBoolean(ModjamMod.spawnTowers);
      ModjamMod.spawnRandomPillars = config.get("general", "spawn_random_pillars", ModjamMod.spawnRandomPillars).getBoolean(ModjamMod.spawnRandomPillars);
      ModjamMod.disableDisadvantages = config.get("general", "disable_disadvantages", ModjamMod.disableDisadvantages).getBoolean(ModjamMod.disableDisadvantages);
      ModjamMod.versionChecking = config.get("general", "version_checking", ModjamMod.versionChecking).getBoolean(ModjamMod.versionChecking);
      ModjamMod.showAllPillarsInCreative = config.get("general", "show_all_pillars_in_creative", ModjamMod.showAllPillarsInCreative).getBoolean(ModjamMod.showAllPillarsInCreative);
      ModjamMod.enableMobKillStats = config.get("general", "enable_mob_kill_stats", ModjamMod.enableMobKillStats).getBoolean(ModjamMod.enableMobKillStats);
      ModjamMod.enableWeaponKillStats = config.get("general", "enable_weapon_kill_stats", ModjamMod.enableWeaponKillStats).getBoolean(ModjamMod.enableWeaponKillStats);
      ModjamMod.enableCraftingStats = config.get("general", "enable_crafting_stats", ModjamMod.enableCraftingStats).getBoolean(ModjamMod.enableCraftingStats);
      ModjamMod.trapsBelowGroundOnly = config.get("general", "traps_below_ground_only", ModjamMod.trapsBelowGroundOnly).getBoolean(ModjamMod.trapsBelowGroundOnly);
   }
}
