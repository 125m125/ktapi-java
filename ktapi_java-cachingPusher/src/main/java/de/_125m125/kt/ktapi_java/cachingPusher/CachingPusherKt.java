package de._125m125.kt.ktapi_java.cachingPusher;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;

import de._125m125.kt.ktapi_java.cachingPusher.objects.TimestampedList;
import de._125m125.kt.ktapi_java.pusher.PusherKt;
import de._125m125.kt.ktapi_java.pusher.PusherListener;
import de._125m125.kt.ktapi_java.pusher.PusherNotification;
import de._125m125.kt.ktapi_java.simple.objects.User;
import de._125m125.kt.ktapi_java.simple.parsers.Parser;

/**
 * 
 */
public class CachingPusherKt extends PusherKt implements PusherListener {

    /** The Set of paths that can be cached. */
    private static final Set<String>                        cacheablePaths = ImmutableSet.of("messages", "trades",
            "itemlist", "payouts", "history", "order");

    /** The cache. */
    private final LoadingCache<CacheKey<Object, Object>, ?> cache          = CacheBuilder.newBuilder()
            .build(new CacheLoader<>());

    /**
     * Instantiates a new caching pusher kt.
     *
     * @param user
     *            the user
     */
    public CachingPusherKt(final User user) {
        super(user);
        for (final String s : CachingPusherKt.cacheablePaths) {
            subscribeToUpdates(this, s, true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de._125m125.kt.ktapi_java.simple.Kt#performRequest(java.lang.String,
     * java.lang.String, java.util.Map, boolean,
     * de._125m125.kt.ktapi_java.simple.parsers.Parser, java.lang.Object)
     */
    @Override
    public <T, U> U performRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<?, ?, T> parser, final T helper) {
        if (!isCacheable(method, path)) {
            return performUncachedRequest(method, path, params, auth, parser, helper);
        } else {
            final CacheKey<T, U> key = new CacheKey<>(method, path, params, auth, parser, helper);
            @SuppressWarnings("unchecked")
            final U result = (U) this.cache.getUnchecked((CacheKey<Object, Object>) key);
            return result;
        }
    }

    /**
     * Perform a request to the kadcontrade api ignoring the cache of the
     * CachingPusherKt.
     *
     * @param <T>
     *            the generic type of the helper
     * @param <U>
     *            the generic type of the result
     * @param method
     *            the method of the request
     * @param path
     *            the path of the request
     * @param params
     *            the parameters for the request
     * @param auth
     *            true, if authentication is required for the request
     * @param parser
     *            the parser
     * @param helper
     *            the helper for the parser
     * @return the parsed result
     * @throws ClassCastException
     *             if the result of the parser is not of the Type &lt;U&gt;
     */
    public <T, U> U performUncachedRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<?, ?, T> parser, final T helper) {
        return super.performRequest(method, path, params, auth, parser, helper);
    }

    /**
     * Checks if is a request with the given method and path is cacheable.
     *
     * @param method
     *            the method
     * @param path
     *            the path
     * @return true, if the reuqest is cacheable
     */
    public boolean isCacheable(final String method, final String path) {
        return "GET".equals(method) && CachingPusherKt.cacheablePaths.contains(path);
    }

    /**
     * Checks if a given object might have updated on the server.
     *
     * @param toCheck
     *            the object to check
     * @return true, if the given object might have updated on the server
     */
    public boolean hasUpdated(final Object toCheck) {
        if (!(toCheck instanceof Timestamped)) {
            return true;
        } else {
            return this.cache.asMap().values().stream().filter(entry -> entry instanceof Timestamped)
                    .filter(entry -> toCheck.getClass().isAssignableFrom(entry.getClass()))
                    .noneMatch(entry -> ((Timestamped) entry).getTimestamp() <= ((Timestamped) toCheck).getTimestamp());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de._125m125.kt.ktapi_java.pusher.PusherListener#update(de._125m125.kt.
     * ktapi_java.pusher.PusherNotification)
     */
    @Override
    public void update(final PusherNotification notification) {
        for (final CacheKey<Object, Object> entry : this.cache.asMap().keySet()) {
            if (entry.path.startsWith(notification.getDetails().get("source"))) {
                this.cache.invalidate(entry);
            }
        }
    }

    /**
     * instances of this class are used as keys for the cache.
     */
    private class CacheKey<T, U> {

        /** The method. */
        final String              method;

        /** The path. */
        final String              path;

        /** The params. */
        final Map<String, String> params;

        /** The auth. */
        final boolean             auth;

        /** The parser. */
        final Parser<?, ?, T>     parser;

        /** The helper. */
        final T                   helper;

        /**
         * Instantiates a new cache key.
         *
         * @param method
         *            the method
         * @param path
         *            the path
         * @param params
         *            the params
         * @param auth
         *            the auth
         * @param parser
         *            the parser
         * @param helper
         *            the helper
         */
        private CacheKey(final String method, final String path, final Map<String, String> params, final boolean auth,
                final Parser<?, ?, T> parser, final T helper) {
            super();
            this.method = method;
            this.path = path;
            this.params = params;
            this.auth = auth;
            this.parser = parser;
            this.helper = helper;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (this.auth ? 1231 : 1237);
            result = prime * result + ((this.helper == null) ? 0 : this.helper.hashCode());
            result = prime * result + ((this.method == null) ? 0 : this.method.hashCode());
            result = prime * result + ((this.params == null) ? 0 : this.params.hashCode());
            result = prime * result + ((this.parser == null) ? 0 : this.parser.hashCode());
            result = prime * result + ((this.path == null) ? 0 : this.path.hashCode());
            return result;
        }

        /**
         * Perform an uncached request for this Cachekey.
         *
         * @return the result of the request
         */
        public U request() {
            return performUncachedRequest(this.method, this.path, this.params, this.auth, this.parser, this.helper);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            @SuppressWarnings("rawtypes")
            final CacheKey other = (CacheKey) obj;
            if (this.auth != other.auth)
                return false;
            if (this.helper == null) {
                if (other.helper != null)
                    return false;
            } else if (!this.helper.equals(other.helper))
                return false;
            if (this.method == null) {
                if (other.method != null)
                    return false;
            } else if (!this.method.equals(other.method))
                return false;
            if (this.params == null) {
                if (other.params != null)
                    return false;
            } else if (!this.params.equals(other.params))
                return false;
            if (this.parser == null) {
                if (other.parser != null)
                    return false;
            } else if (!this.parser.equals(other.parser))
                return false;
            if (this.path == null) {
                if (other.path != null)
                    return false;
            } else if (!this.path.equals(other.path))
                return false;
            return true;
        }

    }

    /**
     * Loads entries for the cache.
     *
     * @param <K>
     *            the key type
     */
    private class CacheLoader<K extends CacheKey<Object, Object>>
            extends com.google.common.cache.CacheLoader<K, Object> {

        /*
         * (non-Javadoc)
         * 
         * @see com.google.common.cache.CacheLoader#load(java.lang.Object)
         */
        @Override
        public Object load(final K key) throws Exception {
            return timestamp(key.request());
        }

        /**
         * Timestamps an object, if possible.
         *
         * @param o
         *            the object to timestamp
         * @return the timestamped object, or the unmodified object if it can
         *         not be timestamped
         */
        private Object timestamp(final Object o) {
            if (o instanceof List) { return new TimestampedList<>((List<?>) o, System.currentTimeMillis()); }
            return o;
        }
    }

}
