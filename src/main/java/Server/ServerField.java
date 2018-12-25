package Server;

import Magic.SRP;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServerField {

    ServerField(String login, Account account) {
        new SRP();
        accounts.put(login, account);
    }

    public Map<String, Account> accounts = UserCache.getInstance().getCache(); // Список аккаунтов

    void receive(String login, long aBig) {
        accounts.get(login).setaBig(aBig);
    }
// Вычисление B, k, r
    void generateB(String login) {
        int b = randomNatural();
        accounts.get(login).setB(b);
        long passV = accounts.get(login).getv();
        long bBig = (SRP.getK() * passV + SRP.powMod(SRP.getG(), b, SRP.getN())) % SRP.getN();
        accounts.get(login).setbBig(bBig);
    }
    private static int randomNatural() {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(1000000 - 10000) + 10000;
    }
    void keyCompute(String login) {
        BigInteger S;
        Account acc = accounts.get(login);
        BigInteger U = new BigInteger(acc.getU(), 16);
        long aBig = acc.getaBig();
        long b = acc.getB();
        long v = acc.getv();
        BigInteger ex1 = BigInteger.valueOf(v).modPow(U, BigInteger.valueOf(SRP.getN()));
        S = ex1.multiply(BigInteger.valueOf(aBig)).modPow(BigInteger.valueOf(b),BigInteger.valueOf(SRP.getN()));
        acc.setKey(SRP.getHash(S.toString().getBytes()));
    }
    void compR(String login) {
        Account acc = accounts.get(login);
        acc.setR(SRP.getHash((acc.getaBig() + acc.getM() + acc.getKey()).getBytes()));
    }
// Генерация подтверждения
    void confirmationHash(String login) {
        Account acc = accounts.get(login);
        BigInteger ex1 = new BigInteger(SRP.getHash(Long.valueOf(SRP.getN()).toString().getBytes()), 16);
        BigInteger ex2 = new BigInteger(SRP.getHash(Long.valueOf(SRP.getG()).toString().getBytes()), 16);
        String arg1 = ex1.xor(ex2).toString();
        byte[] args = (arg1 + SRP.getHash(login.getBytes()) + acc.getUserS() + acc.getaBig() + acc.getbBig() + acc.getKey()).getBytes();
        acc.setM(SRP.getHash(args));
    }
// Вычисление скремблера
    public String scrambler(String login) {
        long a = accounts.get(login).getaBig();
        long b = accounts.get(login).getbBig();
        String con = a + String.valueOf(b);
        String u = SRP.getHash(con.getBytes());
        accounts.get(login).setU(u);
        return u;
    }
}

