/**
 * PyMager Java REST Client
 * Copyright (C) 2008 Sami Dalouche
 *
 * This file is part of PyMager Java REST Client.
 *
 * PyMager Java REST Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PyMager Java REST Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PyMager Java REST Client.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package com.sirika.pymager.client.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.pymager.client.ForbiddenRequestException;
import com.sirika.pymager.client.ImageReference;
import com.sirika.pymager.client.ResourceNotExistingException;
import com.sirika.pymager.client.UnknownDownloadFailureException;
import com.sirika.pymager.client.UrlGenerator;

class HttpDownloadInputStreamSource implements InputStreamSource{
    private final static Logger logger = LoggerFactory.getLogger(HttpDownloadInputStreamSource.class);
    private HttpClient httpClient;
    private UrlGenerator urlGenerator;
    private HttpGet httpGet;
    private ImageReference imageReference;
    
    public HttpDownloadInputStreamSource(HttpClient httpClient, UrlGenerator urlGenerator, ImageReference imageReference) {
	super();
	this.urlGenerator = urlGenerator;
	this.httpClient = httpClient;
	this.imageReference = imageReference;
	
	this.httpGet = new HttpGet(urlGenerator.getImageResourceUrl(imageReference));
    }

    public InputStream getInputStream() throws IOException, ResourceNotExistingException, UnknownDownloadFailureException {
	logger.debug("Generating InputStream for {}", imageReference);
	HttpResponse response = null;
	try {
	    response = httpClient.execute(httpGet);    
	} catch(Exception e) {
	    throw new UnknownDownloadFailureException(imageReference, e);
	}
	
	
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
	try {
	    handleForbidden(response);
	    handle404NotFound(response);
	    handleNon2xx(response);    
	} catch(RuntimeException e) {
	    httpGet.abort();
	    throw e;
	} 
    }

    private void handleNon2xx(HttpResponse response) {
	if(response.getStatusLine().getStatusCode() >= 300) {
	    throw new UnknownDownloadFailureException(imageReference, new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
	}
    }

    private void handle404NotFound(HttpResponse response) {
	if(HttpStatus.SC_NOT_FOUND == response.getStatusLine().getStatusCode()) {
	    throw new ResourceNotExistingException(imageReference);
	}
    }
    
    private void handleForbidden(HttpResponse response) {
	if(HttpStatus.SC_FORBIDDEN == response.getStatusLine().getStatusCode()) {
	    throw new ForbiddenRequestException(new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
	}
    }
    
}