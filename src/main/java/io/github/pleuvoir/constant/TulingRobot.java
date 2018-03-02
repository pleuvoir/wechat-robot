package io.github.pleuvoir.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 图灵机器人配置
 * @author pleuvoir
 */
@Data
@Configuration(value = "tulingRobotConfig")
@ConfigurationProperties(prefix = "tulingRobot")
public class TulingRobot {

	private String openApi;

	private String apiKey;

}
