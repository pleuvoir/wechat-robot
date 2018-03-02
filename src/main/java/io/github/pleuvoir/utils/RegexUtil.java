package io.github.pleuvoir.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	public static String match(String reg, String text) {
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
}
