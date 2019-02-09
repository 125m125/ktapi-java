package de._125m125.kt.ktapi.websocket.responses.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.UpdateNotification;

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

        final UpdateNotification<?> parse = this.uut.parse(raw,
                Optional.of((JsonObject) new JsonParser().parse(raw)));

        assertEquals(true, parse.isSelfCreated());
        assertEquals("rOrders", parse.getChannel());
        assertEquals("1d0tat5", parse.getBase32Uid());
        assertEquals("update", parse.getType());
    }

    @Test
    public void testParsesGeneralNotificationMessages() throws Exception {
        final String raw = "{\"selfCreated\":false,\"uid\":0,\"base32Uid\":\"0\",\"type\":\"update\",\"details\":{\"source\":\"history\",\"channel\":\"history\"}}";

        final UpdateNotification<?> parse = this.uut.parse(raw,
                Optional.of((JsonObject) new JsonParser().parse(raw)));

        assertEquals(false, parse.isSelfCreated());
        assertEquals("history", parse.getChannel());
        assertEquals("0", parse.getBase32Uid());
        assertEquals("update", parse.getType());
    }

    @Test
    public void testParsesEmptyChangedEntries() {
        final String raw = "{\"selfCreated\":true,\"uid\":1510910885,\"base32Uid\":\"1d0tat5\",\"type\":\"update\",\"details\":{\"source\":\"messages\",\"key\":\"1d0tat5\",\"channel\":\"rMessages\"},contents:[]}";

        final UpdateNotification<?> parse = this.uut.parse(raw,
                Optional.of((JsonObject) new JsonParser().parse(raw)));

        assertFalse(parse.hasChangedEntries());
    }

    @Test
    public void testParsesMissingChangedEntries() {
        final String raw = "{\"selfCreated\":true,\"uid\":1510910885,\"base32Uid\":\"1d0tat5\",\"type\":\"update\",\"details\":{\"source\":\"messages\",\"key\":\"1d0tat5\",\"channel\":\"rMessages\"}}";

        final UpdateNotification<?> parse = this.uut.parse(raw,
                Optional.of((JsonObject) new JsonParser().parse(raw)));

        assertFalse(parse.hasChangedEntries());
    }

    @Test
    public void testParsesChangedEntries() {
        final String raw = "{\"selfCreated\":true,\"uid\":1510910885,\"base32Uid\":\"1d0tat5\",\"type\":\"update\",\"details\":{\"source\":\"messages\",\"key\":\"1d0tat5\",\"channel\":\"rMessages\"},contents:[{\"timestamp\":1514764800000,\"message\":\"hello world\"},{\"timestamp\":1514866096000,\"message\":\"hello second world\"}]}";

        final UpdateNotification<?> parse = this.uut.parse(raw,
                Optional.of((JsonObject) new JsonParser().parse(raw)));

        assertTrue(parse.hasChangedEntries());
        final Message first = (Message) parse.getChangedEntries()[0];
        assertEquals(
                ZonedDateTime.of(2018, Month.JANUARY.getValue(), 1, 0, 0, 0, 0, ZoneId.of("Z"))
                        .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
                first.getTime());
        assertEquals("hello world", first.getMessage());
        final Message second = (Message) parse.getChangedEntries()[1];
        assertEquals(
                ZonedDateTime.of(2018, Month.JANUARY.getValue(), 2, 4, 8, 16, 0, ZoneId.of("Z"))
                        .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
                second.getTime());
        assertEquals("hello second world", second.getMessage());
    }
}
