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
