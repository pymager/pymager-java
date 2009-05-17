package com.sirika.httpclienthelpers.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class HttpClientTemplate {
    private final static Logger logger = LoggerFactory.getLogger(HttpClientTemplate.class);
    private HttpClient httpClient;
    private List<HttpErrorHandler> defaultErrorHandlers = Lists.newArrayList();

    public HttpClientTemplate(HttpClient httpClient) {
	super();
	this.httpClient = httpClient;
    }

    public void addDefaultErrorHandler(HttpErrorHandler httpErrorHandler) {
	defaultErrorHandlers.add(httpErrorHandler);
    }

    public Object execute(HttpUriRequest httpUriRequest, HttpResponseCallback httpResponseCallback) {
	return this.execute(httpUriRequest, httpResponseCallback, emptyIterables());
    }

    public Object execute(HttpUriRequest httpUriRequest, HttpResponseCallback httpResponseCallback, Iterable<HttpErrorHandler> httpErrorHandlers) {
	try {
	    final HttpResponse httpResponse = this.httpClient.execute(httpUriRequest);
	    logger.debug("Received Status: {}", httpResponse.getStatusLine());
	    HttpErrorHandler httpErrorHandler = findHttpErrorHandlerApplyingToResponse(httpErrorHandlers, httpResponse);
	    if(httpErrorHandler == null) {
		return httpResponseCallback.doWithHttpResponse(httpResponse);
	    } else {
		httpErrorHandler.handle(httpResponse);
	    }
	} catch(ClientProtocolException e) {
	    httpUriRequest.abort();
	    throw new RuntimeException(e);
	} catch(IOException e) {
	    httpUriRequest.abort();
	    throw new RuntimeException(e);
	} catch(RuntimeException e) {
	    httpUriRequest.abort();
	    throw e;
	} catch(Exception e) {
	    httpUriRequest.abort();
	    throw new RuntimeException(e);
	}
	finally {
	}
	throw new RuntimeException("Should never happen : programming error");
    }

    private HttpErrorHandler findHttpErrorHandlerApplyingToResponse(Iterable<HttpErrorHandler> httpErrorHandlers, final HttpResponse httpResponse) {
	try {
	    return Iterables.find(Iterables.concat(httpErrorHandlers, this.defaultErrorHandlers), new Predicate<HttpErrorHandler>(){
		public boolean apply(HttpErrorHandler input) {
		    return input.apppliesTo(httpResponse.getStatusLine());
		}
	    });    
	} catch(NoSuchElementException e) {
	    return null;
	}
    }

    private Iterable<HttpErrorHandler> emptyIterables() {
	return Iterables.emptyIterable();
    }
}
