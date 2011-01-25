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
package com.sirika.pymager.api;

public class UnknownUploadFailureException extends ImageServerException {
    private static final long serialVersionUID = 1L;

    private ImageId imageId;
    private ImageFormat imageFormat;

    public UnknownUploadFailureException(ImageId imageId,
            ImageFormat imageFormat, Exception e) {
        super(e);
        this.imageId = imageId;
        this.imageFormat = imageFormat;
    }

    public ImageId getImageId() {
        return imageId;
    }

    public ImageFormat getImageFormat() {
        return imageFormat;
    }

}
