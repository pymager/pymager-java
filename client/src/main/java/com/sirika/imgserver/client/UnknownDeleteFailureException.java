package com.sirika.imgserver.client;


public class UnknownDeleteFailureException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private ImageId imageId;
    
    public UnknownDeleteFailureException(ImageId imageId, Exception e) {
	super(e);
	this.imageId = imageId;
    }

    public ImageId getImageId() {
        return imageId;
    }
    
}
