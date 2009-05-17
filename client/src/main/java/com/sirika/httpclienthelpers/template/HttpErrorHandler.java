package com.sirika.httpclienthelpers.template;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public interface HttpErrorHandler {
    boolean apppliesTo(StatusLine statusLine);
    void handle(HttpResponse response) throws Exception;
}
