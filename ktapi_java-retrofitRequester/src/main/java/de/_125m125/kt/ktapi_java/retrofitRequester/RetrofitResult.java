package de._125m125.kt.ktapi_java.retrofitRequester;

import java.io.IOException;

import de._125m125.kt.ktapi_java.core.results.ErrorResponse;
import de._125m125.kt.ktapi_java.core.results.Result;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class RetrofitResult<T> extends Result<T> {
    public RetrofitResult(final Call<T> call, final Converter<ResponseBody, ErrorResponse> errorConverter) {
        super();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(final Call<T> call, final Response<T> response) {
                if (response.isSuccessful()) {
                    setSuccessResult(response.code(), response.body());
                } else {
                    ErrorResponse errorResponse;
                    try {
                        errorResponse = errorConverter.convert(response.errorBody());
                    } catch (final Exception e) {
                        try {
                            errorResponse = new ErrorResponse(response.code(), response.errorBody().string(),
                                    "An unknown Error occurred");
                        } catch (final IOException e1) {
                            errorResponse = new ErrorResponse(response.code(), "unknown : " + e1.toString(),
                                    "An unknown Error occurred");
                        }
                    }
                    setErrorResult(errorResponse);
                }
            }

            @Override
            public void onFailure(final Call<T> call, final Throwable t) {
                setErrorResult(new ErrorResponse(-1, t.toString(), t.getLocalizedMessage()));
            }
        });
    }

}
