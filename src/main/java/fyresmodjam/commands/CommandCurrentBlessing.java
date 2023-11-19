package fyresmodjam.commands;

import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.tileentities.TileEntityPillar;
import java.util.List;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CommandCurrentBlessing implements ICommand {
   public int compareTo(Object arg0) {
      return 0;
   }

   public String getCommandName() {
      return "currentBlessing";
   }

   public String getCommandUsage(ICommandSender icommandsender) {
      return "commands.currentBlessing.usage";
   }

   public List getCommandAliases() {
      return null;
   }

   public void processCommand(ICommandSender icommandsender, String[] astring) {
      if (icommandsender instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)icommandsender;
         String blessing = entityplayer.getEntityData().hasKey("Blessing") ? entityplayer.getEntityData().getString("Blessing") : null;
         int index = 0;

         for(int i = 0; i < TileEntityPillar.validBlessings.length; ++i) {
            if (TileEntityPillar.validBlessings[i].equals(blessing)) {
               index = i;
               break;
            }
         }

         NewPacketHandler.SEND_MESSAGE.sendToPlayer(entityplayer, blessing != null ? "§eCurrent Blessing - §oBlessing of the " + blessing + ": " + TileEntityPillar.blessingDescriptions[index] : "You don't currently have a blessing.");
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
