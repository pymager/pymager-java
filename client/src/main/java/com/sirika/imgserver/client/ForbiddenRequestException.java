package com.sirika.imgserver.client;

public class ForbiddenRequestException extends ImageServerException {

    public ForbiddenRequestException() {
	super();
    }

    public ForbiddenRequestException(String message) {
	super(message);
    }

    public ForbiddenRequestException(Throwable cause) {
	super(cause);
    }

}
