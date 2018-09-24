package com.adobe.aem.guides.wknd.core.components;

public interface Card {
	
	/**
	 * @return a string to display as the Title of the card.
	 */
	String getTitle();
	
	/**
	 * @return a string to display as the Description of the card.
	 */
	String getDescription();

	
	/**
	 * @return a path to populate link around the card.
	 */
	String getLinkpath();
	
	
	/**
	 * @return a path to populate the background image source of the card.
	 */
	String getImgSrc();
	
	/**
	 * @return a boolean if the component has content to display.
	 */
	boolean isEmpty();
}
