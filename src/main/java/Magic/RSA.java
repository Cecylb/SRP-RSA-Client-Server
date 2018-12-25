package Magic;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
    private BigInteger n, d, e;
    private int length; // Разрядность в битах
// Конструктор
    public RSA(int _length) { // Получаем разрядность из main
        length = _length;
        SecureRandom random = new SecureRandom();
        BigInteger p = new BigInteger(length/2, 100, random); // Выбрать большие простые P и Q
        BigInteger q = new BigInteger(length/2, 100, random);
        n = p.multiply(q); // Вычислить N=PQ
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE)); // вычислить вспомогательное число m=(P-1)(Q-1)
        e = new BigInteger("3"); // Выбирается целое число e (1<e<f(n)), взаимно простое со значением функции f(n)
        while (m.gcd(e).intValue() > 1) { // Пока наибольший общий делитель больше одного
            e = e.add(new BigInteger("2")); // е = е + 2
        }
        d = e.modInverse(m); // Вычисляется число d, мультипликативно обратное к числу e по модулю f(n)
    }
    /** Create an instance that can encrypt using someone elses public key. */
    public void setKeys(BigInteger newn, BigInteger newe) {
        n = newn;
        e = newe;
    }
    // Зашифровываем входящее сообщение
    public synchronized BigInteger encrypt(BigInteger message) {
        return message.modPow(e, n); // Пара (e,n) - открытый ключ RSA
    } // c = E(m) = m^e mod n
    // Расшифровываем входящее сообщение
    public synchronized BigInteger decrypt(BigInteger message) {
        return message.modPow(d, n); // Пара (d,n) - закрытый ключ RSA
    } // m = D(c) = c^d mod n

    /** Return the modulus. */
    public synchronized BigInteger getN() {
        return n;
    }

    /** Return the public key. */
    public synchronized BigInteger getE() {
        return e;
    }
}