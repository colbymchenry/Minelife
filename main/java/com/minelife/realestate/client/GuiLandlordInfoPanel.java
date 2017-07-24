package com.minelife.realestate.client;

import com.minelife.Minelife;
import com.minelife.realestate.sign.TileEntityForSaleSign;
import com.minelife.util.client.GuiTextField;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;

public class GuiLandlordInfoPanel extends AbstractZoneGui {

//TODO: Evicition, set amountDue, etc
    private TileEntityForSaleSign forSaleSign;

    public GuiLandlordInfoPanel(int x, int y, int z) {
        forSaleSign = (TileEntityForSaleSign) Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
    }

    private CustomZoneBtn evict, setAmountDue, setGracePeriod;
    private GuiTextField amountDueField, gracePeriodField;

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        super.drawScreen(mouseX, mouseY, f);
        drawBackground();
        evict.drawButton(mc, mouseX, mouseY);
        setAmountDue.drawButton(mc, mouseX, mouseY);
        setGracePeriod.drawButton(mc, mouseX, mouseY);
        amountDueField.drawTextBox();
        gracePeriodField.drawTextBox();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn)
    {
        super.mouseClicked(mouseX, mouseY, mouseBtn);
        // TODO
        if(evict.mousePressed(mc, mouseX, mouseY)) {

        } else if (setAmountDue.mousePressed(mc, mouseX, mouseY)) {

        } else if(setGracePeriod.mousePressed(mc, mouseX, mouseY)) {

        }

        amountDueField.mouseClicked(mouseX, mouseY);
        gracePeriodField.mouseClicked(mouseX, mouseY);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        amountDueField = new GuiTextField(calcX(50), yPosition + 20, 50, fontRendererObj.FONT_HEIGHT);
        gracePeriodField = new GuiTextField(calcX(50), yPosition + 40, 50, fontRendererObj.FONT_HEIGHT);
        evict = new CustomZoneBtn(0, xPosition + 15, yPosition + height - 20, fontRendererObj.getStringWidth("Evict") + 15, 20, "Evict");
        setAmountDue = new CustomZoneBtn(1, amountDueField.getBounds().getX() + amountDueField.getBounds().getWidth() + 10, amountDueField.getBounds().getY(), fontRendererObj.getStringWidth("Set") + 15, 20, "Set");
        setGracePeriod = new CustomZoneBtn(2, gracePeriodField.getBounds().getX() + gracePeriodField.getBounds().getWidth() + 10, gracePeriodField.getBounds().getY(), fontRendererObj.getStringWidth("Set") + 15, 20, "Set");
    }

    public static class PacketOpenGui implements IMessage {

        @SideOnly(Side.SERVER)
        public static void openFromServer(EntityPlayerMP player, int x, int y, int z) {
            Minelife.NETWORK.sendTo(new PacketOpenGui(x, y, z), player);
        }


        public PacketOpenGui()
        {
        }

        private int x, y, z;

        public PacketOpenGui(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {

        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
        }

        public static class Handler implements IMessageHandler<PacketOpenGui, IMessage> {

            @SideOnly(Side.CLIENT)
            public IMessage onMessage(PacketOpenGui message, MessageContext ctx)
            {
                Minecraft.getMinecraft().displayGuiScreen(new GuiLandlordInfoPanel(message.x, message.y, message.z));
                return null;
            }
        }
    }

}
