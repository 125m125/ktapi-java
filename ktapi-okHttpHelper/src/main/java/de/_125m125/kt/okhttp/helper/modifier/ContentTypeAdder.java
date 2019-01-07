package de._125m125.kt.okhttp.helper.modifier;

import okhttp3.Request;

public class ContentTypeAdder extends HeaderAdder {

    public static String createHeader(final Request request) {
        if (request.method().equals("GET")) {
            return null;
        }
        if (request.header("content-type") != null) {
            return null;
        }
        return "application/x-www-form-urlencoded";
    }

    public ContentTypeAdder() {
        super("content-type", ContentTypeAdder::createHeader);
    }

}