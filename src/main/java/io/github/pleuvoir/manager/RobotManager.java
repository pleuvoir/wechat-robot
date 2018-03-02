package io.github.pleuvoir.manager;

import java.io.IOException;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;

/**
 * 机器人管理
 * @author pleuvoir
 */
public interface RobotManager {

	/**
	 * 启动机器人
	 * @throws WriterException 
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	void start() throws Exception;

	/**
	 * 关闭机器人
	 */
	void destroy();

	/**
	 * 当前机器人运行状态
	 */
	boolean robotIsAlive();
}
