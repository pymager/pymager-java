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