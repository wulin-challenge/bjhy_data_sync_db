package com.bjhy.db.converter.core;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport.ConditionAndOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport.ConditionAndOutcomes;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * 自定义log4j配置文件加载
 * @author wubo
 *
 */
public class Log4jConfiguratorLoggingInitializer implements
		ApplicationContextInitializer<ConfigurableApplicationContext>,PriorityOrdered {

	private final Log logger = LogFactory.getLog(getClass());

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		String log4jPath = System.getProperty("user.dir") + File.separator + "config" + File.separator + "log4j.properties";
		File log4jFile = new File(log4jPath);
		if(log4jFile.exists()){
			PropertyConfigurator.configure(log4jPath);
		}
		
	}

	@Override
	public int getOrder() {
		return -100000;
	}

}