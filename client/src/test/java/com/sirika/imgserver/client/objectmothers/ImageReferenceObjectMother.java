package com.sirika.imgserver.client.objectmothers;

import static com.sirika.imgserver.client.ImageFormat.PNG;
import static com.sirika.imgserver.client.ImageScale.width;

import com.sirika.imgserver.client.ImageReference;

public class ImageReferenceObjectMother {

    /**
     * A mountain located in Bejaia, Algeria 
     * (http://photosdesami.com/gallery/v/all/2008/200804/20080426as01/)
     * @return
     */
    public static ImageReference yemmaGouraya() {
	return ImageReference.originalImage("yemmaGouraya");
    }
    
    public static ImageReference yemmaGourayaResizedTo300x200InPng() {
	return ImageReference.originalImage("yemmaGouraya")
		.rescaledTo(width(300).by(200))
		.convertedTo(PNG);
    }
    
    public static ImageReference yemmaGourayaResizedTo300x200InDefaultFormat() {
	return ImageReference.originalImage("yemmaGouraya")
		.rescaledTo(width(300).by(200));
    }
    
    public static ImageReference britneySpears() {
	return ImageReference.originalImage("britneySpears");
    }
}
