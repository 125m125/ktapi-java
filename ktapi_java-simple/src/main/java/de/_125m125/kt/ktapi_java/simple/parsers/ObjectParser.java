package de._125m125.kt.ktapi_java.simple.parsers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowProcessor;

public class ObjectParser<T> implements RowProcessor {

    private final Class<T> clazz;

    private List<ParseableField<T>> fields;

    private final List<T> parsedResults;

    public ObjectParser(final Class<T> clazz) {
	this.clazz = clazz;
	this.parsedResults = new ArrayList<>();
    }

    @Override
    public void processStarted(final ParsingContext context) {
	final Field[] clazzFields = this.clazz.getDeclaredFields();
	final String[] headers = context.headers();

	this.fields = new ArrayList<>();

	for (int i = 0; i < headers.length; i++) {
	    final String header = headers[i].replaceAll(" ", "_");
	    System.out.println("header " + i + ": " + header);
	    for (final Field f : clazzFields) {
		System.out.println("comparing " + f.getName());
		if (f.getName().equals(header)) {
		    System.out.println("potential match!" + f.getType());
		    final Converter c = Converter.getConverterFor(f.getType());
		    System.out.println(c);
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
	private final Field field;
	private final int colIndex;
	private final Converter converter;

	public ParseableField(final Field field, final int colIndex, final Converter converter) {
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

    private static enum Converter {
	StringConverter((e) -> e),
	BooleanConverter((e) -> e == null ? null : Boolean.parseBoolean(e)),
	IntConverter((e) -> e == null ? null : Integer.parseInt(e)),
	LongConverter((e) -> e == null ? null : Long.parseLong(e)),
	DoubleConverter((e) -> e == null ? null : Double.parseDouble(e));

	private static Converter getConverterFor(final Class<?> clazz) {
	    if (clazz.isAssignableFrom(String.class)) {
		return StringConverter;
	    } else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
		return BooleanConverter;
	    } else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
		return IntConverter;
	    } else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
		return LongConverter;
	    } else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
		return DoubleConverter;
	    }
	    return null;
	}

	private final Function<String, Object> converter;

	private Converter(final Function<String, Object> converter) {
	    this.converter = converter;
	}

	public Object apply(final String s) {
	    return this.converter.apply(s);
	}
    }
}
