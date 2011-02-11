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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.sirika.hchelpers.java.DelegatingHttpErrorHandler;
import com.sirika.hchelpers.java.HttpClientTemplate;
import com.sirika.hchelpers.java.HttpErrorHandler;
import com.sirika.hchelpers.java.HttpErrorMatchers;
import com.sirika.hchelpers.java.HttpResponseCallback;
import com.sirika.pymager.api.ImageId;
import com.sirika.pymager.api.UnknownDeleteFailureException;
import com.sirika.pymager.api.UrlGenerator;

public class DeleteImageCommand {
    private static final Logger logger = LoggerFactory.getLogger(DeleteImageCommand.class);

    private HttpClientTemplate httpClientTemplate;
    private UrlGenerator urlGenerator;
    private ImageId imageId;

    public DeleteImageCommand(HttpClient httpClient, UrlGenerator urlGenerator,
            ImageId imageId) {
        super();
        this.httpClientTemplate = new HttpClientTemplate(httpClient);
        this.urlGenerator = urlGenerator;
        this.imageId = imageId;
    }

    public void execute() throws UnknownDeleteFailureException {
        HttpDelete httpDelete = new HttpDelete(urlGenerator.getImageResourceUrl(originalImage(imageId.toString())));

        this.httpClientTemplate.executeWithoutResult(httpDelete,httpErrorHandlers());
    }

    private Iterable<HttpErrorHandler> httpErrorHandlers() {
        return ImmutableList.of(defaultHandler());
    }

    private HttpErrorHandler defaultHandler() {
        return new DelegatingHttpErrorHandler(HttpErrorMatchers.statusCodeGreaterOrEquals(300), new HttpResponseCallback() {
            public Object doWithHttpResponse(HttpResponse httpResponse) throws Exception {
                throw new UnknownDeleteFailureException(imageId,
                        new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), "Error while uploading"));
            }
        });
    }

}
