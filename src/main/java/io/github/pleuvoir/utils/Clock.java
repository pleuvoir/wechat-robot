package io.github.pleuvoir.utils;

/**
 * 计时器
 * @author pleuvoir
 */
public class Clock {
	
	/**
	 * 当前时间戳
	 * @return
	 */
	public static String now() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	/**
	 * 开始时间
	 * @return
	 */
	public static long startTime() {
		return System.currentTimeMillis();
	}
	
	/**
	 * 流逝的时间
	 * @param startTime 开始时间
	 * @return
	 */
	public static String passed(long startTime) {
		return System.currentTimeMillis() - startTime + "ms";
	}

}
