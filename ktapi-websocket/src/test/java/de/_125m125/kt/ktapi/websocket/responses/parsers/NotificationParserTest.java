package de._125m125.kt.ktapi.websocket.responses.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de._125m125.kt.ktapi.websocket.responses.UpdateNotification;

public class NotificationParserTest {

    private NotificationParser uut;

    @Before
    public void beforeNotificationParserTest() {
        this.uut = new NotificationParser();
    }

    @Test
    public void testDetectsUserSpecificNotificationMessages() throws Exception {
        final String raw = "{\"selfCreated\":true,\"uid\":1510910885,\"base32Uid\":\"1d0tat5\",\"type\":\"update\",\"details\":{\"source\":\"trades\",\"key\":\"1d0tat5\",\"channel\":\"rOrders\"}}";
        assertTrue(this.uut.parses(raw, Optional.of((JsonObject) new JsonParser().parse(raw))));
    }

    @Test
    public void testDetectsGeneralNotificationMessages() throws Exception {
        final String raw = "{\"selfCreated\":false,\"uid\":0,\"base32Uid\":\"0\",\"type\":\"update\",\"details\":{\"source\":\"history\",\"channel\":\"history\"}}";
        assertTrue(this.uut.parses(raw, Optional.of((JsonObject) new JsonParser().parse(raw))));
    }

    @Test
    public void testDoesNotDetectResponseMessages() throws Exception {
        final String raw = "{\"rid\":10,\"error\":false}";
        assertFalse(this.uut.parses(raw, Optional.of((JsonObject) new JsonParser().parse(raw))));
    }

    @Test
    public void testParsesUserSpecificNotificationMessages() throws Exception {
        final String raw = "{\"selfCreated\":true,\"uid\":1510910885,\"base32Uid\":\"1d0tat5\",\"type\":\"update\",\"details\":{\"source\":\"trades\",\"key\":\"1d0tat5\",\"channel\":\"rOrders\"}}";

        final UpdateNotification parse = this.uut.parse(raw,
                Optional.of((JsonObject) new JsonParser().parse(raw)));

        assertEquals(true, parse.isSelfCreated());
        assertEquals("rOrders", parse.getChannel());
        assertEquals("1d0tat5", parse.getBase32Uid());
        assertEquals("update", parse.getType());
    }

    @Test
    public void testParsesGeneralNotificationMessages() throws Exception {
        final String raw = "{\"selfCreated\":false,\"uid\":0,\"base32Uid\":\"0\",\"type\":\"update\",\"details\":{\"source\":\"history\",\"channel\":\"history\"}}";

        final UpdateNotification parse = this.uut.parse(raw,
                Optional.of((JsonObject) new JsonParser().parse(raw)));

        assertEquals(false, parse.isSelfCreated());
        assertEquals("history", parse.getChannel());
        assertEquals("0", parse.getBase32Uid());
        assertEquals("update", parse.getType());
    }
}
