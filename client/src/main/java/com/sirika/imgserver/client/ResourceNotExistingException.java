package com.sirika.imgserver.client;

public class ResourceNotExistingException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private ImageReference imageReference;
    
    public ResourceNotExistingException(ImageReference imageReference) {
	super();
	this.imageReference = imageReference;
    }

    public ImageReference getImageReference() {
        return imageReference;
    }
    
}
