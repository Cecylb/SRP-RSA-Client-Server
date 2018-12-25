package Server;

import Magic.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.math.BigInteger;
import java.util.ArrayList;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private ServerField server;
    private int count = 0;
    private String s="";
    private String s1="";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Wrapper msg1 = (Wrapper) msg;
        ArrayList<Object> data = msg1.getData();
        String user = (String) data.get(0);

        switch (msg1.getStage()) {
            case 1: // Фаза 1 - создание аккаунта, Сервер принимает и сохраняет I, s, v
                server = new ServerField(user, new Account((Long) data.get(2), (String) data.get(1)));
                ctx.write("Аккаунт был успешно создан.");
                break;
            case 2:
                long aBig = (Long) data.get(1);
                if (aBig != 0) { // Сервер проверяет, что A - не ноль
                    server.receive(user, aBig);
                    server.generateB(user); // Сервер вычисляет: b - случайное число
                    ctx.write(new Wrapper(1, null, server.accounts.get(user).getbBig() + server.accounts.get(user).getUserS())); // И сервер отсылает клиенту s и B
                } else {
                    ctx.close();
                    throw new RuntimeException("Ошибка во 2 фазе: А - ноль");
                }
                if (!"0".equals(server.scrambler(user))) { //И клиент и сервер вычисляют u=H(A,B)
                    server.keyCompute(user);
                    server.confirmationHash(user);
                    ctx.write(new Wrapper(2, null, server.accounts.get(user).getM()));
                } else { // Если u - ноль, соединение прерывается
                    ctx.close();
                    throw new RuntimeException("Ошибка во 2 фазе: U - ноль");
                }
                break;
            case 3: // Если Ms = Mk, то успех и сервер отправляет клиенту R=H(A,M,k)
                if (data.get(1).equals(server.accounts.get(user).getM())) {
                    server.compR(user);
                    ctx.write(new Wrapper(3, null, server.accounts.get(user).getR()));
                } else {
                    ctx.close();
                    throw new RuntimeException("Ошибка в 3 фазе: подтверждение не пройдено");
                }
                break;
            case 4:
                if (data.get(1).equals(server.accounts.get(user).getR())) {
                    System.out.println("Клиент прошёл проверку.");
                    ctx.write(new Wrapper(4, null, null));
                } else {
                    ctx.close();
                    throw new RuntimeException("Ошибка в 4 фазе: подтверждение не пройдено");
                }
                break;
            case 5:
                if(count==0) {
                    s = (String) msg1.getObject();
                    s1 = server.accounts.get(user).toString();
                    if (server.accounts.containsKey(s)) {
                        count=1;
                        System.out.println("Пользователь найден. Идёт получение ключей.");
                        ctx.write(new Wrapper(5, null, s));
                        break;
                    } else {
                        count=1;
                        System.out.println(s);
                        System.out.println("Пользователь не найден. Идёт информирование клиента.");
                        System.out.println("Or here?");
                        ctx.write(new Wrapper(6, null, server.accounts.get(user).getM()));
                        break;
                    }
                }else if(count==1){
                    count=0;
                    server.accounts.get(s).setE((BigInteger)msg1.getData().get(1));
                    server.accounts.get(s).setN((BigInteger)msg1.getData().get(2));
                    System.out.println("Ключи получены. Идёт отправка клиенту");
                    System.out.println("Here?");
                    ctx.write(new Wrapper(6, msg1.getData(), s1));
                    break;
                }
            case 6:
                if(msg1.getObject() != null){
                    ArrayList<Object> message = msg1.getData();
                    message.add(msg1.getObject());
                    ctx.write(new Wrapper(7,message,s));
                    break;
                }else{
                    ctx.close();
                    System.out.println("Ошибка в фазе 6");
                }
        }
        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
