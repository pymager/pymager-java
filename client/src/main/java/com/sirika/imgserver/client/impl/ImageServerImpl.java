package com.sirika.imgserver.client.impl;


import static com.sirika.imgserver.client.ImageReference.originalImage;
import static com.sirika.imgserver.httpclienthelpers.DefaultHttpClientFactory.defaultHttpClient;

import java.io.IOException;

import org.apache.commons.lang.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageFormat;
import com.sirika.imgserver.client.ImageId;
import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ImageServer;
import com.sirika.imgserver.client.ResourceNotExistingException;
import com.sirika.imgserver.client.UnknownDeleteFailureException;
import com.sirika.imgserver.client.UnknownDownloadFailureException;
import com.sirika.imgserver.client.UnknownUploadFailureException;
import com.sirika.imgserver.client.UrlGenerator;

public class ImageServerImpl implements ImageServer {
    private static final Logger logger = LoggerFactory.getLogger(ImageServerImpl.class);
    private UrlGenerator urlGenerator;
    private HttpClient httpClient;
    
    public ImageServerImpl(String baseImageServiceUrl) {
	this(defaultHttpClient(), defaultUrlGeneratorFor(baseImageServiceUrl));
    }

    public ImageServerImpl(UrlGenerator urlGenerator) {
	this(defaultHttpClient(), urlGenerator);
    }
    
    public ImageServerImpl(HttpClient httpClient, String baseImageServiceUrl) {
	this(httpClient, defaultUrlGeneratorFor(baseImageServiceUrl));
    }
    
    public ImageServerImpl(HttpClient httpClient, UrlGenerator urlGenerator) {
	Validate.notNull(urlGenerator);
	Validate.notNull(httpClient);
	this.urlGenerator = urlGenerator ;
	this.httpClient = httpClient;
	logger.info("Creating Image Server using URLGenerator [{}], HttpClient [{}]", urlGenerator, httpClient);
    }
 
    public void deleteImage(ImageId imageId) {
	logger.debug("Deleting Image [{}] ({})", imageId);
	try {
	    HttpDelete httpDelete = new HttpDelete(getImageResourceUrl(originalImage(imageId.toString())));
	    HttpResponse response = httpClient.execute(httpDelete);
	    handleDeleteResponse(imageId, httpDelete, response);
	} catch (IOException e) {
	    throw new UnknownDeleteFailureException(imageId, e);
	}

    }

    public InputStreamSource downloadImage(ImageReference imageReference) throws ResourceNotExistingException, UnknownDownloadFailureException{
	logger.debug("Downloading Image Reference [{}]", imageReference);
	return new HttpDownloadInputStreamSource(this, httpClient, imageReference);
    }

    public String getImageResourceUrl(ImageReference imageReference) {
	String url = urlGenerator.getImageResourceUrl(imageReference);
	logger.debug("getImageResourceUrl: generated URL : {}", url);
	return url;
    }

    public ImageReference uploadImage(ImageId id, ImageFormat imageFormat, InputStreamSource imageSource)  {
	logger.debug("Uploading Image [{}] ({})", id, imageFormat.mimeType());
	ImageReference imageReference = originalImage(id.toString());
	try {
	    HttpPost httpPost = createHttpPostFor(imageFormat, imageSource, imageReference);
	    HttpResponse response = httpClient.execute(httpPost);
	    handleUploadResponse(id, imageFormat, httpPost, response);
	} catch (IOException e) {
	    throw new UnknownUploadFailureException(id, imageFormat, e);
	}
	return imageReference;
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
    
    private void handleUploadResponse(ImageId id, ImageFormat imageFormat,HttpPost httpPost, HttpResponse response) throws IOException {
	logger.debug("Received status : {}", response.getStatusLine());
	handleUploadNon2xxError(id, imageFormat, httpPost, response);
	HttpEntity entity = response.getEntity();
	if(entity != null) {
	    entity.consumeContent();
	}
    }

    private void handleUploadNon2xxError(ImageId id, ImageFormat imageFormat,HttpPost httpPost, HttpResponse response) {
	if(response.getStatusLine().getStatusCode() >= 300) {
	    httpPost.abort();
	    throw new UnknownUploadFailureException(id, imageFormat, new HttpResponseException(response.getStatusLine().getStatusCode(), "Error while uploading"));
	}
    }

    private HttpPost createHttpPostFor(ImageFormat imageFormat, InputStreamSource imageSource, ImageReference imageReference) throws IOException {
	HttpPost httpPost = new HttpPost(getImageResourceUrl(imageReference));
	httpPost.setEntity(uploadStreamEntity(imageSource, imageFormat));
	return httpPost;
    }

    private InputStreamEntity uploadStreamEntity(InputStreamSource imageSource, ImageFormat imageFormat) throws IOException {
	InputStreamEntity entity = new InputStreamEntity(imageSource.getInputStream(), -1);
	entity.setChunked(true);
	entity.setContentType(imageFormat.mimeType());
	return entity;
    }
    
    public void destroy() throws Exception {
	this.httpClient.getConnectionManager().shutdown();
    }
    
    private static RESTfulUrlGenerator defaultUrlGeneratorFor(String baseImageServiceUrl) {
	Validate.notNull(baseImageServiceUrl);
	return new RESTfulUrlGenerator(baseImageServiceUrl);
    }
}
