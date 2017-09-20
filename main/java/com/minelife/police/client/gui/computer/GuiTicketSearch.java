package com.minelife.police.client.gui.computer;

import com.minelife.Minelife;
import com.minelife.police.TicketSearchResult;
import com.minelife.police.network.PacketRequestTicketSearch;
import com.minelife.util.client.GuiLoadingAnimation;
import com.minelife.util.client.GuiScrollableContent;
import org.lwjgl.input.Mouse;

import java.util.List;

public class GuiTicketSearch extends GuiComputer {

    private List<TicketSearchResult> results;
    private boolean fetchingResults;
    private GuiLoadingAnimation loadingAnimation;
    private GuiResultList guiResultList;

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
        guiResultList = new GuiResultList((this.width - resultListW) / 2, 38 + (this.height - resultListH) /2 , resultListW, resultListH);
        this.buttonList.add(new ComputerButton(0, this.width - 90, guiResultList.yPosition - 30, 80, 20, "Search", 2));
        this.buttonList.add(new ComputerButton(1, this.width - 90, guiResultList.yPosition - 60, 80, 20, "Submit", 2));
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode)
    {
        super.keyTyped(keyChar, keyCode);
        if (fetchingResults) return;
        guiResultList.keyTyped(keyChar, keyCode);
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
    }

    @Override
    protected void mouseClicked(int x, int y, int btn)
    {
        if (fetchingResults) return;
        super.mouseClicked(x, y, btn);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if(fetchingResults) return;
    }

    class GuiResultList extends GuiScrollableContent {

        public GuiResultList(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 0;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {

        }

        @Override
        public int getSize()
        {
            return results.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {

        }
    }

}