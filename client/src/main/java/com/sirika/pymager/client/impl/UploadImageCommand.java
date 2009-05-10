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

import static com.sirika.pymager.client.ImageReference.originalImage;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.pymager.client.BadUploadRequestException;
import com.sirika.pymager.client.ImageAlreadyExistsException;
import com.sirika.pymager.client.ImageFormat;
import com.sirika.pymager.client.ImageId;
import com.sirika.pymager.client.ImageReference;
import com.sirika.pymager.client.UnknownUploadFailureException;
import com.sirika.pymager.client.UrlGenerator;
import com.sirika.pymager.httpclienthelpers.InputStreamSourceBody;
import com.sirika.pymager.httpclienthelpers.RepeatableMultipartEntity;

public class UploadImageCommand {
    public static final String UPLOAD_PARAMETER_NAME = "file";
    private static final Logger logger = LoggerFactory.getLogger(UploadImageCommand.class);
    
    private HttpClient httpClient;
    private UrlGenerator urlGenerator;
    private ImageId imageId;
    private ImageFormat imageFormat;
    private InputStreamSource imageSource;
    
    public UploadImageCommand(HttpClient httpClient, UrlGenerator urlGenerator,
	    ImageId imageId, ImageFormat imageFormat,
	    InputStreamSource imageSource) {
	super();
	this.httpClient = httpClient;
	this.urlGenerator = urlGenerator;
	this.imageId = imageId;
	this.imageFormat = imageFormat;
	this.imageSource = imageSource;
    }

    
    public ImageReference execute() throws UnknownUploadFailureException{
	ImageReference imageReference = originalImage(this.imageId.toString());
	try {
	    HttpPost httpPost = createHttpPostFor(imageFormat, imageSource, imageReference);
	    HttpResponse response = httpClient.execute(httpPost);
	    handleUploadResponse(this.imageId, imageFormat, httpPost, response);
	} catch (IOException e) {
	    throw new UnknownUploadFailureException(this.imageId, imageFormat, e);
	}
	logger.debug("Upload of {} done successfully. Download can be achieved using Image Reference: {}", this.imageId, imageReference);
	return imageReference;
    }
    
    private void handleUploadResponse(ImageId id, ImageFormat imageFormat,HttpPost httpPost, HttpResponse response) throws IOException {
	logger.debug("Received status : {}", response.getStatusLine());
	handleErrors(id, imageFormat, httpPost, response);
	
	HttpEntity entity = response.getEntity();
	if(entity != null) {
	    entity.consumeContent();
	}
    }

    private void handleErrors(ImageId id, ImageFormat imageFormat,HttpPost httpPost, HttpResponse response) {
	try {
	    handleConflictError(id, imageFormat, httpPost, response); 
	    handleBadUploadRequestError(id, imageFormat, httpPost, response); 
	    handleUploadNon2xxError(id, imageFormat, httpPost, response); 
	} catch(RuntimeException e) {
	    httpPost.abort();
	    throw e;
	} 
    }
    
    private void handleBadUploadRequestError(ImageId id, ImageFormat imageFormat,HttpPost httpPost, HttpResponse response) {
	if(response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
	    throw new BadUploadRequestException(id, imageFormat, new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
	}
    }
    
    private void handleConflictError(ImageId id, ImageFormat imageFormat,HttpPost httpPost, HttpResponse response) {
	if(response.getStatusLine().getStatusCode() == HttpStatus.SC_CONFLICT) {
	    throw new ImageAlreadyExistsException(id, imageFormat, new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
	}
    }
    
    private void handleUploadNon2xxError(ImageId id, ImageFormat imageFormat,HttpPost httpPost, HttpResponse response) {
	if(response.getStatusLine().getStatusCode() >= 300) {
	    throw new UnknownUploadFailureException(id, imageFormat, new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
	}
    }

    private HttpPost createHttpPostFor(ImageFormat imageFormat, InputStreamSource imageSource, ImageReference imageReference) throws IOException {
	HttpPost httpPost = new HttpPost(urlGenerator.getImageResourceUrl(imageReference));
	httpPost.setEntity(uploadStreamEntity(imageSource, imageFormat));
	return httpPost;
    }

//    private InputStreamEntity uploadStreamEntity(InputStreamSource imageSource, ImageFormat imageFormat) throws IOException {
//	InputStreamEntity entity = new InputStreamEntity(imageSource.getInputStream(), -1);
//	entity.setChunked(true);
//	entity.setContentType(imageFormat.mimeType());
//	return entity;
//    }
    
    private HttpEntity uploadStreamEntity(InputStreamSource imageSource, ImageFormat imageFormat) throws IOException {
	MultipartEntity entity = new RepeatableMultipartEntity();
	entity.addPart(UPLOAD_PARAMETER_NAME, new InputStreamSourceBody(imageSource, imageFormat.mimeType(), UPLOAD_PARAMETER_NAME));
	return entity;
    }
}
