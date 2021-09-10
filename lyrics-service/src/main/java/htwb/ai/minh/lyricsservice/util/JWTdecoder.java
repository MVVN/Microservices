package htwb.ai.minh.lyricsservice.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

@Component
public class JWTdecoder {
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24h in ms
    private static final String SECRET_KEY = "minh_kbe_songsMS_2021";

    public boolean isTokenValid(String token) {
        try {
            checkToken(token);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public Claims checkToken(String token) throws ExpiredJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody();
    }
}
