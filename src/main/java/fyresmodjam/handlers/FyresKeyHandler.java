package fyresmodjam.handlers;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import fyresmodjam.ModjamMod;
import fyresmodjam.blocks.BlockTrap;
import fyresmodjam.items.ItemTrap;
import fyresmodjam.tileentities.TileEntityPillar;
import fyresmodjam.tileentities.TileEntityTrap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class FyresKeyHandler {
   public static KeyBinding examine = new KeyBinding("Examine", 45, "YWD");
   public static KeyBinding activateBlessing = new KeyBinding("Activate Blessing", 37, "YWD");
   public static KeyBinding[] keyBindings;

   public FyresKeyHandler() {
      KeyBinding[] arr$ = keyBindings;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         KeyBinding k = arr$[i$];
         ClientRegistry.registerKeyBinding(k);
      }

   }

   @SubscribeEvent
   public void keyInput(KeyInputEvent event) {
      if (Minecraft.getMinecraft().inGameHasFocus) {
         Minecraft minecraft = Minecraft.getMinecraft();
         EntityPlayer player = minecraft.thePlayer;
         if (player != null) {
            if (examine.isPressed() && minecraft.objectMouseOver != null) {
               MovingObjectPosition o = minecraft.objectMouseOver;
               if (o.typeOfHit == MovingObjectType.BLOCK) {
                  int x = minecraft.objectMouseOver.blockX;
                  int y = minecraft.objectMouseOver.blockY;
                  int z = minecraft.objectMouseOver.blockZ;
                  if (minecraft.theWorld.getBlock(x, y, z) == ModjamMod.blockPillar && minecraft.theWorld.getBlockMetadata(x, y, z) % 2 == 1) {
                     --y;
                  }

                  TileEntity te = minecraft.theWorld.getTileEntity(x, y, z);
                  String name;
                  if (te != null && te instanceof TileEntityPillar) {
                     int index = 0;

                     for(int i = 0; i < TileEntityPillar.validBlessings.length; ++i) {
                        if (TileEntityPillar.validBlessings[i].equals(((TileEntityPillar)te).blessing)) {
                           index = i;
                           break;
                        }
                     }

                     name = "@§eBlessing of the " + ((TileEntityPillar)te).blessing + ": " + TileEntityPillar.blessingDescriptions[index] + ".";
                     String[] arr$ = name.split("@");
                     int len$ = arr$.length;

                     for(int i$ = 0; i$ < len$; ++i$) {
                        String s2 = arr$[i$];
                        NewPacketHandler.SEND_MESSAGE.data = new Object[]{s2};
                        NewPacketHandler.SEND_MESSAGE.executeClient(Minecraft.getMinecraft().thePlayer);
                     }
                  } else if (te != null && te instanceof TileEntityTrap) {
                     String placedBy = ((TileEntityTrap)te).placedBy;
                     name = placedBy != null ? "§eThis " + ItemTrap.names[te.getBlockMetadata() % BlockTrap.trapTypes].toLowerCase() + " was placed by " + (placedBy.equals(player.getCommandSenderName()) ? "you" : placedBy) + "." : "§eThis " + ItemTrap.names[te.getBlockMetadata() % BlockTrap.trapTypes].toLowerCase() + " doesn't seem to have been placed by anyone.";
                     name = name + " §eTrap is set to " + TileEntityTrap.settings[((TileEntityTrap)te).setting] + ".";
                     NewPacketHandler.SEND_MESSAGE.data = new Object[]{name};
                     NewPacketHandler.SEND_MESSAGE.executeClient(Minecraft.getMinecraft().thePlayer);
                  } else {
                     ItemStack stack = new ItemStack(minecraft.theWorld.getBlock(x, y, z), 1, minecraft.theWorld.getBlockMetadata(x, y, z));
                     if (stack.getItem() != null) {
                        name = stack.getDisplayName().toLowerCase();
                        NewPacketHandler.SEND_MESSAGE.data = new Object[]{"§eIt's a " + name + (!name.contains("block") ? " block." : ".")};
                        NewPacketHandler.SEND_MESSAGE.executeClient(Minecraft.getMinecraft().thePlayer);
                     }
                  }
               } else if (o.typeOfHit == MovingObjectType.ENTITY && o.entityHit != null) {
                  NewPacketHandler.EXAMINE_MOB.sendToServer(o.entityHit.dimension, o.entityHit.getEntityId());
               }
            }

            if (activateBlessing.isPressed()) {
               String blessing = player.getEntityData().getString("Blessing");
               if (blessing != null) {
                  if (minecraft.objectMouseOver != null) {
                     MovingObjectPosition o = minecraft.objectMouseOver;
                     if (o.typeOfHit == MovingObjectType.BLOCK) {
                        NewPacketHandler.ACTIVATE_BLESSING.sendToServer(minecraft.objectMouseOver.blockX, minecraft.objectMouseOver.blockY, minecraft.objectMouseOver.blockZ);
                     }
                  } else {
                     NewPacketHandler.ACTIVATE_BLESSING.sendToServer(player.chunkCoordX, player.chunkCoordY, player.chunkCoordZ);
                  }
               }
            }
         }
      }

   }

   static {
      keyBindings = new KeyBinding[]{examine, activateBlessing};
   }
}
