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
package com.sirika.pymager.api.testhelpers;

import java.io.InputStream;
import java.net.URL;

import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;

public class PictureStreamSourceObjectMother {

    public static InputSupplier<InputStream> yemmaGourayaOriginalPictureStream() {
        return Resources.newInputStreamSupplier(url("com/sirika/pymager/testhelpers/samplepix/original/yemmaGourayaInBejaia.jpg"));
    }

    public static InputSupplier<InputStream> yemmaGourayaDerived100x100PictureStream() {
        return Resources.newInputStreamSupplier(url("com/sirika/pymager/testhelpers/samplepix/derived/yemmaGouraya-100x100.jpg"));
    }

    public static InputSupplier<InputStream> cornicheKabyleOriginalPictureStream() {
        return Resources.newInputStreamSupplier(url("com/sirika/pymager/testhelpers/samplepix/original/cornicheKabyle.jpg"));
    }

    public static InputSupplier<InputStream> textfileresource() {
        return Resources.newInputStreamSupplier(url("com/sirika/pymager/testhelpers/samplepix/somethingthatisnotanimage.txt"));
    }
    
    private static URL url(String classPathUrl) {
        return Thread.currentThread().getContextClassLoader().getResource(classPathUrl);
    }
}
