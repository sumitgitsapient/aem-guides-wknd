package com.adobe.aem.guides.wknd.core.servlets;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.jackrabbit.commons.xml.ToXmlContentHandler;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.model.ConvertAnchor;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;

@Component
public class EmployeeImpl  implements EmployeeInter{
	
	protected final Logger LOG = Logger.getLogger(this.getClass());
	
	private Session session;
	
    @Reference
    ResourceResolverFactory resolverFactory;
    
   

	@Override
	public String getEmployeeData() {
		// TODO Auto-generated method stub
		
		Employee employee = null;
		List<Employee> employList = new ArrayList<Employee>();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put(ResourceResolverFactory.SUBSERVICE,"datawrite");
		ResourceResolver resolver = null;
		try {
			
			resolver = resolverFactory.getServiceResourceResolver(param);
			session = resolver.adaptTo(Session.class);
			LOG.info("Session created");
			//Obtain the query manager for the session ...
			QueryManager qm = session.getWorkspace().getQueryManager();
			//Setup the quesry based on user input
			String sqlStatement = "";
			//Setup the query to get all employee nodes
			
			sqlStatement = "SELECT * FROM [nt:unstructured] AS t WHERE ISDESCENDANTNODE('/content/employees')And contains (status, 'employee')";
			
		    Query query = qm.createQuery(sqlStatement, "JCR-SQL2");
		  //Execute the query and get the results ...
		    QueryResult result = query.execute();
		    //Iterate over the nodes in the results ...
		    NodeIterator iterator = result.getNodes();
		    while(iterator.hasNext())
		    {
		    	employee = new Employee();
		    	Node node = iterator.nextNode();
		    	employee.setName(node.getProperty("name").getString());
		    	employee.setAddress(node.getProperty("address").getString());
		    	employee.setPosition(node.getProperty("job").getString());
		    	employee.setAge(node.getProperty("age").getString());
		    	employee.setDate(node.getProperty("start").getString());
		    	employee.setSalary(node.getProperty("salary").getString());
		    	
		    	employList.add(employee);
		    	
		    }
			
		    session.logout();
		    return convertToString(toXml(employList));
		    
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		
		return null;
	}
	//Convert Employee data retrieved from the AEM JCR
    //into an XML schema to pass back to client
    private Document toXml(List<Employee> employeeList) {
    try
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
                         
        //Start building the XML to pass back to the AEM client
        Element root = doc.createElement( "Employees" );
        doc.appendChild( root );
                      
        //Get the elements from the collection
        int custCount = employeeList.size();
            
        //Iterate through the collection to build up the DOM           
         for ( int index=0; index < custCount; index++) {
         
             //Get the Employee object from the collection
             Employee myEmployee = (Employee)employeeList.get(index);
                              
             Element Employee = doc.createElement( "Employee" );
             root.appendChild( Employee );
                               
             //Add rest of data as child elements to Employee
             //Set Name
             Element name = doc.createElement( "Name" );
             name.appendChild( doc.createTextNode(myEmployee.getName() ) );
             Employee.appendChild( name );
                                                                  
             //Set Address
             Element address = doc.createElement( "Address" );
             address.appendChild( doc.createTextNode(myEmployee.getAddress() ) );
             Employee.appendChild( address );
                            
             //Set position
             Element position = doc.createElement( "Position" );
             position.appendChild( doc.createTextNode(myEmployee.getPosition() ) );
             Employee.appendChild( position );
                           
             //Set age
             Element age = doc.createElement( "Age" );
             age.appendChild( doc.createTextNode(myEmployee.getAge()) );
             Employee.appendChild( age );
              
             //Set Date
             Element date = doc.createElement( "Date" );
             date.appendChild( doc.createTextNode(myEmployee.getDate()) );
             Employee.appendChild( date );
              
             //Set sal
             Element salary = doc.createElement( "Salary" );
             salary.appendChild( doc.createTextNode(myEmployee.getSalary()) );
             Employee.appendChild( salary );
          }
                    
    return doc;
    }
    catch(Exception e)
    {
        e.printStackTrace();
        }
    return null;
    }
         
         
    private String convertToString(Document xml)
    {
    try {
       Transformer transformer = TransformerFactory.newInstance().newTransformer();
      StreamResult result = new StreamResult(new StringWriter());
      DOMSource source = new DOMSource(xml);
      transformer.transform(source, result);
      return result.getWriter().toString();
    } catch(Exception ex) {
          ex.printStackTrace();
    }
      return null;
     }

}
