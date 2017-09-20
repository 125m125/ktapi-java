package de._125m125.kt.ktapi_java.retrofitUnivocityTsvparser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowProcessor;

public class ObjectParser<T> implements RowProcessor {

    private final Class<T>             clazz;

    private List<ParseableField<T>>    fields;

    private final List<T>              parsedResults;

    private final TypeConverterFactory converterFactory;

    public ObjectParser(final Class<T> clazz, final TypeConverterFactory converterFactory) {
        this.clazz = clazz;
        this.converterFactory = converterFactory;
        this.parsedResults = new ArrayList<>();
    }

    @Override
    public void processStarted(final ParsingContext context) {
        final Field[] clazzFields = this.clazz.getDeclaredFields();
        final String[] headers = context.headers();

        this.fields = new ArrayList<>();

        for (int i = 0; i < headers.length; i++) {
            final String header = headers[i].replaceAll(" ", "_");
            for (final Field f : clazzFields) {
                if (f.getName().equals(header)) {
                    final Function<String, Object> c = this.converterFactory.getConverterFor(f.getType());
                    if (c != null) {
                        this.fields.add(new ParseableField<>(f, i, c));
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void rowProcessed(final String[] row, final ParsingContext context) {
        try {
            final T result = this.clazz.newInstance();

            for (final ParseableField<T> parseableField : this.fields) {
                parseableField.apply(result, row);
            }

            this.parsedResults.add(result);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processEnded(final ParsingContext context) {
    }

    public List<T> getResult() {
        return this.parsedResults;
    }

    private static class ParseableField<T> {
        private final Field                    field;
        private final int                      colIndex;
        private final Function<String, Object> converter;

        public ParseableField(final Field field, final int colIndex, final Function<String, Object> converter) {
            super();
            this.field = field;
            this.colIndex = colIndex;
            this.converter = converter;
        }

        public void apply(final T target, final String[] fields)
                throws IllegalArgumentException, IllegalAccessException {
            this.field.setAccessible(true);
            try {
                this.field.set(target, this.converter.apply(fields[this.colIndex]));
            } finally {
                this.field.setAccessible(false);
            }
        }
    }
}
