package fyresmodjam.commands;

import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.misc.EntityStatHelper;
import java.util.List;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class CommandCraftingStats implements ICommand {
   public int compareTo(Object arg0) {
      return 0;
   }

   public String getCommandName() {
      return "craftingKnowledge";
   }

   public String getCommandUsage(ICommandSender icommandsender) {
      return "commands.craftingKnowledge.usage";
   }

   public List getCommandAliases() {
      return null;
   }

   public void processCommand(ICommandSender icommandsender, String[] astring) {
      int page = astring.length > 0 ? Integer.parseInt(astring[0]) - 1 : 0;
      int maxPage = false;
      if (icommandsender instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)icommandsender;
         String message = "§c§oCrafting stats not enabled.";
         if (ModjamMod.enableCraftingStats) {
            message = "@Crafting Knowledge:";
            if (!entityplayer.getEntityData().hasKey("CraftingStats")) {
               message = message + "@    You've yet to learn anything.";
            } else {
               NBTTagCompound craftingStats = entityplayer.getEntityData().getCompoundTag("CraftingStats");
               String trackedItems = craftingStats.hasKey("TrackedItemList") ? craftingStats.getString("TrackedItemList") : "";
               if (trackedItems != null && trackedItems.length() > 0) {
                  String[] trackedItemList = trackedItems.split(";");
                  int maxPage = Math.max(0, (craftingStats.func_150296_c().size() - 1) / 4);
                  if (page > maxPage) {
                     page = maxPage;
                  }

                  if (page < 0) {
                     page = 0;
                  }

                  message = "@Crafting Knowledge (page " + (page + 1) + "/" + (maxPage + 1) + "):";
                  int count = 0;
                  int skip = 0;
                  String[] arr$ = trackedItemList;
                  int len$ = trackedItemList.length;

                  for(int i$ = 0; i$ < len$; ++i$) {
                     String item = arr$[i$];
                     if (skip < page * 4) {
                        ++skip;
                     } else {
                        int kills = craftingStats.getInteger(item);
                        int last = 0;

                        for(int i = 0; i < EntityStatHelper.killCount.length && kills >= EntityStatHelper.killCount[i] * 2; last = i++) {
                        }

                        message = message + "@§b    " + EntityStatHelper.knowledge[last] + " " + item.toLowerCase() + " smith§3 (" + kills + " craft(s)" + (last < EntityStatHelper.knowledge.length - 1 ? ", " + (EntityStatHelper.killCount[last + 1] * 2 - kills) + " craft(s) to next rank)" : ")");
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
