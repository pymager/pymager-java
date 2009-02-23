/**
 * 
 */
package com.sirika.imgserver.client.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ImageServer;
import com.sirika.imgserver.client.ResourceNotExistingException;
import com.sirika.imgserver.client.UnknownFailureException;

class HttpDownloadInputStreamSource implements InputStreamSource{
    private final static Logger logger = LoggerFactory.getLogger(HttpDownloadInputStreamSource.class);
    private HttpClient httpClient;
    private ImageServer imageServer;
    private HttpGet httpGet;
    private ImageReference imageReference;
    
    public HttpDownloadInputStreamSource(ImageServer imageServer, HttpClient httpClient, ImageReference imageReference) {
	super();
	this.imageServer = imageServer;
	this.httpClient = httpClient;
	this.imageReference = imageReference;
	
	this.httpGet = new HttpGet(imageServer.getDownloadUrl(imageReference));
    }

    public InputStream getInputStream() throws IOException, ResourceNotExistingException, UnknownFailureException {
	logger.debug("Generating InputStream for {}", imageReference);
	HttpResponse response = httpClient.execute(httpGet);
	
	logger.debug("Received Status: {}", response.getStatusLine());
	handleErrors(response);

        return generateInputStream(response.getEntity());
    }

    private InputStream generateInputStream(HttpEntity entity) throws IOException {
	if(entity != null) {
	    return entity.getContent();
	} else {
	    return null;
	}
    }

    private void handleErrors(HttpResponse response) {
	handle404NotFound(response);
	handleNon200OK(response);
    }

    private void handleNon200OK(HttpResponse response) {
	if(HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
	    httpGet.abort();
	    throw new UnknownFailureException(imageReference, response.getStatusLine());
	}
    }

    private void handle404NotFound(HttpResponse response) {
	if(HttpStatus.SC_NOT_FOUND == response.getStatusLine().getStatusCode()) {
	    throw new ResourceNotExistingException(imageReference);
	}
    }
    
}