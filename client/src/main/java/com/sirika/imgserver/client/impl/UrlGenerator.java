package com.sirika.imgserver.client.impl;

import com.sirika.imgserver.client.ImageReference;

/**
 * Generates URLs to access the resources references by {@link ImageReference}
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
interface UrlGenerator {
    public abstract String urlFor(ImageReference imageReference);

}