package com.minelife.jobs.job.bountyhunter;

import com.minelife.Minelife;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiBountyCard extends GuiScreen {

    private static ResourceLocation texBG = new ResourceLocation(Minelife.MOD_ID, "textures/gui/bounty_card_gui.png");
    private ItemStack bountyCardStack;
    private String playerName;

    public GuiBountyCard(ItemStack bountyCardStack) {
        this.bountyCardStack = bountyCardStack;
        playerName = NameFetcher.asyncFetchClient(ItemBountyCard.getTarget(bountyCardStack));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
