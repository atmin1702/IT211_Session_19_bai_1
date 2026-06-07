package atmin;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class TokenService {

    private SecretKey key;

    private final long ACCESS_TOKEN_EXPIRATION_MS = TimeUnit.MINUTES.toMillis(15);

    @PostConstruct
    public void init() {
        // Giữ nguyên Secret Key của đề bài hoặc cấu hình của bạn
        String SECRET_KEY = "superSecretKeysuperSecretKeysuperSecretKey";
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // ĐÁP ỨNG ĐỀ BÀI: Hàm nhận vào String username thay vì Object User
    public String generateAccessToken(String username) {
        long nowMillis = System.currentTimeMillis();
        Date issuedAtDate = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + ACCESS_TOKEN_EXPIRATION_MS);

        return Jwts.builder()
                .subject(username)
                .expiration(expirationDate)
                .issuedAt(issuedAtDate)
                .signWith(key)
                .compact();
    }

    // Hàm validate trả về boolean (bạn có thể giữ ném lỗi hoặc return false tùy ý thích,
    // ở đây viết theo mẫu gốc của đề bài cho khớp)
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Hàm bổ trợ phục vụ cho hàm main kiểm thử phía dưới
    public Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    // ĐÁP ỨNG PHẦN 2: Hàm main chứng minh thời gian hết hạn đúng 15 phút
    public static void main(String[] args) {
        TokenService service = new TokenService();
        service.init(); // Kích hoạt secret key

        String testUser = "atmin_user";
        long createTimeMs = System.currentTimeMillis();
        String token = service.generateAccessToken(testUser);

        // Trích xuất lại thời gian từ token
        Date expireDate = service.extractExpiration(token);
        long expireTimeMs = expireDate.getTime();

        // Tính toán khoảng chênh lệch
        long durationMs = expireTimeMs - createTimeMs;
        long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMs);

        System.out.println("=== KẾT QUẢ KIỂM THỬ NỘP BÀI ===");
        System.out.println("Thời gian khởi tạo: " + new Date(createTimeMs));
        System.out.println("Thời gian hết hạn của Token: " + expireDate);
        System.out.println("Thời gian sống tính bằng phút: " + durationMinutes + " phút");

        if (durationMinutes == 15) {
            System.out.println("=> ĐÁP ÁN: CHÍNH XÁC (Token có thời hạn đúng 15 phút).");
        } else {
            System.out.println("=> ĐÁP ÁN: SAI.");
        }
    }
}