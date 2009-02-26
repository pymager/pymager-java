package com.sirika.imgserver.client;

import org.springframework.core.io.InputStreamSource;

/**
 * Represents the Image Server, and the operations we can call on it.
 * This is the starting point of Image Server's client.
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public interface ImageServer {
    String getDownloadUrl(ImageReference imageReference);
    InputStreamSource downloadImage(ImageReference imageReference) throws ResourceNotExistingException, UnknownFailureException;
    ImageReference uploadImage(ImageId id, InputStreamSource imageSource);
    void deleteImage(ImageId imageId);
    void destroy() throws Exception;
}
