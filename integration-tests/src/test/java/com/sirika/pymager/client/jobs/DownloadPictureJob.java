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
package com.sirika.pymager.client.jobs;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Callable;

import org.springframework.core.io.InputStreamSource;

import com.sirika.pymager.client.ImageReference;
import com.sirika.pymager.client.ImageServer;
import com.sirika.pymager.client.ImageServerException;
import com.sirika.pymager.client.testhelpers.PictureStreamAssertionUtils;

/**
 * Downloads a picture and returns a {@link OperationStatus} that informs
 * whether the downloaded picture's content is what was expected
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public class DownloadPictureJob implements Callable<OperationStatus>  {
    private ImageServer imageServer;
    private ImageReference imageReference;
    private InputStreamSource expectedInputStreamSource;
    
    public DownloadPictureJob(ImageServer imageServer,ImageReference imageReference, InputStreamSource expectedInputStreamSource) {
        super();
        this.imageServer = imageServer;
        this.imageReference = imageReference;
        this.expectedInputStreamSource = expectedInputStreamSource;
    }

    public OperationStatus call() throws Exception {
        try {
    	InputStreamSource source = imageServer.downloadImage(imageReference);
    	assertNotNull(source);
    	return new PictureStreamAssertionUtils.PictureStreamAsserter(expectedInputStreamSource, source).isSameStream() ? OperationStatus.OK : OperationStatus.KO;
        } catch(ImageServerException e) {
    	return OperationStatus.KO;
        }
    }
}