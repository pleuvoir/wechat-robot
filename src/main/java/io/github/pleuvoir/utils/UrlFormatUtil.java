package io.github.pleuvoir.utils;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlFormatUtil {
	
	private static Logger logger =  LoggerFactory.getLogger(UrlFormatUtil.class); 

	public static String format(String url, Object... args) {
		logger.debug("替换地址参数，替换前地址：{}", url);
		String replaced = MessageFormat.format(url, args);
		logger.debug("替换地址参数，替换后地址：{}", replaced);
		return replaced;
	}

}
