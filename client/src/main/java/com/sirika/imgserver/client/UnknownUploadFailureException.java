package com.sirika.imgserver.client;


public class UnknownUploadFailureException extends ImageServerException {
    private static final long serialVersionUID = 1L;
    
    private ImageId imageId;
    private ImageFormat imageFormat;
    
    public UnknownUploadFailureException(ImageId imageId, ImageFormat imageFormat, Exception e) {
	super(e);
	this.imageId = imageId;
	this.imageFormat = imageFormat;
    }

    public ImageId getImageId() {
        return imageId;
    }

    public ImageFormat getImageFormat() {
        return imageFormat;
    }
    
}
