package com.playertagsmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.playertagsmod.util.TagStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TagCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tag")
                .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String tagText = StringArgumentType.getString(context, "text").replace("&", "§");

                            applyTagToPlayer(player, tagText);
                            TagStorage.saveTag(player, tagText);

                            context.getSource().sendSuccess(() -> Component.literal("§aТег установлен: " + tagText), false);
                            return 1;
                        }))
                .then(Commands.literal("reset")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            removeTagFromPlayer(player);
                            TagStorage.clearTag(player);

                            context.getSource().sendSuccess(() -> Component.literal("§eТег сброшен."), false);
                            return 1;
                        }))
        );
    }

    public static void applyTagToPlayer(ServerPlayer player, String tagText) {
        Scoreboard scoreboard = player.getScoreboard();
        String teamName = "tag_" + player.getStringUUID().substring(0, 8);

        PlayerTeam team = scoreboard.getPlayerTeam(teamName);
        if (team == null) {
            team = scoreboard.addPlayerTeam(teamName);
        }

        team.setPlayerPrefix(Component.literal(tagText + " "));
        team.setDisplayName(Component.empty());
        scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
    }

    public static void removeTagFromPlayer(ServerPlayer player) {
        Scoreboard scoreboard = player.getScoreboard();
        scoreboard.removePlayerFromTeam(player.getScoreboardName());
    }
}
