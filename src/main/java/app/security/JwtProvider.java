/*package app.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import app.model.XUser;*/

/**
 * Création/vérification/gestion d'un JWT
 */
/*@Component
@Profile("usejwt")
public class JwtProvider {

    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Par simplicité, nous stockons la clef de manière statique. il est sans doute
     * préférable d'avoir un autre API (sur un serveur de configuration) qui nous
     * fournisse la clé.
     */
    /*@Value("${security.jwt.token.secret-key:my-very-big-secret-phrase-for-signature}")
    private String secretText;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000; // 1h

    private SecretKey secretKey;

    @Autowired
    private JwtUserDetails myUserDetails;

    private Set<String> validTokens = new HashSet<>();


    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(secretText.getBytes());
    }

    public String createToken(XUser user) {

        logger.info("Entering the method createToken");
        var rolesAsString = user.getRoles().stream()//
                .filter(Objects::nonNull)//
                .collect(Collectors.toList());
        logger.info(" Recuperation roles finished");
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        logger.info("+++ create token for " + user.getUserName());
        logger.info("+++ date now = " + now);
        logger.info("+++ date validity = " + validity);

        String token =  Jwts.builder()//
                .subject(user.getUserName())//
                .claim("auth", rolesAsString)//
                .issuedAt(now)//
                .expiration(validity)//
                .signWith(secretKey).compact();

        validTokens.add(token);
        return token;
    }

    public Authentication getAuthentication(String token) {
        logger.info("Entering the method getAuthentication");
        UserDetails userDetails = myUserDetails.loadUserByUsername(getUsername(token));
        logger.info("UserDetails found");
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        logger.info("Entering the method getUsername");
        return Jwts.parser()//
                .verifyWith(secretKey).build()//
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        logger.info("+++ Before validate token " + token);
        try {
            logger.info("entering try");
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            boolean valid = validTokens.contains(token);
            if(!valid) {
                logger.info(" Not valid Token " + token);
            }
            return valid;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Expired or invalid JWT token");
        }
    }

    public void invalidateToken(String token) {
        logger.info("Entering the method invalidateToken");
        validTokens.remove(token);
    }

}*/
package app.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
@Profile("usejwt")
public class JwtProvider {

    @Value("${security.jwt.token.secret-key:my-very-big-secret-phrase-for-signature}")
    private String secretText;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000; // 1h

    private SecretKey secretKey;

    private final Set<String> validTokens = new HashSet<>();

    private final PersonUserDetails personUserDetails;

    public JwtProvider(PersonUserDetails personUserDetails) {
        this.personUserDetails = personUserDetails;
    }

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(secretText.getBytes());
    }

    public String createToken(String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String token = Jwts.builder()
                .subject(email)
                .claim("auth", role)
                .claim("jti", UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();

        validTokens.add(token);
        return token;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = personUserDetails.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return validTokens.contains(token);
        } catch (Exception e) {
            throw new JwtException("Expired or invalid JWT token");
        }
    }

    public void invalidateToken(String token) {
        validTokens.remove(token);
    }
}


