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
package de._125m125.kt.ktapi.websocket.responses;

import java.util.Optional;

public class ResponseMessage {
    private final Integer   rid;
    private final Long      pong;
    private final String    error;
    private final Throwable errorCause;

    public ResponseMessage(final Integer rid, final Long pong, final String error,
            final Throwable errorCause) {
        super();
        this.rid = rid;
        this.pong = pong;
        this.error = error;
        this.errorCause = errorCause;
    }

    public ResponseMessage(final String error, final Throwable errorCause) {
        this(null, null, error, errorCause);
    }

    public Optional<Integer> getRequestId() {
        return Optional.ofNullable(this.rid);
    }

    public Optional<Long> getServerTime() {
        return Optional.ofNullable(this.pong);
    }

    public Optional<String> getError() {
        return this.error == null || "false".equals(this.error) ? Optional.empty()
                : Optional.of(this.error);
    }

    public Optional<Throwable> getErrorCause() {
        return Optional.ofNullable(this.errorCause);
    }

    public boolean success() {
        return !getError().isPresent();
    }
}
