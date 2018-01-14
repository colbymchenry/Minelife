package com.minelife.gangs;

import com.minelife.Minelife;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.reorder.MultiStageReorder;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.function.operation.BlockMapEntryPlacer;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.io.Closer;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * THIS BLOCK IS UNUSED. BUT IT HAS VITAL CODE FOR COPING AND PASTING SCHEMATICS
 *
 */
public class BlockVaultCreator extends Block {

    private IIcon icon;

    public BlockVaultCreator() {
        super(Material.iron);
        setBlockName("vault_creator");
        setBlockTextureName(Minelife.MOD_ID + ":vault_creator");
        setCreativeTab(CreativeTabs.tabAllSearch);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f, float f1, float f2) {
        if(world.isRemote) return true;

        Closer closer = Closer.create();

        try {
            // loading
            ClipboardFormat format = ClipboardFormat.findByAlias("mcedit");
            FileInputStream fis = closer.register(new FileInputStream(new File(Minelife.getConfigDirectory(), "vault_schematics/iron.schematic")));
            BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
            ClipboardReader reader = format.getReader(bis);
            Clipboard clipboard = reader.read( ForgeWorldEdit.inst.getWorld(world).getWorldData());


            int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

            // rotating
            AffineTransform transform = new AffineTransform();
            transform = transform.rotateY(-(l == 0 ? 90 : l == 1 ? 180 : l == 2 ? 270 : 0));
            transform = transform.rotateX(-(0.0D));
            transform = transform.rotateZ(-(0.0D));



            // pasting
            Region region = clipboard.getRegion();
            Vector to = new Vector(x, y, z);

            Extent sourceExtent = clipboard;
            Vector from = clipboard.getOrigin();

            Extent targetExtent = ForgeWorldEdit.inst.getWorld(world);

            ForwardExtentCopy copy = new ForwardExtentCopy(sourceExtent, region, from, targetExtent, to);

            copy.setTransform(copy.getTransform().combine(transform));

            Operations.completeLegacy(copy);


        } catch (IOException var25) {
            System.out.println("Schematic could not read or it does not exist: " + var25.getMessage());
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        } finally {
            try {
                closer.close();
            } catch (IOException var24) {
                var24.printStackTrace();
            }

        }
        return true;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(Minelife.MOD_ID + ":vault_creator");
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return icon;
    }

}
