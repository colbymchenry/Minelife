package com.minelife.realestate.client;

import com.google.common.collect.Maps;
import com.minelife.realestate.Member;
import com.minelife.realestate.Zone;
import com.minelife.util.client.GuiScrollList;
import com.minelife.util.client.GuiTickBox;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

import java.util.Map;

public class GuiZoneMembers extends AbstractZoneGui {

    private Content content;
    private Zone zone;

    // TODO: Need to add AddMember section at the bottom of the list
    public GuiZoneMembers(Zone zone)
    {
        super(200, 200);
        this.zone = zone;
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

        private Map<Member, GuiTickBox> placeMap = Maps.newHashMap();
        private Map<Member, GuiTickBox> breakMap = Maps.newHashMap();
        private Map<Member, GuiTickBox> interactMap = Maps.newHashMap();

        public Content(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);

            for(Member member : zone.getMembers()) {
                placeMap.put(member, new GuiTickBox(mc, 100, 18, member.isAllowPlacing()));
                breakMap.put(member, new GuiTickBox(mc, 100, 28, member.isAllowPlacing()));
                interactMap.put(member, new GuiTickBox(mc, 100, 38, member.isAllowPlacing()));
            }
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 50;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            Member member = (Member) zone.getMembers().toArray()[index];
            int y = 5;
            mc.fontRenderer.drawString(member.getName(), 10, y+=12, 0xFFFFFF);
            mc.fontRenderer.drawString("Place", 20, 18, 0xFFFFFF);
            mc.fontRenderer.drawString("Break", 20, 28, 0xFFFFFF);
            mc.fontRenderer.drawString("Interact", 20, 38, 0xFFFFFF);
            placeMap.get(member).draw();
            breakMap.get(member).draw();
            interactMap.get(member).draw();
        }

        @Override
        public int getSize()
        {
            return zone.getMembers().size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            ((GuiTickBox)placeMap.values().toArray()[index]).mouseClicked(mouseX, mouseY);
            ((GuiTickBox)breakMap.values().toArray()[index]).mouseClicked(mouseX, mouseY);
            ((GuiTickBox)interactMap.values().toArray()[index]).mouseClicked(mouseX, mouseY);
        }

        @Override
        public void drawBackground()
        {
        }

    }

}
