package net.openrs.cache.def;

import java.util.HashMap;
import java.util.Map;

public class ConfigIndex {

	public static String get(int key) {
		return indicies498.getOrDefault(key, String.valueOf(key));
	}
	public static int forValue(String val) {
		for (Map.Entry<Integer, String> e : indicies498.entrySet()) {
			if (e.getValue().equals(val)) {
				return e.getKey();
			}
		}
		return -1;
	}

	private final static Map<Integer, String> indicies498 = new HashMap<>();
	static {
		indicies498.put(1, "floor_underlay");
		indicies498.put(3, "identify_kit");
		indicies498.put(4, "floor_overlay");
		indicies498.put(5, "short_cache");
		indicies498.put(14, "varbit_cache");
		indicies498.put(16, "varp_cache");
		indicies498.put(10, "items");
		indicies498.put(9, "npcs");
		indicies498.put(8, "mapped_values");
		indicies498.put(6, "objects");
		indicies498.put(12, "seq");
		indicies498.put(13, "spotanim");

	}

}
