/**
 * PyMager Java REST Client
 * Copyright (C) 2008 Sami Dalouche
 *
 * This file is part of PyMager Java REST Client.
 *
 * PyMager Java REST Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PyMager Java REST Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PyMager Java REST Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sirika.pymager.client.testhelpers;

import static com.sirika.pymager.client.ImageFormat.PNG;
import static com.sirika.pymager.client.ImageScale.width;
import static com.sirika.pymager.client.testhelpers.ImageIdObjectMother.cornicheKabyleId;
import static com.sirika.pymager.client.testhelpers.ImageIdObjectMother.yemmaGourayaId;

import com.sirika.pymager.client.ImageId;
import com.sirika.pymager.client.ImageReference;

public class ImageReferenceObjectMother {

    /**
     * A mountain located in Bejaia, Algeria 
     * (http://photosdesami.com/gallery/v/all/2008/200804/20080426as01/)
     * @return
     */
    public static ImageReference yemmaGouraya() {
	return ImageReference.originalImage(yemmaGourayaId());
    }
    
    public static ImageReference yemmaGourayaResizedTo300x200InPng() {
	return ImageReference.originalImage(yemmaGourayaId())
		.rescaledTo(width(300).by(200))
		.convertedTo(PNG);
    }
    
    public static ImageReference yemmaGourayaResizedTo300x200InDefaultFormat() {
	return ImageReference.originalImage(yemmaGourayaId())
		.rescaledTo(width(300).by(200));
    }
    
    public static ImageReference cornicheKabyle() {
	return ImageReference.originalImage(cornicheKabyleId());
    }
    
    public static ImageReference britneySpears() {
	return ImageReference.originalImage("britneySpears");
    }
}
