package io.github.pleuvoir.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.pleuvoir.smartresponse.SmartResponseService;
import io.github.pleuvoir.wrapper.ApplicationContextWrap;

/**
 * 智能机器人路由工厂
 * @author pleuvoir
 */
@Service
public class RobotRouteFactory {

	@Value(value = "${smartresponse}")
	String smartresponse;

	public SmartResponseService route() {
		return ApplicationContextWrap.getBean(smartresponse);
	}

}
