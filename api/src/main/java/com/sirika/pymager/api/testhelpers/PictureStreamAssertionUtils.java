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

import static com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother.cornicheKabyleOriginalPictureStream;
import static com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaDerived100x100PictureStream;
import static com.sirika.pymager.api.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

public class PictureStreamAssertionUtils {
    public static class PictureStreamAsserter {
        private InputSupplier<InputStream> expected;
        private InputSupplier<InputStream> actual;

        public PictureStreamAsserter(InputSupplier<InputStream> expected, InputSupplier<InputStream> actual) {
            super();
            this.expected = expected;
            this.actual = actual;
        }

        public boolean isSameStream() throws IOException {
            return ByteStreams.equal(expected, actual);
        };
    }

    public static boolean isYemmaGourayaPicture(InputSupplier<InputStream> iss) throws IOException {
        return new PictureStreamAsserter(yemmaGourayaOriginalPictureStream(),iss).isSameStream();
    }

    public static boolean isCornicheKabylePicture(InputSupplier<InputStream> iss)
            throws IOException {
        return new PictureStreamAsserter(cornicheKabyleOriginalPictureStream(),iss).isSameStream();
    }

    public static boolean is100x100CornicheKabylePicture(InputSupplier<InputStream> iss) throws IOException {
        return new PictureStreamAsserter(yemmaGourayaDerived100x100PictureStream(), iss).isSameStream();
    }
}
