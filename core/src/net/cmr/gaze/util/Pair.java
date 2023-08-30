package net.cmr.gaze.util;

import java.io.Serializable;
import java.util.Objects;

public class Pair<T1, T2> implements Serializable {

	T1 one;
	T2 two;
	
	public Pair(T1 one, T2 two) {
		this.one = one;
		this.two = two;
	}
	
	public T1 getFirst() {return one;}
	public T2 getSecond() {return two;}
	
	@Override
	public int hashCode() {
		return Objects.hash(one.hashCode(), two.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Pair) {
			Pair<?, ?> pair = (Pair<?, ?>) obj;
			return Objects.equals(pair.getFirst(), getFirst()) && Objects.equals(pair.getSecond(), pair.getSecond());
		}
		return false;
	}
	
}
