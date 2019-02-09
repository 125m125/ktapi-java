package de._125m125.kt.ktapi.smartcache;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;

public class InvalidationCallbackTest {

    private InvalidationCallback<String> uut;
    private KtSmartCache                 requester;

    @Before
    public void setUp() throws Exception {
        this.requester = mock(KtSmartCache.class);
        this.uut = new InvalidationCallback<>(this.requester, "testKey");
    }

    @Test
    public void testOnErrorInvalidates() {
        this.uut.onError(new Throwable());

        verify(this.requester, times(1)).invalidate("testKey", null);
    }

    @Test
    public void testOnFailureDoesNotInvalidateOnClientError() {
        this.uut.onFailure(400, "invalid", "testing");

        verify(this.requester, times(0)).invalidate(any(), any());
    }

    @Test
    public void testOnFailureInvalidatesOnServerError() {
        this.uut.onFailure(500, "invalid", "testing");

        verify(this.requester, times(1)).invalidate("testKey", null);
    }

    @Test
    public void testOnSuccessInvalidatesOnSuccess() {
        this.uut.onSuccess(200, "success");

        verify(this.requester, times(1)).invalidate(eq("testKey"),
                AdditionalMatchers.aryEq(new String[] { "success" }));
    }
}
