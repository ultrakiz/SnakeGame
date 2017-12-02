import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

import static java.lang.Thread.sleep;

public class Server implements Runnable, ActionListener {

    int fSize = Game.FIELD_SIZE;

    public int serverMap[][] = new int[fSize][fSize];
    public int clientMap[][] = new int[fSize][fSize];

    Timer timer     = new Timer(200, this);

    @Override
    public void run() {

        Gson gson = new Gson();
        ClientServerData oData = new ClientServerData();    // класс, в который запихиваем все передаваемые данные туда-сюда

        //timer.start();
        try {

            ServerSocket ss = new ServerSocket(6666);

            //создадим форму ожидания
            JFrame waitFrame = new JFrame("Ожидание подключения");
            waitFrame newWaitFrame  = new waitFrame(waitFrame, ss);

            //произошло подключение клиента
            Socket socket = ss.accept();
            //закроем форму ожидания

            //создаём сервер игры
            JFrame newFrame = new JFrame("Snake game - server");
            Game newGame = new Game(newFrame, 1);
            newFrame.add(newGame);
            waitFrame.dispose();

            //System.out.println("Есть подключение клиента!");


            OutputStream ops    = socket.getOutputStream();
            InputStream ips     = socket.getInputStream();

            ObjectOutputStream oup = new ObjectOutputStream(ops);
            oup.flush();
            ObjectInputStream inp = new ObjectInputStream(ips);

            while (!newGame.serverFinished) {

                //sleep(10);
                //запихиваем все нужные для обмена данные в экземпляр класса oData
                //флаги
                oData.delClientSegment  = newGame.delClientSegment;
                oData.addClientSegment  = newGame.addClientSegment;
                //карта для клиента
                oData.map               = (int[][]) newGame.getMap();
                //пакуем oData в json
                String gsonMap = gson.toJson((ClientServerData) oData);

                //отправим карту клиенту
                oup.writeObject(gsonMap);
                oup.flush();
                oup.reset();

                //данные отправлены - сбросим флаги
                newGame.resetFlags();

//                sleep(50);

                //получим данные от клиента
                String gsonInpData = (String) inp.readObject();
                oData = gson.fromJson(gsonInpData, ClientServerData.class);

                //загрузим их в игру
                newGame.setServerData(oData);

            }

            //всё завершим
            oup.flush();
            oup.reset();
            socket.close();

        } catch (Exception e) {
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
