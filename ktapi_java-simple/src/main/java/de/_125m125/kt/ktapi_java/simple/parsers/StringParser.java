package de._125m125.kt.ktapi_java.simple.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import de._125m125.kt.ktapi_java.core.Parser;

public class StringParser implements Parser<String, String, Void> {

    @Override
    public String parse(final Reader content) {
        try (BufferedReader br = new BufferedReader(content)) {
            final StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }
            return result.toString();
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String parse(final Reader content, final Void v) {
        return parse(content);
    }

    @Override
    public String getResponseType() {
        return "*";
    }

}
