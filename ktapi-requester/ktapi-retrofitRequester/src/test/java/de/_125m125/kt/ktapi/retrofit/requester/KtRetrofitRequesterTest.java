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
package de._125m125.kt.ktapi.retrofit.requester;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import de._125m125.kt.ktapi.core.PayoutType;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.results.ErrorResponse;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.results.WriteResult;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.retrofit.requester.modifier.RetrofitModifier;
import de._125m125.kt.okhttp.helper.modifier.ClientModifier;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.converter.gson.GsonConverterFactory;

public class KtRetrofitRequesterTest {

    private static final String BASE_URL = "https://does.not.exist";
    private FakeInterceptor     fakeInterceptor;
    private KtRetrofitRequester uut;

    @Before
    public void beforeKtRetrofitRequesterTest() throws Exception {
        this.fakeInterceptor = new FakeInterceptor();
        this.uut = new KtRetrofitRequester(getClass().getName(), KtRetrofitRequesterTest.BASE_URL,
                new ClientModifier[] { c -> c.addInterceptor(this.fakeInterceptor) },
                new RetrofitModifier[] {
                        r -> r.addConverterFactory(GsonConverterFactory.create()) },
                value -> new Gson().fromJson(value.charStream(), ErrorResponse.class));
    }

    @After
    public void afterKtRetrofitRequesterTest() {
        this.uut.close();
    }

    @Test(timeout = 1000)
    public void testCreatePayout() throws Exception {
        this.fakeInterceptor.setCode(200);
        this.fakeInterceptor
                .setResource("/de/_125m125/kt/ktapi/responses/payout/PayoutCreationSuccess.json");

        final Result<WriteResult<Payout>> result = this.uut
                .createPayout(new TokenUserKey("8", "16"), PayoutType.DELIVERY, "4", "1");

        assertEquals(true, result.isSuccessful());
        assertEquals(200, result.getStatus());
        assertEquals(PayoutType.DELIVERY.getComName(),
                result.getContent().getObject().getPayoutType());
        assertEquals("4", result.getContent().getObject().getMaterial());
        assertEquals(1, result.getContent().getObject().getAmount(), 1e-10);

        assertEquals("/users/8/payouts", this.fakeInterceptor.getUri().getPath());
        assertTrue("Body should contain payout type", this.fakeInterceptor.getBody()
                .contains("type=" + PayoutType.DELIVERY.getComName()));
        assertTrue("Body should contain material",
                this.fakeInterceptor.getBody().contains("item=4"));
        assertTrue("Body should contain payout type",
                this.fakeInterceptor.getBody().contains("amount=1"));
    }

    private static class FakeInterceptor implements Interceptor {
        private String resource;
        private String body;
        private URI    uri;
        private int    code = 200;

        @Override
        public Response intercept(final Chain chain) throws IOException {
            this.uri = chain.request().url().uri();
            final Buffer buffer = new Buffer();
            chain.request().body().writeTo(buffer);
            this.body = buffer.readUtf8();

            return new Response.Builder().code(getCode()).request(chain.request())
                    .protocol(Protocol.HTTP_1_0).message("success")
                    .body(ResponseBody.create(MediaType.parse("application/json"),
                            IOUtils.toString(KtRetrofitRequesterTest.class
                                    .getResourceAsStream(this.resource), StandardCharsets.UTF_8)))
                    .addHeader("content-type", "application/json").build();
        }

        public void setResource(final String resource) {
            this.resource = resource;
        }

        public int getCode() {
            return this.code;
        }

        public void setCode(final int code) {
            this.code = code;
        }

        public URI getUri() {
            return this.uri;
        }

        public String getBody() {
            return this.body;
        }

    }
}
