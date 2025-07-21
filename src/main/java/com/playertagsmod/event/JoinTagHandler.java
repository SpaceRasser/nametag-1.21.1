package com.playertagsmod.event;

import com.playertagsmod.command.TagCommand;
import com.playertagsmod.util.TagStorage;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.server.level.ServerPlayer;

public class JoinTagHandler {

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        String savedTag = TagStorage.loadTag(player);
        if (savedTag != null && !savedTag.isEmpty()) {
            TagCommand.applyTagToPlayer(player, savedTag);
        }
    }
}
