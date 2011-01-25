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
/**
 * 
 */
package com.sirika.pymager.api.impl;

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
import com.sirika.pymager.api.ForbiddenRequestException;
import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.ResourceNotExistingException;
import com.sirika.pymager.api.UnknownDownloadFailureException;
import com.sirika.pymager.api.UrlGenerator;

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