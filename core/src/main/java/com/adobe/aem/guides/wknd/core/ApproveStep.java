package com.adobe.aem.guides.wknd.core;

import java.util.List;

import javax.jcr.Session;


import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowNode;


@Component(service = WorkflowProcess.class, property = {"process.label = Good Step"})
public class ApproveStep implements WorkflowProcess {

	protected final Logger log =  LoggerFactory.getLogger(ApproveStep.class);
	
	@Reference
	ResourceResolverFactory resolverFactory;
	@Reference
	ModNode theNode;
	private Session session;
	
	
	@Override
	public void execute(WorkItem item, WorkflowSession wfsession, MetaDataMap arg2) throws WorkflowException {
	try {
		log.info("Inside execute method");
		//Get the asset from the file system
		WorkflowNode myNode = item.getNode();
		
		//Get the title of workflow step
		String myTitle = myNode.getTitle();
		
		log.info("Title is"+myTitle);
		
		//Get the payload data
		WorkflowData workflowData = item.getWorkflowData();
		
		//Get the path of the asset
		String path = workflowData.getPayload().toString();
		
		//Get only name of the asset-including the ext
		int index = path.lastIndexOf("/");
		String fileName = path.substring(index+1);
		
		String approveUser =   getUserWhomApproved(wfsession,item);
		//Get the user whom approved the workflow
		   log.info("**** This asset was accepted " +fileName +" and approved by "+approveUser);  
		    
		   theNode.updateNode(path) ;
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}
	}
	//Gets the User from approves the payload
	private String getUserWhomApproved(WorkflowSession wfsession, WorkItem item) {
		
		try {
			
			
			List<HistoryItem> historyList = wfsession.getHistory(item.getWorkflow());
			int listSize = historyList.size();
			HistoryItem lastItem = historyList.get(listSize - 1);
			String lastComment = lastItem.getComment();
			 
		     String lastAction = lastItem.getAction();
		 
		     String lastUser = lastItem.getUserId();
		      return lastUser;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "no user";
	}

}
