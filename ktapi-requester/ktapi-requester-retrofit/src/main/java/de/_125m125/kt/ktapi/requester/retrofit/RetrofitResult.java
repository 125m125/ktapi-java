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
package de._125m125.kt.ktapi.requester.retrofit;

import java.io.IOException;

import de._125m125.kt.ktapi.core.results.ErrorResponse;
import de._125m125.kt.ktapi.core.results.Result;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class RetrofitResult<T> extends Result<T> {
    public RetrofitResult(final Call<T> call,
            final Converter<ResponseBody, ErrorResponse> errorConverter) {
        super();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(final Call<T> call, final Response<T> response) {
                if (response.isSuccessful()) {
                    setSuccessResult(response.code(), response.body());
                } else {
                    ErrorResponse errorResponse;
                    try {
                        final ResponseBody errorBody = response.errorBody();
                        final String error = errorBody.string();
                        try {
                            errorResponse = errorConverter
                                    .convert(ResponseBody.create(errorBody.contentType(), error));
                        } catch (final Exception e) {
                            errorResponse = new ErrorResponse(response.code(), error,
                                    "An unknown Error occurred");
                        }
                    } catch (final IOException e1) {
                        errorResponse = new ErrorResponse(response.code(),
                                "unknown : " + e1.toString(), "An unknown Error occurred");
                    }
                    setFailureResult(errorResponse);
                }
            }

            @Override
            public void onFailure(final Call<T> call, final Throwable t) {
                setErrorResult(t);
            }
        });
    }

}
