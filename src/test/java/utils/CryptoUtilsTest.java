package utils;

import junit.framework.TestCase;

import java.util.Arrays;

import static utils.CryptoUtils.getHashAsByte;
import static utils.CryptoUtils.getHashAsString;

/**
 * Created by dshelygin on 25.09.2017.
 */
public class CryptoUtilsTest extends TestCase {
    public void testGetHashAsByte() throws Exception {
        byte[] expectedResult = new byte[] { 117,-19,21,-40,77,-8,66,-111,-58,127,-32, 123, -14, 52, -84, 105, -23, 42,
            -100, 42, 55, -114, -26, 47, 52, 42, -9, 57, -24, 41, -21, -87 };
        byte[] actualResult = getHashAsByte("Hello World".getBytes(),"GOST3411");;
        assertTrue(Arrays.equals(expectedResult,actualResult));

    }

    public void testGetHashAsString() throws Exception {
        String expectedResult = "75ED15D84DF84291C67FE07BF234AC69E92A9C2A378EE62F342AF739E829EBA9";
        String actualResult = getHashAsString ("Hello World".getBytes(),"GOST3411");

        assertEquals(expectedResult,actualResult);

    }

}