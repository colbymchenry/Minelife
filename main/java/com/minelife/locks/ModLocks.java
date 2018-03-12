package com.minelife.locks;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import com.minelife.MLItems;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.permission.ModPermission;
import com.minelife.util.server.BlockBreakFix;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

public class ModLocks extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityLock.class, "tileEntityLock");
        MinecraftForge.EVENT_BUS.register(this);
        registerPacket(PacketUnlock.Handler.class, PacketUnlock.class, Side.CLIENT);
        registerPacket(PacketInteract.Handler.class, PacketInteract.class, Side.SERVER);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.locks.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.locks.server.ServerProxy.class;
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        TileEntityLock tileLock = findLock(event.world, event.x, event.y, event.z);

        if (tileLock == null) return;

        if (event.world.isRemote) {
            event.setCanceled(true);
            Minelife.NETWORK.sendToServer(new PacketInteract(event.x, event.y, event.z, event.face));
            return;
        }

        if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && ModPermission.hasPermission(event.entityPlayer.getUniqueID(), "locks.bypass")) {
            event.world.getBlock(tileLock.protectX, tileLock.protectY, tileLock.protectZ).
                    onBlockActivated(event.world, tileLock.protectX, tileLock.protectY, tileLock.protectZ, event.entityPlayer, event.face, 0.5f, 0.5f, 0.5f);
            return;
        }

        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.entityPlayer.getHeldItem() == null || event.entityPlayer.getHeldItem().getItem() != MLItems.lockPick) {
            if(ModPermission.hasPermission(event.entityPlayer.getUniqueID(), "locks.bypass")) return;
            event.setCanceled(true);
            BlockBreakFix.onBreak(new BlockEvent.BreakEvent(event.x, event.y, event.z, event.world, event.world.getBlock(event.x, event.y, event.z), event.world.getBlockMetadata(event.x, event.y, event.z), event.entityPlayer));
            return;
        }

        if (tileLock.attemptUnlock()) {
            event.world.playSoundEffect(event.x, event.y, event.z, Minelife.MOD_ID + ":lock_unlocked", 1.0f, 1.0f);
            Vector3 vec3 = new Vector3(tileLock.xCoord, tileLock.yCoord + 0.25, tileLock.zCoord);
            event.world.setBlockToAir(tileLock.xCoord, tileLock.yCoord, tileLock.zCoord);
            InventoryUtils.dropItem(new ItemStack(tileLock.lockType == LockType.IRON ? MLItems.ironLock :
                    tileLock.lockType == LockType.GOLD ? MLItems.goldLock : tileLock.lockType == LockType.DIAMOND ? MLItems.diamondLock :
                            MLItems.obsidianLock), event.world, vec3);

            if (findLock(event.world, event.x, event.y, event.z) == null)
                Minelife.NETWORK.sendTo(new PacketUnlock(tileLock.xCoord, tileLock.yCoord, tileLock.zCoord), (EntityPlayerMP) event.entityPlayer);
        } else {
            event.setCanceled(true);
            event.world.playSoundEffect(event.x, event.y, event.z, Minelife.MOD_ID + ":lock_pick_use", 1.0f, 1.0f);
            ItemStack heldItem = event.entityPlayer.getHeldItem().copy();
            heldItem.stackSize -= 1;
            if (heldItem.stackSize <= 0) {
                event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, null);
            } else {
                event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, heldItem);
            }
        }
    }

    // TODO: Implement double chests
    private TileEntityLock findLock(World world, int blockX, int blockY, int blockZ) {
        if (world.getTileEntity(blockX, blockY, blockZ) != null && world.getTileEntity(blockX, blockY, blockZ) instanceof TileEntityLock) {
            return (TileEntityLock) world.getTileEntity(blockX, blockY, blockZ);
        }

        TileEntityLock tileLockNegX = getLock(world, blockX - 1, blockY, blockZ);
        TileEntityLock tileLockNegZ = getLock(world, blockX, blockY, blockZ - 1);
        TileEntityLock tileLockPosX = getLock(world, blockX + 1, blockY, blockZ);
        TileEntityLock tileLockPosZ = getLock(world, blockX, blockY, blockZ + 1);

        boolean isDoor = world.getBlock(blockX, blockY, blockZ) == Blocks.wooden_door;
        boolean isTopDoor = isDoor && world.getBlock(blockX, blockY - 1, blockZ) == Blocks.wooden_door;
        boolean isBottomDoor = isDoor && world.getBlock(blockX, blockY + 1, blockZ) == Blocks.wooden_door;

        if (check(tileLockNegX, blockX, blockY, blockZ)) {
            return tileLockNegX;
        } else {
            if (isTopDoor) {
                tileLockNegX = getLock(world, blockX - 1, blockY - 1, blockZ);
                if (check(tileLockNegX, blockX, blockY - 1, blockZ)) return tileLockNegX;
            } else if (isBottomDoor) {
                tileLockNegX = getLock(world, blockX - 1, blockY + 1, blockZ);
                if (check(tileLockNegX, blockX, blockY + 1, blockZ)) return tileLockNegX;
            }
        }

        if (check(tileLockNegZ, blockX, blockY, blockZ)) {
            return tileLockNegZ;
        } else {
            if (isTopDoor) {
                tileLockNegZ = getLock(world, blockX, blockY - 1, blockZ - 1);
                if (check(tileLockNegZ, blockX, blockY - 1, blockZ)) return tileLockNegZ;
            } else if (isBottomDoor) {
                tileLockNegZ = getLock(world, blockX, blockY + 1, blockZ - 1);
                if (check(tileLockNegZ, blockX, blockY + 1, blockZ)) return tileLockNegZ;
            }
        }

        if (check(tileLockPosX, blockX, blockY, blockZ)) {
            return tileLockPosX;
        } else {
            if (isTopDoor) {
                tileLockPosX = getLock(world, blockX + 1, blockY - 1, blockZ);
                if (check(tileLockPosX, blockX, blockY - 1, blockZ)) return tileLockPosX;
            } else if (isBottomDoor) {
                tileLockPosX = getLock(world, blockX + 1, blockY + 1, blockZ);
                if (check(tileLockPosX, blockX, blockY + 1, blockZ)) return tileLockPosX;
            }
        }

        if (check(tileLockPosZ, blockX, blockY, blockZ)) {
            return tileLockPosZ;
        } else {
            if (isTopDoor) {
                tileLockPosZ = getLock(world, blockX, blockY - 1, blockZ + 1);
                if (check(tileLockPosZ, blockX, blockY - 1, blockZ)) return tileLockPosZ;
            } else if (isBottomDoor) {
                tileLockPosZ = getLock(world, blockX, blockY + 1, blockZ + 1);
                if (check(tileLockPosZ, blockX, blockY + 1, blockZ)) return tileLockPosZ;
            }
        }

        return null;
    }

    private boolean check(TileEntityLock tileLock, int blockX, int blockY, int blockZ) {
        return tileLock != null && tileLock.protectX == blockX && tileLock.protectY == blockY && tileLock.protectZ == blockZ;
    }

    private TileEntityLock getLock(World world, int x, int y, int z) {
        return world.getTileEntity(x, y, z) instanceof TileEntityLock ? (TileEntityLock) world.getTileEntity(x, y, z) : null;
    }
}
