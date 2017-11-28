import com.google.gson.Gson;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import static java.lang.Thread.sleep;

public class Client implements Runnable, ActionListener {

    int fSize = Game.FIELD_SIZE;

    public int serverMap[][] = new int[fSize][fSize];
    public int clientMap[][] = new int[fSize][fSize];

    int secCounter  = 1; //частота появления ништяков (в секундах)
    int frequency   = 1; //частота с которой будут встречаться увеличивающие тело ништяки 1..10

    int freqTimer   = 200;//частота таймера

    Timer timer     = new Timer(freqTimer, this); // скорость движения змейкиы

    int presSec     = new Date().getSeconds();
    int timeRepaint;

    public void run() {
        int serverPort = 6666; // здесь обязательно нужно указать порт к которому привязывается сервер.
        String address = "127.0.0.1"; // это IP-адрес компьютера, где исполняется наша серверная программа.

        Gson gson = new Gson();
        ClientServerData oData = new ClientServerData();    // класс, в который запихиваем все передаваемые данные туда-сюда

        try {
            InetAddress ipAddress = InetAddress.getByName(address); // создаем объект который отображает вышеописанный IP-адрес.
            Socket socket = new Socket(ipAddress, serverPort); // создаем сокет используя IP-адрес и порт сервера.

            //создаём клиента игры
            JFrame newFrame = new JFrame("Snake game - client");
            Game newGame = new Game(newFrame, 2);
            newFrame.add(newGame);


            OutputStream ops    = socket.getOutputStream();
            InputStream ips     = socket.getInputStream();

            ObjectOutputStream oup = new ObjectOutputStream(ops);
            oup.flush();
            ObjectInputStream inp = new ObjectInputStream(ips);


            while (true) {
                //немного выждем
                sleep(10);

                //получим карту от сервера и нарисуем её
                String gsonInpData = (String) inp.readObject();
                oData = gson.fromJson(gsonInpData, ClientServerData.class);

                newGame.setClientData(oData);

                //немного выждем
                sleep(10);

                //запихиваем все нужные для обмена данные в экземпляр класса oData
                //получим полну карту на клиенте и отправим её серверу
                oData.map               = newGame.getMap();
                //пакуем oData в json
                String gsonOutData = gson.toJson((ClientServerData) oData);
                //отправляем серверу
                oup.writeObject(gsonOutData);
                oup.flush();
                oup.reset();
            }
        } catch (Exception e) {
            System.out.println("Cannot connect to Server");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}

