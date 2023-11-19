package fyresmodjam.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import fyresmodjam.ModjamMod;
import fyresmodjam.misc.ItemStatHelper;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class ClientTickHandler {
   public static long time = System.currentTimeMillis();

   @SubscribeEvent
   public void clientTick(ClientTickEvent event) {
      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
      if (System.currentTimeMillis() - time > 200L && player != null) {
         if (player.openContainer != null) {
            boolean sendPacket = false;
            ItemStack[] arr$ = player.inventory.mainInventory;
            int len$ = arr$.length;
            int i$ = 0;

            while(true) {
               if (i$ >= len$) {
                  if (sendPacket) {
                     NewPacketHandler.UPDATE_PLAYER_ITEMS.sendToServer((Object)null);
                     time = System.currentTimeMillis();
                  }
                  break;
               }

               Object object = arr$[i$];
               if (object != null && object instanceof ItemStack) {
                  ItemStack stack = (ItemStack)object;
                  if (stack.getItem() != null && !ItemStatHelper.skip.contains(stack.getItem().getClass()) && (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("processed") || stack.getTagCompound().getString("processed").equals("false"))) {
                     sendPacket = true;
                  }
               }

               ++i$;
            }
         }

         player.triggerAchievement(ModjamMod.startTheGame);
      }

      if (FyresKeyHandler.examine.getKeyCode() != ModjamMod.examineKey || FyresKeyHandler.activateBlessing.getKeyCode() != ModjamMod.blessingKey) {
         ModjamMod.examineKey = FyresKeyHandler.examine.getKeyCode();
         ModjamMod.blessingKey = FyresKeyHandler.activateBlessing.getKeyCode();
         Configuration config = new Configuration(new File(ModjamMod.configPath));
         config.load();
         config.get("Keybindings", "examine_key", ModjamMod.examineKey).set(ModjamMod.examineKey);
         config.get("Keybindings", "blessing_key", ModjamMod.blessingKey).set(ModjamMod.blessingKey);
         config.save();
      }

   }
}
