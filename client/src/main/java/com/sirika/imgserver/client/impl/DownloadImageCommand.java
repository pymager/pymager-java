/**
 * ImgServer Java REST Client
 * Copyright (C) 2008 Sami Dalouche
 *
 * This file is part of ImgServer Java REST Client.
 *
 * ImgServer Java REST Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ImgServer Java REST Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ImgServer.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sirika.imgserver.client.impl;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ResourceNotExistingException;
import com.sirika.imgserver.client.UnknownDownloadFailureException;
import com.sirika.imgserver.client.UrlGenerator;

public class DownloadImageCommand {
    private static final Logger logger = LoggerFactory.getLogger(DownloadImageCommand.class);
    
    private HttpClient httpClient;
    private UrlGenerator urlGenerator;
    private ImageReference imageReference;
    
    public DownloadImageCommand(HttpClient httpClient,UrlGenerator urlGenerator, ImageReference imageReference) {
	super();
	this.httpClient = httpClient;
	this.urlGenerator = urlGenerator;
	this.imageReference = imageReference;
    }
    
    public InputStreamSource execute() throws ResourceNotExistingException, UnknownDownloadFailureException{
	return new HttpDownloadInputStreamSource(this.httpClient, this.urlGenerator, this.imageReference);
    }
    
}
