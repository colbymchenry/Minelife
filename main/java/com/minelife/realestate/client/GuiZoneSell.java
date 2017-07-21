package com.minelife.realestate.client;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.realestate.sign.BlockForSaleSign;
import com.minelife.realestate.sign.TileEntityForSaleSign;
import com.minelife.util.client.GuiTextField;
import com.minelife.util.client.GuiTickBox;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

import java.util.logging.Level;

import static sun.audio.AudioPlayer.player;

public class GuiZoneSell extends AbstractZoneGui {

    private int signX, signY, signZ;

    private GuiTextField priceField, billingDurationField;
    private GuiTickBox forRent, allowBreaking, allowPlacing, allowInteracting;
    private CustomZoneBtn sellBtn;

    public GuiZoneSell(int signX, int signY, int signZ)
    {
        super(200, 200);
        this.signX = signX;
        this.signY = signY;
        this.signZ = signZ;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        super.drawScreen(mouseX, mouseY, f);
        drawBackground();
        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.drawString("Price", calcX(fontRendererObj.getStringWidth("Price")), priceField.getBounds().getY() - 12, 0xFFFFFF);
        fontRendererObj.drawString("Billing Duration (In Days)", calcX(fontRendererObj.getStringWidth("Billing Duration (In Days)")), billingDurationField.getBounds().getY() - 12, 0xFFFFFF);
        fontRendererObj.setUnicodeFlag(false);
        priceField.drawTextBox();
        billingDurationField.drawTextBox();
        forRent.drawTickBox();
        sellBtn.drawButton(mc, mouseX, mouseY);

        allowPlacing.drawTickBox();
        allowBreaking.drawTickBox();
        allowInteracting.drawTickBox();

        fontRendererObj.drawString("For Rent?", this.xPosition + 10, forRent.yPosition + (18 - fontRendererObj.FONT_HEIGHT) / 2, 0xFFFFFF);
        fontRendererObj.drawString("Allow Breaking?", this.xPosition + 10, allowBreaking.yPosition + (18 - fontRendererObj.FONT_HEIGHT) / 2, 0xFFFFFF);
        fontRendererObj.drawString("Allow Placing?", this.xPosition + 10, allowPlacing.yPosition + (18 - fontRendererObj.FONT_HEIGHT) / 2, 0xFFFFFF);
        fontRendererObj.drawString("Allow Interacting?", this.xPosition + 10, allowInteracting.yPosition + (18 - fontRendererObj.FONT_HEIGHT) / 2, 0xFFFFFF);
    }

    @Override
    protected void keyTyped(char c, int k)
    {
        super.keyTyped(c, k);
        if (isLong("" + c))
            priceField.textboxKeyTyped(c, k);
        if (k == Keyboard.KEY_BACK)
            billingDurationField.textboxKeyTyped(c, k);
        else if (billingDurationField.getText().length() < 2)
            if (isLong("" + c))
                billingDurationField.textboxKeyTyped(c, k);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn)
    {
        super.mouseClicked(x, y, btn);
        priceField.mouseClicked(x, y);
        billingDurationField.mouseClicked(x, y);
        forRent.mouseClicked(x, y);
        allowInteracting.mouseClicked(x, y);
        allowPlacing.mouseClicked(x, y);
        allowBreaking.mouseClicked(x, y);

        if (sellBtn.mousePressed(mc, x, y)) {
            long price = priceField.getText().isEmpty() ? 0 : isLong(priceField.getText()) ? Long.parseLong(priceField.getText()) : 0;
            long billingDuration = billingDurationField.getText().isEmpty() ? 0 : isLong(billingDurationField.getText()) ? Long.parseLong(billingDurationField.getText()) : 0;
            Minelife.NETWORK.sendToServer(new PacketUpdateForSaleSign(signX, signY, signZ, price, billingDuration, forRent.isChecked(), allowBreaking.isChecked(), allowPlacing.isChecked(), allowInteracting.isChecked()));
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        priceField = new GuiTextField(calcX(100), this.yPosition + 20, 100, fontRendererObj.FONT_HEIGHT);
        forRent = new GuiTickBox(mc, this.xPosition + this.bgWidth - 50, this.priceField.getBounds().getY() + 20, false);
        billingDurationField = new GuiTextField(calcX(13), forRent.yPosition + 40, 13, fontRendererObj.FONT_HEIGHT);
        allowBreaking = new GuiTickBox(mc, forRent.xPosition, billingDurationField.getBounds().getY() + 22, false);
        allowPlacing = new GuiTickBox(mc, forRent.xPosition, allowBreaking.yPosition + 22, false);
        allowInteracting = new GuiTickBox(mc, forRent.xPosition, allowPlacing.yPosition + 22, false);
        sellBtn = new CustomZoneBtn(0, calcX(35), this.yPosition + this.bgHeight - 30, 35, 20, "Sell");

        billingDurationField.setEnabled(false);
        allowBreaking.enabled = false;
        allowPlacing.enabled = false;
        allowInteracting.enabled = false;
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        priceField.update();
        billingDurationField.update();
        billingDurationField.setEnabled(forRent.isChecked());
        allowBreaking.enabled = forRent.isChecked();
        allowPlacing.enabled = forRent.isChecked();
        allowInteracting.enabled = forRent.isChecked();
    }

    private boolean isLong(String s)
    {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static class PacketOpenGuiZoneSell implements IMessage {

        @SideOnly(Side.SERVER)
        public static void openFromServer(TileEntityForSaleSign tileEntityForSaleSign, EntityPlayerMP player)
        {
            Minelife.NETWORK.sendTo(new PacketOpenGuiZoneSell(tileEntityForSaleSign.xCoord, tileEntityForSaleSign.yCoord, tileEntityForSaleSign.zCoord), player);
        }

        private int x, y, z;

        public PacketOpenGuiZoneSell()
        {
        }

        public PacketOpenGuiZoneSell(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            x = buf.readInt();
            y = buf.readInt();
            z = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
        }

        public static class Handler implements IMessageHandler<PacketOpenGuiZoneSell, IMessage> {

            @SideOnly(Side.CLIENT)
            public IMessage onMessage(PacketOpenGuiZoneSell message, MessageContext ctx)
            {
                Minecraft.getMinecraft().displayGuiScreen(new GuiZoneSell(message.x, message.y, message.z));
                return null;
            }
        }
    }

    public static class PacketUpdateForSaleSign implements IMessage {

        private int x, y, z;
        private long priceField, billingDurationField;
        private boolean forRent, allowBreaking, allowPlacing, allowInteracting;

        public PacketUpdateForSaleSign()
        {
        }

        public PacketUpdateForSaleSign(int x, int y, int z, long priceField, long billingDurationField, boolean forRent, boolean allowBreaking, boolean allowPlacing, boolean allowInteracting)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.priceField = priceField;
            this.billingDurationField = billingDurationField;
            this.forRent = forRent;
            this.allowBreaking = allowBreaking;
            this.allowPlacing = allowPlacing;
            this.allowInteracting = allowInteracting;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            x = buf.readInt();
            y = buf.readInt();
            z = buf.readInt();
            priceField = buf.readLong();
            billingDurationField = buf.readLong();
            forRent = buf.readBoolean();
            allowBreaking = buf.readBoolean();
            allowPlacing = buf.readBoolean();
            allowInteracting = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
            buf.writeLong(priceField);
            buf.writeLong(billingDurationField);
            buf.writeBoolean(forRent);
            buf.writeBoolean(allowBreaking);
            buf.writeBoolean(allowPlacing);
            buf.writeBoolean(allowInteracting);
        }

        public static class Handler implements IMessageHandler<PacketUpdateForSaleSign, IMessage> {

            @SideOnly(Side.SERVER)
            public IMessage onMessage(PacketUpdateForSaleSign message, MessageContext ctx)
            {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                player.closeScreen();
                try {
                    if (player.worldObj.getTileEntity(message.x, message.y, message.z) == null ||
                            !(player.worldObj.getTileEntity(message.x, message.y, message.z) instanceof TileEntityForSaleSign))
                        throw new CustomMessageException("Could not find sign. If this is an error notify an admin.");

                    TileEntityForSaleSign tileEntityForSaleSign = (TileEntityForSaleSign) player.worldObj.getTileEntity(message.x, message.y, message.z);

                    tileEntityForSaleSign.setBillingPeriod(message.billingDurationField);
                    tileEntityForSaleSign.setPrice(message.priceField);
                    tileEntityForSaleSign.setRentable(message.forRent);
                    tileEntityForSaleSign.setAllowBreaking(message.allowBreaking);
                    tileEntityForSaleSign.setAllowPlacing(message.allowPlacing);
                    tileEntityForSaleSign.setAllowInteracting(message.allowInteracting);

                    player.addChatComponentMessage(new ChatComponentText("Zone is now up for sale!"));
                } catch (Exception e) {
                    if (e instanceof CustomMessageException) {
                        player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
                    } else {
                        e.printStackTrace();
                        Minelife.getLogger().log(Level.SEVERE, "", e);
                        player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
                    }

                    BlockForSaleSign.getBlock(true).dropBlockAsItem(player.worldObj, message.x, message.y, message.z, player.worldObj.getBlockMetadata(message.x, message.y, message.z), 0);
                    player.worldObj.setBlockToAir(message.x, message.y, message.z);
                }
                return null;
            }
        }
    }

}
