package fyresmodjam.commands;

import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.misc.EntityStatHelper;
import java.util.List;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class CommandKillStats implements ICommand {
   public int compareTo(Object arg0) {
      return 0;
   }

   public String getCommandName() {
      return "creatureKnowledge";
   }

   public String getCommandUsage(ICommandSender icommandsender) {
      return "commands.creatureKnowledge.usage";
   }

   public List getCommandAliases() {
      return null;
   }

   public void processCommand(ICommandSender icommandsender, String[] astring) {
      int page = astring.length > 0 ? Integer.parseInt(astring[0]) - 1 : 0;
      if (icommandsender instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)icommandsender;
         String message = "§c§oMob kill stats not enabled.";
         if (ModjamMod.enableMobKillStats) {
            message = "@Creature Knowledge:";
            if (!entityplayer.getEntityData().hasKey("KillStats")) {
               message = message + "@    You've yet to learn anything.";
            } else {
               NBTTagCompound killStats = entityplayer.getEntityData().getCompoundTag("KillStats");
               String trackedMobs = killStats.hasKey("TrackedMobList") ? killStats.getString("TrackedMobList") : "";
               if (trackedMobs != null && trackedMobs.length() > 0) {
                  String[] trackedMobList = trackedMobs.split(";");
                  int maxPage = Math.max(0, (killStats.func_150296_c().size() - 1) / 4);
                  if (page > maxPage) {
                     page = maxPage;
                  }

                  if (page < 0) {
                     page = 0;
                  }

                  message = "@Creature Knowledge (page " + (page + 1) + "/" + (maxPage + 1) + "):";
                  int count = 0;
                  int skip = 0;
                  String[] arr$ = trackedMobList;
                  int len$ = trackedMobList.length;

                  for(int i$ = 0; i$ < len$; ++i$) {
                     String mob = arr$[i$];
                     if (skip < page * 4) {
                        ++skip;
                     } else {
                        int kills = killStats.getInteger(mob);
                        int last = 0;

                        for(int i = 0; i < EntityStatHelper.killCount.length && kills >= EntityStatHelper.killCount[i]; last = i++) {
                        }

                        message = message + "@§b    " + EntityStatHelper.knowledge[last] + " " + mob.toLowerCase() + " slayer§3 " + (last > 0 ? "+" + EntityStatHelper.damageBonusString[last] + "% damage bonus (" : "(") + kills + " kill(s)" + (last < EntityStatHelper.knowledge.length - 1 ? ", " + (EntityStatHelper.killCount[last + 1] - kills) + " kill(s) to next rank)" : ")");
                        ++count;
                        if (count >= 4) {
                           break;
                        }
                     }
                  }
               }
            }
         }

         NewPacketHandler.SEND_MESSAGE.sendToPlayer(entityplayer, message);
      }

   }

   public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
      return true;
   }

   public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
      return null;
   }

   public boolean isUsernameIndex(String[] astring, int i) {
      return false;
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }
}
