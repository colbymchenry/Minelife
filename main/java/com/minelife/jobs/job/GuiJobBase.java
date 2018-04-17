package com.minelife.jobs.job;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.network.PacketSellItemStack;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public abstract class GuiJobBase extends GuiScreen {

    private List<SellingOption> sellingOptions;
    private int guiLeft, guiTop, guiWidth = 150, guiHeight = 230;
    private SellingList sellingList;
    private EnumJob job;

    public GuiJobBase(EnumJob job) {
        this.sellingOptions = job.getHandler().getSellingOptions();
        this.job = job;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);
        sellingList.draw(mouseX, mouseY, Mouse.getDWheel());

        if (this.sellingList.hoveringOption != null)
            drawHoveringText(Lists.newArrayList(this.sellingList.hoveringOption.getStack().getDisplayName(), TextFormatting.GRAY + TextFormatting.ITALIC.toString() + "$" + NumberConversions.format(sellingList.hoveringOption.getPrice())), mouseX, mouseY);

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.player.sendMessage(new TextComponentString(farewellMessage(mc.player)));
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - guiWidth) / 2;
        guiTop = (this.height - guiHeight) / 2;

        sellingList = new SellingList(mc, guiLeft + 4, guiTop + 4, guiWidth - 8, guiHeight - 8);
    }

    public abstract String farewellMessage(EntityPlayer player);

    class SellingList extends GuiScrollableContent {

        SellingOption hoveringOption;
        List<GuiButton> sellingButtons = Lists.newArrayList();

        public SellingList(Minecraft mc, int x, int y, int width, int height) {
            super(mc, x, y, width, height);
            int yOffset = -20;

            for (int i = 0; i < sellingOptions.size(); i++)
                sellingButtons.add(new GuiButton(i, width - 60, yOffset += 30, 30, 20, "Sell"));
        }

        @Override
        public int getObjectHeight(int index) {
            return 10 + 30 * sellingOptions.size();
        }

        @Override
        public int getSize() {
            return 1;
        }

        boolean foundOne = false;

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            foundOne = false;

            for (int i = 0; i < sellingButtons.size(); i++) {
                GlStateManager.disableLighting();
                sellingButtons.get(i).drawButton(mc, mouseX, mouseY, 0);

                SellingOption option = sellingOptions.get(i);
                GuiFakeInventory.renderItemInventory(option.getStack(), 30, (i * 30) + 10, true);
                if (mouseX >= 30 && mouseX <= 30 + 16 && mouseY >= (i * 30) + 10 &&
                        mouseY <= (i * 30) + 10 + 16) {
                    foundOne = true;
                    hoveringOption = option;
                }
            }

            if (!foundOne) hoveringOption = null;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            sellingButtons.forEach(btn -> {
                if(btn.mousePressed(mc, mouseX, mouseY)) {
                    Minelife.getNetwork().sendToServer(new PacketSellItemStack(job, sellingOptions.get(btn.id).getStack()));
                }
            });
        }

        @Override
        public void drawSelectionBox(int index, int width, int height) {

        }
    }
}
