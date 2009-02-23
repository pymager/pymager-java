package com.sirika.imgserver.client;

import org.apache.http.StatusLine;

public class UnknownFailureException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private ImageReference imageReference;
    private StatusLine statusLine;
    
    public UnknownFailureException(ImageReference imageReference, StatusLine statusLine) {
	super();
	this.imageReference = imageReference;
	this.statusLine = statusLine;
    }

    public ImageReference getImageReference() {
        return imageReference;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }
    
}
