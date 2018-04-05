package com.minelife.economy.client.gui.atm;

import com.minelife.Minelife;
import com.minelife.economy.Bill;
import com.minelife.economy.network.PacketRequestBills;
import com.minelife.util.DateHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiLoadingAnimation;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class GuiATMBills extends GuiATMBase {

    private Set<Bill> bills;
    private Content content;
    private GuiLoadingAnimation loadingAnimation;

    public GuiATMBills(long balance) {
        super(balance);
        Minelife.getNetwork().sendToServer(new PacketRequestBills());
    }

    public GuiATMBills(long balance, Set<Bill> bills) {
        super(balance);
        this.bills = bills;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if(bills == null) {
            loadingAnimation.drawLoadingAnimation();
        } else {
            content.draw(mouseX, mouseY, Mouse.getDWheel());
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        if(bills == null) {
            loadingAnimation = new GuiLoadingAnimation(this.width / 2, this.height / 2, 64, 64);
            return;
        }
        content = new Content(mc, (this.width - 256) / 2, (this.height - 150) / 2, 256, 180);
    }

    private class Content extends GuiScrollableContent {

        public Content(Minecraft mc, int x, int y, int width, int height) {
            super(mc, x, y, width, height);
        }

        @Override
        public int getObjectHeight(int index) {
            return fontRenderer.FONT_HEIGHT * 3 + 4;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            Bill bill = (Bill) bills.toArray()[index];
            Calendar now = Calendar.getInstance();
            Date billDate = bill.getDueDate();

            long diffDays = DateHelper.getDiffDays(billDate.after(now.getTime()) ? now.getTime() : billDate, billDate.after(now.getTime()) ? billDate : now.getTime());
            long diffHours = DateHelper.getDiffHours(billDate.after(now.getTime()) ? now.getTime() : billDate, billDate.after(now.getTime()) ? billDate : now.getTime());
            long diffMinutes = DateHelper.getDiffMinutes(billDate.after(now.getTime()) ? now.getTime() : billDate, billDate.after(now.getTime()) ? billDate : now.getTime());
            long diffSeconds = DateHelper.getDiffSeconds(billDate.after(now.getTime()) ? now.getTime() : billDate, billDate.after(now.getTime()) ? billDate : now.getTime());

            GlStateManager.enableBlend();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            if(now.getTime().before(billDate)) {
                if(diffDays < 1 && diffHours < 1 && diffMinutes < 20) {
                    GL11.glColor4f(191f / 255f, 186f / 255f, 42f / 255f, 188f / 255f);
                } else {
                    GL11.glColor4f(42f / 255f, 191f / 255f, 49f / 255f, 188f / 255f);
                }
                GuiHelper.drawRect(0, 0, width, getObjectHeight(index));
            } else {
                GL11.glColor4f(244f / 255f, 66f / 255f, 66f / 255f, 188f/255f);
                GuiHelper.drawRect(0, 0, width, getObjectHeight(index));
            }
            GlStateManager.disableBlend();

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1);

            int y = -fontRenderer.FONT_HEIGHT + 3;
//            fontRenderer.drawStringWithShadow(billDate.after(now.getTime()) ? TextFormatting.GREEN + "------------ UNTIL NEXT PAYMENT -----------" : TextFormatting.RED + "---------------- OVER DUE ----------------", 3, y += fontRenderer.FONT_HEIGHT, 0xFFFFFF);
            fontRenderer.drawStringWithShadow(bill.getMemo(), 3, y += fontRenderer.FONT_HEIGHT, 0xFFFFFF);
            fontRenderer.drawStringWithShadow(diffDays + " days " + diffHours + " hours " + diffMinutes + " minutes " + diffSeconds + " and seconds", 3, y += fontRenderer.FONT_HEIGHT, 0xFFFFFF);
            fontRenderer.drawStringWithShadow("Amount Due: $" + NumberConversions.format(bill.getAmountDue()), 3, y += fontRenderer.FONT_HEIGHT, 0xFFFFFF);
//            fontRenderer.drawStringWithShadow(billDate.after(now.getTime()) ? TextFormatting.GREEN + "-----------------------------------------" : TextFormatting.RED + "-----------------------------------------", 3, y += fontRenderer.FONT_HEIGHT, 0xFFFFFF);
        }

        @Override
        public int getSize() {
            return bills.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            if(doubleClick) mc.displayGuiScreen(new GuiATMPayBill(balance, (Bill) bills.toArray()[index]));
        }

        @Override
        public void drawBackground() {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(175f/255f, 175f/255f, 175f/255f, 1f);
            GuiHelper.drawRect(x, y, width, height);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1);
        }

    }
}
