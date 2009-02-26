package com.sirika.imgserver.client.impl;


import static com.sirika.imgserver.httpclienthelpers.DefaultHttpClientFactory.defaultHttpClient;

import org.apache.commons.lang.Validate;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageId;
import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ImageServer;
import com.sirika.imgserver.client.ResourceNotExistingException;
import com.sirika.imgserver.client.UnknownFailureException;

public class ImageServerImpl implements ImageServer {
    private static final Logger logger = LoggerFactory.getLogger(ImageServerImpl.class);
    private UrlGenerator urlGenerator;
    private HttpClient httpClient;
    
    public ImageServerImpl(String baseImageServiceUrl) {
	this.urlGenerator = defaultUrlGeneratorFor(baseImageServiceUrl);
	this.httpClient = defaultHttpClient();
	logCreation();
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
	logCreation();
    }

    private void logCreation() {
	logger.info("Creating Image Server using URLGenerator [{}], HttpClient [{}]", urlGenerator, httpClient);
    }
   
    private static RESTfulUrlGenerator defaultUrlGeneratorFor(String baseImageServiceUrl) {
	Validate.notNull(baseImageServiceUrl);
	return new RESTfulUrlGenerator(baseImageServiceUrl);
    }
    
    public void deleteImage(ImageId imageId) {
	// TODO Auto-generated method stub

    }

    public InputStreamSource downloadImage(ImageReference imageReference) throws ResourceNotExistingException, UnknownFailureException{
	logger.debug("Generating InputStreamSource for Image Reference [{}]", imageReference);
	return new HttpDownloadInputStreamSource(this, httpClient, imageReference);
    }

    public String getDownloadUrl(ImageReference imageReference) {
	String url = urlGenerator.urlFor(imageReference);
	if(logger.isDebugEnabled()) {
	    logger.debug("getDownloadUrl: generated URL : {}", url);
	}
	return url;
    }

    public ImageReference uploadImage(ImageId id, InputStreamSource imageSource) {
	return null;
    }
    
    public void destroy() throws Exception {
	this.httpClient.getConnectionManager().shutdown();
    }
}
