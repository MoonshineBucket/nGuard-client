package ru.argentoz.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;

public class ProfilerHook extends Profiler {

    private boolean doScreen = true;
    private int delay = 0, shouldDelay = 8000;

    public void startSection(String name) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if(name.equals("tick") && minecraft.thePlayer != null && doScreen && !minecraft.isSingleplayer()) {
            if(++delay == shouldDelay) {
                ScreenMaster.saveScreenshot(minecraft.thePlayer.getEntityName(),
                        minecraft.displayWidth, minecraft.displayHeight);
                doScreen = false;
            }
        }

        super.startSection(name);
    }

}