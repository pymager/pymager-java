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
package com.sirika.imgserver.client.impl;

import com.sirika.imgserver.client.ImageReference;
import com.sirika.imgserver.client.UrlGenerator;

/**
 * Generates URLs such as :
 * <ul>
 *   <li>http://baseurl:8000/derived/mypic-800x600.png</li>
 *   <li>http://baseurl:8000/original/mypic</li>
 * </ul>
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
class RESTfulUrlGenerator implements UrlGenerator {

    private String baseImageServiceUrl;

    public RESTfulUrlGenerator(String baseImageServiceUrl) {
	super();
	this.baseImageServiceUrl = baseImageServiceUrl;
    }
    
    /* (non-Javadoc)
     * @see com.sirika.imgserver.client.UrlGenerator#urlFor(com.sirika.imgserver.client.ImageReference)
     */
    public String getImageResourceUrl(ImageReference imageReference) {
	if(imageReference.isDerived()) {
	    return String.format("%s/derived/%s-%sx%s.%s", 
		    this.baseImageServiceUrl, 
		    imageReference.getId(),
		    imageReference.getRescaling().getWidth(), 
		    imageReference.getRescaling().getHeight(), 
		    imageReference.getImageFormat().extension());
	} else {
	    return String.format("%s/original/%s", 
		    this.baseImageServiceUrl,
		    imageReference.getId());
	}
    }

    public String toString() {
	return String.format("RESTful URL Generator pointing to %s", baseImageServiceUrl);
    }
    
    
}
