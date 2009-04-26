/**
 * 
 */
package com.sirika.imgserver.httpclienthelpers;

import org.apache.http.entity.mime.MultipartEntity;

public final class RepeatableMultipartEntity extends MultipartEntity {
    @Override
    public boolean isRepeatable() {
    return true;
    }
}