package de._125m125.kt.ktapi.retrofit.tsvparser.univocity;

import de._125m125.kt.ktapi.retrofit.requester.modifier.ConverterFactoryAdder;
import retrofit2.Converter.Factory;

public class UnivocityConverterFactoryAdder extends ConverterFactoryAdder {

    public UnivocityConverterFactoryAdder() {
        this("text/tsv", new UnivocityConverterFactory());
    }

    public UnivocityConverterFactoryAdder(final String header, final Factory factory) {
        super(header, factory);
    }

}
