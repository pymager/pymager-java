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


import static com.sirika.imgserver.httpclienthelpers.DefaultHttpClientFactory.defaultHttpClient;

import org.apache.commons.lang.Validate;
import org.apache.http.client.HttpClient;
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

public class HttpImageServer implements ImageServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpImageServer.class);
    private UrlGenerator urlGenerator;
    private HttpClient httpClient;
    
    public HttpImageServer(String baseImageServiceUrl) {
	this(defaultHttpClient(), defaultUrlGeneratorFor(baseImageServiceUrl));
    }

    public HttpImageServer(UrlGenerator urlGenerator) {
	this(defaultHttpClient(), urlGenerator);
    }
    
    public HttpImageServer(HttpClient httpClient, String baseImageServiceUrl) {
	this(httpClient, defaultUrlGeneratorFor(baseImageServiceUrl));
    }
    
    public HttpImageServer(HttpClient httpClient, UrlGenerator urlGenerator) {
	logger.info("Creating Image Server using , HttpClient [{}], URLGenerator [{}]", httpClient, urlGenerator);
	Validate.notNull(urlGenerator);
	Validate.notNull(httpClient);
	this.urlGenerator = urlGenerator ;
	this.httpClient = httpClient;
    }
 
    public void deleteImage(ImageId imageId) throws UnknownDeleteFailureException{
	logger.debug("Deleting Image: {}", imageId);
	new DeleteImageCommand(httpClient, urlGenerator, imageId).execute();

    }

    public InputStreamSource downloadImage(ImageReference imageReference) throws ResourceNotExistingException, UnknownDownloadFailureException{
	logger.debug("Downloading Image Reference: {}", imageReference);
	return new HttpDownloadInputStreamSource(httpClient, urlGenerator, imageReference);
    }

    public String getImageResourceUrl(ImageReference imageReference) {
	String url = urlGenerator.getImageResourceUrl(imageReference);
	logger.debug("getImageResourceUrl: generated URL : {}", url);
	return url;
    }

    public ImageReference uploadImage(ImageId imageId, ImageFormat imageFormat, InputStreamSource imageSource)  throws UnknownUploadFailureException{
	logger.debug("Uploading Image: {} ({})", imageId, imageFormat.mimeType());
	return new UploadImageCommand(httpClient, urlGenerator, imageId, imageFormat, imageSource).execute();
    }
 
    public void destroy() throws Exception {
	this.httpClient.getConnectionManager().shutdown();
    }
    
    private static RESTfulUrlGenerator defaultUrlGeneratorFor(String baseImageServiceUrl) {
	Validate.notNull(baseImageServiceUrl);
	return new RESTfulUrlGenerator(baseImageServiceUrl);
    }
}
