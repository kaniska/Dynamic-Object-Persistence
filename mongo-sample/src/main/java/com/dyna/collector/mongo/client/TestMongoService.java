/**
 * 
 */
package com.dyna.collector.mongo.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import com.dyna.collector.mongo.domain.Person;

/**
 * @author Kaniska_Mandal
 *
 */
@Repository
public class TestMongoService {
	@Autowired
	MongoOperations mongoOperations;

	public void run() {

		if (mongoOperations.collectionExists(Person.class)) {
			mongoOperations.dropCollection(Person.class);
		}

		mongoOperations.createCollection(Person.class);

		Person p = new Person(12345, "Alan", "Pitrode", 45);

		mongoOperations.insert(p);

		List<Person> results = mongoOperations.findAll(Person.class);
		for(Person person : results) {
			System.out.println("Result: " + person.getFirstName() + " " + person.getLastName());
		}
		
	}
}
