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
package de._125m125.kt.okhttp.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import de._125m125.kt.okhttp.helper.modifier.ClientModifier;
import de._125m125.kt.okhttp.helper.modifier.HeaderAdder;
import de._125m125.kt.okhttp.helper.modifier.HeaderAdder.ConflictMode;
import okhttp3.OkHttpClient;

public class OkHttpClientBuilder {

    private final String               appName;
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
    public OkHttpClientBuilder(final String appName, final ClientModifier... modifiers) {
        this.appName = appName;
        this.modifiers = new ArrayList<>(modifiers.length + 1);
        for (final ClientModifier clientModifier : modifiers) {
            addModifier(clientModifier);
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
        if (this.client != null) {
            throw new IllegalStateException(
                    "The client was already build. No more modifiers can be added.");
        }
        Objects.requireNonNull(modifier);
        if (this.modifiers.contains(modifier)) {
            return this;
        }
        if (hasConflictingModifier(modifier)) {
            throw new IllegalArgumentException(
                    "This builder already contains a unique modifier of type "
                            + modifier.getClass());
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
     * <p>
     * Builds the OkHttpClient with the specified modifiers and registers the given referencer as an
     * active user.
     * </p>
     *
     * <p>
     * When the OkHttpClient is not used anymore, {@link OkHttpClientBuilder#close(Object)} should
     * be called with the same object used as referencer here.
     * </p>
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
        addModifier(new HeaderAdder("user-agent", "KtApi-Java-OkHttp-" + this.appName,
                ConflictMode.SKIP));
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
