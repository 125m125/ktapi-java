package de._125m125.kt.ktapi.core.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class KtUserStoreTest {

    private AbstractTokenUser       initialUserA;
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
        assertEquals(this.initialUserA,
                this.uut.get(new TokenUserKey("1", "2"), TokenUserKey.class));
        assertEquals(this.initialUserB,
                this.uut.get(this.initialUserB.getKey(), CertificateUserKey.class));
    }

    @Test
    public void testAddUser() {
        final AbstractTokenUser newUser = new TokenUser("2", "3", "4");
        assertNull(this.uut.add(newUser));
        assertEquals(newUser, this.uut.get(newUser.getKey(), TokenUserKey.class));
    }

    @Test
    public void testReplaceUser() {
        final AbstractTokenUser newUser = new TokenUser("1", "2", "4");
        assertEquals(this.initialUserA, this.uut.add(newUser));
        assertEquals(newUser, this.uut.get(newUser.getKey(), TokenUserKey.class));
    }

    @Test
    public void testClassIdentifier() {
        final CertificateUser user = new CertificateUser("1", "2", new char[] { 'a' });
        assertNull(this.uut.get(user.getKey(), CertificateUserKey.class));
        assertNull(this.uut.add(user));
    }

    @Test
    public void testGetByIdentifier() {
        assertEquals(this.initialUserA, this.uut
                .get("de._125m125.kt.ktapi.core.users.TokenUserKey:1:2", TokenUserKey.class));
    }

    @Test
    public void testGetByIdentifierWithMismatchingType() {
        assertNull(this.uut.get("de._125m125.kt.ktapi.core.users.TokenUserKey:1:2",
                CertificateUserKey.class));
    }

    @Test
    public void testGetByIdUserIdentifier() {
        assertEquals(this.initialUserA,
                this.uut.get("de._125m125.kt.ktapi.core.users.IdUserKey:1:", TokenUserKey.class));
    }

    @Test
    public void testGetByIdUserIdentifierWithMultipleMatches() {
        final AbstractTokenUser user = new TokenUser("1", "3", "4");
        this.uut.add(user);
        final AbstractTokenUser result = this.uut.get("de._125m125.kt.ktapi.core.users.IdUserKey:1:",
                TokenUserKey.class);
        assertNotNull(result);
        assertTrue(result.equals(this.initialUserA) || result.equals(user));
    }

    @Test
    public void testGetUnknownUserByIdUserIdentifier() {
        final AbstractTokenUser result = this.uut.get("de._125m125.kt.ktapi.core.users.IdUserKey:2:",
                TokenUserKey.class);
        assertNull(result);
    }

    @Test
    public void testGetUnknownUserKeyByIdUserIdentifier() {
        final IdUser result = this.uut.get("de._125m125.kt.ktapi.core.users.IdUserKey:1:",
                IdUserKey.class);
        assertNull(result);
    }

}
