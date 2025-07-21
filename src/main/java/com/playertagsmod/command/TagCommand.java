package com.playertagsmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.playertagsmod.util.TagStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagCommand {
    // Шаблон градиента: <gradient:#RRGGBB:#RRGGBB>Текст</gradient>
    private static final Pattern GRADIENT = Pattern.compile(
            "<gradient:#([0-9A-Fa-f]{6}):#([0-9A-Fa-f]{6})>(.+?)</gradient>"
    );
    // Шаблон жирного: <bold>Текст</bold>
    private static final Pattern BOLD = Pattern.compile(
            "<bold>(.+?)</bold>"
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("tag")
                        .requires(src -> src.hasPermission(0)) // без OP
                        .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String rawTag = StringArgumentType.getString(ctx, "text");

                                    applyTagToPlayer(player, rawTag);
                                    TagStorage.saveTag(player, rawTag);

                                    // превью в чат
                                    MutableComponent preview = parseTag(rawTag);
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("§aТег установлен: ").append(preview),
                                            false
                                    );
                                    return 1;
                                }))
                        .then(Commands.literal("reset")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    removeTagFromPlayer(player);
                                    TagStorage.clearTag(player);

                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("§eТег сброшен."),
                                            false
                                    );
                                    return 1;
                                }))
        );
    }

    public static void applyTagToPlayer(ServerPlayer player, String rawTag) {
        Scoreboard scoreboard = player.getScoreboard();
        String teamName = "tag_" + player.getStringUUID().substring(0, 8);

        PlayerTeam team = scoreboard.getPlayerTeam(teamName);
        if (team == null) {
            team = scoreboard.addPlayerTeam(teamName);
        }

        // парсим и добавляем пробел
        MutableComponent prefix = parseTag(rawTag).append(Component.literal(" "));
        team.setPlayerPrefix(prefix);
        team.setDisplayName(Component.empty());
        scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
    }

    public static void removeTagFromPlayer(ServerPlayer player) {
        player.getScoreboard().removePlayerFromTeam(player.getScoreboardName());
    }

    /**
     * Ручной парсер: градиент → символ за символом, или жирный,
     * иначе plain literal.
     */
    public static MutableComponent parseTag(String raw) {
        Matcher mg = GRADIENT.matcher(raw);
        if (mg.matches()) {
            int c1 = Integer.parseInt(mg.group(1), 16);
            int c2 = Integer.parseInt(mg.group(2), 16);
            String txt = mg.group(3);
            MutableComponent out = Component.literal("");
            int len = txt.length();
            for (int i = 0; i < len; i++) {
                double t = len == 1 ? 0.0 : (double)i / (len - 1);
                int r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
                int r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
                int r = (int)Math.round(r1 + (r2 - r1) * t);
                int g = (int)Math.round(g1 + (g2 - g1) * t);
                int b = (int)Math.round(b1 + (b2 - b1) * t);
                int rgb = (r << 16) | (g << 8) | b;
                out.append(
                        Component.literal(String.valueOf(txt.charAt(i)))
                                .withStyle(style -> style.withColor(
                                        net.minecraft.network.chat.TextColor.fromRgb(rgb)
                                ))
                );
            }
            return out;
        }

        Matcher mb = BOLD.matcher(raw);
        if (mb.matches()) {
            return Component.literal(mb.group(1))
                    .withStyle(style -> style.withBold(true));
        }

        // без тегов
        return Component.literal(raw);
    }
}
