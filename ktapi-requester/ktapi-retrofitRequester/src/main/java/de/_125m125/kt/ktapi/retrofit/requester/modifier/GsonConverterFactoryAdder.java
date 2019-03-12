package de._125m125.kt.ktapi.retrofit.requester.modifier;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import retrofit2.converter.gson.GsonConverterFactory;

public class GsonConverterFactoryAdder extends ConverterFactoryAdder {

    public GsonConverterFactoryAdder() {
        this(new GsonBuilder().registerTypeAdapter(Double.class, new TypeAdapter<Double>() {

            @Override
            public void write(final JsonWriter writer, final Double value) throws IOException {
                if (value == null) {
                    writer.nullValue();
                    return;
                }
                writer.value(value);
            }

            @Override
            public Double read(final JsonReader reader) throws IOException {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return null;
                }
                final String stringValue = reader.nextString();
                try {
                    final Double value = Double.valueOf(stringValue);
                    return value;
                } catch (final NumberFormatException e) {
                    return null;
                }
            }
        }).create());
    }

    public GsonConverterFactoryAdder(final Gson gson) {
        this("application/json;q=0.5", GsonConverterFactory.create(gson));
    }

    public GsonConverterFactoryAdder(final String header, final GsonConverterFactory factory) {
        super(header, factory);
    }

}
