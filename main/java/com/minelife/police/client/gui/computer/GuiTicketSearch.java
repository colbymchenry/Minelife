package com.minelife.police.client.gui.computer;

import com.minelife.Minelife;
import com.minelife.police.TicketSearchResult;
import com.minelife.police.client.gui.ticket.GuiTicket;
import com.minelife.police.network.PacketRequestTicketSearch;
import com.minelife.util.client.GuiLoadingAnimation;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

import java.util.List;

public class GuiTicketSearch extends GuiComputer {

    private List<TicketSearchResult> results;
    private boolean fetchingResults;
    private GuiLoadingAnimation loadingAnimation;
    private GuiResultList guiResultList;
    private GuiTextField ticketField, officerField, playerField;

    public GuiTicketSearch()
    {
        fetchingResults = true;
        Minelife.NETWORK.sendToServer(new PacketRequestTicketSearch(null, null, 0));
    }

    public GuiTicketSearch(List<TicketSearchResult> results)
    {
        this.results = results;
        fetchingResults = false;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        if(fetchingResults) {
            loadingAnimation = new GuiLoadingAnimation((this.width - 72) / 2, 10 + (this.height - 72) / 2, 72, 72);
            return;
        }

        int resultListW = this.width - 20, resultListH = this.height - 100;
        guiResultList = new GuiResultList(mc,(this.width - resultListW) / 2, 38 + (this.height - resultListH) /2 , resultListW, resultListH);
        this.buttonList.add(new ComputerButton(0, this.width - 90, guiResultList.yPosition - 30, 80, 20, "Search", 2));
        this.buttonList.add(new ComputerButton(1, this.width - 90, guiResultList.yPosition - 60, 80, 20, "Submit", 2));
        ticketField = new GuiTextField(fontRendererObj, 10, this.sectionHeight, 50, 20);
        officerField = new GuiTextField(fontRendererObj,10 + 50, this.sectionHeight, 50, 20);
        playerField = new GuiTextField(fontRendererObj,10 + 50 + 50, this.sectionHeight, 50, 20);
    }

    @Override
    protected void actionPerformed(GuiButton btn)
    {
        // search
        if(btn.id == 0) {

        }
        // submit
        else if (btn.id == 1) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiSubmitTicket());
        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode)
    {
        super.keyTyped(keyChar, keyCode);
        if (fetchingResults) return;
        guiResultList.keyTyped(keyChar, keyCode);
        ticketField.keyTyped(keyChar, keyCode);
        officerField.keyTyped(keyChar, keyCode);
        playerField.keyTyped(keyChar, keyCode);
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f)
    {
        drawBackground();
        if(fetchingResults) {
            loadingAnimation.drawLoadingAnimation();
            return;
        }

        super.drawScreen(mouse_x, mouse_y, f);
        guiResultList.draw(mouse_x, mouse_y, Mouse.getDWheel());
        ticketField.drawTextBox();
        officerField.drawTextBox();
        playerField.drawTextBox();
    }

    @Override
    protected void mouseClicked(int x, int y, int btn)
    {
        if (fetchingResults) return;
        ticketField.mouseClicked(x, y);
        officerField.mouseClicked(x, y);
        playerField.mouseClicked(x, y);
        super.mouseClicked(x, y, btn);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if(fetchingResults) return;
        ticketField.update();
        officerField.update();
        playerField.update();
    }

    class GuiResultList extends GuiScrollableContent {

        GuiResultList(Minecraft mc, int xPosition, int yPosition, int width, int height)
        {
            super(mc, xPosition, yPosition, width, height);
        }

        @Override
        public int getObjectHeight(int index)
        {
            return getLines(index).length * fontRendererObj.FONT_HEIGHT + 2;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            String[] lines = getLines(index);
            fontRendererObj.drawString(lines[0], 2, 2, 0xFFFFFF);
            fontRendererObj.drawString(lines[1], 2, 2 + fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
            fontRendererObj.drawString(lines[2], 2, 2 + (fontRendererObj.FONT_HEIGHT * 2), 0xFFFFFF);
        }

        @Override
        public int getSize()
        {
            return results.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            // TODO: Can't view inventory of ticket when coming from here.
            if(doubleClick) mc.displayGuiScreen(new GuiTicket(results.get(index).ticketStack));
        }

        String[] getLines(int index) {
            TicketSearchResult result = results.get(index);
            return new String[]{"Ticket ID: #" + result.ticketID, "Officer: " + result.officerName, "Player: " + result.playerName};
        }
    }

}