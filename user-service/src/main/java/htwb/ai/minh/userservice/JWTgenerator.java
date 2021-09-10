package htwb.ai.minh.userservice;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JWTgenerator {
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24h in ms
    private static final String SECRET_KEY = "minh_kbe_songsMS_2021";

    public String generateToken(User user) {
        return createToken(user.getUserId(), user.getFirstName() + user.getLastName());
    }

    private String createToken(String userId, String subject) {
        return Jwts.builder()
                .setId(userId)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
