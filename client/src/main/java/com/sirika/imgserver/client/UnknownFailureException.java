package com.sirika.imgserver.client;


public class UnknownFailureException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private ImageReference imageReference;
    
    public UnknownFailureException(ImageReference imageReference, Exception e) {
	super(e);
	this.imageReference = imageReference;
    }

    public ImageReference getImageReference() {
        return imageReference;
    }
    
}
