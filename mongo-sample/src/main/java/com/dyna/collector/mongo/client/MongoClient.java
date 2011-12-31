package com.dyna.collector.mongo.client;

import java.net.UnknownHostException;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoClient {

	public static void main(String[] args) {
		System.out.println("Bootstrapping Mongo");
		ConfigurableApplicationContext context = null;
		context = new ClassPathXmlApplicationContext(
				"META-INF/spring/bootstrap.xml");

		TestMongoService helloMongo = context.getBean(TestMongoService.class);
		helloMongo.run();

	}

}
