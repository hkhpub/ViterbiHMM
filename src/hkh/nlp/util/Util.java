package hkh.nlp.util;

import java.util.ArrayList;

public class Util {

	public static boolean equals(final String s1, final String s2) {
		return s1 != null && s2 != null && s1.hashCode() == s2.hashCode() && s1.equals(s2);
	}
	
	public static String[] removeEmpty(String[] arr) {
		ArrayList<String> list = new ArrayList<String>();
		for (String str : arr) {
			if (str.indexOf('/') < 0)
				continue;
			list.add(str);
		}
		return list.toArray(new String[]{});
	}
}
