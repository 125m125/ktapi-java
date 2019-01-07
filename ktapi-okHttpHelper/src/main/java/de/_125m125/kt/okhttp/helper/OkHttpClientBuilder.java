package de._125m125.kt.okhttp.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de._125m125.kt.okhttp.helper.modifier.ClientModifier;
import de._125m125.kt.okhttp.helper.modifier.ContentTypeAdder;
import de._125m125.kt.okhttp.helper.modifier.UserKeyRemover;
import okhttp3.OkHttpClient;

public class OkHttpClientBuilder {

    private final List<ClientModifier> modifiers;
    private OkHttpClient               client;

    public OkHttpClientBuilder(final ClientModifier... modifiers) {
        this.modifiers = new ArrayList<>(modifiers.length);
        for (final ClientModifier clientModifier : modifiers) {
            this.modifiers.add(clientModifier);
        }
    }

    public OkHttpClientBuilder addModifier(final ClientModifier modifier) {
        Objects.requireNonNull(modifier);
        if (this.modifiers.contains(modifier)) {
            return this;
        }
        if (hasConflictingModifier(modifier)) {
            throw new IllegalArgumentException(
                    "This builder already contains a unique modifier of type " + modifier.getClass());
        }
        if (this.client != null) {
            throw new IllegalStateException("The client was already build. No more modifiers can be added.");
        }
        this.modifiers.add(modifier);
        return this;
    }

    public boolean hasConflictingModifier(final ClientModifier modifier) {
        return this.modifiers.stream().anyMatch(modifier::conflictsWith);
    }

    public OkHttpClientBuilder recommendedModifiers() {
        return this;
    }

    public boolean hasModifier(final Class<? extends ClientModifier> clazz) {
        return this.modifiers.stream().map(Object::getClass).anyMatch(clazz::equals);
    }

    public OkHttpClient build() {
        return build(null);
    }

    public OkHttpClient build(final Object referencer) {
        if (!hasModifier(UserKeyRemover.class)) {
            addModifier(new UserKeyRemover());
        }
        if (!hasModifier(ContentTypeAdder.class)) {
            addModifier(new ContentTypeAdder());
        }
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        for (final ClientModifier clientModifier : this.modifiers) {
            clientBuilder = clientModifier.modify(clientBuilder);
        }
        return this.client = clientBuilder.build();
    }
}
