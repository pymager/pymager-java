/**
 * 
 */
package com.sirika.httpclienthelpers.template;

import org.apache.http.StatusLine;


public abstract class AbstractHttpErrorHandler implements HttpErrorHandler {
    public interface ErrorMatcher {
	boolean matches(StatusLine statusLine);
    }
    
    private ErrorMatcher errorMatcher;
    public AbstractHttpErrorHandler(ErrorMatcher errorMatcher) {
        super();
        this.errorMatcher = errorMatcher;
    }

    public boolean apppliesTo(StatusLine statusLine) {
        return errorMatcher.matches(statusLine);
    }
    
    public static ErrorMatcher statusCodeEquals(final int httpErrorCode) {
	return new ErrorMatcher() {
	    public boolean matches(StatusLine statusLine) {
		return httpErrorCode == statusLine.getStatusCode();
	    }
	};
    }
    
    public static ErrorMatcher statusCodeGreaterOrEquals(final int httpErrorCode) {
	return new ErrorMatcher() {
	    public boolean matches(StatusLine statusLine) {
		return statusLine.getStatusCode() >= httpErrorCode ;
	    }
	};
    }

}