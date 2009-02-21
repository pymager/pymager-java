package com.sirika.imgserver.client;

/**
 * Generates URLs to access the resources references by {@link ImageReference}
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public interface UrlGenerator {
    public abstract String urlFor(ImageReference imageReference);

}