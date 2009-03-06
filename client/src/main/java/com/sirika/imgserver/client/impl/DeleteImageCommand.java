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
package com.sirika.imgserver.client.impl;

import static com.sirika.imgserver.client.ImageReference.originalImage;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sirika.imgserver.client.ImageId;
import com.sirika.imgserver.client.UnknownDeleteFailureException;
import com.sirika.imgserver.client.UrlGenerator;

public class DeleteImageCommand {
    private static final Logger logger = LoggerFactory.getLogger(DeleteImageCommand.class);
    
    private HttpClient httpClient;
    private UrlGenerator urlGenerator;
    private ImageId imageId;
    
    public DeleteImageCommand(HttpClient httpClient, UrlGenerator urlGenerator,
	    ImageId imageId) {
	super();
	this.httpClient = httpClient;
	this.urlGenerator = urlGenerator;
	this.imageId = imageId;
    }
    
    public void execute() throws UnknownDeleteFailureException{
	try {
	    HttpDelete httpDelete = new HttpDelete(urlGenerator.getImageResourceUrl(originalImage(imageId.toString())));
	    HttpResponse response = httpClient.execute(httpDelete);
	    handleDeleteResponse(imageId, httpDelete, response);
	} catch (IOException e) {
	    throw new UnknownDeleteFailureException(imageId, e);
	}
    }
    
    private void handleDeleteResponse(ImageId id, HttpDelete httpPost, HttpResponse response) throws IOException {
	logger.debug("Received status : {}", response.getStatusLine());
	handleDeleteNon2xxError(id, httpPost, response);
	HttpEntity entity = response.getEntity();
	if(entity != null) {
	    entity.consumeContent();
	}
    }
    
    private void handleDeleteNon2xxError(ImageId id, HttpDelete httpPost, HttpResponse response) {
	if(response.getStatusLine().getStatusCode() >= 300) {
	    httpPost.abort();
	    throw new UnknownDeleteFailureException(id, new HttpResponseException(response.getStatusLine().getStatusCode(), "Error while uploading"));
	}
    }
}
