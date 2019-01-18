package de._125m125.kt.ktapi.websocket;

import java.lang.reflect.Array;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import de._125m125.kt.ktapi.core.entities.Entity;
import de._125m125.kt.ktapi.core.entities.UpdateNotification;

public class SemiparsedUpdateNotification<T> extends UpdateNotification<T> {

    private final JsonArray contents;

    public SemiparsedUpdateNotification(final boolean selfCreated, final long uid,
            final String base32Uid, final Map<String, String> details, final JsonArray contents) {
        super(selfCreated, uid, base32Uid, details, null);
        this.contents = contents;
    }

    @Override
    public boolean hasChangedEntries() {
        return this.contents != null && this.contents.size() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] getChangedEntries() {
        if (this.contents == null) {
            return (T[]) new Object[0];
        }
        if (!super.hasChangedEntries()) {
            super.changedEntries = (T[]) new Gson().fromJson(this.contents,
                    Array.newInstance(Entity.forUpdateChannel(getSource()).getInstanceClass(), 0)
                            .getClass());
        }
        return super.getChangedEntries();
    }
}
