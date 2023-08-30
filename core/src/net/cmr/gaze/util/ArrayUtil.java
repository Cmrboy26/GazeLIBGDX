package net.cmr.gaze.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ArrayUtil {
	
	public static <T> boolean contains(T[] array, T object) {
		if(array == null) {
			return false;
		}
		for(T tee : array) {
			if(tee.equals(object)) {
				return true;
			}
		}
		return false;
	}
	
	public static <T> int firstIndexOfClass(ArrayList<?> objects, Class<T> classTest) {
		for(int i = 0; i < objects.size(); i++) {
			if(classTest.isInstance(objects.get(i))) {
				return i;
			}
		}
		return -1;
	}
	
	public static <T> void printTwoDimArray(T[][] array) {
		if(array == null || array.length==0) {
			System.out.println("[]");
			return;
		}
		String output = "";
		for(T[] tx : array) {
			for(T t : tx) {
				if(t == null) {output+="[null]"; continue;}
				output+="["+t.toString()+"]";
			}
			output+="\n";
			//if(t == null) {output+="[null]"; continue;}
			//output+="["+t.toString()+"]";
		}
		System.out.println(output);
	}
	
	public static <T> void printArray(T[] array) {
		if(array == null || array.length==0) {
			System.out.println("[]");
			return;
		}
		String output = "";
		for(T t : array) {
			if(t == null) {output+="[null]"; continue;}
			output+="["+t.toString()+"]";
		}
		System.out.println(output);
	}
	
	public static <T> String toArrayString(T[] array) {
		String output = "";
		for(T t : array) {
			if(t == null) {output+="[null]"; continue;}
			output+="["+t.toString()+"]";
		}
		return output;
	}
	
	/*public static byte[] readAllBytes(InputStream stream) {
		try {
			ReadAllBytesInputStream input = new ReadAllBytesInputStream() {
				@Override
				public int read() throws IOException {
					return stream.read();
				}
			};
			byte[] b = input.readNBytes(Integer.MAX_VALUE);
			input.close();
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}*/

	
}
