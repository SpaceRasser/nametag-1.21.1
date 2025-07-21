package com.playertagsmod.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class TagStorage {
    private static final String TAG_KEY = "PlayerTag";

    public static void saveTag(ServerPlayer player, String tag) {
        CompoundTag data = player.getPersistentData();
        data.putString(TAG_KEY, tag);
    }

    public static String loadTag(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        return data.contains(TAG_KEY) ? data.getString(TAG_KEY) : null;
    }

    public static void clearTag(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        data.remove(TAG_KEY);
    }
}
