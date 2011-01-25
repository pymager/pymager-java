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

import static com.sirika.pymager.api.ImageFormat.PNG;
import static com.sirika.pymager.api.ImageScale.width;
import static com.sirika.pymager.api.testhelpers.ImageIdObjectMother.cornicheKabyleId;
import static com.sirika.pymager.api.testhelpers.ImageIdObjectMother.yemmaGourayaId;

import com.sirika.pymager.api.ImageId;
import com.sirika.pymager.api.ImageReference;

public class ImageReferenceObjectMother {

    /**
     * A mountain located in Bejaia, Algeria
     * (http://photosdesami.com/gallery/v/all/2008/200804/20080426as01/)
     * 
     * @return
     */
    public static ImageReference yemmaGouraya() {
        return ImageReference.originalImage(yemmaGourayaId());
    }

    public static ImageReference yemmaGourayaResizedTo300x200InPng() {
        return ImageReference.originalImage(yemmaGourayaId()).rescaledTo(
                width(300).by(200)).convertedTo(PNG);
    }

    public static ImageReference yemmaGourayaResizedTo300x200InDefaultFormat() {
        return ImageReference.originalImage(yemmaGourayaId()).rescaledTo(
                width(300).by(200));
    }

    public static ImageReference cornicheKabyle() {
        return ImageReference.originalImage(cornicheKabyleId());
    }

    public static ImageReference britneySpears() {
        return ImageReference.originalImage("britneySpears");
    }
}
