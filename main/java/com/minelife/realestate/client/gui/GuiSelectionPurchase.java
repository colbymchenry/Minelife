package com.minelife.realestate.client.gui;

import com.minelife.realestate.client.Selection;
import com.minelife.realestate.client.renderer.SelectionRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class GuiSelectionPurchase extends GuiScreen {

    private static int buttonID = 0;

    private Selection selection;
    private GuiButton cancel;
    private GuiButton confirm;

    public GuiSelectionPurchase(Selection selection) {
        this.selection = selection;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == this.cancel) {
            this.mc.displayGuiScreen(null);
        } else if (button == this.confirm) {
            selection.purchase();
            this.mc.displayGuiScreen(null);
            SelectionRenderer.clear();
        }
        super.actionPerformed(button);
    }

    @Override
    public void initGui() {
        int width = 200;
        int height = 20;
        this.buttonList.add(this.cancel = new GuiButton(buttonID++, 3 * this.width / 4 - width / 2, 3 * this.height / 4 - height / 2, width, height, "Cancel Purchase"));
        this.buttonList.add(this.confirm = new GuiButton(buttonID++, 1 * this.width / 4 - width / 2, 3 * this.height / 4 - height / 2, width, height, "Confirm Purchase"));
        super.initGui();
    }

}