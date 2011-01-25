/**
 * Copyright 2009 Sami Dalouche
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sirika.pymager.api.impl;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.InputSupplier;
import com.sirika.hchelpers.client.DefaultHttpClientFactory;
import com.sirika.pymager.api.ImageFormat;
import com.sirika.pymager.api.ImageId;
import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.ImageServer;
import com.sirika.pymager.api.ResourceNotExistingException;
import com.sirika.pymager.api.UnknownDeleteFailureException;
import com.sirika.pymager.api.UnknownGetFailureException;
import com.sirika.pymager.api.UnknownUploadFailureException;
import com.sirika.pymager.api.UrlGenerator;
import com.sirika.pymager.api.internal.DeleteImageCommand;
import com.sirika.pymager.api.internal.HttpDownloadInputStreamSupplier;
import com.sirika.pymager.api.internal.RESTfulUrlGenerator;
import com.sirika.pymager.api.internal.UploadImageCommand;

public class HttpImageServer implements ImageServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpImageServer.class);
    private UrlGenerator urlGenerator;
    private HttpClient httpClient;

    /**
     * Instanciates the server with a {@link RESTfulUrlGenerator} and the given base URL
     * 
     * @param baseImageServiceUrl
     */
    public HttpImageServer(String baseImageServiceUrl) {
        this(DefaultHttpClientFactory.defaultHttpClient(), defaultUrlGeneratorFor(baseImageServiceUrl));
    }

    public HttpImageServer(UrlGenerator urlGenerator) {
        this(DefaultHttpClientFactory.defaultHttpClient(), urlGenerator);
    }

    public HttpImageServer(String baseImageServiceUrl, HttpClient httpClient) {
        this(httpClient, defaultUrlGeneratorFor(baseImageServiceUrl));
    }

    public HttpImageServer(HttpClient httpClient, UrlGenerator urlGenerator) {
        logger.info("Creating Image Server using , HttpClient [{}], URLGenerator [{}]", httpClient, urlGenerator);
        Preconditions.checkArgument(urlGenerator != null, "urlGenerator is required");
        Preconditions.checkArgument(httpClient != null, "httpClient is required");
        
        this.urlGenerator = urlGenerator;
        this.httpClient = httpClient;
    }

    public void deleteImage(ImageId imageId) throws UnknownDeleteFailureException {
        logger.debug("Deleting Image: {}", imageId);
        new DeleteImageCommand(httpClient, urlGenerator, imageId).execute();

    }

    public InputSupplier<InputStream> downloadImage(ImageReference imageReference) throws ResourceNotExistingException, UnknownGetFailureException {
        logger.debug("Downloading Image Reference: {}", imageReference);
        return new HttpDownloadInputStreamSupplier(httpClient, urlGenerator, imageReference);
    }

    public String getImageResourceUrl(ImageReference imageReference) {
        String url = urlGenerator.getImageResourceUrl(imageReference);
        logger.debug("getImageResourceUrl: generated URL : {}", url);
        return url;
    }

    public ImageReference uploadImage(ImageId imageId, ImageFormat imageFormat, InputSupplier<InputStream> imageSource) throws UnknownUploadFailureException {
        logger.debug("Uploading Image: {} ({})", imageId, imageFormat.mimeType());
        return new UploadImageCommand(httpClient, urlGenerator, imageId, imageFormat, imageSource).execute();
    }

    public void destroy() throws Exception {
        this.httpClient.getConnectionManager().shutdown();
    }

    private static RESTfulUrlGenerator defaultUrlGeneratorFor( String baseImageServiceUrl) {
        Preconditions.checkArgument(baseImageServiceUrl != null, "baseImageServiceUrl is required");
        return new RESTfulUrlGenerator(baseImageServiceUrl);
    }
}
