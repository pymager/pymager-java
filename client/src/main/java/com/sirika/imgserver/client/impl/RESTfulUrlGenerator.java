package com.sirika.imgserver.client.impl;

import com.sirika.imgserver.client.ImageReference;

/**
 * Generates URLs such as :
 * <ul>
 *   <li>http://baseurl:8000/derived/mypic-800x600.png</li>
 *   <li>http://baseurl:8000/original/mypic</li>
 * </ul>
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public class RESTfulUrlGenerator implements UrlGenerator {

    private String baseImageServiceUrl;

    public RESTfulUrlGenerator(String baseImageServiceUrl) {
	super();
	this.baseImageServiceUrl = baseImageServiceUrl;
    }
    
    /* (non-Javadoc)
     * @see com.sirika.imgserver.client.UrlGenerator#urlFor(com.sirika.imgserver.client.ImageReference)
     */
    public String urlFor(ImageReference imageReference) {
	if(imageReference.isDerived()) {
	    return String.format("%s/derived/%s-%sx%s.%s", 
		    this.baseImageServiceUrl, 
		    imageReference.getId(),
		    imageReference.getRescaling().getWidth(), 
		    imageReference.getRescaling().getHeight(), 
		    imageReference.getImageFormat().extension());
	} else {
	    return String.format("%s/original/%s", 
		    this.baseImageServiceUrl,
		    imageReference.getId());
	}
    }
}
