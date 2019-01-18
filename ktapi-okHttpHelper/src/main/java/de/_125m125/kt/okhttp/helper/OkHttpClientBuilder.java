package de._125m125.kt.okhttp.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import de._125m125.kt.okhttp.helper.modifier.CertificatePinnerAdder;
import de._125m125.kt.okhttp.helper.modifier.ClientModifier;
import okhttp3.OkHttpClient;

public class OkHttpClientBuilder {

    private final List<ClientModifier> modifiers;
    private OkHttpClient               client;
    private final Set<Object>          references = new HashSet<>();

    private boolean                    closed     = false;

    /**
     * Instantiates a new OkHttpClientBuilder with the given modifiers as initial modifiers.
     *
     * @param modifiers
     *            the inital modifiers
     */
    public OkHttpClientBuilder(final ClientModifier... modifiers) {
        this.modifiers = new ArrayList<>(modifiers.length);
        for (final ClientModifier clientModifier : modifiers) {
            this.modifiers.add(clientModifier);
        }
    }

    /**
     * Registers a new modifier.
     *
     * @param modifier
     *            the new modifier @return this builder @throws
     * @return the ok http client builder
     */
    public OkHttpClientBuilder addModifier(final ClientModifier modifier) {
        Objects.requireNonNull(modifier);
        if (this.modifiers.contains(modifier)) {
            return this;
        }
        if (hasConflictingModifier(modifier)) {
            throw new IllegalArgumentException(
                    "This builder already contains a unique modifier of type "
                            + modifier.getClass());
        }
        if (this.client != null) {
            throw new IllegalStateException(
                    "The client was already build. No more modifiers can be added.");
        }
        this.modifiers.add(modifier);
        return this;
    }

    /**
     * checks if any already registered modifier conflicts with the given modifier.
     * 
     * @param modifier
     *            the modifier to check for conflicts
     * @return true, if one of the registered modifiers conflicts.
     */
    public boolean hasConflictingModifier(final ClientModifier modifier) {
        return this.modifiers.stream().anyMatch(modifier::conflictsWith);
    }

    /**
     * adds interceptors for the following tasks:
     * <ul>
     * <li>pinning the certificate for https://kt.125m125.de</li>
     * </ul>
     * 
     * @return this
     */
    public OkHttpClientBuilder recommendedModifiers() {
        if (!hasModifier(CertificatePinnerAdder.class)) {
            addModifier(new CertificatePinnerAdder());
        }
        return this;
    }

    public boolean hasModifier(final Class<? extends ClientModifier> clazz) {
        return this.modifiers.stream().map(Object::getClass).anyMatch(clazz::equals);
    }

    /**
     * builds the OkHttpClient with the specified modifiers.
     * 
     * @return the OkHttpClient
     * @deprecated use {@link OkHttpClientBuilder#build(Object)} and
     *             {@link OkHttpClientBuilder#close(Object)} to allow clean closing of the build
     *             OkHttpClient when not used anymore.
     */
    @Deprecated
    public OkHttpClient build() {
        return build(null);
    }

    /**
     * Builds the OkHttpClient with the specified modifiers and registers the given referencer as an
     * active user.
     * 
     * When the OkHttpClient is not used anymore, {@link OkHttpClientBuilder#close(Object)} should
     * be called with the same object used as referencer here.
     * 
     * @param referencer
     *            the reference to use as an active indicator.
     * @return the OkHttpClient
     */
    public OkHttpClient build(final Object referencer) {
        if (this.closed) {
            throw new IllegalStateException("The client is already closed.");
        }
        this.references.add(referencer);
        if (this.client != null) {
            return this.client;
        }
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        for (final ClientModifier clientModifier : this.modifiers) {
            clientBuilder = clientModifier.modify(clientBuilder);
        }
        return this.client = clientBuilder.build();
    }

    /**
     * Closes the built OkHttpClient without paying attention to other users of that client.
     * 
     * @deprecated use {@link OkHttpClientBuilder#build(Object)} and
     *             {@link OkHttpClientBuilder#close(Object)} to allow clean closing of the build
     *             OkHttpClient when not used anymore.
     */
    @Deprecated
    public void forceClose() {
        close();
    }

    private void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.client != null) {
            this.client.connectionPool().evictAll();
            this.client.dispatcher().executorService().shutdown();
        }
    }

    /**
     * removes the referencer from the list of active users of the client and closes the client if
     * there are no more references.
     * 
     * @param referencer
     *            the reference used to acquire the OkHttpClient instance.
     */
    public void close(final Object referencer) {
        this.references.remove(referencer);
        if (this.references.isEmpty()) {
            close();
        }
    }
}
