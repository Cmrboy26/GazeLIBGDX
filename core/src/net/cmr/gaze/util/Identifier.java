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

	@Override
	public String toString() {
		return "ID:\""+id+"\"";
	}
	
	public String getID() {
		return id;
	}

	public String appendIDAfter(Identifier id) {
		return this.id+"."+id.id;
	}

	public Identifier append(Identifier id) {
		return new Identifier(this.id+"."+id.id);
	}

	public Identifier[] splitID() {
		String[] split = id.split("\\.");
		Identifier[] identifiers = new Identifier[split.length];
		for(int i = 0; i < split.length; i++) {
			identifiers[i] = new Identifier(split[i]);
		}
		return identifiers;
	}
	
	public static String combine(Identifier... identifiers) {
		if(identifiers == null || identifiers.length < 1) {
			throw new IndexOutOfBoundsException();
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
	
	public static Identifier combineIDs(Identifier... identifiers) {
		if(identifiers == null || identifiers.length < 1) {
			throw new IndexOutOfBoundsException();
		}
		String output = identifiers[0].id;
		for(int i = 1; i < identifiers.length; i++) {
			if(i+1 != identifiers.length) {
				output+=".";
			}
			output+=identifiers[i].id;
		}
		return new Identifier(output);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Identifier)) {
			return false;
		}
		return id.equals(((Identifier) obj).id);
	}
	
}
