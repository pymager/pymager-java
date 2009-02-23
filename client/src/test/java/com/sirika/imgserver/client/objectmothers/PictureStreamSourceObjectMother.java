package com.sirika.imgserver.client.objectmothers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;

public class PictureStreamSourceObjectMother {

    public static InputStreamSource yemmaGourayaPictureStream() {
	return new ClassPathResource("/com/sirika/imgserver/samplePictures/yemmaGourayaInBejaia.jpg");
    }
    
    public static InputStreamSource cornicheKabylePictureStream() {
	return new ClassPathResource("/com/sirika/imgserver/samplePictures/cornicheKabyle.jpg");
    }
}
