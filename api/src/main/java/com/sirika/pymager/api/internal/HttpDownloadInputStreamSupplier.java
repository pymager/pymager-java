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
package com.sirika.pymager.api.internal;

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

import com.google.common.collect.ImmutableList;
import com.google.common.io.InputSupplier;
import com.sirika.hchelpers.client.DelegatingHttpErrorHandler;
import com.sirika.hchelpers.client.HttpClientTemplate;
import com.sirika.hchelpers.client.HttpErrorHandler;
import com.sirika.hchelpers.client.HttpErrorMatchers;
import com.sirika.hchelpers.client.HttpResponseCallback;
import com.sirika.pymager.api.ForbiddenRequestException;
import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.ResourceNotExistingException;
import com.sirika.pymager.api.UnknownGetFailureException;
import com.sirika.pymager.api.UrlGenerator;

public class HttpDownloadInputStreamSupplier implements InputSupplier<InputStream> {
    private final static Logger logger = LoggerFactory.getLogger(HttpDownloadInputStreamSupplier.class);
    private HttpClientTemplate httpClientTemplate;
    private HttpGet httpGet;
    private ImageReference imageReference;

    public HttpDownloadInputStreamSupplier(HttpClient httpClient, UrlGenerator urlGenerator, ImageReference imageReference) {
        super();
        this.httpClientTemplate = new HttpClientTemplate(httpClient);
        this.imageReference = imageReference;
        this.httpGet = new HttpGet(urlGenerator.getImageResourceUrl(imageReference));
    }

    private Iterable<HttpErrorHandler> httpErrorHandlers() {
        return ImmutableList.of(forbiddenErrorHandler(), notFoundHandler(),
                defaultHandler());
    }

    private HttpErrorHandler forbiddenErrorHandler() {
        return new DelegatingHttpErrorHandler(HttpErrorMatchers.statusCodeEquals(HttpStatus.SC_FORBIDDEN), new HttpResponseCallback() {
            public Object doWithHttpResponse(HttpResponse httpResponse) throws Exception {
                throw new ForbiddenRequestException(
                        new HttpResponseException(
                                httpResponse.getStatusLine().getStatusCode(), 
                                httpResponse.getStatusLine().getReasonPhrase()));
            }
        });
    }

    private HttpErrorHandler notFoundHandler() {
        return new DelegatingHttpErrorHandler(HttpErrorMatchers.statusCodeEquals(HttpStatus.SC_NOT_FOUND), new HttpResponseCallback() {
            public Object doWithHttpResponse(HttpResponse httpResponse) throws Exception {
                throw new ResourceNotExistingException(imageReference);
            }
        });
    }

    private HttpErrorHandler defaultHandler() {
        return new DelegatingHttpErrorHandler(HttpErrorMatchers.statusCodeGreaterOrEquals(300), new HttpResponseCallback() {
            public Object doWithHttpResponse(HttpResponse httpResponse) throws Exception {
                throw new UnknownGetFailureException(imageReference,
                        new HttpResponseException(httpResponse.getStatusLine()
                                .getStatusCode(), httpResponse.getStatusLine()
                                .getReasonPhrase()));
            }
        });
    }

    private InputStream generateInputStream(HttpEntity entity) throws IOException {
        if (entity != null) {
            return entity.getContent();
        } else {
            return null;
        }
    }

    public InputStream getInput() throws IOException {
        logger.debug("Generating InputStream for {}", imageReference);

        return (InputStream) this.httpClientTemplate.execute(this.httpGet,
                new HttpResponseCallback() {
                    public Object doWithHttpResponse(HttpResponse httpResponse)
                            throws Exception {
                        return generateInputStream(httpResponse.getEntity());
                    }
                }, httpErrorHandlers());

    }

}