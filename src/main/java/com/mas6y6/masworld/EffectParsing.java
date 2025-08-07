package com.mas6y6.masworld;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class EffectParsing {
    private static final String DELIMITER = ",";

    public static String serialize(List<String> effectIds) {
        if (effectIds == null || effectIds.isEmpty()) return "";
        return String.join(DELIMITER, effectIds);
    }

    public static List<String> deserialize(String stored) {
        if (stored == null || stored.isBlank()) return Collections.emptyList();
        return Arrays.asList(stored.split(DELIMITER));
    }

    public static PersistentDataType<?, String> getType() {
        return PersistentDataType.STRING;
    }
}
