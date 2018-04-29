package com.minelife.airdrop;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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
        int weight = ModAirdrop.config.getInt("Weight");
//        System.out.println("LOOTSIZE: " + ModAirdrop.getLoot().size());
        Random random = new Random();

        if(ModAirdrop.getLoot().isEmpty()) return;

        for(int i = 0; i < 64; i++) {
            if(weight <= 0) break;
            Loot l = ModAirdrop.getLoot().get(random.nextInt(ModAirdrop.getLoot().size()));
            int size = random.nextInt(l.itemStack.getCount() < 1 ? 1 : l.itemStack.getCount());
            size = size == 0 ? 1 : size;
            ItemStack stack = new ItemStack(l.itemStack.getItem(), size, l.itemStack.getItemDamage());
            loot.add(stack);
            weight -= l.weight;
        }

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
