package com.example.test.payment;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String secretKey = "re4etnlejhsuvSCFSGKCMSUY142857cdfcdsjigir56rh3rdcjyj6wqvghyubvkfbvjynhbcdshfregv395409vnfj5f002922935eu67ythdgfafweavtfgjsxdslfhgedgbuytv";
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)  // Sử dụng secret key để giải mã
                    .parseClaimsJws(token)     // Giải mã token
                    .getBody();
            // Trả về userId (trong payload của token)
            return claims.get("userId", Long.class);
        } catch (SignatureException e) {
            throw new IllegalArgumentException("Invalid JWT signature", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    // Kiểm tra token có hợp lệ không (ví dụ, đã hết hạn chưa)
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;  // Token hợp lệ
        } catch (SignatureException e) {
            return false;  // Token không hợp lệ
        } catch (Exception e) {
            return false;  // Các lỗi khác
        }
    }
}
