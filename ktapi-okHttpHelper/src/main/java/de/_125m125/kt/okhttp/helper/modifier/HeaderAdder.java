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

import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;

public class HeaderAdder implements ClientModifier {

    public static interface HeaderProducer {
        public String apply(Request r) throws IOException;
    }

    private final String         name;
    private final HeaderProducer valueProducer;

    public HeaderAdder(final String name, final String value) {
        this(name, r -> value);
    }

    public HeaderAdder(final String name, final HeaderProducer valueProducer) {
        this.name = name;
        this.valueProducer = valueProducer;
    }

    @Override
    public Builder modify(final Builder builder) {
        return builder.addInterceptor(chain -> {
            final String value = HeaderAdder.this.valueProducer.apply(chain.request());
            Request request = chain.request();
            if (value != null) {
                request = chain.request().newBuilder().addHeader(HeaderAdder.this.name, value)
                        .build();
            }
            return chain.proceed(request);
        });
    }

    @Override
    public boolean conflictsWith(final ClientModifier modifier) {
        if (!(modifier instanceof HeaderAdder)) {
            return false;
        }
        return this.name.equals(((HeaderAdder) modifier).name);
    }
}
