package de._125m125.kt.ktapi_java.simple.parsers;

import java.io.Reader;
import java.util.List;

import com.univocity.parsers.csv.CsvParserSettings;

import de._125m125.kt.ktapi_java.core.Parser;

public class CsvParser implements Parser<List<String[]>, List<?>, Class<?>> {

    @Override
    public List<String[]> parse(final Reader content) {
	final CsvParserSettings settings = new CsvParserSettings();
	settings.getFormat().setLineSeparator("\n");
	final com.univocity.parsers.csv.CsvParser parser = new com.univocity.parsers.csv.CsvParser(settings);
	final List<String[]> allRows = parser.parseAll(content);
	return allRows;
    }

    @Override
    public List<?> parse(final Reader content, final Class<?> U) {
	final ObjectParser<?> rowProcessor = new ObjectParser<>(U);

	final CsvParserSettings parserSettings = new CsvParserSettings();
	parserSettings.setProcessor(rowProcessor);
	parserSettings.setHeaderExtractionEnabled(true);

	final com.univocity.parsers.csv.CsvParser parser = new com.univocity.parsers.csv.CsvParser(parserSettings);
	parser.parse(content);

	final List<?> beans = rowProcessor.getResult();

	return beans;
    }

    @Override
    public String getResponseType() {
	return "text/csv";
    }

}
