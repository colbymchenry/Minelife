package com.minelife.realestate.client;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.realestate.Zone;
import com.minelife.realestate.ZoneForSale;
import com.minelife.util.client.GuiScrollList;
import com.minelife.util.client.GuiTickBox;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;

import java.util.logging.Level;

public class GuiZoneSell extends AbstractZoneGui {

    private Content content;

    public GuiZoneSell()
    {
        super(200, 200);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        this.drawBackground();
        content.draw(mouseX, mouseY, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char c, int keyCode)
    {
        super.keyTyped(c, keyCode);
        content.keyTyped(c, keyCode);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn)
    {
        super.mouseClicked(x, y, btn);
    }

    @Override
    protected void actionPerformed(GuiButton btn)
    {
        super.actionPerformed(btn);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        content = new Content(this.xPosition, this.yPosition, this.bgWidth, this.bgHeight);
    }

    @Override
    public void updateScreen()
    {
    }

    private class Content extends GuiScrollList {

        private GuiTextField titleField, priceField;
        private com.minelife.util.client.GuiTextField description;
        private GuiTickBox forRent, allowPlacement, allowBreaking, allowInteracting;
        private CustomZoneBtn sellBtn;

        public Content(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);

            this.titleField = new GuiTextField(mc.fontRenderer, calcX(175) - this.xPosition, 30, 175, 20);
            this.titleField.setMaxStringLength(this.titleField.getMaxStringLength() + 2);

            this.description = new com.minelife.util.client.GuiTextField(calcX(175) - this.xPosition, this.titleField.yPosition + 50, 175, 50);
            this.priceField = new GuiTextField(mc.fontRenderer, calcX(100) - this.xPosition, this.description.getBounds().getY() + this.description.getBounds().getHeight() + 30, 100, 20);

            int tickboxPosX = width - 50;
            this.forRent = new GuiTickBox(mc, tickboxPosX, this.priceField.yPosition + 50, false);
            this.allowPlacement = new GuiTickBox(mc, tickboxPosX, this.forRent.yPosition + 30, false);
            this.allowBreaking = new GuiTickBox(mc, tickboxPosX, this.allowPlacement.yPosition + 30, false);
            this.allowInteracting = new GuiTickBox(mc, tickboxPosX, this.allowBreaking.yPosition + 30, false);

            this.sellBtn = new CustomZoneBtn(0, (width - mc.fontRenderer.getStringWidth("Sell") + 4) / 2, getObjectHeight(0) - 30, mc.fontRenderer.getStringWidth("Sell") + 20, 20, "Sell");
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 375;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            mc.fontRenderer.drawString("Title", calcX(mc.fontRenderer.getStringWidth("Title")) - this.xPosition, this.titleField.yPosition - 15, 0xFFFFFF);
            this.titleField.drawTextBox();
            mc.fontRenderer.drawString("Price", calcX(mc.fontRenderer.getStringWidth("Price")) - this.xPosition, this.priceField.yPosition - 15, 0xFFFFFF);
            this.priceField.drawTextBox();
            mc.fontRenderer.drawString("Description", calcX(mc.fontRenderer.getStringWidth("Description")) - this.xPosition, this.description.getBounds().getY() - 15, 0xFFFFFF);
            this.description.drawTextBox();

            mc.fontRenderer.drawString("For Rent (Player charged per day)", 10, this.forRent.yPosition + 5, 0xFFFFFF);
            this.forRent.draw();
            mc.fontRenderer.drawString("Allow Placement", 10, this.allowPlacement.yPosition + 5, 0xFFFFFF);
            this.allowPlacement.draw();
            mc.fontRenderer.drawString("Allow Breaking", 10, this.allowBreaking.yPosition + 5, 0xFFFFFF);
            this.allowBreaking.draw();
            mc.fontRenderer.drawString("Allow Interacting", 10, this.allowInteracting.yPosition + 5, 0xFFFFFF);
            this.allowInteracting.draw();

            this.sellBtn.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public int getSize()
        {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            this.titleField.mouseClicked(mouseX, mouseY, 0);
            this.priceField.mouseClicked(mouseX, mouseY, 0);
            this.description.mouseClicked(mouseX, mouseY);

            this.forRent.mouseClicked(mouseX, mouseY);
            this.allowPlacement.mouseClicked(mouseX, mouseY);
            this.allowBreaking.mouseClicked(mouseX, mouseY);
            this.allowInteracting.mouseClicked(mouseX, mouseY);

            // TODO: Incorporate other gui elements for packet
            if(sellBtn.mousePressed(mc, mouseX, mouseY))
            {
                Minelife.NETWORK.sendToServer(new PacketSellZone());
            }
        }

        @Override
        public void keyTyped(char keycode, int keynum)
        {
            super.keyTyped(keycode, keynum);
            this.titleField.textboxKeyTyped(keycode, keynum);
            this.priceField.textboxKeyTyped(keycode, keynum);
            this.description.textboxKeyTyped(keycode, keynum);
        }

        @Override
        public void drawBackground()
        {
        }

        @Override
        public void drawSelectionBox(int index, int width, int height)
        {
        }

    }

    public static class PacketSellZone implements IMessage {

        private long price;
        private int daysToPay;
        private boolean forRent, allowPlacement, allowBreaking, allowInteracting;
        private boolean allowAddingMembers;

        public PacketSellZone()
        {
        }

        public PacketSellZone(long price, int daysToPay, boolean forRent, boolean allowPlacement, boolean allowBreaking, boolean allowInteracting, boolean allowAddingMembers)
        {
            this.price = price;
            this.daysToPay = daysToPay;
            this.forRent = forRent;
            this.allowPlacement = allowPlacement;
            this.allowBreaking = allowBreaking;
            this.allowInteracting = allowInteracting;
            this.allowAddingMembers = allowAddingMembers;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            price = buf.readLong();
            daysToPay = buf.readInt();
            forRent = buf.readBoolean();
            allowPlacement = buf.readBoolean();
            allowBreaking = buf.readBoolean();
            allowInteracting = buf.readBoolean();
            allowAddingMembers = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeLong(price);
            buf.writeInt(daysToPay);
            buf.writeBoolean(forRent);
            buf.writeBoolean(allowPlacement);
            buf.writeBoolean(allowBreaking);
            buf.writeBoolean(allowInteracting);
            buf.writeBoolean(allowAddingMembers);
        }

        public static class Handler implements IMessageHandler<PacketSellZone, IMessage> {

            @SideOnly(Side.SERVER)
            public IMessage onMessage(PacketSellZone message, MessageContext ctx)
            {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                Zone zone = Zone.getZone(player.getEntityWorld(), Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

                try {
                    if (zone.getOwner() != null && !zone.getOwner().equals(player.getUniqueID()))
                        throw new CustomMessageException(EnumChatFormatting.RED + "Only the owner can sell the zone.");

                    if(ZoneForSale.hasListing(zone)) throw new CustomMessageException(EnumChatFormatting.RED + "Zone already for sale.");

                    new ZoneForSale(zone, message.forRent, message.daysToPay, message.allowBreaking,
                            message.allowPlacement, message.allowInteracting, message.allowAddingMembers).save();

                    player.addChatComponentMessage(new ChatComponentText("Zone is now for sale!"));
                }catch(Exception e) {
                    if(e instanceof CustomMessageException) {
                        player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
                    } else {
                        e.printStackTrace();
                        Minelife.getLogger().log(Level.SEVERE, "", e);
                        player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
                    }
                }
                return null;
            }
        }
    }

}