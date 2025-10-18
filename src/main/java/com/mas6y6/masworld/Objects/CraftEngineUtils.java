package com.mas6y6.masworld.Objects;

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public class CraftEngineUtils {
    public static Key getCustomBlockKey(ImmutableBlockState blockState) {
        return blockState.owner().value().id();
    }

    public static @Nullable ImmutableBlockState getCustomBlockState(Block block) {
        return CraftEngineBlocks.getCustomBlockState(block.getBlockData());
    }

    public static Key generateKey(String namespace, String value) {
        Key key = new Key(namespace, value);
        return key;
    }
}
