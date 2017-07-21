package com.minelife.realestate.client;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.sign.TileEntityForSaleSign;
import com.minelife.util.NumberConversions;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.logging.Level;

public class GuiZonePurchase extends AbstractZoneGui {

    private TileEntityForSaleSign forSaleSign;

    private CustomZoneBtn purchaseBtn;

    public GuiZonePurchase(TileEntityForSaleSign forSaleSign)
    {
        super(200, 200);
        this.forSaleSign = forSaleSign;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        super.drawScreen(mouseX, mouseY, f);
        this.drawBackground();
        String price = "Price: $" + NumberConversions.formatter.format(forSaleSign.getPrice());
        String billingDuration = "Billed every " + forSaleSign.getBillingPeriod() + " days.";

        fontRendererObj.drawString(price, calcX(fontRendererObj.getStringWidth(price)), yPosition + 20, 0xFFFFFF);
        fontRendererObj.drawString(billingDuration, calcX(fontRendererObj.getStringWidth(billingDuration)), yPosition + 40, 0xFFFFFF);

        fontRendererObj.drawString("Allow Breaking", xPosition + 5, yPosition + 60, 0xFFFFFF);
        fontRendererObj.drawString("Allow Placing", xPosition + 5, yPosition + 80, 0xFFFFFF);
        fontRendererObj.drawString("Allow Interacting", xPosition + 5, yPosition + 100, 0xFFFFFF);
        fontRendererObj.drawString(forSaleSign.isAllowBreaking() ? "Yes" : "No", xPosition + bgWidth - 15, yPosition + 60, 0xFFFFFF);
        fontRendererObj.drawString(forSaleSign.isAllowPlacing() ? "Yes" : "No", xPosition + bgWidth - 15, yPosition + 80, 0xFFFFFF);
        fontRendererObj.drawString(forSaleSign.isAllowInteracting() ? "Yes" : "No", xPosition + bgWidth - 15, yPosition + 100, 0xFFFFFF);

        purchaseBtn.drawButton(mc, mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int btn)
    {
        super.mouseClicked(mouseX, mouseY, btn);
        if (purchaseBtn.mousePressed(mc, mouseX, mouseY)) {
            // TODO
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        purchaseBtn = new CustomZoneBtn(0, calcX(75), this.yPosition + bgHeight - 30, 75, 20, "Purchase");
        purchaseBtn.enabled = ModEconomy.BALANCE_WALLET_CLIENT >= forSaleSign.getPrice();
    }

    public static class PacketPurchaseZone implements IMessage {

        private int signX, signY, signZ;

        public PacketPurchaseZone()
        {
        }

        public PacketPurchaseZone(int signX, int signY, int signZ)
        {
            this.signX = signX;
            this.signY = signY;
            this.signZ = signZ;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            this.signX = buf.readInt();
            this.signY = buf.readInt();
            this.signZ = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(this.signX);
            buf.writeInt(this.signY);
            buf.writeInt(this.signZ);
        }

        public static class Handler implements IMessageHandler<PacketPurchaseZone, IMessage> {

            @SideOnly(Side.SERVER)
            public IMessage onMessage(PacketPurchaseZone message, MessageContext ctx)
            {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                player.closeScreen();
                try {
                    if(player.worldObj.getTileEntity(message.signX, message.signY, message.signZ) == null ||
                            !(player.worldObj.getTileEntity(message.signX, message.signY, message.signZ)instanceof TileEntityForSaleSign))
                        throw new CustomMessageException("For sale sign not found.");

                    TileEntityForSaleSign forSaleSign = (TileEntityForSaleSign) player.worldObj.getTileEntity(message.signX, message.signY, message.signZ);

                    if(ModEconomy.getBalance(player.getUniqueID(), true) < forSaleSign.getPrice()) throw new CustomMessageException("Insufficient funds.");

                    if(forSaleSign.isRentable()) {
                        forSaleSign.getMembers().clear();
                        forSaleSign.setRenter(player.getUniqueID());
                    } else {
                        // TODO: Purchasing
                    }

                    player.addChatComponentMessage(new ChatComponentText("Zone purchased!"));
                } catch (Exception e) {
                    if (e instanceof CustomMessageException) {
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

    public static class PacketOpenGuiZonePurchase implements IMessage {

        @SideOnly(Side.SERVER)
        public static void openFromServer(TileEntityForSaleSign tileEntityForSaleSign, EntityPlayerMP player)
        {
            Minelife.NETWORK.sendTo(new PacketOpenGuiZonePurchase(tileEntityForSaleSign.xCoord, tileEntityForSaleSign.yCoord, tileEntityForSaleSign.zCoord), player);
        }

        private int x, y, z;

        public PacketOpenGuiZonePurchase()
        {
        }

        public PacketOpenGuiZonePurchase(int x, int y, int z)
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

        public static class Handler implements IMessageHandler<PacketOpenGuiZonePurchase, IMessage> {

            @SideOnly(Side.CLIENT)
            public IMessage onMessage(PacketOpenGuiZonePurchase message, MessageContext ctx)
            {
                if(Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z) == null) return null;
                if(!(Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z) instanceof TileEntityForSaleSign)) return null;
                TileEntityForSaleSign forSaleSign = (TileEntityForSaleSign) Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z);
                Minecraft.getMinecraft().displayGuiScreen(new GuiZonePurchase(forSaleSign));
                return null;
            }
        }
    }
}
