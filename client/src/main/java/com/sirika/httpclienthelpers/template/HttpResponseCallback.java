/**
 * 
 */
package com.sirika.httpclienthelpers.template;

import org.apache.http.HttpResponse;

public interface HttpResponseCallback {
    Object doWithHttpResponse(HttpResponse httpResponse) throws Exception;
}