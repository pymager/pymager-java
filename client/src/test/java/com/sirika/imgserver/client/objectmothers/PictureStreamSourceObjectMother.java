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

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;

public class PictureStreamSourceObjectMother {

    public static InputStreamSource yemmaGourayaOriginalPictureStream() {
	return new ClassPathResource("/com/sirika/imgserver/samplepix/original/yemmaGourayaInBejaia.jpg");
    }
    
    public static InputStreamSource yemmaGourayaDerived100x100PictureStream() {
	return new ClassPathResource("/com/sirika/imgserver/samplepix/derived/yemmaGouraya-100x100.jpg");
    }
    
    public static InputStreamSource cornicheKabyleOriginalPictureStream() {
	return new ClassPathResource("/com/sirika/imgserver/samplepix/original/cornicheKabyle.jpg");
    }
}
