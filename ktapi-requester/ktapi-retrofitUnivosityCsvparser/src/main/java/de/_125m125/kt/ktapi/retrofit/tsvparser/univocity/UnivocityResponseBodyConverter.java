package de._125m125.kt.ktapi.retrofit.tsvparser.univocity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class UnivocityResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private Class<?> entryClazz;

    public UnivocityResponseBodyConverter(final Class<T> clazz, final Class<?> entryClazz) {
        this.entryClazz = entryClazz;

    }

    @SuppressWarnings("unchecked")
    @Override
    public T convert(final ResponseBody value) throws IOException {
        BeanListProcessor<?> rowProcessor = new BeanListProcessor<>(entryClazz);
        for (Field field : this.entryClazz.getDeclaredFields()) {
            rowProcessor.getColumnMapper().attributeToColumnName(field.getName(),
                    field.getName().replaceAll("_", " "));
        }

        final TsvParserSettings parserSettings = new TsvParserSettings();
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        final TsvParser parser = new TsvParser(parserSettings);
        parser.parse(value.byteStream());

        final List<?> beans = rowProcessor.getBeans();
        return (T) beans;
    }

}
