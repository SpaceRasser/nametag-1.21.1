package com.playertagsmod.event;

import com.playertagsmod.command.TagCommand;
import com.playertagsmod.util.TagStorage;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class JoinTagHandler {
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        String raw = TagStorage.loadTag(player);
        if (raw == null || raw.isEmpty()) return;

        MutableComponent prefix = TagCommand.parse(raw);

        TagCommand.apply(player, prefix);
    }
}
