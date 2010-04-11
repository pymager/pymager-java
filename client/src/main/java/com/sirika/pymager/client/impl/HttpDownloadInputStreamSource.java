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
/**
 * 
 */
package com.sirika.pymager.client.impl;

import static com.sirika.httpclienthelpers.template.AbstractHttpErrorHandler.statusCodeEquals;
import static com.sirika.httpclienthelpers.template.AbstractHttpErrorHandler.statusCodeGreaterOrEquals;

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

import com.google.common.collect.ImmutableList;
import com.sirika.httpclienthelpers.template.AbstractHttpErrorHandler;
import com.sirika.httpclienthelpers.template.HttpClientTemplate;
import com.sirika.httpclienthelpers.template.HttpErrorHandler;
import com.sirika.httpclienthelpers.template.HttpResponseCallback;
import com.sirika.pymager.client.ForbiddenRequestException;
import com.sirika.pymager.client.ImageReference;
import com.sirika.pymager.client.ResourceNotExistingException;
import com.sirika.pymager.client.UnknownDownloadFailureException;
import com.sirika.pymager.client.UrlGenerator;

class HttpDownloadInputStreamSource implements InputStreamSource {
    private final static Logger logger = LoggerFactory
            .getLogger(HttpDownloadInputStreamSource.class);
    private HttpClientTemplate httpClientTemplate;
    private HttpGet httpGet;
    private ImageReference imageReference;

    public HttpDownloadInputStreamSource(HttpClient httpClient,
            UrlGenerator urlGenerator, ImageReference imageReference) {
        super();
        this.httpClientTemplate = new HttpClientTemplate(httpClient);
        this.imageReference = imageReference;
        this.httpGet = new HttpGet(urlGenerator
                .getImageResourceUrl(imageReference));
    }

    public InputStream getInputStream() throws IOException,
            ResourceNotExistingException, UnknownDownloadFailureException {
        logger.debug("Generating InputStream for {}", imageReference);

        return (InputStream) this.httpClientTemplate.execute(this.httpGet,
                new HttpResponseCallback() {
                    public Object doWithHttpResponse(HttpResponse httpResponse)
                            throws Exception {
                        return generateInputStream(httpResponse.getEntity());
                    }
                }, httpErrorHandlers());

    }

    private Iterable<HttpErrorHandler> httpErrorHandlers() {
        return ImmutableList.of(forbiddenErrorHandler(), notFoundHandler(),
                defaultHandler());
    }

    private HttpErrorHandler forbiddenErrorHandler() {
        return new AbstractHttpErrorHandler(
                statusCodeEquals(HttpStatus.SC_FORBIDDEN)) {
            public void handle(HttpResponse response) throws Exception {
                throw new ForbiddenRequestException(new HttpResponseException(
                        response.getStatusLine().getStatusCode(), response
                                .getStatusLine().getReasonPhrase()));
            }
        };
    }

    private HttpErrorHandler notFoundHandler() {
        return new AbstractHttpErrorHandler(
                statusCodeEquals(HttpStatus.SC_NOT_FOUND)) {
            public void handle(HttpResponse response) throws Exception {
                throw new ResourceNotExistingException(imageReference);
            }
        };
    }

    private HttpErrorHandler defaultHandler() {
        return new AbstractHttpErrorHandler(statusCodeGreaterOrEquals(300)) {
            public void handle(HttpResponse response) throws Exception {
                throw new UnknownDownloadFailureException(imageReference,
                        new HttpResponseException(response.getStatusLine()
                                .getStatusCode(), response.getStatusLine()
                                .getReasonPhrase()));
            }
        };
    }

    private InputStream generateInputStream(HttpEntity entity)
            throws IOException {
        if (entity != null) {
            return entity.getContent();
        } else {
            return null;
        }
    }

}