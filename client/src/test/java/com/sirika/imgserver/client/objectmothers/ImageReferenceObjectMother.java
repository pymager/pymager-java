package com.sirika.imgserver.client.objectmothers;

import static com.sirika.imgserver.client.ImageFormat.PNG;
import static com.sirika.imgserver.client.ImageScale.width;

import com.sirika.imgserver.client.ImageReference;

public class ImageReferenceObjectMother {

    public static ImageReference britneySpearsOriginal() {
	return ImageReference.originalImage("britney");
    }
    
    public static ImageReference britneySpearsResizedTo300x200InPng() {
	return ImageReference.originalImage("britney")
		.rescaledTo(width(300).by(200))
		.convertedTo(PNG);
    }
    
    public static ImageReference britneySpearsResizedTo300x200InDefaultFormat() {
	return ImageReference.originalImage("britney")
		.rescaledTo(width(300).by(200));
    }
}
