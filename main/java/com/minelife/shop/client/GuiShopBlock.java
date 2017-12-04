package com.minelife.shop.client;

import com.minelife.shop.TileEntityShopBlock;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class GuiShopBlock extends GuiScreen {

    private GuiFakeInventory fake_inventory;

    private static final Color slot_color = new Color(0x919191);
    private TileEntityShopBlock tile_entity;
    private int bg_width = 200, bg_height = 150, xPosition, yPosition;
    private GuiTextField price_field, amount_field;
    private int slot_to_sale = -1;

    public GuiShopBlock(TileEntityShopBlock tile_entity) {
        this.tile_entity = tile_entity;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bg_width, bg_height);
        fontRendererObj.drawStringWithShadow("Amount:", amount_field.xPosition - 40, amount_field.yPosition + 4, 0xFFFFFF);
        amount_field.drawTextBox();
        fontRendererObj.drawStringWithShadow("Price:", price_field.xPosition - 32, price_field.yPosition + 4, 0xFFFFFF);
        price_field.drawTextBox();
        fake_inventory.draw(mouse_x, mouse_y);

        if (this.slot_to_sale != -1 && mc.thePlayer.inventory.getStackInSlot(slot_to_sale) != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef(xPosition + 20, yPosition + 20, zLevel);
            GL11.glTranslatef(6, 6, 6);
            GL11.glScalef(2, 2, 2);
            GL11.glTranslatef(-6, -6, -6);
            fake_inventory.item_renderer.drawItemStack(mc.thePlayer.inventory.getStackInSlot(slot_to_sale), 0, 0);
            GL11.glPopMatrix();

            if(mouse_x >= xPosition + 14 && mouse_x <= xPosition + 46 && mouse_y >= yPosition + 14 && mouse_y <= yPosition + 46) {
                fake_inventory.item_renderer.renderToolTip(mc.thePlayer.inventory.getStackInSlot(slot_to_sale), mouse_x, mouse_y);
            }
        }

    }

    @Override
    protected void keyTyped(char key_char, int key_id) {
        super.keyTyped(key_char, key_id);
        price_field.textboxKeyTyped(key_char, key_id);
        amount_field.textboxKeyTyped(key_char, key_id);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
        price_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
        amount_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
        if (fake_inventory.getClickedSlot(mouse_x, mouse_y) != -1)
            this.slot_to_sale = fake_inventory.getClickedSlot(mouse_x, mouse_y);
        // TODO: We have selecting the item and rendering it, now we need to be able to send a packet to the server and we also need to make sure they can only enter integers and decimals into the dollar amount.
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = (this.width - bg_width) / 2;
        yPosition = (this.height - bg_height) / 2;
        amount_field = new GuiTextField(fontRendererObj, xPosition + ((bg_width - 75) / 2) + 50, yPosition + 10, 75, 20);
        price_field = new GuiTextField(fontRendererObj, xPosition + ((bg_width - 75) / 2) + 50, yPosition + 40, 75, 20);

        int xOffset = xPosition + 20;
        int yOffset = yPosition + 70;

        fake_inventory = new GuiFakeInventory(mc) {
            @Override
            public void setupSlots() {
                // players hotbar
                for (int x = 0; x < 9; ++x) {
                    this.slots.add(new ItemSlot(mc.thePlayer.inventory.mainInventory[x], new java.awt.Rectangle(xOffset + x * 18, yOffset + 58, 16, 16), x));
                }

                // players inventory
                for (int y = 0; y < 3; ++y) {
                    for (int x = 0; x < 9; ++x) {
                        this.slots.add(new ItemSlot(mc.thePlayer.inventory.mainInventory[x + y * 9 + 9], new java.awt.Rectangle(xOffset + x * 18, yOffset + y * 18, 16, 16), x + y * 9 + 9));
                    }
                }
            }
        };

        fake_inventory.slotColor = new Color(0x8B8B8B);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        price_field.updateCursorCounter();
        amount_field.updateCursorCounter();
    }
}
