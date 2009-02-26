package com.sirika.imgserver.client;

public class ImageServerException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ImageServerException() {
	super();
    }

    public ImageServerException(String message, Throwable cause) {
	super(message, cause);
    }

    public ImageServerException(String message) {
	super(message);
    }

    public ImageServerException(Throwable cause) {
	super(cause);
    }
}
