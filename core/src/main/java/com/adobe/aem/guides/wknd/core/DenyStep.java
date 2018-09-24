package com.adobe.aem.guides.wknd.core;

import javax.jcr.Session;


import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowNode;

@Component(service = WorkflowProcess.class,property = {"process.label = Deny Process"})
public class DenyStep implements WorkflowProcess{

	protected final Logger LOG =  LoggerFactory.getLogger(DenyStep.class);
	
	private Session session;
	
	@Reference
	ResourceResolverFactory resolverFactory;

	
	@Override
	public void execute(WorkItem item, WorkflowSession wfsession, MetaDataMap arg2) throws WorkflowException {
		
		try {
			LOG.info("inside delete execute");
			
			//Get the Assets from the file system for a test
			WorkflowNode myNode = item.getNode();
			
			//returns the title of the workflow step
			
		   String myTitle = myNode.getTitle();
		   
		   LOG.info("**** The title is "+myTitle); 
		   
		   
		 //Get the path of the asset
		   WorkflowData workflowdata = item.getWorkflowData();
		   
		 //Get the path of the asset
		   
		   String path = workflowdata.getPayloadType().toString();
		   
		   
		 //Get only the name of the asset - including the ext
		    int index = path.lastIndexOf("/");
		    String fileName = path.substring(index + 1);
		         
		            
		    LOG.info("**** This asset was rejected " +fileName );
		   
		   
		   
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
