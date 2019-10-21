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
package de._125m125.kt.ktapi.requester.retrofit.modifier;

import de._125m125.kt.okhttp.helper.modifier.ClientModifier;
import de._125m125.kt.okhttp.helper.modifier.HeaderAdder;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit.Builder;

public class ConverterFactoryAdder extends HeaderAdder implements HybridModifier {
    private final Factory factory;

    public ConverterFactoryAdder(final String header, final Factory factory) {
        super("Accept", header);
        this.factory = factory;
    }

    @Override
    public Builder modify(final Builder builder) {
        return builder.addConverterFactory(this.factory);
    }

    @Override
    public boolean conflictsWith(final ClientModifier modifier) {
        return modifier.getClass() == this.getClass();
    }
}