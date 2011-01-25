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
package com.sirika.pymager.api.impl;

import static com.sirika.httpclienthelpers.template.AbstractHttpErrorHandler.statusCodeEquals;
import static com.sirika.httpclienthelpers.template.AbstractHttpErrorHandler.statusCodeGreaterOrEquals;
import static com.sirika.pymager.api.ImageReference.originalImage;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.google.common.collect.ImmutableList;
import com.sirika.httpclienthelpers.springframework.InputStreamSourceBody;
import com.sirika.httpclienthelpers.springframework.RepeatableMultipartEntity;
import com.sirika.httpclienthelpers.template.AbstractHttpErrorHandler;
import com.sirika.httpclienthelpers.template.HttpClientTemplate;
import com.sirika.httpclienthelpers.template.HttpErrorHandler;
import com.sirika.pymager.api.BadUploadRequestException;
import com.sirika.pymager.api.ImageAlreadyExistsException;
import com.sirika.pymager.api.ImageFormat;
import com.sirika.pymager.api.ImageId;
import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.UnknownUploadFailureException;
import com.sirika.pymager.api.UrlGenerator;

public class UploadImageCommand {
    public static final String UPLOAD_PARAMETER_NAME = "file";
    private static final Logger logger = LoggerFactory
            .getLogger(UploadImageCommand.class);

    private HttpClientTemplate httpClientTemplate;
    private UrlGenerator urlGenerator;
    private ImageId imageId;
    private ImageFormat imageFormat;
    private InputStreamSource imageSource;

    public UploadImageCommand(HttpClient httpClient, UrlGenerator urlGenerator,
            ImageId imageId, ImageFormat imageFormat,
            InputStreamSource imageSource) {
        super();
        this.httpClientTemplate = new HttpClientTemplate(httpClient);
        this.urlGenerator = urlGenerator;
        this.imageId = imageId;
        this.imageFormat = imageFormat;
        this.imageSource = imageSource;
    }

    public ImageReference execute() throws UnknownUploadFailureException {
        ImageReference imageReference = originalImage(this.imageId.toString());
        HttpPost httpPost = null;
        try {
            httpPost = createHttpPostFor(imageFormat, imageSource,
                    imageReference);
        } catch (IOException e) {
            throw new UnknownUploadFailureException(this.imageId, imageFormat,
                    e);
        }

        this.httpClientTemplate.executeWithoutResult(httpPost,
                httpErrorHandlers());

        logger
                .debug(
                        "Upload of {} done successfully. Download can be achieved using Image Reference: {}",
                        this.imageId, imageReference);
        return imageReference;
    }

    private Iterable<HttpErrorHandler> httpErrorHandlers() {
        return ImmutableList.of(conflictErrorHandler(),
                badUploadRequestErrorHandler(), defaultHandler());
    }

    private HttpErrorHandler conflictErrorHandler() {
        return new AbstractHttpErrorHandler(
                statusCodeEquals(HttpStatus.SC_CONFLICT)) {
            public void handle(HttpResponse response) throws Exception {
                throw new ImageAlreadyExistsException(imageId, imageFormat,
                        new HttpResponseException(response.getStatusLine()
                                .getStatusCode(), response.getStatusLine()
                                .getReasonPhrase()));
            }
        };
    }

    private HttpErrorHandler badUploadRequestErrorHandler() {
        return new AbstractHttpErrorHandler(
                statusCodeEquals(HttpStatus.SC_BAD_REQUEST)) {
            public void handle(HttpResponse response) throws Exception {
                throw new BadUploadRequestException(imageId, imageFormat,
                        new HttpResponseException(response.getStatusLine()
                                .getStatusCode(), response.getStatusLine()
                                .getReasonPhrase()));
            }
        };
    }

    private HttpErrorHandler defaultHandler() {
        return new AbstractHttpErrorHandler(statusCodeGreaterOrEquals(300)) {
            public void handle(HttpResponse response) throws Exception {
                throw new UnknownUploadFailureException(imageId, imageFormat,
                        new HttpResponseException(response.getStatusLine()
                                .getStatusCode(), response.getStatusLine()
                                .getReasonPhrase()));
            }
        };
    }

    private HttpPost createHttpPostFor(ImageFormat imageFormat,
            InputStreamSource imageSource, ImageReference imageReference)
            throws IOException {
        HttpPost httpPost = new HttpPost(urlGenerator
                .getImageResourceUrl(imageReference));
        httpPost.setEntity(uploadStreamEntity(imageSource, imageFormat));
        return httpPost;
    }

    private HttpEntity uploadStreamEntity(InputStreamSource imageSource,
            ImageFormat imageFormat) throws IOException {
        MultipartEntity entity = new RepeatableMultipartEntity();
        entity.addPart(UPLOAD_PARAMETER_NAME, new InputStreamSourceBody(
                imageSource, imageFormat.mimeType(), UPLOAD_PARAMETER_NAME));
        return entity;
    }
}
