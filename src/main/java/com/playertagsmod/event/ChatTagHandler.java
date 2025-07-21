package com.playertagsmod.event;

import com.playertagsmod.util.TagStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

public class ChatTagHandler {

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String tag = TagStorage.loadTag(player);

        if (tag != null && !tag.isEmpty()) {
            String rawMessage = event.getRawText();
            // Добавляем только тег перед стандартным именем игрока
            Component finalMessage = Component.literal("")
                    .append(Component.literal(tag + " ")) // Например: "[Admin] "
                    .append(Component.literal(":" + rawMessage)); // Само сообщение


        }
    }
}
