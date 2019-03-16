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
package de._125m125.kt.okhttp.helper.modifier;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;

public class HeaderAdder implements ClientModifier {
    public static enum ConflictMode {
        ABORT,
        APPEND,
        REPLACE,
        SKIP,
    }

    public static interface HeaderProducer {
        public String apply(Request r) throws IOException;
    }

    private final String         name;
    private final HeaderProducer valueProducer;
    private final ConflictMode   mode;

    public HeaderAdder(final String name, final String value) {
        this(name, r -> value);
    }

    public HeaderAdder(final String name, final String value, final ConflictMode mode) {
        this(name, r -> value, mode);
    }

    public HeaderAdder(final String name, final HeaderProducer valueProducer) {
        this(name, valueProducer, ConflictMode.APPEND);
    }

    public HeaderAdder(final String name, final HeaderProducer valueProducer,
            final ConflictMode mode) {
        this.name = Objects.requireNonNull(name).toLowerCase();
        this.valueProducer = Objects.requireNonNull(valueProducer);
        this.mode = Objects.requireNonNull(mode);
    }

    @Override
    public Builder modify(final Builder builder) {
        return builder.addInterceptor(createInterceptor());
    }

    protected Interceptor createInterceptor() {
        return chain -> {
            Request request = chain.request();
            final String value = HeaderAdder.this.valueProducer.apply(request);
            if (value != null) {
                if (value.isEmpty()) {
                    request = request.newBuilder().removeHeader(HeaderAdder.this.name).build();
                } else {
                    if (request.headers(this.name).isEmpty()) {
                        request = request.newBuilder().addHeader(HeaderAdder.this.name, value)
                                .build();
                    } else {
                        switch (this.mode) {
                        case APPEND:
                            request = request.newBuilder().addHeader(HeaderAdder.this.name, value)
                                    .build();
                            break;
                        case REPLACE:
                            request = request.newBuilder().header(HeaderAdder.this.name, value)
                                    .build();
                            break;
                        case SKIP:
                            break;
                        case ABORT:
                            throw new IllegalStateException(
                                    "Header " + this.name + " was already set!");
                        default:
                            throw new IllegalArgumentException(
                                    "ConflictMode " + this.mode + " is currently not supported!");
                        }
                    }
                }
            }
            return chain.proceed(request);
        };
    }

    public String getName() {
        return this.name;
    }
}
