package de._125m125.kt.ktapi_java.retrofitUnivocityTsvparser;

import java.io.IOException;
import java.util.List;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class UnivocityResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final TypeConverterFactory DEFAULT_CONVERTER_FACTORY = new TypeConverterFactory();

    private final TypeConverterFactory        converterFactory;

    private Class<?>                          entryClazz;

    public UnivocityResponseBodyConverter(final Class<T> clazz, final Class<?> entryClazz) {
        this(clazz, entryClazz, UnivocityResponseBodyConverter.DEFAULT_CONVERTER_FACTORY);
    }

    public UnivocityResponseBodyConverter(final Class<T> clazz, final Class<?> entryClazz,
            final TypeConverterFactory converterFactory) {
        this.entryClazz = entryClazz;
        this.converterFactory = converterFactory;

    }

    @SuppressWarnings("unchecked")
    @Override
    public T convert(final ResponseBody value) throws IOException {
        final ObjectParser<?> rowProcessor = new ObjectParser<>(this.entryClazz, this.converterFactory);

        final TsvParserSettings parserSettings = new TsvParserSettings();
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        final TsvParser parser = new TsvParser(parserSettings);
        parser.parse(value.byteStream());

        final List<?> beans = rowProcessor.getResult();
        return (T) beans;
    }

}
