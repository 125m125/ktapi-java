/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de._125m125.kt.ktapi.websocket.responses.parsers;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de._125m125.kt.ktapi.core.entities.UpdateNotification;
import de._125m125.kt.ktapi.websocket.responses.ResponseMessage;
import de._125m125.kt.ktapi.websocket.responses.SessionResponse;

public class MessageParser {
    public Optional<Object> parse(final String message) {
        final JsonElement parse = new JsonParser().parse(message);
        if (parse instanceof JsonObject) {
            final JsonObject jsonObject = (JsonObject) parse;
            if (jsonObject.has("rid")) {
                if (jsonObject.has("session")) {
                    return Optional.of(new Gson().fromJson(jsonObject, SessionResponse.class));
                }
                return Optional.of(new Gson().fromJson(jsonObject, ResponseMessage.class));
            }
            if (jsonObject.has("type")) {
                if ("update".equals(jsonObject.get("type").getAsString())) {
                    return Optional.of(new Gson().fromJson(jsonObject, UpdateNotification.class));
                }
            }
        }
        return Optional.empty();
    }
}
