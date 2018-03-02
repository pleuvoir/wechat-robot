package io.github.pleuvoir.exception;

/**
 * 机器人异常
 * @author pleuvoir
 */
public class RobotException extends RuntimeException {

	private static final long serialVersionUID = -443304902255027299L;

	public RobotException() {
		super();
	}

	public RobotException(String message) {
		super(message);
	}

	public RobotException(Throwable arg0) {
		super(arg0);
	}
	
}
