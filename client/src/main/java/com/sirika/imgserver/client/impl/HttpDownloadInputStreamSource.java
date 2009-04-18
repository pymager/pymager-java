/**
 * ImgServer Java REST Client
 * Copyright (C) 2008 Sami Dalouche
 *
 * This file is part of ImgServer Java REST Client.
 *
 * ImgServer Java REST Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ImgServer Java REST Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ImgServer.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ResourceNotExistingException;
import com.sirika.imgserver.client.UnknownDownloadFailureException;
import com.sirika.imgserver.client.UrlGenerator;

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
	try {
	    handle404NotFound(response);
	    handleNon2xx(response);    
	} catch(RuntimeException e) {
	    httpGet.abort();
	    throw e;
	} 
    }

    private void handleNon2xx(HttpResponse response) {
	if(response.getStatusLine().getStatusCode() >= 300) {
	    throw new UnknownDownloadFailureException(imageReference, new HttpResponseException(response.getStatusLine().getStatusCode(), "Error while downloading"));
	}
    }

    private void handle404NotFound(HttpResponse response) {
	if(HttpStatus.SC_NOT_FOUND == response.getStatusLine().getStatusCode()) {
	    throw new ResourceNotExistingException(imageReference);
	}
    }
    
}