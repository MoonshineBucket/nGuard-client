package ru.argentoz;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import ru.argentoz.handlers.ProfilerHook;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Mod(modid = "MinecraftHelper", name = "Helper", version = "0.2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {"nGuard"}, packetHandler = CPacket.class)
public class Main {

    @Mod.EventHandler
    public void invokeInit(FMLInitializationEvent e) {
        if(e.getSide() == Side.CLIENT) {
            for(Field field : Minecraft.class.getDeclaredFields()) {
                if(field.getType().equals(Profiler.class)) {
                    try {
                        if(!field.isAccessible())
                            field.setAccessible(true);
                        if(Modifier.isFinal(field.getModifiers())) {
                            Field modifiers = field.getClass().getDeclaredField("modifiers");
                            modifiers.setAccessible(true);

                            modifiers.setInt(field, field.getModifiers() & 0xFFFFFFEF);
                        }

                        field.set(Minecraft.getMinecraft(), new ProfilerHook());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }
    }

}