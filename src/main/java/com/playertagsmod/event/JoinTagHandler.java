package com.playertagsmod.event;

import com.playertagsmod.command.TagCommand;
import com.playertagsmod.util.TagStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public class JoinTagHandler {
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        String raw = TagStorage.loadTag(player);
        if (raw != null && !raw.isEmpty()) {
            TagCommand.applyTagToPlayer(player, raw);
        }
    }
}
