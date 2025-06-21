import org.jasypt.util.text.BasicTextEncryptor;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        a = Math.abs(a);
        b = Math.abs(b);
        if(a>b)
            a = b ^ a^(b = a);
        System.out.printf("Trong đoạn [%d, %d] có 4 số nguyên tố là", a, b);
        for(int i = a ; i<=b ;i++){
            if(MillerRabin.isPrime(i))
                System.out.print(" "+i);
        }
        System.out.print(".");
    }

}

class Time{
    public static void main(String[] args) {
        PrettyTime p = new PrettyTime(new Locale("vi"));
       // PrettyTime p = new PrettyTime(Locale.Vi);
        Date updatedAt = new Date(System.currentTimeMillis() + 32 * 60 * 1000); // cách đây 32 phút


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime one = LocalDateTime.now().minusDays(1);
        LocalDateTime tow = LocalDateTime.now().plusDays(2);
        Duration duration = Duration.between(now, tow);
        Duration duration2 = Duration.between(one, now);
        System.out.println("Còn lại: " + duration.toHours() + " giờ (" + duration.toDays() + " ngày)");
        System.out.println("Còn lại: " + duration2.toHours() + " giờ (" + duration2.toDays() + " ngày)");
    }
}

class MapClass{
    public static void main(String[] args) {
        for(int i = 0 ; i<30;i++){
            int to = (int)(Math.random()*20);
            int from = (int)(Math.random()*(Math.max(to - 3, 0)));
            from= Math.max(from, to-10);
            System.out.println(i+": " +from +" " +to);
        }
    }
}

class MillerRabin{
    public static boolean isPrime(long n) {
        if (n < 2) return false;
        if (n < 4) return true;
        if (n % 2 == 0) return false;

        long d = n - 1;
        int r = 0;
        while (d % 2 == 0) {
            d /= 2;
            r++;
        }

        long[] bases = 	{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        for (long a : bases) {
            if (a >= n) continue;
            if (!millerTest(a, d, n, r)) return false;
        }
        return true;
    }

    private static boolean millerTest(long a, long d, long n, int r) {
        long x = powMod(a, d, n);
        if (x == 1 || x == n - 1) return true;

        for (int i = 1; i < r; i++) {
            x = mulMod(x, x, n);
            if (x == n - 1) return true;
        }
        return false;
    }

    private static long powMod(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1)
                result = mulMod(result, base, mod);
            base = mulMod(base, base, mod);
            exp >>= 1;
        }
        return result;
    }

    private static long mulMod(long a, long b, long mod) {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).longValue();
    }
}

class RSAManualDecrypt {

    public static void main(String[] args) {
        BigInteger n = new BigInteger("71641831546926719303369645296528546480083425905458247405279061196214424558100678947996271179659761521775290973790597533683668081173314940392098256721488468660504161994357");

        BigInteger e = new BigInteger("65537");

        BigInteger c = new BigInteger("63127079832500412362950100242549738176318170072331491750802716138621322974529994914407846448954487685068331564008936808539420562251661435790855422130443584773306161128156");

        BigInteger p = new BigInteger("8464149782874043593254414191179506861158311266932799636000173971661904149225893113311");
        BigInteger q = new BigInteger("8464149782874043593254414191179506861158311266932799636000173971661904149225893113387");

        // Tính phi(n) = (p - 1)(q - 1)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // Tính d = e^-1 mod phi
        BigInteger d = e.modInverse(phi);

        // Giải mã: m = c^d mod n
        BigInteger m = c.modPow(d, n);
        System.out.println("Check n = p * q: " + p.multiply(q).equals(n));
        // Chuyển kết quả về chuỗi
        String message = new String(m.toByteArray(), StandardCharsets.UTF_8);
        System.out.println("Decrypted message: " + message);
    }
}



 class FermatFactor {
    // Trả về phần nguyên tiếp theo đảm bảo a^2 ≥ n
    static BigInteger ceilSqrt(BigInteger n) {
        BigInteger a = new BigInteger(n.sqrt().toString());
        if (a.multiply(a).compareTo(n) < 0) a = a.add(BigInteger.ONE);
        return a;
    }

    public static void main(String[] args) {
        BigInteger n = new BigInteger("71641831546926719303369645296528546480083425905458247405279061196214424558100678947996271179659761521775290973790597533683668081173314940392098256721488468660504161994357");


        BigInteger a = ceilSqrt(n);
        BigInteger b2 = a.multiply(a).subtract(n);

        while (true) {
            BigInteger b = b2.sqrt();
            if (b.multiply(b).equals(b2)) {
                BigInteger p = a.subtract(b);
                BigInteger q = a.add(b);
                System.out.println("Found factors p=" + p + " and q=" + q);
                break;
            }
            a = a.add(BigInteger.ONE);
            b2 = a.multiply(a).subtract(n);
        }
    }
}


