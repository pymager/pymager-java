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
package org.iglootools.pymager.api.internal;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.iglootools.hchelpers.java.DelegatingHttpErrorHandler;
import org.iglootools.hchelpers.java.HttpClientTemplate;
import org.iglootools.hchelpers.java.HttpErrorHandler;
import org.iglootools.hchelpers.java.HttpErrorMatchers;
import org.iglootools.hchelpers.java.HttpInputSupplier;
import org.iglootools.hchelpers.java.HttpResponseCallback;
import org.iglootools.pymager.api.ForbiddenRequestException;
import org.iglootools.pymager.api.ImageReference;
import org.iglootools.pymager.api.ResourceNotExistingException;
import org.iglootools.pymager.api.UnknownGetFailureException;
import org.iglootools.pymager.api.UrlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.io.InputSupplier;

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
        return new DelegatingHttpErrorHandler(HttpErrorMatchers.statusCodeEquals(HttpStatus.SC_FORBIDDEN), new HttpResponseCallback<Void>() {
            public Void doWithHttpResponse(HttpResponse httpResponse) throws Exception {
                throw new ForbiddenRequestException(
                        new HttpResponseException(
                                httpResponse.getStatusLine().getStatusCode(), 
                                httpResponse.getStatusLine().getReasonPhrase()));
            }
        });
    }

    private HttpErrorHandler notFoundHandler() {
        return new DelegatingHttpErrorHandler(HttpErrorMatchers.statusCodeEquals(HttpStatus.SC_NOT_FOUND), new HttpResponseCallback<Void>() {
            public Void doWithHttpResponse(HttpResponse httpResponse) throws Exception {
                throw new ResourceNotExistingException(imageReference);
            }
        });
    }

    private HttpErrorHandler defaultHandler() {
        return new DelegatingHttpErrorHandler(HttpErrorMatchers.statusCodeGreaterOrEquals(300), new HttpResponseCallback<Void>() {
            public Void doWithHttpResponse(HttpResponse httpResponse) throws Exception {
                throw new UnknownGetFailureException(imageReference,
                        new HttpResponseException(httpResponse.getStatusLine()
                                .getStatusCode(), httpResponse.getStatusLine()
                                .getReasonPhrase()));
            }
        });
    }
}
