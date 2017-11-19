package com.kuiren.common.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

	public Map<String, ConfigItem> allConfigMap = new HashMap<String, ConfigItem>();

	public ConfigItem getConfigItem(String key) {
		if (allConfigMap.containsKey(key)) {
			return allConfigMap.get(key);
		}
		return null;
	}
	
}
