package de._125m125.kt.ktapi.retrofitUnivocityTsvparser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
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
                    final Function<String, Object> c = this.converterFactory
                            .getConverterFor(f.getType());
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
        if (this.fields == null) {
            throw new IllegalStateException(
                    "processStarted should have been called before rowProcessed");
        }
        try {
            final Constructor<T> constructor = this.clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            final T result = constructor.newInstance();

            for (final ParseableField<T> parseableField : this.fields) {
                parseableField.apply(result, row);
            }

            this.parsedResults.add(result);
        } catch (final PrivilegedActionException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processEnded(final ParsingContext context) {
        this.fields = null;
    }

    public List<T> getResult() {
        return this.parsedResults;
    }

    private static class ParseableField<T> {
        private final Field                    field;
        private final int                      colIndex;
        private final Function<String, Object> converter;

        public ParseableField(final Field field, final int colIndex,
                final Function<String, Object> converter) {
            super();
            this.field = field;
            this.colIndex = colIndex;
            this.converter = converter;
        }

        public void apply(final T target, final String[] fields) throws PrivilegedActionException {
            AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
                ParseableField.this.field.setAccessible(true);
                ParseableField.this.field.set(target,
                        ParseableField.this.converter.apply(fields[ParseableField.this.colIndex]));
                return null;
            });
        }
    }
}
