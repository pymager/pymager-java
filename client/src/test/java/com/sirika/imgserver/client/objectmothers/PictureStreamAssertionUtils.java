package com.sirika.imgserver.client.objectmothers;

import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.cornicheKabylePictureStream;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaPictureStream;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamSource;

public class PictureStreamAssertionUtils {
    public static class PictureStreamAsserter {
	private InputStreamSource expected;
	private InputStreamSource actual;
	
	public PictureStreamAsserter(InputStreamSource expected,InputStreamSource actual) {
	    super();
	    this.expected = expected;
	    this.actual = actual;
	}
	
	public boolean isSameStream() throws IOException {
	    InputStream expectedStream = expected.getInputStream();
	    InputStream actualStream = actual.getInputStream();
	    boolean b = IOUtils.contentEquals(expectedStream, actualStream);
	    IOUtils.closeQuietly(expectedStream);
	    IOUtils.closeQuietly(actualStream);
	    return b;
	};
    }
    
    public static boolean isYemmaGourayaPicture(InputStreamSource iss) throws IOException {
	return new PictureStreamAsserter(yemmaGourayaPictureStream(), iss).isSameStream();
    }
    
    public static boolean isCornicheKabylePicture(InputStreamSource iss) throws IOException {
	return new PictureStreamAsserter(cornicheKabylePictureStream(), iss).isSameStream();
    }
}
