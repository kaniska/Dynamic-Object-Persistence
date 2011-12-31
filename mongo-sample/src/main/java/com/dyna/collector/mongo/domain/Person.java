package com.dyna.collector.mongo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Kaniska_Mandal
 */
@Document
public class Person {
	@Id
	private String id;
	@Indexed(unique = true)
	private Integer ssn;
	private String firstName;
	@Indexed
	private String lastName;
	private Integer age;
	@Transient
	private Integer accountTotal;

	public Person(Integer ssn) {
		this.ssn = ssn;
	}

	@PersistenceConstructor
	public Person(Integer ssn, String firstName, String lastName, Integer age) {
		this.ssn = ssn;
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
	}

	public String getId() {
		return id;
	}

	// no setter for Id. (getter is only exposed for some unit testing)

	public Integer getSsn() {
		return ssn;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the age
	 */
	public Integer getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(Integer age) {
		this.age = age;
	}

	/**
	 * @return the accountTotal
	 */
	public Integer getAccountTotal() {
		return accountTotal;
	}

	/**
	 * @param accountTotal the accountTotal to set
	 */
	public void setAccountTotal(Integer accountTotal) {
		this.accountTotal = accountTotal;
	}

}
