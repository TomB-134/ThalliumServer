package net.thallium.utils;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.Tuple;

import java.util.HashMap;

public class SpawnReporter {
	public static final HashMap<Integer, HashMap<EnumCreatureType, Tuple<Integer, Integer>>> mobcaps = new HashMap<>();

	static {
		mobcaps.put(-1, new HashMap<>());
		mobcaps.put(0, new HashMap<>());
		mobcaps.put(1, new HashMap<>());
	}
}
