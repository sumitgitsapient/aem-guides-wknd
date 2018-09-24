package com.adobe.aem.guides.wknd.core;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

@Component
public class CreateSamplePageImpl implements CreateSamplePage {

	private ResourceResolver resolver;

	@Override
	public String createPage(String path, String pageName, String template, String pageTitle,ResourceResolver resolver1)
			throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		
		this.resolver=resolver1;
		// TODO Auto-generated method stub
		String renderer = "wknd/components/structure/page";
		Page prodPage = null;
		PageManager pageManager = resolver.adaptTo(PageManager.class);
		try {
			prodPage = pageManager.create(path, pageName, template, pageTitle);
			Node pageNode = prodPage.adaptTo(Node.class);
			Node jcrNode = null;

			if (prodPage.hasContent()) {

				jcrNode = prodPage.getContentResource().adaptTo(Node.class);
			} else {
				jcrNode = pageNode.addNode("jcr:content", "cq:PageContent");
			}
			jcrNode.setProperty("sling:resourceType", renderer);

			Node parNode = jcrNode.addNode("par");
			parNode.setProperty("sling:resourceType", "foundation/components/parsys");

			Node textNode = parNode.addNode("text");
			textNode.setProperty("sling:resourceType", "foundation/components/text");
			textNode.setProperty("textIsRich", "true");
			textNode.setProperty("text", "Test page");
			return "created";

		} catch (WCMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    
		
	}

}
