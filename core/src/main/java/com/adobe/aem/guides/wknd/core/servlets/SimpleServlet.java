package com.adobe.aem.guides.wknd.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
 
@Component(service=Servlet.class,

property={

        Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",

        "sling.servlet.methods=" + HttpConstants.METHOD_GET,

        "sling.servlet.paths="+ "/bin/myCustData"

   })
public class SimpleServlet extends SlingAllMethodsServlet {
  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
      
    private final Logger logger = LoggerFactory.getLogger(getClass());
      
    @Reference
    private EmployeeInter emplData;
  
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
         
        try
        {
        logger.info("About to call");  
             
        String data=  emplData.getEmployeeData(); 
        logger.info("DATA IS "+data);   
        resp.getWriter().write(data);
      
        }
        catch (Exception e)
        {
            e.printStackTrace(); 
        }
                  
        }
   
}