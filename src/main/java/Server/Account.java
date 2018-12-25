package Server;

import java.math.BigInteger;

public class Account {
    private String s; // Соль, созланная при регистрации
    private long v; // Верификатор пароля
    private long bBig; // (k*v + g^b % N) % N
    private long aBig; // g^a % N
    private long b; // Случайное число, генерируемое сервером
    private String m; // Подтверждение, генерируемое клиентом
    private String r; // Подтверждение, генерируемое сервером
    private String u; // Скремблер
    private String k; // Общий ключ сессии
    private BigInteger n;
    private BigInteger e;
// Геттеры
    public String getUserS() { return s; }
    public long getv() { return v; }
    public long getB() {
    return b;
}
    public String getM() {
        return m;
    }
    public String getR() {
        return r;
    }
    public String getKey() { return k; }
    public long getbBig() { return bBig; }
    public long getaBig() { return aBig; }
    public String getU() { return u; }
    public BigInteger getN() { return n; }
    public BigInteger getE() {return  e; }
// Сеттеры
    public Account(long v, String s) {
    this.v = v;
    this.s = s;
    }
    public void setB(long b) {
    this.b = b;
}
    public void setM(String m) { this.m = m; }
    public void setR(String r) {
    this.r = r;
}
    public void setKey(String k) { this.k = k; }
    public void setbBig(long bBig) { this.bBig = bBig; }
    public void setaBig(long aBig) { this.aBig = aBig; }
    public void setU(String u) { this.u = u; }
    public void setN(BigInteger n) { this.n = n; }
    public void setE(BigInteger e) {this.e =  e; }

}
