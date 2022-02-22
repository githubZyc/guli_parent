package com.atguigu.commonutils.tool;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * JWT包含信息：
 * 每一个子串表示了一个功能块，总共有以下三个部分：JWT头（Header）、有效载荷(Claims)、签名(Signature)
 * JWT Header(头)：JWT头部分是一个描述JWT元数据的JSON对象，通常如下所示：
 * {
 *   "alg": "HS256",
 *   "typ": "JWT"
 * }
 * 在上面的代码中：
 * alg：属性表示签名使用的算法。默认为HMAC SHA256（写为HS256）；
 * typ：属性表示令牌的类型。JWT令牌统一写为JWT。最后，使用Base64 URL算法将上述JSON对象转换为字符串保存。
 *
 * Claims(有效载荷)：有效载荷部分，是JWT的主体内容部分，也是一个JSON对象，包含需要传递的数据。 JWT指定七个默认字段供选择。
 * 默认可用字段：
 * {
 * iss：发行人
 * exp：到期时间
 * sub：主题
 * aud：用户
 * nbf：在此之前不可用
 * iat：发布时间
 * jti：JWT ID用于标识该JWT
 * }。除默认字段外可自定义扩展字段用于载体内容
 *
 * Signature（签名）：
 * 签名哈希部分是对上面两部分数据签名，通过指定的算法生成哈希，以确保数据不会被篡改。
 *
 */
public class JWTUtils {
    //过期时间
    public static final long EXPIRE = 1000 * 60 * 60 * 24;
    //签名串：服务端保存 不可泄露
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";

    public static String getJwtToken(String id, String nickname){
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("guli-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .claim("id", id)
                .claim("nickname", nickname)
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
    }

    /**
     * 判断token是否存在与有效
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从header头中获取token,判断token是否存在与有效
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if(StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token获取会员id
     * @param request
     * @return
     */
    public static String getMemberIdByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if(StringUtils.isEmpty(jwtToken)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("id");
    }

    /**
     * 根据token获取会员id
     * @param jwtToken
     * @return
     */
    public static String getMemberIdByJwtToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("id");
    }
}
