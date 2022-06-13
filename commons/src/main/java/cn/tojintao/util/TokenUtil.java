package cn.tojintao.util;

import cn.tojintao.exception.ConditionException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Calendar;
import java.util.Date;

public class TokenUtil {
    private static final String SECRET_KEY = "qwertyuiop";
    private static final String ISSUER = "签发者";

    public static String generateAccessToken(Integer userId) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 2);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    public static String generateRefreshToken(Integer userId) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    public static Integer verifyToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getKeyId();
            return Integer.valueOf(userId);
        } catch (TokenExpiredException e){
            throw new ConditionException(402, "token过期！");
        } catch (Exception e){
            throw new ConditionException("token认证失败！");
        }
    }
}
