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

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;

public class PictureStreamSourceObjectMother {

    public static InputStreamSource yemmaGourayaOriginalPictureStream() {
        return new ClassPathResource(
                "/com/sirika/pymager/testhelpers/samplepix/original/yemmaGourayaInBejaia.jpg");
    }

    public static InputStreamSource yemmaGourayaDerived100x100PictureStream() {
        return new ClassPathResource(
                "/com/sirika/pymager/testhelpers/samplepix/derived/yemmaGouraya-100x100.jpg");
    }

    public static InputStreamSource cornicheKabyleOriginalPictureStream() {
        return new ClassPathResource(
                "/com/sirika/pymager/testhelpers/samplepix/original/cornicheKabyle.jpg");
    }

    public static InputStreamSource textfileresource() {
        return new ClassPathResource(
                "/com/sirika/pymager/testhelpers/samplepix/somethingthatisnotanimage.txt");
    }
}
