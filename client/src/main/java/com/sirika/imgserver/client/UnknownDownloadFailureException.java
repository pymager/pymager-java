package com.sirika.imgserver.client;


public class UnknownDownloadFailureException extends ImageServerException {
    private static final long serialVersionUID = 1L;
    
    private ImageReference imageReference;
    
    public UnknownDownloadFailureException(ImageReference imageReference, Exception e) {
	super(e);
	this.imageReference = imageReference;
    }

    public ImageReference getImageReference() {
        return imageReference;
    }
    
}
