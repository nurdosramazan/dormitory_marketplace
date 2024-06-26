package nu.senior_project.dormitory_marketplace.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt_secret_key}")
    private String SECRET_KEY;
    private Long expirationPeriod = 30L * 24 * 60 * 60 * 1000;
    public boolean isValidToken(String token, UserDetails userDetails) {
        Claims claims = extractClaims(token);
        String username = extractUsername(claims);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(claims);
    }

    private boolean isTokenExpired(Claims claims) {
        return extractExpirationTime(claims).before(new Date());
    }

    public Date extractExpirationTime(Claims claims) {
        return claims.getExpiration();
    }
    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public String extractUsername(String token) {
        return extractUsername(extractClaims(token));
    }

    public String generateJwtToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationPeriod))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.
                parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

}
