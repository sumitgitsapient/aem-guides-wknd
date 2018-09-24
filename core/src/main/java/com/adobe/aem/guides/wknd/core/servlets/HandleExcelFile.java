package com.adobe.aem.guides.wknd.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service=Servlet.class,

property={

        Constants.SERVICE_DESCRIPTION + "=Servlet to read excel data and persist to AEM",

        "sling.servlet.methods=" + HttpConstants.METHOD_POST,

        "sling.servlet.paths="+ "/bin/excelfile"

   })
public class HandleExcelFile extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	private Session session;
	
	@Override
	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp)
			throws ServletException, IOException {
		
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		
		try {
			
			final boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			PrintWriter out = null;
			LOG.info("Get the stream1");
			
			if(isMultiPart)
			{
				final Map<String ,RequestParameter[]> params = request.getRequestParameterMap();
				LOG.info("Get the stream2");
				for(Map.Entry<String ,RequestParameter[]> pairs: params.entrySet())
				{
					
					final String k = pairs.getKey();
					final RequestParameter[] pArr = pairs.getValue();
					final RequestParameter param = pArr[0];
					final InputStream inputStream = param.getInputStream();
					
					int excelValue = injectSpreadSheet(inputStream);
		               if (excelValue == 0)
		                     out.println("Customer data from the Excel Spread Sheet has been successfully imported into the AEM JCR");
		               else
		                   out.println("Customer data could not be imported into the AEM JCR");
					
					
					
				}
				
				
				
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
	}

	private int injectSpreadSheet(InputStream inputStream) {
		try {
			
			LOG.info("Get the Stream3");
			LOG.info("Get the SpreadSheet");
			LOG.info("GET THE STREAM33");
            //Get the spreadsheet
            Workbook workbook = Workbook.getWorkbook(inputStream);
              
            LOG.info("GET THE STREAMWorkbook");
            Sheet sheet = workbook.getSheet(0);
              
             
            LOG.info("GET THE STREAMWorkbook");
           String firstName = ""; 
           String lastName = ""; 
           String address = "";
           String desc = "";
             
           LOG.info("GET THE STREAM44"); 
           for (int index=0; index<4;index++)
           {
               Cell a3 = sheet.getCell(0, index+2);
               Cell b3 = sheet.getCell(1,index+2); 
               Cell c3 = sheet.getCell(2,index+2);
               Cell d3 = sheet.getCell(3,index+2);
     
               firstName = a3.getContents(); 
               lastName = b3.getContents(); 
               address = c3.getContents();
               desc = d3.getContents();
                 
                
               LOG.info("About to inject cust data ..." +firstName);
                
               //Store the excel data into the Adobe AEM JCR
              injestCustData(firstName,lastName,address, desc);
              
             
               }    
			
           return 0;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1 ;
	}

	
	
	//Stores customer data in the Adobe CQ JCR
	private int injestCustData(String firstName, String lastName, String address, String desc) {
		// TODO Auto-generated method stub
		LOG.info("Stores customer data in the Adobe CQ JCR");
		int num = 0;
		Map<String,Object> param = new HashMap<String,Object>();
		param.put(ResourceResolverFactory.SUBSERVICE,"datawrite");
		ResourceResolver resolver = null;
		
		try {
			
			resolver = resolverFactory.getServiceResourceResolver(param);
			session = resolver.adaptTo(Session.class);
			LOG.info("Session created");
			//Create a node that represents the root node
            Node root = session.getRootNode(); 
                     
            //Get the content node in the JCR
            Node content = root.getNode("content");
                      
            //Determine if the content/customer node exists
            Node customerRoot = null;
            int custRec = doesCustExist(content);
         
             
            LOG.info("*** Value of  custRec is ..." +custRec);                               
            //-1 means that content/customer does not exist
            if (custRec == -1)
            {
                //content/customer does not exist -- create it
                customerRoot = content.addNode("customerexcel");
            }
           else
           {
               //content/customer does exist -- retrieve it
               customerRoot = content.getNode("customerexcel");
           }
                      
         int custId = custRec+1; //assign a new id to the customer node
                      
         //Store content from the client JSP in the JCR
        Node custNode = customerRoot.addNode("customer"+firstName+lastName+custId, "nt:unstructured"); 
               
            //make sure name of node is unique
        custNode.setProperty("id", custId); 
        custNode.setProperty("firstName", firstName); 
        custNode.setProperty("lastName", lastName); 
        custNode.setProperty("address", address);  
        custNode.setProperty("desc", desc);
                                    
        // Save the session changes and log out
        session.save(); 
        session.logout();
        return custId; 
			
			
			
			
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0 ;
	}

	private int doesCustExist(Node content) {
		try
        {
            int index = 0 ; 
            int childRecs = 0 ; 
               
        java.lang.Iterable<Node> custNode = JcrUtils.getChildNodes(content, "customerexcel");
        Iterator it = custNode.iterator();
                    
        //only going to be 1 content/customer node if it exists
        if (it.hasNext())
            {
            //Count the number of child nodes in content/customer
            Node customerRoot = content.getNode("customerexcel");
            Iterable itCust = JcrUtils.getChildNodes(customerRoot); 
            Iterator childNodeIt = itCust.iterator();
                   
            //Count the number of customer child nodes 
            while (childNodeIt.hasNext())
            {
                childRecs++;
                childNodeIt.next();
            }
             return childRecs; 
           }
        else
            return -1; //content/customer does not exist
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
     }
	}

	
	
	
	
	 
	
	
	


