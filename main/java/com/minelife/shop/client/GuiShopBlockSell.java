package com.minelife.shop.client;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.shop.TileEntityShopBlock;
import com.minelife.shop.network.PacketSetShopBlock;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.text.NumberFormat;
import java.text.ParseException;

public class GuiShopBlockSell extends GuiScreen {

    private GuiFakeInventory fake_inventory;

    private static final Color slot_color = new Color(0x919191);
    private TileEntityShopBlock tile_entity;
    private int bg_width = 200, bg_height = 150, xPosition, yPosition;
    private GuiTextField price_field, amount_field;
    private ItemStack stackToDisplay;
    private GuiButton set_btn;

    public GuiShopBlockSell(TileEntityShopBlock tile_entity) {
        this.tile_entity = tile_entity;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bg_width, bg_height);
        set_btn.drawButton(mc, mouse_x, mouse_y);
        fontRendererObj.drawStringWithShadow("Stack:", amount_field.xPosition - 50 + 17, amount_field.yPosition + 1, 0xFFFFFF);
        fontRendererObj.drawStringWithShadow("Size:", amount_field.xPosition - 47 + 17, amount_field.yPosition + 11, 0xFFFFFF);
        amount_field.drawTextBox();
        fontRendererObj.drawStringWithShadow("Price:", price_field.xPosition - 32, price_field.yPosition + 4, 0xFFFFFF);
        price_field.drawTextBox();
        fake_inventory.draw(mouse_x, mouse_y);


        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(99f / 255f, 99f / 255f, 99f / 255f, 1f);
        GuiUtil.drawImage(xPosition + 20, yPosition + 20, 32, 32);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        if (this.stackToDisplay != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef(xPosition + 26, yPosition + 26, zLevel);
            GL11.glTranslatef(6, 6, 6);
            GL11.glScalef(2, 2, 2);
            GL11.glTranslatef(-6, -6, -6);
            fake_inventory.item_renderer.drawItemStack(stackToDisplay, 0, 0);
            GL11.glPopMatrix();

            if (mouse_x >= xPosition + 14 && mouse_x <= xPosition + 46 && mouse_y >= yPosition + 14 && mouse_y <= yPosition + 46) {
                fake_inventory.item_renderer.renderToolTip(stackToDisplay, mouse_x, mouse_y);
            }
        }

    }

    @Override
    protected void keyTyped(char key_char, int key_id) {
        super.keyTyped(key_char, key_id);

        if(ModEconomy.handleInput(price_field.getText(), price_field.isFocused(), key_char, key_id)) price_field.textboxKeyTyped(key_char, key_id);
        if (stackToDisplay != null && (NumberConversions.isInt(String.valueOf(key_char)) || key_id == Keyboard.KEY_BACK) && NumberConversions.toInt(amount_field.getText() + key_char) <= stackToDisplay.getMaxStackSize()) amount_field.textboxKeyTyped(key_char, key_id);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
        price_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
        amount_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
        if (fake_inventory.getClickedSlot(mouse_x, mouse_y) != -1) {
            this.stackToDisplay = mc.thePlayer.inventory.getStackInSlot(fake_inventory.getClickedSlot(mouse_x, mouse_y)).copy();
            this.stackToDisplay.stackSize = 1;
        }

        if(set_btn.mousePressed(mc, mouse_x, mouse_y)) {
            Minelife.NETWORK.sendToServer(new PacketSetShopBlock(this.stackToDisplay, NumberConversions.toInt(amount_field.getText()),
                    NumberConversions.toDouble(price_field.getText()), tile_entity.xCoord, tile_entity.yCoord, tile_entity.zCoord));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = (this.width - bg_width) / 2;
        yPosition = (this.height - bg_height) / 2;
        price_field = new GuiTextField(fontRendererObj, xPosition + ((bg_width - 75) / 2) + 50, yPosition + 10, 75, 20);
        amount_field = new GuiTextField(fontRendererObj, xPosition + ((bg_width - 75) / 2) + 50, yPosition + 40, 31, 20);
        set_btn = new GuiButton(0, amount_field.xPosition + amount_field.width + 10, amount_field.yPosition, 35, 20, "Set");
        set_btn.enabled = false;

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

        if(tile_entity.getStackToSale() != null) {
            this.stackToDisplay = tile_entity.getStackToSale().copy();
            this.stackToDisplay.stackSize = 1;
            this.price_field.setText("" + tile_entity.getPrice());
            this.amount_field.setText("" + tile_entity.getStackToSale().stackSize);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        price_field.updateCursorCounter();
        amount_field.updateCursorCounter();
        set_btn.enabled = !price_field.getText().isEmpty() && !amount_field.getText().isEmpty();
        amount_field.setEnabled(stackToDisplay != null);
        if(stackToDisplay == null) {
            amount_field.setText("");
        } else {
            if(NumberConversions.isInt(amount_field.getText())) {
                if(NumberConversions.toInt(amount_field.getText()) > stackToDisplay.getMaxStackSize()) {
                    amount_field.setText("" + stackToDisplay.getMaxStackSize());
                }
            }
        }
    }
}
