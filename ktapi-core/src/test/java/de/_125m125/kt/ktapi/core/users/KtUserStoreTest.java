package de._125m125.kt.ktapi.core.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class KtUserStoreTest {

    private TokenUser       initialUserA;
    private CertificateUser initialUserB;

    private KtUserStore     uut;

    @Before
    public void beforeKtUserStoreTest() {
        this.initialUserA = new TokenUser("1", "2", "3");
        this.initialUserB = new CertificateUser("1", "path/to/cert", new char[] { 'a' });

        this.uut = new KtUserStore(this.initialUserA, this.initialUserB);
    }

    @Test
    public void testRetrieveInitialUsers() {
        assertEquals(this.initialUserA, this.uut.get(new TokenUserKey("1", "2"), TokenUser.class));
        assertEquals(this.initialUserB,
                this.uut.get(this.initialUserB.getKey(), CertificateUser.class));
    }

    @Test
    public void testAddUser() {
        final TokenUser newUser = new TokenUser("2", "3", "4");
        assertNull(this.uut.add(newUser));
        assertEquals(newUser, this.uut.get(newUser.getKey(), TokenUser.class));
    }

    @Test
    public void testReplaceUser() {
        final TokenUser newUser = new TokenUser("1", "2", "4");
        assertEquals(this.initialUserA, this.uut.add(newUser));
        assertEquals(newUser, this.uut.get(newUser.getKey(), TokenUser.class));
    }

    @Test
    public void testClassIdentifier() {
        final CertificateUser user = new CertificateUser("1", "2", new char[] { 'a' });
        assertNull(this.uut.get(user.getKey(), CertificateUser.class));
        assertNull(this.uut.add(user));
    }

    @Test
    public void testGetByIdentifier() {
        assertEquals(this.initialUserA,
                this.uut.get("de._125m125.kt.ktapi.core.users.TokenUser:1:2", TokenUser.class));
    }

    @Test
    public void testGetByIdentifierWithMismatchingType() {
        assertNull(this.uut.get("de._125m125.kt.ktapi.core.users.TokenUser:1:2",
                CertificateUser.class));
    }

    @Test
    public void testGetByIdUserIdentifier() {
        assertEquals(this.initialUserA,
                this.uut.get("de._125m125.kt.ktapi.core.users.IdUser:1:", TokenUser.class));
    }

    @Test
    public void testGetByIdUserIdentifierWithMultipleMatches() {
        final TokenUser user = new TokenUser("1", "3", "4");
        this.uut.add(user);
        final TokenUser result = this.uut.get("de._125m125.kt.ktapi.core.users.IdUser:1:",
                TokenUser.class);
        assertNotNull(result);
        assertTrue(result.equals(this.initialUserA) || result.equals(user));
    }

    @Test
    public void testGetUnknownUserByIdUserIdentifier() {
        final TokenUser result = this.uut.get("de._125m125.kt.ktapi.core.users.IdUser:2:",
                TokenUser.class);
        assertNull(result);
    }

    @Test
    public void testGetUnknownUserKeyByIdUserIdentifier() {
        final IdUser result = this.uut.get("de._125m125.kt.ktapi.core.users.IdUserKey:1:",
                IdUser.class);
        assertNull(result);
    }

}
