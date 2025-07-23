package com.playertagsmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.playertagsmod.util.TagStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagCommand {
    private static final Pattern PAT_GRADIENT = Pattern.compile(
            "<gradient:#([0-9A-Fa-f]{6}):#([0-9A-Fa-f]{6})>(.*?)</gradient>",
            Pattern.DOTALL
    );
    private static final Pattern PAT_BOLD = Pattern.compile(
            "<bold>(.*?)</bold>",
            Pattern.DOTALL
    );
    private static final Pattern PAT_COLOR = Pattern.compile(
            "<(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|dark_gray|blue|green|aqua|red|light_purple|yellow|white)>(.*?)</\\1>",
            Pattern.DOTALL
    );

    private static final Map<String, Integer> NAMED_COLORS = Map.ofEntries(
            Map.entry("black",        0x000000),
            Map.entry("dark_blue",    0x0000AA),
            Map.entry("dark_green",   0x00AA00),
            Map.entry("dark_aqua",    0x00AAAA),
            Map.entry("dark_red",     0xAA0000),
            Map.entry("dark_purple",  0xAA00AA),
            Map.entry("gold",         0xFFAA00),
            Map.entry("gray",         0xAAAAAA),
            Map.entry("dark_gray",    0x555555),
            Map.entry("blue",         0x5555FF),
            Map.entry("green",        0x55FF55),
            Map.entry("aqua",         0x55FFFF),
            Map.entry("red",          0xFF5555),
            Map.entry("light_purple", 0xFF55FF),
            Map.entry("yellow",       0xFFFF55),
            Map.entry("white",        0xFFFFFF)
    );

    public static void register(CommandDispatcher<CommandSourceStack> disp) {
        disp.register(
                Commands.literal("tag")
                        .requires(src -> src.hasPermission(0))
                        .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String raw = StringArgumentType.getString(ctx, "text");

                                    MutableComponent comp = parse(raw);
                                    apply(player, comp);
                                    TagStorage.saveTag(player, raw);

                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("§aTag set: ").append(comp),
                                            false
                                    );
                                    return 1;
                                }))
                        .then(Commands.literal("reset")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    remove(player);
                                    TagStorage.clearTag(player);
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("§eTag cleared."),
                                            false
                                    );
                                    return 1;
                                }))
        );
    }

    public static void apply(ServerPlayer player, MutableComponent prefix) {
        Scoreboard sb = player.getScoreboard();
        String teamName = "tag_" + player.getStringUUID().substring(0, 8);
        PlayerTeam team = sb.getPlayerTeam(teamName);
        if (team == null) team = sb.addPlayerTeam(teamName);
        team.setPlayerPrefix(prefix.append(Component.literal(" ")));
        team.setDisplayName(Component.empty());
        sb.addPlayerToTeam(player.getScoreboardName(), team);
    }

    private static void remove(ServerPlayer player) {
        player.getScoreboard().removePlayerFromTeam(player.getScoreboardName());
    }

    public static MutableComponent parse(String input) {
        return parseRec(input);
    }

    private static MutableComponent parseRec(String input) {
        MutableComponent result = Component.literal("");
        int idx = 0;
        while (idx < input.length()) {
            Matcher mg = PAT_GRADIENT.matcher(input);
            Matcher mb = PAT_BOLD.matcher(input);
            Matcher mc = PAT_COLOR.matcher(input);
            boolean foundG = mg.find(idx);
            boolean foundB = mb.find(idx);
            boolean foundC = mc.find(idx);
            if (!foundG && !foundB && !foundC) {
                result.append(Component.literal(input.substring(idx)));
                break;
            }
            int posG = foundG ? mg.start() : Integer.MAX_VALUE;
            int posB = foundB ? mb.start() : Integer.MAX_VALUE;
            int posC = foundC ? mc.start() : Integer.MAX_VALUE;

            if (posG < posB && posG < posC) {
                // gradient
                if (mg.start() > idx)
                    result.append(Component.literal(input.substring(idx, mg.start())));
                int c1 = Integer.parseInt(mg.group(1), 16);
                int c2 = Integer.parseInt(mg.group(2), 16);
                String inner = mg.group(3);
                result.append(applyGradient(inner, c1, c2));
                idx = mg.end();
            } else if (posB < posC) {
                // bold
                if (mb.start() > idx)
                    result.append(Component.literal(input.substring(idx, mb.start())));
                String inner = mb.group(1);
                result.append(parseRec(inner).withStyle(s -> s.withBold(true)));
                idx = mb.end();
            } else {
                // named color
                if (mc.start() > idx)
                    result.append(Component.literal(input.substring(idx, mc.start())));
                String tag = mc.group(1);
                String inner = mc.group(2);
                result.append(applyColor(inner, NAMED_COLORS.get(tag)));
                idx = mc.end();
            }
        }
        return result;
    }

    private static MutableComponent applyGradient(String text, int c1, int c2) {
        MutableComponent comp = Component.literal("");
        int len = text.length();
        for (int i = 0; i < len; i++) {
            double t = len == 1 ? 0 : (double)i / (len - 1);
            int r = (int)(((c1 >> 16) & 0xFF) * (1 - t) + ((c2 >> 16) & 0xFF) * t);
            int g = (int)(((c1 >> 8 ) & 0xFF) * (1 - t) + ((c2 >> 8 ) & 0xFF) * t);
            int b = (int)(((c1      ) & 0xFF) * (1 - t) + ((c2      ) & 0xFF) * t);
            comp.append(
                    Component.literal(String.valueOf(text.charAt(i)))
                            .withStyle(s -> s.withColor(TextColor.fromRgb((r << 16) | (g << 8) | b)))
            );
        }
        return comp;
    }

    private static MutableComponent applyColor(String text, int color) {
        return Component.literal(text)
                .withStyle(s -> s.withColor(TextColor.fromRgb(color)));

    }
}