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
package com.sirika.pymager.client.impl;

import static com.sirika.httpclienthelpers.template.AbstractHttpErrorHandler.statusCodeGreaterOrEquals;
import static com.sirika.pymager.client.ImageReference.originalImage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.sirika.httpclienthelpers.template.AbstractHttpErrorHandler;
import com.sirika.httpclienthelpers.template.HttpClientTemplate;
import com.sirika.httpclienthelpers.template.HttpErrorHandler;
import com.sirika.httpclienthelpers.template.HttpResponseCallback;
import com.sirika.pymager.client.ImageId;
import com.sirika.pymager.client.UnknownDeleteFailureException;
import com.sirika.pymager.client.UrlGenerator;

public class DeleteImageCommand {
    private static final Logger logger = LoggerFactory.getLogger(DeleteImageCommand.class);
    
    private HttpClientTemplate httpClientTemplate;
    private UrlGenerator urlGenerator;
    private ImageId imageId;
    
    public DeleteImageCommand(HttpClient httpClient, UrlGenerator urlGenerator,
	    ImageId imageId) {
	super();
	this.httpClientTemplate = new HttpClientTemplate(httpClient);
	this.urlGenerator = urlGenerator;
	this.imageId = imageId;
    }
    
    public void execute() throws UnknownDeleteFailureException{
	HttpDelete httpDelete = new HttpDelete(urlGenerator.getImageResourceUrl(originalImage(imageId.toString())));
	
	this.httpClientTemplate.execute(httpDelete, new HttpResponseCallback() {
	    public Object doWithHttpResponse(HttpResponse httpResponse) throws Exception {
		HttpEntity entity = httpResponse.getEntity();
		if(entity != null) {
		    entity.consumeContent();
		}
		return null;
	    }    
	}, httpErrorHandlers());
    }
    
    private Iterable<HttpErrorHandler> httpErrorHandlers() {
	return ImmutableList.of(defaultHandler());
    }
    
    private HttpErrorHandler defaultHandler() {
	return new AbstractHttpErrorHandler(statusCodeGreaterOrEquals(300)) {
	    public void handle(HttpResponse response) throws Exception {
		throw new UnknownDeleteFailureException(imageId, new HttpResponseException(response.getStatusLine().getStatusCode(), "Error while uploading"));
	    }
	};
    }
    
}
