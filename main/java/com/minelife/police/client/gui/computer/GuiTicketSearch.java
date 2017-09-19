package com.minelife.police.client.gui.computer;

import com.minelife.police.TicketSearchResult;
import com.minelife.util.client.GuiLoadingAnimation;
import com.minelife.util.client.GuiScrollableContent;

import java.util.List;

public class GuiTicketSearch extends GuiComputer {

    private List<TicketSearchResult> results;
    private boolean fetchingResults;
    private GuiLoadingAnimation loadingAnimation;

    public GuiTicketSearch()
    {
        fetchingResults = true;
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
            loadingAnimation = new GuiLoadingAnimation(this.width / 2, this.height / 2, this.width / 4, this.height / 4);
            return;
        }

        this.buttonList.add(new ComputerButton(0, this.width - 60, this.sectionHeight + 5, 50, 20, "Search", 2));
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode)
    {
        super.keyTyped(keyChar, keyCode);
        if (fetchingResults) return;
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