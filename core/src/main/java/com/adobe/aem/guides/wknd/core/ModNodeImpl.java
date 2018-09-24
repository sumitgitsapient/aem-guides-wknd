package com.adobe.aem.guides.wknd.core;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;


import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class ModNodeImpl implements ModNode {
	
	protected final Logger LOG =  LoggerFactory.getLogger(ModNodeImpl.class);
	
	private Session session;
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	

	@Override
	public void updateNode(String path) {
		
		Map<String,Object> param = new HashMap<String,Object>();
		param.put(ResourceResolverFactory.SUBSERVICE,"datawrite");
		ResourceResolver resolver = null;
		try {
			
			resolver = resolverFactory.getServiceResourceResolver(param);
			session = resolver.adaptTo(Session.class);
			LOG.info("Session created");
			//Create a node that represents the root node
			Node root = session.getRootNode();
			
			String metaPath = path + "/jcr:content/metadata"  ;
			//Remove the first / char - JCR API does not like that
            String newPath = path.replaceFirst("/", "");   
             
            String finalPath = newPath+"/jcr:content/metadata" ;
            
            //This return the metadata under the assets node
            Node rcontent = root.getNode(finalPath);
            
            String ttt = rcontent.getPath(); 
            
            LOG.info("**** This meta path is " + ttt);
            
            rcontent.setProperty("approve", true);
			
			
			
			
			
			
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
