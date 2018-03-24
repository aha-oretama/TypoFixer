package jp.aha.oretama.typoChecker;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @author aha-oretama
 */
public class EncryptionUtil {

    public static RSAPrivateKey getPrivateKey(Path path) throws IOException, GeneralSecurityException {
        String key = Files.readAllLines(path).stream().reduce((s, s2) -> String.join("\n", s, s2)).get();
        return getPrivateKey(key);
    }

    public static RSAPrivateKey getPrivateKey(InputStream stream) throws IOException, GeneralSecurityException {
        String key = IOUtils.toString(stream, Charset.defaultCharset());
        return getPrivateKey(key);
    }

    private static RSAPrivateKey getPrivateKey(String key) throws GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) kf.generatePrivate(keySpec);
    }
}
