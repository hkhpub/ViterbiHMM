package hkh.nlp.util;

public class Util {

	public static boolean equals(final String s1, final String s2) {
		return s1 != null && s2 != null && s1.hashCode() == s2.hashCode() && s1.equals(s2);
	}

	public static String getPosSequence(String posPair) {
		String[] pairs = posPair.split("[+]");
		StringBuffer sb = new StringBuffer();
		for (String pair : pairs) {
			int index = pair.indexOf('/');
			if (index < 0) {
				continue;
			}
			sb.append(pair.substring(index+1)).append('+');
		}
		return sb.toString();
	}
}
