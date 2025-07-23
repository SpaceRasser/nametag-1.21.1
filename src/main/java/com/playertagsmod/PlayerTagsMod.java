package com.playertagsmod;

import com.playertagsmod.command.TagCommand;
import com.playertagsmod.event.JoinTagHandler;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod(PlayerTagsMod.MODID)
public class PlayerTagsMod {
    public static final String MODID = "playertagsmod";

    public PlayerTagsMod() {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new JoinTagHandler());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        TagCommand.register(event.getDispatcher());
        System.out.println("[PlayerTagsMod] /tag registered");
    }
}
