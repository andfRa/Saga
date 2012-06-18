package org.saga.metadata;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.saga.Saga;

public class SpawnerTag implements MetadataValue{

	/**
	 * Key for spawner tag.
	 */
	public static String METADATA_KEY = "spawner";

	/**
	 * Value for spawner tag.
	 */
	public static SpawnerTag METADATA_VALUE = new SpawnerTag();
	
	
	@Override
	public boolean asBoolean() {
		return true;
	}

	@Override
	public byte asByte() {
		return 0;
	}

	@Override
	public double asDouble() {
		return 0;
	}

	@Override
	public float asFloat() {
		return 0;
	}

	@Override
	public int asInt() {
		return 0;
	}

	@Override
	public long asLong() {
		return 0;
	}

	@Override
	public short asShort() {
		return 0;
	}

	@Override
	public String asString() {
		return "spawner";
	}

	@Override
	public Plugin getOwningPlugin() {
		return Saga.plugin();
	}

	@Override
	public void invalidate() {
	}

	@Override
	public Object value() {
		return asString();
	}
	

}
