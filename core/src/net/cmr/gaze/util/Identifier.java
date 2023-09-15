package net.cmr.gaze.util;

import java.util.UUID;

public class Identifier {

	String id;
	
	public Identifier(String id) {
		this.id = id.replaceAll(" ", "").toLowerCase();
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public String appendIDAfter(Identifier id) {
		return this.id+"."+id.id;
	}
	
	public static String combineIDs(Identifier... identifiers) {
		if(identifiers == null || identifiers.length < 1) {
			throw new IndexOutOfBoundsException(identifiers.length);
		}
		String output = identifiers[0].id;
		for(int i = 1; i < identifiers.length; i++) {
			if(i+1 != identifiers.length) {
				output+=".";
			}
			output+=identifiers[i].id;
		}
		return output;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Identifier)) {
			return false;
		}
		return id.equals(((Identifier) obj).id);
	}
	
}
