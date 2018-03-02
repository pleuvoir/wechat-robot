package io.github.pleuvoir.utils;

public class SleepUtil {

	/**
	 * 休息一下
	 */
	public static void sleep(Long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
