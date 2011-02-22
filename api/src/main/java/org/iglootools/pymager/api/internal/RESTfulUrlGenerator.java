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
package org.iglootools.pymager.api.internal;

import org.iglootools.pymager.api.ImageReference;
import org.iglootools.pymager.api.UrlGenerator;

/**
 * Generates URLs such as :
 * <ul>
 * <li>http://baseurl:8000/derived/mypic-800x600.png</li>
 * <li>http://baseurl:8000/original/mypic</li>
 * </ul>
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 * 
 */
public class RESTfulUrlGenerator implements UrlGenerator {

    private String baseImageServiceUrl;

    public RESTfulUrlGenerator(String baseImageServiceUrl) {
        super();
        this.baseImageServiceUrl = baseImageServiceUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.iglootoolsools.imgserver.client.UrlGenerator#urlorg.iglootoolslootools.imgserver.
     * client.ImageReference)
     */
    public String getImageResourceUrl(ImageReference imageReference) {
        if (imageReference.isDerived()) {
            return String.format("%s/derived/%s-%sx%s.%s",
                    this.baseImageServiceUrl, imageReference.getId(),
                    imageReference.getRescaling().getWidth(), imageReference
                            .getRescaling().getHeight(), imageReference
                            .getImageFormat().extension());
        } else {
            return String.format("%s/original/%s", this.baseImageServiceUrl, imageReference.getId());
        }
    }

    public String toString() {
        return String.format("RESTful URL Generator pointing to %s", baseImageServiceUrl);
    }

}
