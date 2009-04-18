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
package com.sirika.imgserver.client.objectmothers;

import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.cornicheKabyleOriginalPictureStream;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaDerived100x100PictureStream;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;

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
	return new PictureStreamAsserter(yemmaGourayaOriginalPictureStream(), iss).isSameStream();
    }
    
    public static boolean isCornicheKabylePicture(InputStreamSource iss) throws IOException {
	return new PictureStreamAsserter(cornicheKabyleOriginalPictureStream(), iss).isSameStream();
    }
    
    public static boolean is100x100CornicheKabylePicture(InputStreamSource iss) throws IOException {
	return new PictureStreamAsserter(yemmaGourayaDerived100x100PictureStream(), iss).isSameStream();
    }
}
