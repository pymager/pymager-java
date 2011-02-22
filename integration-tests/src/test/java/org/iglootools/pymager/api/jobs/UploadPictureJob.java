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
package org.iglootools.pymager.api.jobs;

import static org.iglootools.pymager.api.ImageFormat.JPEG;

import java.io.InputStream;
import java.util.concurrent.Callable;

import org.iglootools.pymager.api.ImageId;
import org.iglootools.pymager.api.ImageServer;
import org.iglootools.pymager.api.ImageServerException;

import com.google.common.io.InputSupplier;

/**
 * Upload a picture and returns a {@link OperationStatus} that informs whether
 * the picture has been successfully uploaded
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 * 
 */
public class UploadPictureJob implements Callable<OperationStatus> {
    private ImageServer imageServer;
    private ImageId imageId;
    private InputSupplier<InputStream> inputStreamSource;

    public UploadPictureJob(ImageServer imageServer, ImageId imageId,InputSupplier<InputStream> inputstreamSource) {
        super();
        this.imageServer = imageServer;
        this.imageId = imageId;
        this.inputStreamSource = inputstreamSource;
    }

    public OperationStatus call() throws Exception {
        try {
            imageServer.uploadImage(imageId, JPEG, inputStreamSource);
            return OperationStatus.OK;
        } catch (ImageServerException e) {
            return OperationStatus.KO;
        }
    }
}