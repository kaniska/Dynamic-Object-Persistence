package com.dyna.collector.mongo.client;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * http://kkovacs.eu/cassandra-vs-mongodb-vs-couchdb-vs-redis
 * http://www.mongodb.org/display/DOCS/Updating#Updating-%24set
 * http://www.mongodb.org/display/DOCS/Updating+Data+in+Mongo
 * http://stackoverflow
 * .com/questions/4185105/ways-to-implement-data-versioning-in-mongodb
 * http://strangeowl.blogspot.com/2010/09/versioning-objects-and-mongodb.html
 * 
 * @author Kaniska_Mandal
 */

public class MongoRestClient {

	private final static String addCustomerXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<account id=\"AC1234\" firstname=\"Joe\" lastname=\"Smith\" zipcode=\"2345\">"
			+ "<location id=\"4678\" country=\"USA\"></location>"
			+ "<opportunity id=\"8678\" country=\"USA\"></opportunity>"
			+ "</account>";
	
	private final static String addRevenueXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<revenue id=\"RV1234\" accountId=\"Joe\" amount=\"23456\">"
			+ "<salesrep name=\"Peter Smith\" territory=\"NA\"></salesrep>"
			+ "</revenue>";

	public static void main(String[] args) {
		try {
			testAddCustomer();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws IOException
	 * @throws HttpException
	 */
	private static void testAddCustomer() throws IOException, HttpException {

		DefaultHttpClient httpClient = null;
		try {
			httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(
					"http://localhost:8086/dynamic-data-collection/mongo/entities/add");
			postRequest.setHeader("Accept", "application/xml");

			StringEntity input = new StringEntity(addRevenueXML);
			input.setContentType("application/xml");
			input.setContentEncoding("UTF-8");
			postRequest.setEntity(input);

			HttpResponse response = httpClient.execute(postRequest);
			System.out.println("Response headers ---");
			org.apache.http.Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i].toString());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

}
