package com.dyna.collector.mongo.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.dyna.collector.mongo.services.MongoDataAccessService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * Reference 1 :
 * http://static.springsource.org/spring-ws/site/reference/html/common.html
 * Handle JDom Node with XPath -- private XPathOperations template = new
 * Jaxp13XPathTemplate();
 * 
 * public void doXPath(Source request) { String name =
 * template.evaluateAsString("/Contacts/Contact/Name", request); // do something
 * with name } Reference 2 :
 * http://static.springsource.org/spring-ws/site/reference/html/tutorial.html
 * 
 * http://www.mongodb.org/display/DOCS/SQL+to+Mongo+Mapping+Chart
 * http://software.dzhuvinov.com/json-rpc-2.0-server.html
 * 
 * @author Kaniska_Mandal
 * 
 */
@Controller
@RequestMapping(value = "/mongo/", headers = MongoRestController.acceptHeader)
public class MongoRestController {
	protected static final String acceptHeader = "Accept=application/json, application/xml, text/xml";

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private MongoDataAccessService mongoDataAccessService;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("mongoConfig") private MongoConfig mongoConfig;
	 */

	private org.jdom.xpath.XPath idExpr;
	{
		try {
			idExpr = org.jdom.xpath.XPath.newInstance("//account[@id]");
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/entities/", method = RequestMethod.GET)
	public @ResponseBody
	List<DBObject> findAll(String entityName) {
		return mongoDataAccessService.getAll(entityName);
	}

	// Experimental
	@RequestMapping(value = "/entities/add", method = RequestMethod.POST)
	public ResponseEntity<String> addEntity(@RequestBody String request) {

		// Load the XML document
		InputStream ioStream = null;
		try {
			ioStream = new ByteArrayInputStream(request.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// configure the document builder factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		// get a builder to create a DOM document
		DocumentBuilder domBuilder = null;
		try {
			domBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		// parse the damned document
		org.w3c.dom.Document w3cDocument = null;
		try {
			w3cDocument = domBuilder.parse(ioStream);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// create a JDOM DOM Builder
		DOMBuilder jdomBuilder = new DOMBuilder();
		// create a JDOM Document from a w3c DOM
		org.jdom.Document jdomDocument = jdomBuilder.build(w3cDocument);

		org.jdom.Element rootElement = jdomDocument.getRootElement();

		String rootElemName = rootElement.getName();
		if (!mongoDataAccessService.collectionExists(rootElemName)) {
			mongoDataAccessService.createCollection(rootElemName);
		}

		// CREATE //UPDATE //DELETE

		DBObject parentDocument = createDocumentFromAttribute(rootElement);

		// iterate through child elements of root
		for (Object obj : rootElement.getChildren()) {
			if (!(obj instanceof org.jdom.Element)) {
				break;
			}

			org.jdom.Element element = (org.jdom.Element) obj;
			if (!element.getChildren().isEmpty()) {
				break; // don't proceed -- this is just a mere parent name -
						// like 'Opportunities' that hold Opportunity elements
			}

			// Create an embedded Collection i.e. Sub Table corresponding to
			// each Child Element
			String elementName = element.getName();
			if (!mongoDataAccessService.collectionExists(elementName)) {
				mongoDataAccessService.createCollection(elementName);
			}

			// Create
			DBObject childDocument = createDocumentFromAttribute(element);
			// finally save the childDocument
			if (!childDocument.toMap().isEmpty()) {
				mongoDataAccessService.add(childDocument, elementName);
			}

			String elementId = (String) childDocument.get("_id"); // (get the
																	// element
																	// id from
																	// childDocument)
			DBRef childDocumentRef = null;
			try {
				childDocumentRef = new DBRef(
						mongoDataAccessService.getDB("staging"), elementName,
						elementId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// DBObject childDocument = childDocumentRef.fetch();

			parentDocument.put(elementName + "_ref", childDocumentRef);
		}
		// finally save the parentDocument
		if (!parentDocument.toMap().isEmpty()) {
			mongoDataAccessService.add(parentDocument, rootElemName);
		}

		return new ResponseEntity<String>("Hello Mongo", HttpStatus.ACCEPTED);
	}

	/**
	 * Dynamically create the document from DOM element
	 * @param element
	 * @param collectionName
	 */
	private DBObject createDocumentFromAttribute(org.jdom.Element element) {
		List<?> list = element.getAttributes();
		DBObject dbo = new BasicDBObject();
		for (Object attr : list) {
			org.jdom.Attribute source = (org.jdom.Attribute) attr;
			
			if (source.getName().equals("Id")) {
				// if(mongoDataAccessService.get("_id") == null) {
				dbo.put("_id", source.getValue()); // use the original
													// account_id from
													// salesforce instead of
													// auto-generated id
				// candidate for shard key -- TODO
				// }
			} else {
				dbo.put(source.getName(), source.getValue());
			}
		}
		return dbo;
	}
}