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
package de._125m125.kt.ktapi.retrofit.tsvparser.univocity;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class UnivocityConverterFactory extends Converter.Factory {
    @SuppressFBWarnings(justification = "if rawtype is of type is List, "
            + "Type has to be ParameterizedType", value = "BC_UNCONFIRMED_CAST")
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(final Type type,
            final Annotation[] annotations, final Retrofit retrofit) {
        if (!List.class.equals(getRawType(type))) {
            return null;
        }
        final ParameterizedType parameterizedType = (ParameterizedType) type;
        return new UnivocityResponseBodyConverter<>(getRawType(type),
                (Class<?>) parameterizedType.getActualTypeArguments()[0]);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(final Type type,
            final Annotation[] parameterAnnotations, final Annotation[] methodAnnotations,
            final Retrofit retrofit) {
        return null;
    }
}
