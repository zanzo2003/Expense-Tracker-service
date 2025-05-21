package com.bhaskarshashwath.expense.tracker.service;

import com.bhaskarshashwath.expense.tracker.entities.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.awt.dnd.DropTarget;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import java.util.Map;


@Service
public class JwtService {


    private final String SECRET = "e8b9c4f2a1d7e5b0394c6a8f3d2e9b1c5a7f4d8e0b3c9a2f6e1d5b7a4c8f0e2";
    private final int rounds = 12;

    public String getUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extracatAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Boolean validateToken(String token, UserInfo user){
        return ( getUsername(token).equals(user.getUsername()) && !isTokenExpired(token));
    }

    public String createToken(Map<String, Object> claims, String username){

        return Jwts
                .builder()
                .claims(claims)
                .subject(username)
                .setIssuedAt( new Date(System.currentTimeMillis()))
                .setExpiration( new Date(System.currentTimeMillis() + 1000*60*1)) // set the expiration time to 1 minute from creation
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Claims extracatAllClaims(String token){
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(this.SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
