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
import com.sirika.hchelpers.java.DelegatingHttpErrorHandler;
import com.sirika.hchelpers.java.HttpClientTemplate;
import com.sirika.hchelpers.java.HttpErrorHandler;
import com.sirika.hchelpers.java.HttpErrorMatchers;
import com.sirika.hchelpers.java.HttpInputSupplier;
import com.sirika.hchelpers.java.HttpResponseCallback;
import com.sirika.pymager.api.ForbiddenRequestException;
import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.ResourceNotExistingException;
import com.sirika.pymager.api.UnknownGetFailureException;
import com.sirika.pymager.api.UrlGenerator;

public class DownloadImageCommand {
    private static final Logger logger = LoggerFactory.getLogger(DownloadImageCommand.class);

    private HttpClient httpClient;
    private UrlGenerator urlGenerator;
    private ImageReference imageReference;

    public DownloadImageCommand(HttpClient httpClient,
            UrlGenerator urlGenerator, ImageReference imageReference) {
        super();
        this.httpClient = httpClient;
        this.urlGenerator = urlGenerator;
        this.imageReference = imageReference;
    }

    public InputSupplier<InputStream> execute() throws ResourceNotExistingException, UnknownGetFailureException {
        return inputStreamSupplier();
    }
    
    private InputSupplier<InputStream> inputStreamSupplier() {
        return new HttpInputSupplier(new HttpClientTemplate(httpClient), new HttpGet(urlGenerator.getImageResourceUrl(imageReference)),httpErrorHandlers());
    }
    
    private Iterable<HttpErrorHandler> httpErrorHandlers() {
        return ImmutableList.of(forbiddenErrorHandler(), notFoundHandler(),defaultHandler());
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
}
