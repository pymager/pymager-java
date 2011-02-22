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
package org.iglootools.pymager.api.impl;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.iglootools.hchelpers.core.DefaultHttpClientFactory;
import org.iglootools.pymager.api.ImageFormat;
import org.iglootools.pymager.api.ImageId;
import org.iglootools.pymager.api.ImageReference;
import org.iglootools.pymager.api.ImageServer;
import org.iglootools.pymager.api.ResourceNotExistingException;
import org.iglootools.pymager.api.UnknownDeleteFailureException;
import org.iglootools.pymager.api.UnknownGetFailureException;
import org.iglootools.pymager.api.UnknownUploadFailureException;
import org.iglootools.pymager.api.UrlGenerator;
import org.iglootools.pymager.api.internal.DeleteImageCommand;
import org.iglootools.pymager.api.internal.DownloadImageCommand;
import org.iglootools.pymager.api.internal.RESTfulUrlGenerator;
import org.iglootools.pymager.api.internal.UploadImageCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.InputSupplier;

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
        return new DownloadImageCommand(httpClient, urlGenerator, imageReference).execute();
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
