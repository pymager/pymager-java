package com.sirika.imgserver.client;

import org.springframework.core.io.InputStreamSource;

/**
 * Represents the Image Server, and the operations we can call on it.
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public interface ImageServer {
    String downloadUrl(ImageReference imageReference);
    InputStreamSource downloadImage(ImageReference imageReference);
    ImageReference uploadImage(ImageId id, InputStreamSource imageSource);
    void deleteImage(ImageId imageId);
}
