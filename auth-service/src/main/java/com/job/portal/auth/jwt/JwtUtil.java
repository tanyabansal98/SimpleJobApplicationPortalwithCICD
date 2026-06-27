package com.job.portal.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * The JwtUtil class acts like a laminating machine and a security scanner for our JWT tokens.
 * It is responsible for creating (laminating) new tokens when a user logs in, and 
 * verifying (scanning) tokens when they are sent back to us.
 *
 * @Component: Tells Spring to automatically create and manage one single instance (Bean) 
 * of this class, which we can inject into other classes whenever we need JWT support.
 */
@Component
public class JwtUtil {

    // The secret passphrase used as a stamp for our signature. We read this from configuration.
    private final String secretString;

    // The duration in milliseconds before the token expires (becomes invalid).
    private final long expirationMs;

    // The actual cryptographic key generated from our secretString.
    private final SecretKey secretKey;

    /**
     * Constructor: Spring will automatically call this and inject the configuration values.
     * 
     * @Value("${jwt.secret}"): Reads the secret string from application.yml.
     * @Value("${jwt.expiration-ms}"): Reads the token lifetime in milliseconds.
     */
    public JwtUtil(
            @Value("${jwt.secret}") String secretString,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretString = secretString;
        this.expirationMs = expirationMs;
        
        // Keys.hmacShaKeyFor: Converts our plain text password into a secure cryptographic key 
        // using the HMAC-SHA algorithm. This key is used to sign and verify our tokens.
        // It requires the key to be at least 256 bits (32 bytes/characters) long.
        this.secretKey = Keys.hmacShaKeyFor(this.secretString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a new JWT token for a user.
     * Analogy: Creating a new wristband for a concert-goer at the ticket gate.
     *
     * @param email The user's email address (will be the Subject / Identifier)
     * @param role The user's access level (e.g. STUDENT, EMPLOYER)
     * @param userId The unique ID of the user in our database
     * @return A signed JWT token string
     */
    public String generateToken(String email, String role, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        // Jwts.builder() is the assembly line for creating the token.
        return Jwts.builder()
                // 1. Subject: Who does this token represent? We store the email here.
                .subject(email)
                // 2. Custom Claims: Custom details we want to write onto the wristband.
                .claim("role", role)
                .claim("userId", userId)
                // 3. Metadata: When was this wristband issued?
                .issuedAt(now)
                // 4. Metadata: When does this wristband expire?
                .expiration(expiryDate)
                // 5. Signature: Securely seal the token with our Secret Key using HS256 algorithm.
                .signWith(secretKey, Jwts.SIG.HS256)
                // 6. Compact: Pack the header, payload, and signature into a single dot-separated string.
                .compact();
    }

    /**
     * Validates if a token is authentic, untampered, and not expired.
     * Analogy: The security guard at the gate checking if the wristband is real and still valid.
     *
     * @param token The JWT token string sent by the client
     * @return true if valid, false if invalid or expired
     */
    public boolean validateToken(String token) {
        try {
            // We parse the token. If parsing succeeds without throwing an exception, 
            // the signature is valid and the token has not expired.
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // JwtException covers signatures that don't match, expired tokens, or malformed tokens.
            // Returning false rather than throwing keeps our controller simple.
            return false;
        }
    }

    /**
     * Extracts all information (claims) stored inside the token.
     * Analogy: Reading the text printed on the wristband.
     *
     * @param token The JWT token string
     * @return The Claims object representing the token's payload
     * @throws JwtException if the token cannot be parsed, has an invalid signature, or is expired
     */
    public Claims extractClaims(String token) {
        // Jwts.parser() is the scanner machine.
        return Jwts.parser()
                // 1. Tell the parser what Secret Key it must use to check the signature.
                .verifyWith(secretKey)
                // 2. Build the parser instance.
                .build()
                // 3. Parse the token. If someone tampered with it, this step will throw an exception.
                .parseSignedClaims(token)
                // 4. Retrieve the payload (the claims).
                .getPayload();
    }
}
