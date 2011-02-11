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

import static com.sirika.pymager.api.ImageReference.originalImage;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.io.InputSupplier;
import com.sirika.hchelpers.core.mime.InputSupplierSourceBody;
import com.sirika.hchelpers.core.mime.RepeatableMultipartEntity;
import com.sirika.hchelpers.java.DelegatingHttpErrorHandler;
import com.sirika.hchelpers.java.HttpClientTemplate;
import com.sirika.hchelpers.java.HttpErrorHandler;
import com.sirika.hchelpers.java.HttpErrorMatchers;
import com.sirika.hchelpers.java.HttpResponseCallback;
import com.sirika.pymager.api.BadUploadRequestException;
import com.sirika.pymager.api.ImageAlreadyExistsException;
import com.sirika.pymager.api.ImageFormat;
import com.sirika.pymager.api.ImageId;
import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.UnknownUploadFailureException;
import com.sirika.pymager.api.UrlGenerator;

public class UploadImageCommand {
    public static final String UPLOAD_PARAMETER_NAME = "file";
    private static final Logger logger = LoggerFactory.getLogger(UploadImageCommand.class);

    private HttpClientTemplate httpClientTemplate;
    private UrlGenerator urlGenerator;
    private ImageId imageId;
    private ImageFormat imageFormat;
    private InputSupplier<InputStream> imageSource;

    public UploadImageCommand(HttpClient httpClient, UrlGenerator urlGenerator,
            ImageId imageId, ImageFormat imageFormat,
            InputSupplier<InputStream> imageSource) {
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
            httpPost = createHttpPostFor(imageFormat, imageSource, imageReference);
        } catch (IOException e) {
            throw new UnknownUploadFailureException(this.imageId, imageFormat, e);
        }

        this.httpClientTemplate.executeWithoutResult(httpPost, httpErrorHandlers());

        logger.debug("Upload of {} done successfully. Download can be achieved using Image Reference: {}",
                     this.imageId, 
                     imageReference);
        return imageReference;
    }

    private Iterable<HttpErrorHandler> httpErrorHandlers() {
        return ImmutableList.of(conflictErrorHandler(),
                badUploadRequestErrorHandler(), defaultHandler());
    }

    private HttpErrorHandler conflictErrorHandler() {
        return new DelegatingHttpErrorHandler(HttpErrorMatchers.statusCodeEquals(HttpStatus.SC_CONFLICT), new HttpResponseCallback() {
            public Object doWithHttpResponse(HttpResponse httpResponse) throws Exception {
                throw new ImageAlreadyExistsException(imageId, imageFormat,
                        new HttpResponseException(httpResponse.getStatusLine()
                                .getStatusCode(), httpResponse.getStatusLine()
                                .getReasonPhrase()));
            }
        });
    }

    private HttpErrorHandler badUploadRequestErrorHandler() {
        return new DelegatingHttpErrorHandler(HttpErrorMatchers.statusCodeEquals(HttpStatus.SC_BAD_REQUEST), new HttpResponseCallback() {
            public Object doWithHttpResponse(HttpResponse httpResponse) throws Exception {
                throw new BadUploadRequestException(imageId, imageFormat,
                        new HttpResponseException(httpResponse.getStatusLine()
                                .getStatusCode(), httpResponse.getStatusLine()
                                .getReasonPhrase()));
            }
        });
    }

    private HttpErrorHandler defaultHandler() {
        return new DelegatingHttpErrorHandler(HttpErrorMatchers.statusCodeGreaterOrEquals(300), new HttpResponseCallback() {
            public Object doWithHttpResponse(HttpResponse httpResponse) throws Exception {
                throw new UnknownUploadFailureException(imageId, imageFormat,
                        new HttpResponseException(httpResponse.getStatusLine()
                                .getStatusCode(), httpResponse.getStatusLine()
                                .getReasonPhrase()));
            }
        });
    }

    private HttpPost createHttpPostFor(ImageFormat imageFormat,InputSupplier<InputStream> imageSource, ImageReference imageReference) throws IOException {
        HttpPost httpPost = new HttpPost(urlGenerator.getImageResourceUrl(imageReference));
        httpPost.setEntity(uploadStreamEntity(imageSource, imageFormat));
        return httpPost;
    }

    private HttpEntity uploadStreamEntity(InputSupplier<InputStream> imageSource, ImageFormat imageFormat) throws IOException {
        MultipartEntity entity = new RepeatableMultipartEntity();
        entity.addPart(UPLOAD_PARAMETER_NAME, new InputSupplierSourceBody(imageSource, imageFormat.mimeType(), UPLOAD_PARAMETER_NAME));
        return entity;
    }
}
