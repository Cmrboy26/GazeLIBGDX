package net.cmr.gaze.util;

public class RomanNumeral {
	public static String toRoman(int num) {
        int[] values = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
        String[] symbols = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

        StringBuilder romanNumeral = new StringBuilder();
        int index = 0;

        while (num > 0) {
            while (num >= values[index]) {
                num -= values[index];
                romanNumeral.append(symbols[index]);
            }
            index++;
        }

        return romanNumeral.toString();
    }
}
