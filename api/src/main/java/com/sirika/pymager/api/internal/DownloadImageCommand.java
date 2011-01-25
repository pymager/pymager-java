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

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.InputSupplier;
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
        return new HttpDownloadInputStreamSupplier(this.httpClient, this.urlGenerator, this.imageReference);
    }

}
