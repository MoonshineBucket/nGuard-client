package ru.argentoz;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import ru.argentoz.handlers.ScreenMaster;

public class CPacket implements IPacketHandler {

    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if(packet.channel.equals("nGuard")) {
            try {
                String[] data = new String(packet.data).split(";");
                if(data[1].equals("screen")) {
                    Minecraft minecraft = Minecraft.getMinecraft();
                    ScreenMaster.saveScreenshot(data[0], minecraft.displayWidth, minecraft.displayHeight);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}