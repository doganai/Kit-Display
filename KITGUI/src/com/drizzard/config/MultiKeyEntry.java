package com.drizzard.config;

import java.util.Map.Entry;

public class MultiKeyEntry<obj1, obj2> implements Entry<obj1, obj2> {
	private obj1 o1;
	private obj2 o2;
	
	public MultiKeyEntry(obj1 key, obj2 value){
		this.o1 = key;
		this.o2 = value;
	}
	
	@Override
	public obj1 getKey() {
		return this.o1;
	}

	@Override
	public obj2 getValue() {
		return this.o2;
	}

	@Override
	public obj2 setValue(obj2 value) {
		this.o2 = value;
		return value;
	}
}
