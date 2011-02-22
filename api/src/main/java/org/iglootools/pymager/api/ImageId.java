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
package org.iglootools.pymager.api;

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * An Image ID
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 * 
 */
public class ImageId {
    private String id;

    private ImageId(String id) {
        Preconditions.checkArgument(id != null, "ID cannnot be null");
        Preconditions.checkArgument(
            CharMatcher.ASCII.and(CharMatcher.JAVA_LETTER_OR_DIGIT)
                .matchesAllOf(id), "ID cannot contain special characters");
        
        this.id = id;
    }

    public static ImageId imageId(String id) {
        return new ImageId(id);
    }

    public String toString() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageId other = (ImageId) obj;
        
        return Objects.equal(this.id, other.id);
    }

}
