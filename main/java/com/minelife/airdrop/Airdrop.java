package com.minelife.airdrop;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Airdrop implements Comparable<Airdrop> {

    public UUID id;
    public double x, y, z;
    public List<ItemStack> loot;
    public World world;

    public static Airdrop fromBytes(ByteBuf buf) {
        Airdrop airdrop = new Airdrop();
        airdrop.x = buf.readDouble();
        airdrop.y = buf.readDouble();
        airdrop.z = buf.readDouble();
        airdrop.id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        return airdrop;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        ByteBufUtils.writeUTF8String(buf, id.toString());
    }

    public void initLoot() {
        loot = Lists.newArrayList();
        AirdropTable airdropTable = new AirdropTable();
        airdropTable.getResult().forEach(drop -> {
            ItemStackDropable d = ((ItemStackDropable) drop);
            ItemStack stack = d.getValue().copy();
            stack.setCount(MathHelper.floor(MathHelper.nextDouble(world.rand, d.getMinSize(), d.getMaxSize())));
            loot.add(stack);
        });
    }

    public void spawnChest() {
        world.setBlockState(new BlockPos(x, y + 1, z), Blocks.CHEST.getDefaultState());
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(new BlockPos(x, y + 1, z));
        if (chest != null) {
            for (int i = 0; i < loot.size(); i++) {
                chest.setInventorySlotContents(i, loot.get(i));
            }

            world.markBlockRangeForRenderUpdate(chest.getPos(), chest.getPos());
            world.notifyBlockUpdate(chest.getPos(), world.getBlockState(chest.getPos()), world.getBlockState(chest.getPos()), 3);
            world.scheduleBlockUpdate(chest.getPos(), chest.getBlockType(), 0, 0);
            chest.markDirty();
        }
    }

    public void spawnBandits() {
        int amountToSpawn = world.rand.nextInt(4) + 2;

        for (int i = 0; i < amountToSpawn; i++) {
            EntityBandit bandit = new EntityBandit(world);
            int xPos = (int) (x + MathHelper.nextDouble(world.rand, -10, 10));
            int zPos = (int) (z + MathHelper.nextDouble(world.rand, -10, 10));
            int yPos = world.getTopSolidOrLiquidBlock(new BlockPos(xPos, 0, zPos)).getY();
            bandit.setPosition(xPos + 0.5, yPos, zPos + 0.5);
            bandit.setEquipmentBasedOnDifficulty(null);
            bandit.setTimeSpawned(Calendar.getInstance().getTime());
            world.spawnEntity(bandit);
        }

    }

    public void sendToAll() {
        Minelife.getNetwork().sendToAll(new PacketAirdrop(this));
    }


    @Override
    public int compareTo(Airdrop o) {
        return o.id.compareTo(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Airdrop && ((Airdrop) obj).id.equals(id);
    }
}
