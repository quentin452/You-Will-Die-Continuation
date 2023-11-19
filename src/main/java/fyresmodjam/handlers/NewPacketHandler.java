package fyresmodjam.handlers;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.blocks.BlockTrap;
import fyresmodjam.misc.EntityStatHelper;
import fyresmodjam.misc.ItemStatHelper;
import fyresmodjam.tileentities.TileEntityTrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.nio.charset.Charset;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;

public class NewPacketHandler {
   public static NewPacketHandler.BasicPacket[] packetTypes = new NewPacketHandler.BasicPacket[256];
   public static int[] potionValues = new int[12];
   public static int[] potionDurations = new int[12];
   public static int[][] mushroomColors = new int[13][2];
   public static String currentDisadvantage = null;
   public static String currentTask = null;
   public static int currentTaskID = -1;
   public static int currentTaskAmount = 0;
   public static int progress = 0;
   public static int tasksCompleted = 0;
   public static int rewardLevels = 0;
   public static boolean enderDragonKilled = false;
   public static boolean trapsDisabled = false;
   public static final NewPacketHandler.BasicPacket UPDATE_BLESSING = new NewPacketHandler.BasicPacket(1) {
      public void executeBoth(EntityPlayer player) {
         player.getEntityData().setString("Blessing", (String)this.data[0]);
      }

      public Class[] getExpectedClasses() {
         return new Class[]{String.class};
      }
   };
   public static final NewPacketHandler.BasicPacket PLAY_SOUND = new NewPacketHandler.BasicPacket(2) {
      public void executeServer(EntityPlayer player) {
         String sound = (String)this.data[0];
         int x = (Integer)this.data[1];
         int y = (Integer)this.data[2];
         int z = (Integer)this.data[3];
         player.worldObj.playSound((double)x, (double)y, (double)z, "fyresmodjam:" + sound, 1.0F, 1.0F, false);
      }

      public Class[] getExpectedClasses() {
         return new Class[]{String.class, Integer.class, Integer.class, Integer.class};
      }
   };
   public static final NewPacketHandler.BasicPacket UPDATE_POTION_KNOWLEDGE = new NewPacketHandler.BasicPacket(3) {
      public void executeBoth(EntityPlayer player) {
         player.getEntityData().setIntArray("PotionKnowledge", (int[])((int[])this.data[0]));
      }

      public Class[] getExpectedClasses() {
         return new Class[]{int[].class};
      }
   };
   public static final NewPacketHandler.BasicPacket SEND_MESSAGE = new NewPacketHandler.BasicPacket(4) {
      public void executeClient(EntityPlayer player) {
         String style = "";
         String[] arr$ = ((String)this.data[0]).split("@");
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            String[] words = s.split(" ");
            s = "";
            String[] arr$x = words;
            int len$x = words.length;

            for(int i$x = 0; i$x < len$x; ++i$x) {
               String word = arr$x[i$x];

               String string;
               for(s = s + style + word + " "; word.contains("§"); word = word.replaceFirst(string, "")) {
                  int firstOccurance = word.indexOf("§");
                  string = word.substring(firstOccurance, firstOccurance + 2);
                  if (style.contains(string)) {
                     style = style.replace(string, "");
                  }

                  style = style + string;
                  if (string.equals("§r")) {
                     style = "";
                  }
               }
            }

            player.addChatComponentMessage(new ChatComponentText(s));
         }

      }

      public Class[] getExpectedClasses() {
         return new Class[]{String.class};
      }
   };
   public static final NewPacketHandler.BasicPacket UPDATE_WORLD_DATA = new NewPacketHandler.BasicPacket(5) {
      public void executeClient(EntityPlayer player) {
         NewPacketHandler.potionValues = (int[])((int[])this.data[0]);
         NewPacketHandler.potionDurations = (int[])((int[])this.data[1]);
         NewPacketHandler.currentDisadvantage = (String)this.data[2];
         NewPacketHandler.currentTask = (String)this.data[3];
         NewPacketHandler.currentTaskID = (Integer)this.data[4];
         NewPacketHandler.currentTaskAmount = (Integer)this.data[5];
         NewPacketHandler.progress = (Integer)this.data[6];
         NewPacketHandler.tasksCompleted = (Integer)this.data[7];
         NewPacketHandler.enderDragonKilled = (Boolean)this.data[8];
         NewPacketHandler.trapsDisabled = !(Boolean)this.data[9];
         NewPacketHandler.rewardLevels = (Integer)this.data[10];
         NewPacketHandler.mushroomColors = (int[][])((int[][])this.data[11]);
      }

      public Class[] getExpectedClasses() {
         return new Class[]{int[].class, int[].class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Boolean.class, Boolean.class, Integer.class, int[][].class};
      }
   };
   public static final NewPacketHandler.BasicPacket UPDATE_PLAYER_ITEMS = new NewPacketHandler.BasicPacket(6) {
      public void executeServer(EntityPlayer player) {
         ItemStack[] arr$ = player.inventory.mainInventory;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Object stack = arr$[i$];
            if (stack != null && stack instanceof ItemStack) {
               ItemStatHelper.processItemStack((ItemStack)stack, ModjamMod.r);
            }
         }

      }

      public Class[] getExpectedClasses() {
         return null;
      }
   };
   public static final NewPacketHandler.BasicPacket DISARM_TRAP = new NewPacketHandler.BasicPacket(7) {
      public void executeServer(EntityPlayer player) {
         int blockX = (Integer)this.data[0];
         int blockY = (Integer)this.data[1];
         int blockZ = (Integer)this.data[2];
         boolean mechanic = (Boolean)this.data[3];
         String blessing = null;
         if (player.getEntityData().hasKey("Blessing")) {
            blessing = player.getEntityData().getString("Blessing");
         }

         boolean scout = blessing != null && blessing.equals("Scout");
         TileEntity te = player.worldObj.getTileEntity(blockX, blockY, blockZ);
         boolean yours = te != null && te instanceof TileEntityTrap ? player.getCommandSenderName().equals(((TileEntityTrap)te).placedBy) : false;
         if (!yours) {
            label151: {
               if (mechanic) {
                  if (ModjamMod.r.nextInt(4) != 0) {
                     break label151;
                  }
               } else if (ModjamMod.r.nextInt(4) == 0) {
                  break label151;
               }

               int trapType = player.worldObj.getBlockMetadata(blockX, blockY, blockZ);
               if (trapType % BlockTrap.trapTypes == 0) {
                  player.attackEntityFrom(DamageSource.cactus, 4.0F + (float)(scout ? 1 : 0));
                  if (ModjamMod.r.nextInt(16 - (scout ? 4 : 0)) == 0) {
                     player.addPotionEffect(new PotionEffect(Potion.poison.id, 100 + (scout ? 25 : 0), 1));
                  }
               } else if (trapType % BlockTrap.trapTypes == 1) {
                  if (!player.isBurning()) {
                     player.setFire(5 + (scout ? 1 : 0));
                  }
               } else if (trapType % BlockTrap.trapTypes == 2) {
                  player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100 + (scout ? 25 : 0), 1));
                  player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100 + (scout ? 25 : 0), 1));
               }

               player.worldObj.setBlockToAir(blockX, blockY, blockZ);
               NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§c§oYou failed to disarm the trap.");
               if (CommonTickHandler.worldData.getDisadvantage().equals("Explosive Traps")) {
                  player.worldObj.setBlockToAir(blockX, blockY, blockZ);
                  player.worldObj.createExplosion((Entity)null, (double)((float)blockX + 0.5F), (double)((float)blockY + 0.5F), (double)((float)blockZ + 0.5F), 1.33F, true);
               }

               player.triggerAchievement(ModjamMod.whoops);
               return;
            }
         }

         boolean var10000;
         label141: {
            if (!yours) {
               label137: {
                  if (mechanic) {
                     if (ModjamMod.r.nextBoolean()) {
                        break label137;
                     }
                  } else if (ModjamMod.r.nextInt(4) == 0) {
                     break label137;
                  }

                  var10000 = false;
                  break label141;
               }
            }

            var10000 = true;
         }

         boolean salvage = var10000;
         NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§e§o" + (!salvage ? "You disarmed the trap." : "You disarm and salvage the trap."));
         if (salvage) {
            player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, (double)((float)blockX + 0.5F), (double)blockY, (double)((float)blockZ + 0.5F), new ItemStack(ModjamMod.itemTrap, 1, player.worldObj.getBlockMetadata(blockX, blockY, blockZ) % BlockTrap.trapTypes)));
         }

         player.worldObj.setBlockToAir(blockX, blockY, blockZ);
      }

      public Class[] getExpectedClasses() {
         return new Class[]{Integer.class, Integer.class, Integer.class, Boolean.class};
      }
   };
   public static final NewPacketHandler.BasicPacket EXAMINE_MOB = new NewPacketHandler.BasicPacket(8) {
      public void executeServer(EntityPlayer player) {
         int dimension = (Integer)this.data[0];
         int entityID = (Integer)this.data[1];
         WorldServer server = null;
         WorldServer[] arr$ = MinecraftServer.getServer().worldServers;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            WorldServer s = arr$[i$];
            if (s.provider.dimensionId == dimension) {
               server = s;
               break;
            }
         }

         if (server != null) {
            Entity entity = server.getEntityByID(entityID);
            if (entity != null) {
               String blessing2 = entity.getEntityData().hasKey("Blessing") ? entity.getEntityData().getString("Blessing") : null;
               if (blessing2 != null) {
                  NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§eYou notice " + entity.getCommandSenderName() + "§e is using Blessing of the " + blessing2 + ".");
               } else {
                  NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§eThere doesn't seem to be anything special about " + (entity instanceof EntityPlayer ? "" : "this ") + entity.getCommandSenderName() + "§e.");
               }
            }
         }

      }

      public Class[] getExpectedClasses() {
         return new Class[]{Integer.class, Integer.class};
      }
   };
   public static final NewPacketHandler.BasicPacket LEVEL_UP = new NewPacketHandler.BasicPacket(9) {
      public void executeBoth(EntityPlayer player) {
         player.addExperienceLevel((Integer)this.data[0]);
      }

      public Class[] getExpectedClasses() {
         return new Class[]{Integer.class};
      }
   };
   public static final NewPacketHandler.BasicPacket ACTIVATE_BLESSING = new NewPacketHandler.BasicPacket(10) {
      public void executeServer(EntityPlayer player) {
         int x = (Integer)this.data[0];
         int y = (Integer)this.data[1];
         int z = (Integer)this.data[2];
         String blessing = EntityStatHelper.getStat(player, "Blessing");
         boolean blessingActive = EntityStatHelper.hasStat(player, "BlessingActive") ? Boolean.parseBoolean(EntityStatHelper.getStat(player, "BlessingActive")) : false;
         if (!EntityStatHelper.hasStat(player, "BlessingCooldown")) {
            EntityStatHelper.giveStat(player, "BlessingCooldown", 0);
         }

         long time = CommonTickHandler.worldData != null && CommonTickHandler.worldData.getDisadvantage().equals("Neverending Rain") ? player.worldObj.getTotalWorldTime() : player.worldObj.getWorldTime();
         if (EntityStatHelper.getStat(player, "BlessingCooldown").equals("0")) {
            if (!blessingActive) {
               if (blessing != null) {
                  if (blessing.equals("Berserker")) {
                     if (EntityStatHelper.hasStat(player, "BlessingCounter") && Integer.parseInt(EntityStatHelper.getStat(player, "BlessingCounter")) > 0) {
                        blessingActive = true;
                        NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§cYou enter berserk mode.");
                     } else {
                        NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§cYou have no berserk counters.");
                     }
                  } else if (blessing.equals("Mechanic")) {
                     TileEntity te = player.worldObj.getTileEntity(x, y, z);
                     if (te != null && te instanceof TileEntityTrap) {
                        NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§e§oYou disarm and salvage the trap.");
                        player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, (double)((float)x + 0.5F), (double)y, (double)((float)z + 0.5F), new ItemStack(ModjamMod.itemTrap, 1, player.worldObj.getBlockMetadata(x, y, z) % BlockTrap.trapTypes)));
                        player.worldObj.setBlockToAir(x, y, z);
                        EntityStatHelper.giveStat(player, "BlessingCooldown", 24000L - time % 24000L);
                     } else {
                        NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§e§oNo selected trap.");
                     }
                  }
               }
            } else {
               blessingActive = false;
               if (blessing != null && blessing.equals("Berserker")) {
                  NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§cYou calm down.");
                  EntityStatHelper.giveStat(player, "BlessingCooldown", 1200);
               }

               EntityStatHelper.giveStat(player, "BlessingTimer", 0);
            }
         } else {
            NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "§cBlessing is on cooldown. (" + Integer.parseInt(EntityStatHelper.getStat(player, "BlessingCooldown")) / 20 + "s)");
         }

         EntityStatHelper.giveStat(player, "BlessingActive", blessingActive);
      }

      public Class[] getExpectedClasses() {
         return new Class[]{Integer.class, Integer.class, Integer.class};
      }
   };
   public static final NewPacketHandler.BasicPacket UPDATE_STAT = new NewPacketHandler.BasicPacket(11) {
      public void executeBoth(EntityPlayer player) {
         EntityStatHelper.giveStat(player, (String)this.data[0], this.data[1]);
      }

      public Class[] getExpectedClasses() {
         return new Class[]{String.class, String.class};
      }
   };

   public static void sendPacketToPlayer(NewPacketHandler.IPacket packet, EntityPlayer player) {
      ((FMLEmbeddedChannel)ModjamMod.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
      ((FMLEmbeddedChannel)ModjamMod.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
      ((FMLEmbeddedChannel)ModjamMod.channels.get(Side.SERVER)).writeOutbound(new Object[]{packet});
   }

   public static void sendPacketToAllPlayers(NewPacketHandler.IPacket packet) {
      ((FMLEmbeddedChannel)ModjamMod.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALL);
      ((FMLEmbeddedChannel)ModjamMod.channels.get(Side.SERVER)).writeOutbound(new Object[]{packet});
   }

   public static void sendPacketToServer(NewPacketHandler.IPacket packet) {
      ((FMLEmbeddedChannel)ModjamMod.channels.get(Side.CLIENT)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.TOSERVER);
      ((FMLEmbeddedChannel)ModjamMod.channels.get(Side.CLIENT)).writeOutbound(new Object[]{packet});
   }

   public static class BasicPacket implements NewPacketHandler.IPacket {
      public static Class[] validClassArray = new Class[]{Integer.class, Boolean.class, String.class, Character.class, Byte.class, Float.class, Double.class, int[].class, int[][].class};
      public static ArrayList validClasses = new ArrayList();
      public Object[] data = null;
      public byte type;

      public BasicPacket() {
      }

      public BasicPacket(int type) {
         if (type != 0 && NewPacketHandler.packetTypes[type] == null) {
            NewPacketHandler.packetTypes[type] = this;
            this.type = (byte)type;
         } else {
            throw new RuntimeException("Packet slot " + type + " already in use.");
         }
      }

      public BasicPacket(NewPacketHandler.BasicPacket packet, Object... data) {
         if (packet.type > 0 && packet.type < NewPacketHandler.packetTypes.length && packet == NewPacketHandler.packetTypes[packet.type]) {
            this.type = packet.type;
            Class[] classes = this.getExpectedClasses();
            if (classes != null) {
               Class[] arr$ = classes;
               int len$ = classes.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  Class c = arr$[i$];
                  if (!validClasses.contains(c)) {
                     throw new RuntimeException("Argument class not valid. (" + c + ")");
                  }
               }

               if (data == null || data.length != classes.length) {
                  throw new RuntimeException("Wrong number of arguments provided.");
               }

               for(int i = 0; i < data.length; ++i) {
                  if (data[i].getClass() != classes[i]) {
                     throw new RuntimeException("Wrong argument class provided. (" + data[i].getClass() + ", expected " + classes[i] + ")");
                  }
               }
            }

            this.data = data;
         } else {
            throw new RuntimeException("Must supply valid packet type.");
         }
      }

      public void readBytes(ByteBuf bytes) {
         this.type = bytes.readByte();
         Class[] classes = this.getExpectedClasses();
         if (classes != null) {
            this.data = new Object[classes.length];

            for(int i = 0; i < classes.length; ++i) {
               if (classes[i] == Integer.class) {
                  this.data[i] = bytes.readInt();
               }

               int i2;
               if (classes[i] == int[].class) {
                  int[] array = new int[bytes.readInt()];

                  for(i2 = 0; i2 < array.length; ++i2) {
                     array[i2] = bytes.readInt();
                  }

                  this.data[i] = array;
               }

               if (classes[i] != int[][].class) {
                  if (classes[i] == Boolean.class) {
                     this.data[i] = bytes.readBoolean();
                  } else if (classes[i] == String.class) {
                     int length = bytes.readInt();

                     try {
                        byte[] stringBytes = new byte[length];
                        bytes.readBytes(stringBytes);
                        this.data[i] = new String(stringBytes, "UTF-8");
                     } catch (Exception var7) {
                        var7.printStackTrace();
                     }
                  } else if (classes[i] == Byte.class) {
                     this.data[i] = bytes.readByte();
                  } else if (classes[i] == Float.class) {
                     this.data[i] = bytes.readDouble();
                  } else if (classes[i] == Double.class) {
                     this.data[i] = bytes.readFloat();
                  } else if (classes[i] == Character.class) {
                     this.data[i] = bytes.readChar();
                  }
               } else {
                  int[][] array = new int[bytes.readInt()][];

                  for(i2 = 0; i2 < array.length; ++i2) {
                     array[i2] = new int[bytes.readInt()];

                     for(int i3 = 0; i3 < array[i2].length; ++i3) {
                        array[i2][i3] = bytes.readInt();
                     }
                  }

                  this.data[i] = array;
               }
            }
         }

      }

      public void writeBytes(ByteBuf bytes) {
         bytes.writeByte(this.type);
         if (this.data != null) {
            for(int i = 0; i < this.data.length; ++i) {
               if (this.data[i] instanceof Integer) {
                  bytes.writeInt((Integer)this.data[i]);
               } else if (this.data[i] instanceof int[]) {
                  bytes.writeInt(((int[])((int[])this.data[i])).length);

                  for(int i2 = 0; i2 < ((int[])((int[])this.data[i])).length; ++i2) {
                     bytes.writeInt(((int[])((int[])this.data[i]))[i2]);
                  }
               } else if (!(this.data[i] instanceof int[][])) {
                  if (this.data[i] instanceof Boolean) {
                     bytes.writeBoolean((Boolean)this.data[i]);
                  } else if (this.data[i] instanceof String) {
                     byte[] stringBytes = ((String)this.data[i]).getBytes(Charset.forName("UTF-8"));
                     bytes.writeInt(stringBytes.length);
                     bytes.writeBytes(stringBytes);
                  } else if (this.data[i] instanceof Byte) {
                     bytes.writeByte((Byte)this.data[i]);
                  } else if (this.data[i] instanceof Float) {
                     bytes.writeDouble((Double)this.data[i]);
                  } else if (this.data[i] instanceof Double) {
                     bytes.writeFloat((Float)this.data[i]);
                  } else if (this.data[i] instanceof Character) {
                     bytes.writeChar((Character)this.data[i]);
                  }
               } else {
                  int[][] values = (int[][])((int[][])this.data[i]);
                  bytes.writeInt(values.length);

                  for(int i2 = 0; i2 < values.length; ++i2) {
                     bytes.writeInt(values[i2].length);

                     for(int i3 = 0; i3 < values[i2].length; ++i3) {
                        bytes.writeInt(values[i2][i3]);
                     }
                  }
               }
            }
         }

      }

      public void executeClient(EntityPlayer player) {
         if (NewPacketHandler.packetTypes[this.type] != this) {
            NewPacketHandler.packetTypes[this.type].executeClient(player);
         }

      }

      public void executeServer(EntityPlayer player) {
         if (NewPacketHandler.packetTypes[this.type] != this) {
            NewPacketHandler.packetTypes[this.type].executeServer(player);
         }

      }

      public void executeBoth(EntityPlayer player) {
         if (NewPacketHandler.packetTypes[this.type] != this) {
            NewPacketHandler.packetTypes[this.type].executeBoth(player);
         }

      }

      public Class[] getExpectedClasses() {
         return NewPacketHandler.packetTypes[this.type] != this ? NewPacketHandler.packetTypes[this.type].getExpectedClasses() : null;
      }

      public void sendToPlayer(EntityPlayer player, Object... data) {
         NewPacketHandler.sendPacketToPlayer(new NewPacketHandler.BasicPacket(this, data), player);
      }

      public void sendToAllPlayers(Object... data) {
         NewPacketHandler.sendPacketToAllPlayers(new NewPacketHandler.BasicPacket(this, data));
      }

      public void sendToServer(Object... data) {
         NewPacketHandler.sendPacketToServer(new NewPacketHandler.BasicPacket(this, data));
      }

      static {
         Class[] arr$ = validClassArray;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Class c = arr$[i$];
            validClasses.add(c);
         }

      }
   }

    public static class ChannelHandler extends FMLIndexedMessageToMessageCodec<NewPacketHandler.IPacket> {
      public ChannelHandler() {
         this.addDiscriminator(0, NewPacketHandler.BasicPacket.class);
      }

      public void encodeInto(ChannelHandlerContext ctx, NewPacketHandler.IPacket packet, ByteBuf data) throws Exception {
         packet.writeBytes(data);
      }

      public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, NewPacketHandler.IPacket packet) {
         packet.readBytes(data);
         if (packet instanceof NewPacketHandler.BasicPacket) {
            NewPacketHandler.packetTypes[((NewPacketHandler.BasicPacket)packet).type].data = ((NewPacketHandler.BasicPacket)packet).data;
         }

         EntityPlayer player;
         switch(FMLCommonHandler.instance().getEffectiveSide()) {
         case CLIENT:
            player = this.getClientPlayer();
            if (player != null) {
               packet.executeClient(player);
               packet.executeBoth(player);
            }
            break;
         case SERVER:
            INetHandler netHandler = (INetHandler)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            player = ((NetHandlerPlayServer)netHandler).playerEntity;
            if (player != null) {
               packet.executeServer(player);
               packet.executeBoth(player);
            }
         }

      }

      @SideOnly(Side.CLIENT)
      public EntityPlayer getClientPlayer() {
         return Minecraft.getMinecraft().thePlayer;
      }
   }

   public interface IPacket {
      void readBytes(ByteBuf var1);

      void writeBytes(ByteBuf var1);

      void executeClient(EntityPlayer var1);

      void executeServer(EntityPlayer var1);

      void executeBoth(EntityPlayer var1);
   }
}
