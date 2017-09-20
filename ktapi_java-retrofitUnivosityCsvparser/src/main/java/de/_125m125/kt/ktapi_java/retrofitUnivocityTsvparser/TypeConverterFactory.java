package de._125m125.kt.ktapi_java.retrofitUnivocityTsvparser;

import java.util.function.Function;

public class TypeConverterFactory {

    public Function<String, Object> getConverterFor(final Class<?> clazz) {
        for (final TypeConverter converter : TypeConverter.values()) {
            for (final Class<?> c : converter.classes) {
                if (clazz.isAssignableFrom(c)) {
                    return converter;
                }
            }
        }
        return null;
    }

    private enum TypeConverter implements Function<String, Object> {
        StringConverter((e) -> e, String.class),
        BooleanConverter((e) -> e == null ? null : Boolean.parseBoolean(e), Boolean.class, boolean.class),
        IntConverter((e) -> e == null ? null : Integer.parseInt(e), Integer.class, int.class),
        LongConverter((e) -> e == null ? null : Long.parseLong(e), Long.class, long.class),
        DoubleConverter((e) -> e == null ? null : Double.parseDouble(e), Double.class, double.class);

        private final Function<String, Object> converter;
        private final Class<?>[]               classes;

        private TypeConverter(final Function<String, Object> converter, final Class<?>... classes) {
            this.converter = converter;
            this.classes = classes;
        }

        @Override
        public Object apply(final String s) {
            return this.converter.apply(s);
        }
    }
}
