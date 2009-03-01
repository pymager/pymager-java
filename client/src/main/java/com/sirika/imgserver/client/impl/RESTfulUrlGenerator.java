package com.sirika.imgserver.client.impl;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.UrlGenerator;

/**
 * Generates URLs such as :
 * <ul>
 *   <li>http://baseurl:8000/derived/mypic-800x600.png</li>
 *   <li>http://baseurl:8000/original/mypic</li>
 * </ul>
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
class RESTfulUrlGenerator implements UrlGenerator {

    private String baseImageServiceUrl;

    public RESTfulUrlGenerator(String baseImageServiceUrl) {
	super();
	this.baseImageServiceUrl = baseImageServiceUrl;
    }
    
    /* (non-Javadoc)
     * @see com.sirika.imgserver.client.UrlGenerator#urlFor(com.sirika.imgserver.client.ImageReference)
     */
    public String getImageResourceUrl(ImageReference imageReference) {
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

    public String toString() {
	return String.format("RESTful URL Generator pointing to %s", baseImageServiceUrl);
    }
    
    
}
