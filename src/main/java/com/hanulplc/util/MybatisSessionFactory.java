package com.hanulplc.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;

@Slf4j
public class MybatisSessionFactory {

	private static final String RESOURCE = "mybatis/mybatis-config.xml";
	private static final String ENV_PROD = "prod";
	private static final String ENV_DEV = "dev";

	private MybatisSessionFactory() {
	}

	public static SqlSession newProdSession() {
		return newSession(ENV_PROD);
	}

	public static SqlSession newDevSession() {
		return newSession(ENV_DEV);
	}

	private static SqlSession newSession(String environment) {
		SqlSession session = null;

		try {
			Reader reader = Resources.getResourceAsReader(RESOURCE);
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, environment);
			session = sqlSessionFactory.openSession(false);

		} catch (Exception e) {
			log.error("session 생성 중 에러 발생", e);
		}

		return session;
	}
}
