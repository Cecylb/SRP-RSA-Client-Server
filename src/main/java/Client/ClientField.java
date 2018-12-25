package Client;

import Magic.RSA;
import Magic.SRP;
import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

public class ClientField {

    private String s; // Соль, созланная при регистрации
    private long v; // Верификатор пароля
    private long bBig; // (k*v + g^b % N) % N
    private long aBig; // g^a % N
    private String m; // Подтверждение, генерируемое сервером
    private String r; // Подтверждение, генерируемое клиентом
    private String u; // Скремблер
    private String k; // Общий ключ сессии
    private BigInteger x; // секретный ключ, x = H(s, p)
    private String password; // Пароль
    private String login; // Логин
    private int a; // Случайное число, генерируемое клиентом
    private RSA rsa = new RSA(2048);

    ClientField(String login, String password) {
        new SRP();
        this.password = password;
        this.login = login;
        s = UUID.randomUUID().toString();
        String hash = SRP.getHash((s + this.password).getBytes());
        x = new BigInteger(hash, 16);
        System.out.println(x);
        BigInteger pv = BigInteger.valueOf(SRP.getG()).modPow(x, BigInteger.valueOf(SRP.getN()));
        v = Long.valueOf(pv.toString());
    }
// Генерация подтверждения
    String confirmationHash() {
        BigInteger ex1 = new BigInteger(SRP.getHash(Long.valueOf(SRP.getN()).toString().getBytes()), 16);
        BigInteger ex2 = new BigInteger(SRP.getHash(Long.valueOf(SRP.getG()).toString().getBytes()), 16);
        String arg1 = ex1.xor(ex2).toString();
        byte[] args = (arg1 + SRP.getHash(login.getBytes()) + s + aBig + bBig + k).getBytes();
        m = SRP.getHash(args);
        return m;
    }
// Вычисление k, r, a
    void keyComp() {
        BigInteger S;
        BigInteger U = new BigInteger(u, 16);
        long ex1 = bBig - SRP.getK() * v;
        BigInteger ex2 = U.multiply(x).add(BigInteger.valueOf(a));
        S = BigInteger.valueOf(ex1).modPow(ex2, BigInteger.valueOf(SRP.getN()));
        k = SRP.getHash(S.toString().getBytes());
    }
    String compR() {
        r = SRP.getHash((aBig + m + k).getBytes());
        return r;
    }
    long compA() {
        a = randomNatural();
        aBig = SRP.powMod(SRP.getG(), a, SRP.getN());
        return aBig;
    }
    int randomNatural() {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(1000000 - 10000) + 10000;
    }
// Вычисление скремблера
    String scrambler() {
        long a = aBig;
        long b = bBig;
        String con = a + String.valueOf(b);
        String u = SRP.getHash(con.getBytes());
        this.u = u;
        return u;
    }

// Геттеры
    public String getSalt() {
        return s;
    }
    public long getPass_verifier() {
        return v;
    }
    public String getId() {
        return login;
    }
    public String getR() {
        return r;
    }
    public String getM() {
        return m;
    }
    public BigInteger getE() { return rsa.getE(); }
    public BigInteger getN() { return rsa.getN(); }
    public BigInteger getMsg(BigInteger msg) {return rsa.decrypt(msg); }
    public BigInteger getC(String text) { return (rsa.encrypt(new BigInteger(text.getBytes())));  }
// Сеттеры
    public void setbBig(long bBig) {
        this.bBig = bBig;
    }
    public void setKeys(BigInteger n, BigInteger e) { rsa.setKeys(n, e);}
}