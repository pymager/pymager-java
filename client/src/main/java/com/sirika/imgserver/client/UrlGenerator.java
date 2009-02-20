package com.sirika.imgserver.client;

public class UrlGenerator {

    private String baseImageServiceUrl;

    public UrlGenerator(String baseImageServiceUrl) {
	super();
	this.baseImageServiceUrl = baseImageServiceUrl;
    }
    
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
