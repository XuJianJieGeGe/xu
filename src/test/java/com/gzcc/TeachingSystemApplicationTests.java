package com.gzcc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TeachingSystemApplicationTests {

	//记录器
	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void contextLoads() {

		//日志的级别由低到高
		//可以调整日志的输出级别，日志就会只在这个级别或更高级别生效
		logger.trace("这是trace()日志");
		logger.debug("这是debug()日志");

		//springboot默认使用的是info级别，没有指定就要springboot默认的级别，root级别
		logger.info("这是info()日志");
		logger.warn("这是warn()日志");
		logger.error("这是error()日志");
	}

}
