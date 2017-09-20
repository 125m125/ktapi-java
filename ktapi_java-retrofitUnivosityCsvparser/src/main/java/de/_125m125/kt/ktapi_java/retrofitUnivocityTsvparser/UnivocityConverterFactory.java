package de._125m125.kt.ktapi_java.retrofitUnivocityTsvparser;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class UnivocityConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(final Type type, final Annotation[] annotations,
            final Retrofit retrofit) {
        if (!List.class.equals(getRawType(type))) {
            return null;
        }
        final ParameterizedType parameterizedType = (ParameterizedType) type;
        return new UnivocityResponseBodyConverter<>(getRawType(type),
                (Class<?>) parameterizedType.getActualTypeArguments()[0]);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(final Type type, final Annotation[] parameterAnnotations,
            final Annotation[] methodAnnotations, final Retrofit retrofit) {
        return null;
    }
}
