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
package com.sirika.pymager.api.jobs;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import com.sirika.pymager.api.ImageReference;
import com.sirika.pymager.api.ImageServer;
import com.sirika.pymager.api.ImageServerException;
import com.sirika.pymager.api.testhelpers.PictureStreamAssertionUtils;

/**
 * Downloads a picture and returns a {@link OperationStatus} that informs
 * whether the downloaded picture's content is what was expected
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 * 
 */
public class DownloadPictureJob implements Callable<OperationStatus> {
    private final static Logger logger = LoggerFactory
            .getLogger(DownloadPictureJob.class);
    private ImageServer imageServer;
    private ImageReference imageReference;
    private InputStreamSource expectedInputStreamSource;

    public DownloadPictureJob(ImageServer imageServer,
            ImageReference imageReference,
            InputStreamSource expectedInputStreamSource) {
        super();
        this.imageServer = imageServer;
        this.imageReference = imageReference;
        this.expectedInputStreamSource = expectedInputStreamSource;
    }

    public OperationStatus call() throws Exception {
        try {
            InputStreamSource source = imageServer
                    .downloadImage(imageReference);
            assertNotNull(source);
            if (!isSameStream(source)) {
                logger.warn("Stream is different");
                OutputStream os = new FileOutputStream(new File("/tmp/stream"));
                InputStream is = source.getInputStream();
                IOUtils.copy(is, os);
                IOUtils.closeQuietly(os);
                IOUtils.closeQuietly(is);
            }

            return isSameStream(source) ? OperationStatus.OK
                    : OperationStatus.KO;
        } catch (ImageServerException e) {
            logger.error("Unexpected error", e);
            return OperationStatus.KO;
        }
    }

    private boolean isSameStream(InputStreamSource source) throws IOException {
        return new PictureStreamAssertionUtils.PictureStreamAsserter(
                expectedInputStreamSource, source).isSameStream();
    }
}