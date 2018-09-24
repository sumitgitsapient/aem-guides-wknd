package com.adobe.aem.guides.wknd.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

import com.adobe.aem.guides.wknd.core.CreateSamplePage;
import com.adobe.aem.guides.wknd.core.ModNodeImpl;
import com.day.cq.dam.core.process.CreateWebEnabledImageProcess;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Component(service=Servlet.class,

property={

        Constants.SERVICE_DESCRIPTION + "= Servlet for Creating page ",

        "sling.servlet.methods=" + HttpConstants.METHOD_GET,

        "sling.servlet.paths="+ "/bin/createpage"

   })
public class CreatePageServlet extends SlingSafeMethodsServlet {
	protected final Logger LOG =  LoggerFactory.getLogger(CreatePageServlet.class);

	
	private static final long serialVersionUID = 1L;
	
	private Session session;
	@Reference
	ResourceResolverFactory resolverFactory;
	@Reference
	CreateSamplePage samplePage;
	
	protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
		
		
		
		try {
			Map<String,Object> param = new HashMap<String,Object>();
			param.put(ResourceResolverFactory.SUBSERVICE,"datawrite");
			ResourceResolver resolver = null;
			String src1="/content/dam/wknd/en/los-angeles/annie-spratt-7693011111.jpg";
			String src2="/content/dam/wknd/en/los-angeles/annie-spratt-76930.jpg";
			resolver.resolve(src1);
			
			String path="/content/wknd/en/sport";
			String pageTitle = req.getParameter("pageTitle");
			String pageName = req.getParameter("pageName");
			String template="/conf/wknd/settings/wcm/templates/article-page-template";
			//String renderer="wknd/components/structure/page";
			resolver = resolverFactory.getServiceResourceResolver(param);
			session = resolver.adaptTo(Session.class);
			String createPageUsingAPI = samplePage.createPage(path, pageName, template, pageTitle,resolver);
			if(createPageUsingAPI.equals("created"))
			    LOG.info("Page created Successfully");
			     
			else
				{LOG.info("Page cannot be created");}
			   session.save();
	           session.logout();
			}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	
	
	

}
