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
package de._125m125.kt.okhttp.helper.modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import de._125m125.kt.okhttp.helper.modifier.HeaderAdder.ConflictMode;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Request;

@RunWith(JUnitParamsRunner.class)
public class HeaderAdderTest {

    private void runTest(final HeaderAdder uut, final Request.Builder requestBuilder,
            final List<String> expected) throws IOException {
        final Interceptor interceptor = uut.createInterceptor();
        final Chain chain = mock(Chain.class);

        final Request request = requestBuilder.url("https://verylikelynonexistingurl.abc").build();
        when(chain.request()).thenReturn(request);
        final Request[] result = new Request[1];
        when(chain.proceed(any())).then(invocation -> {
            if (result[0] != null) {
                fail("a result was already set!");
            } else {
                result[0] = invocation.getArgumentAt(0, Request.class);
            }
            return null;
        });
        interceptor.intercept(chain);
        if (result[0] == null) {
            fail("chain did not proceed");
        }
        assertEquals(expected, result[0].headers("test-header"));
    }

    @Test
    @Parameters
    public void testModifyRequestCommon(final HeaderAdder uut, final Request.Builder requestBuilder,
            final List<String> expected) throws Exception {
        runTest(uut, requestBuilder, expected);
    }

    @Test(expected = IllegalStateException.class)
    public void testModifyRequestConflictAbort() throws Exception {
        runTest(new HeaderAdder("test-header", "failure", ConflictMode.ABORT),
                new Request.Builder().addHeader("test-header", "initialHeader"),
                Arrays.asList("impossible"));
    }

    @Test
    public void testModifyRequestConflictAppend() throws Exception {
        runTest(new HeaderAdder("test-header", "success", ConflictMode.APPEND),
                new Request.Builder().addHeader("test-header", "initialHeader"),
                Arrays.asList("initialHeader", "success"));
    }

    @Test
    public void testModifyRequestConflictReplace() throws Exception {
        runTest(new HeaderAdder("test-header", "success", ConflictMode.REPLACE),
                new Request.Builder().addHeader("test-header", "initialHeader"),
                Arrays.asList("success"));
    }

    @Test
    public void testModifyRequestConflictSkip() throws Exception {
        runTest(new HeaderAdder("test-header", "success", ConflictMode.SKIP),
                new Request.Builder().addHeader("test-header", "initialHeader"),
                Arrays.asList("initialHeader"));
    }

    public Object[][] parametersForTestModifyRequestCommon() {
        final ConflictMode[] cmValues = ConflictMode.values();
        final int cmLength = cmValues.length;

        final Object[][] result = new Object[cmLength * 5][3];

        int currentOffset = 0;
        // all conflict modes add header if header has no previous value
        for (int i = 0; i < cmValues.length; i++) {
            final ConflictMode cm = cmValues[i];
            result[i][0] = new HeaderAdder("test-header", "success", cm);
            result[i + cmLength][0] = new HeaderAdder("test-header",
                    r -> r != null ? "success" : "failure", cm);
        }
        for (int i = 0; i < cmValues.length * 2; i++) {
            result[i][1] = new Request.Builder();
            result[i][2] = Arrays.asList("success");
        }
        currentOffset += cmLength * 2;

        // all conflict modes remove headers if header has previous value and new header is empty
        // string
        for (int i = 0; i < cmValues.length; i++) {
            final ConflictMode cm = cmValues[i];
            result[i + currentOffset][0] = new HeaderAdder("test-header", "", cm);
            result[i + cmLength + currentOffset][0] = new HeaderAdder("test-header",
                    r -> r != null ? "" : "failure", cm);
        }
        for (int i = 0; i < cmValues.length * 2; i++) {
            result[i + currentOffset][1] = new Request.Builder().addHeader("test-header",
                    "initialHeader");
            result[i + currentOffset][2] = Arrays.asList();
        }
        currentOffset += cmLength * 2;

        // all conflict modes do nothing if inserting null
        for (int i = 0; i < cmValues.length; i++) {
            final ConflictMode cm = cmValues[i];
            result[i + currentOffset][0] = new HeaderAdder("test-header",
                    r -> r != null ? null : "failure", cm);
            result[i + currentOffset][1] = new Request.Builder().addHeader("test-header",
                    "initialHeader");
            result[i + currentOffset][2] = Arrays.asList("initialHeader");
        }
        currentOffset += cmLength;

        return result;
    }
}
