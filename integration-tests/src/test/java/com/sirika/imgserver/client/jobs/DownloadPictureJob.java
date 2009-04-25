package com.sirika.imgserver.client.jobs;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Callable;

import org.springframework.core.io.InputStreamSource;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.ImageServer;
import com.sirika.imgserver.client.ImageServerException;
import com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils;

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