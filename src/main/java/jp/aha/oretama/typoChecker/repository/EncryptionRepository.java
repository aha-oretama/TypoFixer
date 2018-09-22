package jp.aha.oretama.typoChecker.repository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jp.aha.oretama.typoChecker.utils.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Date;

/**
 * @author aha-oretama
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class EncryptionRepository {
    @Value("${application.pem-file}")
    private String PEM_FILE;
    @Value("${application.app-id}")
    private String appId;
    private final ResourceLoader resourceLoader;

    private static final long EXPIRATION_TIME = 30 * 1000; // 30 second.
    private static final long TIME_DELTA = 5 * 1000; // 5 second.

    private PrivateKey getPrivateKey() throws IOException, GeneralSecurityException {
        String pemStr = System.getenv().getOrDefault("PEM", "");
        if (StringUtils.isEmpty(pemStr)) {
            Resource resource = resourceLoader.getResource("classpath:" + PEM_FILE);
            return EncryptionUtil.getPrivateKey(resource.getInputStream());
        }
        return EncryptionUtil.getPrivateKey(pemStr);
    }

    public String getJwt() throws IOException, GeneralSecurityException {
        String jwt = Jwts.builder().setIssuer(appId)
                .setIssuedAt(new Date(System.currentTimeMillis() - TIME_DELTA))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.RS256, getPrivateKey())
                .compact();
        log.info(jwt);
        return jwt;
    }
}
