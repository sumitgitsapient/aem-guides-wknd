package com.adobe.aem.guides.wknd.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;





@Component(service=Servlet.class,

property={

        Constants.SERVICE_DESCRIPTION + "= Servlet for Invoking Workflow",

        "sling.servlet.methods=" + HttpConstants.METHOD_GET,

        "sling.servlet.paths="+ "/bin/invokeWF"

   })
public class WorkFlowServlet extends SlingSafeMethodsServlet {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	private static final long serialVersionUID = 1L;
	
	private Session session;
	
	@Reference
	ResourceResolverFactory resolverFactory;
	@Reference
    WorkflowService workflowService;
	
	
	protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
		
		Map<String,Object> param = new HashMap<String,Object>();
		param.put(ResourceResolverFactory.SUBSERVICE,"datawrite");
		ResourceResolver resolver = null;
		
		try {
			
			resolver = resolverFactory.getServiceResourceResolver(param);
			session = resolver.adaptTo(Session.class);
			LOG.info("Session created in Servlet");
			String assetPath = req.getParameter("assetPath");
			String email = req.getParameter("email");
			LOG.info("IN SERVET - asset path "+assetPath) ; 
			LOG.info("IN SERVET - email "+email) ;
			
			//Create a workflow session
			WorkflowSession wfSession =  workflowService.getWorkflowSession(session);
			//name of the workflow
			String workFlowName = "/var/workflow/models/approveasset";
			//Get the workflow model
			WorkflowModel workFlowModel = wfSession.getModel(workFlowName);
			//Get the workflow payload
			WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", assetPath);
			//Before we Send Payload to the Workflow - we need to update email prop on the Payload node
		       updateEmailProp(assetPath, email);
		       
		       //Run the workflow
		      wfSession.startWorkflow(workFlowModel, wfData);
		      session.save();
		      session.logout();
			
			
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		
		
	}


	private void updateEmailProp(String assetPath, String email) {
		 try{
	         
             
		        //Create APp ;ogic to change image NODE PROP
		        //Create a node that represents the root node
		        Node root = session.getRootNode(); 
		         
		         
		        String metaPath = assetPath +"/jcr:content/metadata"  ;
		         
		         
		        LOG.info("**** ABOUT TO GET PATH"); 
		         
		      //Remove the first / char - JCR API does not like that
		        String newPath = assetPath.replaceFirst("/", "");   
		         
		        String finalPath = newPath+"/jcr:content/metadata" ;
		          
		        //This returns the metadata node under the asset node
		        Node rcontent = root.getNode(finalPath);
		         
		        rcontent.setProperty("email", email);
		        
		         
		        }
		        catch(Exception e)
		        {
		            e.printStackTrace(); 
		        }
		
	}
	}
	
	
	


