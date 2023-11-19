package fyresmodjam.commands;

import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.misc.EntityStatHelper;
import java.util.List;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class CommandWeaponStats implements ICommand {
   public int compareTo(Object arg0) {
      return 0;
   }

   public String getCommandName() {
      return "weaponKnowledge";
   }

   public String getCommandUsage(ICommandSender icommandsender) {
      return "commands.weaponKnowledge.usage";
   }

   public List getCommandAliases() {
      return null;
   }

   public void processCommand(ICommandSender icommandsender, String[] astring) {
      int page = astring.length > 0 ? Integer.parseInt(astring[0]) - 1 : 0;
      int maxPage = false;
      if (icommandsender instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)icommandsender;
         String message = "§c§oWeapon kill stats not enabled.";
         if (ModjamMod.enableWeaponKillStats) {
            message = "@Weapon Knowledge:";
            if (!entityplayer.getEntityData().hasKey("WeaponStats")) {
               message = message + "@    You've yet to learn anything.";
            } else {
               NBTTagCompound itemStats = entityplayer.getEntityData().getCompoundTag("WeaponStats");
               String trackedItems = itemStats.hasKey("TrackedItemList") ? itemStats.getString("TrackedItemList") : "";
               if (trackedItems != null && trackedItems.length() > 0) {
                  String[] trackedItemList = trackedItems.split(";");
                  int maxPage = Math.max(0, itemStats.func_150296_c().size() / 4);
                  if (page > maxPage) {
                     page = maxPage;
                  }

                  if (page < 0) {
                     page = 0;
                  }

                  message = "@Weapon Knowledge (page " + (page + 1) + "/" + (maxPage + 1) + "):";
                  int count = 0;
                  int skip = 0;
                  String[] arr$ = trackedItemList;
                  int len$ = trackedItemList.length;

                  for(int i$ = 0; i$ < len$; ++i$) {
                     String item = arr$[i$];
                     if (skip < page * 4) {
                        ++skip;
                     } else {
                        int kills = itemStats.getInteger(item);
                        int last = 0;

                        for(int i = 0; i < EntityStatHelper.killCount.length && kills >= EntityStatHelper.killCount[i] * 2; last = i++) {
                        }

                        message = message + "@§b    " + EntityStatHelper.knowledge[last] + " " + item.toLowerCase() + " user§3 " + (last > 0 ? "+" + EntityStatHelper.damageBonusString[last] + "% damage bonus (" : "(") + kills + " kill(s)" + (last < EntityStatHelper.knowledge.length - 1 ? ", " + (EntityStatHelper.killCount[last + 1] * 2 - kills) + " kill(s) to next rank)" : ")");
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
