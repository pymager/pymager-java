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
package com.sirika.imgserver.client.jobs;

import static com.sirika.imgserver.client.ImageFormat.JPEG;

import java.util.concurrent.Callable;

import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageId;
import com.sirika.imgserver.client.ImageServer;
import com.sirika.imgserver.client.ImageServerException;

/**
 * Upload a picture and returns a {@link OperationStatus} that informs whether the picture has
 * been successfully uploaded
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public class UploadPictureJob implements Callable<OperationStatus>  {
    private ImageServer imageServer;
    private ImageId imageId;
    private InputStreamSource inputStreamSource;
    
    public UploadPictureJob(ImageServer imageServer, ImageId imageId,InputStreamSource inputstreamSource) {
        super();
        this.imageServer = imageServer;
        this.imageId = imageId;
        this.inputStreamSource = inputstreamSource;
    }


    public OperationStatus call() throws Exception {
        try {
    	imageServer.uploadImage(imageId, JPEG, inputStreamSource);    
    	return OperationStatus.OK;
        } catch(ImageServerException e) {
    	return OperationStatus.KO;
        }
    }
}