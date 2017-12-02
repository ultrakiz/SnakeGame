import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Date;

import static java.lang.Thread.sleep;

public class Game extends JPanel implements ActionListener {
    private JPanel mainPanel;

    public final static int FIELD_SIZE  = 12; // количество ячеек на поле
    public final static int CELL_SIZE   = 50; // размер ячейки поля  в пикселях
    public final static int MENU_HEIGTH = 50; //высота менюшки, с инфой внизу экрана
    public final static int ASPECT_RATIO = 1; //коэффициент, "сжатия" всех картинок
    public final static int F_SIZE_X = (CELL_SIZE) * FIELD_SIZE;
    public final static int F_SIZE_Y = (CELL_SIZE) * FIELD_SIZE;
    public final static int PLAYER1 = 3, PLAYER2 = 5;

    Image imgField      = new ImageIcon("res/field.jpg").getImage();
    Image imgPlayerHead = new ImageIcon("res/head.png").getImage().getScaledInstance(CELL_SIZE,CELL_SIZE,Image.SCALE_DEFAULT);
    Image imgPlayerBody = new ImageIcon("res/snakeBody.png").getImage().getScaledInstance(CELL_SIZE,CELL_SIZE,Image.SCALE_DEFAULT);
    Image imgPl2Head     = new ImageIcon("res/pl2head.png").getImage().getScaledInstance(CELL_SIZE,CELL_SIZE,Image.SCALE_DEFAULT);
    Image imgPl2Body     = new ImageIcon("res/pl2body.png").getImage().getScaledInstance(CELL_SIZE,CELL_SIZE,Image.SCALE_DEFAULT);
    Image imgFood       = new ImageIcon("res/food.png").getImage().getScaledInstance(CELL_SIZE,CELL_SIZE,Image.SCALE_DEFAULT);
    Image imgDrink      = new ImageIcon("res/drink.png").getImage().getScaledInstance(CELL_SIZE,CELL_SIZE,Image.SCALE_DEFAULT);

    int speedUp     = 10; //через сколько бутылок надо ускоряться
    int freqTimer;          //частота таймера
    int timerUp     = 20; //на сколько ускорять таймер

    Timer timer;    // скорость движения змейкиы

    int presSec;

    int timeFood;

    public static Player player, player2;

    int secCounter; //частота появления ништяков (в секундах)
    int frequency; //частота с которой будут встречаться увеличивающие тело ништяки 1..10

    int cSize           = CELL_SIZE;  // размер одной клетки в пикселях
    static int fSize    = FIELD_SIZE; // размер поля

    static int[][] map;         // виртуальная карта
    static int[][] clientMap;   // виртуальная карта

    int segmentX, segmentY;                 // координаты сегмента змеи

    String accText = "Выпито бутылок: ";
    int account; //счет

    String levelText = "     Скорость: ";
    int level; //счет

    Sounds.PlaySnd sound = new Sounds.PlaySnd();
    Thread sndThread = new Thread(sound);

    Sounds.PlayMus music = new Sounds.PlayMus();
    Thread musThread = new Thread(music);

    JFrame myframe;// = new JFrame();
    JPanel panel = new JPanel();

    JLabel accLabel     = new JLabel();
    JLabel levelLabel   = new JLabel();

    //в каком режиме запущена игра
    int gameMode;

    //какой цифрой будет отмечаться игрок на карте
    int playerNum, playerNum2;

    // клавиша, нажатая на клиенте - передадим её на сервер, чтобы изменять положение второй змейки
    public int clientKeyCode;

    // флаги для сетевой игры
    public boolean clientFinished;  // флаг прекращения игры клиентом
    public boolean serverFinished;  // флаг прекращения игры сервером
    public boolean delClientSegment;      // флаг удаления сегмента на клиенте
    public boolean addClientSegment;      // флаг добавления сегмента на клиенте

   //mode = 0 - сингл плеер, 1 - это сервер, 2 - это клиент
    public Game(JFrame frame, int mode) {

        map         = new int[fSize][fSize];
        clientMap   = new int[fSize][fSize];

        //инициализируем всё заново
        switch (mode) {
            case 0: // это сингл плеер
                playerNum = PLAYER1;
                player = new Player(3, 3);
                map[player.getHeadX()][player.getHeadY()] = playerNum;
                break;
            case 1: // сервер в мультиплеере
                playerNum = PLAYER1;
                player    = new Player(3,3 );
                map[player.getHeadX()][player.getHeadY()] = playerNum;

                playerNum2 = PLAYER2;
                player2    = new Player(10, 10);
                map[player2.getHeadX()][player2.getHeadY()] = playerNum2;
                break;
            case 2: // клиент в мультиплеере
                playerNum = PLAYER2;
                player2   = new Player(10, 10);
                map[player2.getHeadX()][player2.getHeadY()] = playerNum2;
            default:
        }

        clientKeyCode = 0;
        presSec     = new Date().getSeconds();
        account     = 0;
        level       = 0;
        secCounter  = 1;
        frequency   = 1;
        freqTimer   = 200;
        timer       = new Timer(freqTimer, this); // скорость движения змейкиы

        myframe     = frame;
        gameMode    = mode;

        panel.setBounds(0, 0, F_SIZE_X, F_SIZE_Y);

        frame.add(panel, BorderLayout.SOUTH);

        //получим разрешение экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        frame.setSize(CELL_SIZE * FIELD_SIZE+CELL_SIZE, CELL_SIZE * FIELD_SIZE + MENU_HEIGTH *2);
        frame.setLocation(screenSize.width / 16, screenSize.height / 16);
        frame.setVisible(true);
        addLabels();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //если клавиша нажата на клиенте - просто запомним её (чтобы потом отправить серверу)
                if (gameMode == 2) {
                    clientKeyCode = e.getKeyCode();
                } else {

                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_ESCAPE) {
                        //если выбрали - завершить игру
                        timer.stop();
                        if (JOptionPane.showConfirmDialog(null, "Завершить игру ?", "Выход из игры", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            endGame(0);
                        } else {
                            timer.start();
                        }
                        return;
                    } else player.keyPressed(e);
                }
            }
        });

        timer.start();
        //musThread.start();
    }

    public void addLabels() {


        accLabel.setFont(new Font("Georgia", Font.PLAIN, 20));
        accLabel.setForeground(Color.BLUE);
        accLabel.setText(accText + account);

        panel.add(accLabel, BorderLayout.SOUTH);

        levelLabel.setFont(new Font("Georgia", Font.PLAIN, 20));
        levelLabel.setForeground(Color.BLUE);
        levelLabel.setText(levelText + level);

        panel.add(levelLabel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameMode == 0 || gameMode == 1) repaintSingle(g);
            else repaintClient(g);
    }

    public void drawCell(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int frameSize = CELL_SIZE * FIELD_SIZE;

        g2d.setColor(new Color(33, 177, 76));
        g2d.setStroke(new BasicStroke(1));
        g2d.fillRect(0 , 0, frameSize, frameSize);

        g2d.setColor(new Color(0, 0, 0));

        for(int x = 0; x <= FIELD_SIZE; x++) {
            g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, frameSize);
        }

        for(int y = 0; y <= FIELD_SIZE; y++) {
            g2d.drawLine(0,y * CELL_SIZE, frameSize, y * CELL_SIZE);
        }
    }

    public void repaintSingle(Graphics g) {
        drawCell(g);
        //получим коодинаты головы первой змеи на виртуальной карте
        int pX = player.getHeadX();
        int pY = player.getHeadY();

        //если подошли к границам экрана - смерть игрока
        if (endOfField(pX, pY)) {
            endGame(1);
            return;
        }

        //если это сервер в мультиплеере
        if (gameMode == 1) {
            //получим коодинаты головы второй змеи на виртуальной карте
            int p2X = player2.getHeadX();
            int p2Y = player2.getHeadY();
            if (endOfField(p2X, p2Y)) endGame(2);
        }

        //отметим все сегменты змейки на карте
        setSnakeOnMap(player, playerNum);

        if (gameMode == 1) setSnakeOnMap(player2, playerNum2);

        //пробежим всю карту и выведем все ништяки на карте
        for(int row = 0; row < fSize; row++) {
            for(int col = 0; col < fSize; col++) {
                switch(map[row][col]) {
                    case 1: //бухло
                        g.drawImage(imgDrink, row * cSize, col * cSize, null);
                        break;
                    case 2: // закусь
                        g.drawImage(imgFood, row * cSize, col * cSize, null);
                        break;
                    case PLAYER1: // тело p1
                        g.drawImage(imgPlayerBody, row * cSize, col * cSize, null);
                        break;
                    case PLAYER1+1: // голова p1
                        g.drawImage(imgPlayerHead, row * cSize, col * cSize, null);
                        break;
                    case PLAYER2: //тело p2
                            g.drawImage(imgPl2Body, row * cSize, col * cSize, null);
                        break;
                    case PLAYER2+1: // голова p2
                        g.drawImage(imgPl2Head, row * cSize, col * cSize, null);
                        break;
                    default:
                        break;
                }
            }
        }

        //получим следующую секунду
        presSec = new Date().getSeconds();
        //если подходим к концу минуты - обнулим счетчик
        if(presSec == 0) {
            timeFood = -1;
        }

        //если пришло время создавать пищу
        if(timeFood+secCounter < presSec ) {
            boolean createFood = false;
            //генерируем новую пищу
            //если пища сгенерилась на змее - пересоздадим
            timeFood = presSec;

            while(!createFood) {


                int foodX = (int) (Math.random() * Game.FIELD_SIZE);
                int foodY = (int) (Math.random() * Game.FIELD_SIZE);

                //System.out.println("Создали еду x: " + foodX + "  y: " + foodY);
                if (map[foodX][foodY] == 0){

                    if ((Math.random() * 10) > frequency) {
                        map[foodX][foodY] = 1;
                        g.drawImage(imgDrink, foodX * cSize, foodY * cSize, null);
                    } else {
                        map[foodX][foodY] = 2;
                        g.drawImage(imgFood, foodX * cSize, foodY * cSize, null);
                    }
                    createFood = true;
                    repaint();
                }
            }
        }
    }

    //отметить тело змейки на карте
    public void setSnakeOnMap(Player pl, int playerNum) {
        for (int i = 0; i< pl.length; i++) {
            //запомним координаты X и Y текущего сегмента змейки
            segmentX = pl.snakeBody.get(i).x;
            segmentY = pl.snakeBody.get(i).y;
            //голова на карте отмечается как playerNum + 1
            map[segmentX][segmentY] = i == 0 ? playerNum+1 : playerNum;
        }
    }

    //проверим, не врезались ли в экран
    public boolean endOfField(int pX, int pY) {
        if ( pX >= fSize ||
                pY >= fSize ||
                pX < 0 ||
                pY < 0) {
            return true;
        }

        return false;
    }

    public void repaintServer(Graphics g) {
/*        //пробежим всю карту и выведем все ништяки на карте
        for(int row = 0; row < fSize; row++) {
            for(int col = 0; col < fSize; col++) {
                if(map[row][col] == 1) {
                    g.drawImage(imgDrink, row * cSize, col * cSize, null);
                }
                if(map[row][col] == 2) {
                    g.drawImage(imgFood, row * cSize, col * cSize, null);
                }

            }
        }

        //получим коодинаты головы змеи на виртуальной карте
        int pX = player.getX();
        int pY = player.getY();

        //если подошли к границам экрана - смерть
        if ( pX >= fSize ||
                pY >= fSize ||
                pX < 0 ||
                pY < 0) {

            endGame();
        }

        //змея съела ништяк - добавим сегмент
        if (map[pX][pY] == 1 ) {
            map[pX][pY] = 0;
            player.addSegment(pX, pY);
            account +=1;
            accLabel.setText(accText + account);
            if (account % speedUp == 0 ) {
                freqTimer -= timerUp;
                timer.setDelay(freqTimer);
                level +=1;
                levelLabel.setText(levelText + level);
            }

//            Thread sndThread = new Thread(sound);
//            sndThread.start();

        }
        //змея съела отраву - грохнем один сегмент
        if (map[pX][pY] == 2 ) {
            map[pX][pY] = 0;
            player.delSegment();
            if (player.length == 0) endGame();
        }
        //получим следующую секунду
        presSec = new Date().getSeconds();
        //если подходим к концу минуты - обнулим счетчик
        if(presSec == 0) {
            timeFood = -1;
        }

        //если пришло время создавать пищу
        if(timeFood+secCounter < presSec ) {
            boolean createFood = false;
            //генерируем новую пищу
            //если пища сгенерилась на змее - пересоздадим
            timeFood = presSec;

            while(!createFood) {
                Food food = new Food();
                if (map[food.getX()][food.getY()] == 0){

                    if ((Math.random() * 10) > frequency) {
                        g.drawImage(imgDrink, food.getX() * cSize, food.getY() * cSize, null);
                        map[food.getX()][food.getY()] = 1;
                    } else {
                        g.drawImage(imgFood, food.getX() * cSize, food.getY() * cSize, null);
                        map[food.getX()][food.getY()] = 2;
                    }
                    createFood = true;
                }
            }
        }
*/
    }

    public void repaintClient(Graphics g) {
        drawCell(g);
//        //получим коодинаты головы змеи на виртуальной карте
//        int p2X = player2.getHeadX();
//        int p2Y = player2.getHeadY();
//
//        //если подошли к границам экрана - смерть
//        if ( endOfField(p2X, p2Y)) {
//            endGame(2);
//            return;
//        }

//        //нарисуем каждый сегмент змейки
//        setSnakeOnMap(player2, playerNum2);

        //пробежим всю карту и выведем все ништяки на карте
        for(int row = 0; row < fSize; row++) {
            for(int col = 0; col < fSize; col++) {
                switch(map[row][col]) {
                    case 1: //бухло
                        g.drawImage(imgDrink, row * cSize, col * cSize, null);
                        break;
                    case 2: // закусь
                        g.drawImage(imgFood, row * cSize, col * cSize, null);
                        break;
                    case PLAYER1: // тело p1
                        g.drawImage(imgPlayerBody, row * cSize, col * cSize, null);
                        break;
                    case PLAYER1+1: // голова p1
                        g.drawImage(imgPlayerHead, row * cSize, col * cSize, null);
                        break;
                    case PLAYER2: //тело p2
                        g.drawImage(imgPl2Body, row * cSize, col * cSize, null);
                        break;
                    case PLAYER2+1: // голова p2
                        g.drawImage(imgPl2Head, row * cSize, col * cSize, null);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void endGame(int z) {
        //System.out.println(z);

        if (gameMode == 1) clientFinished = true;
        if (gameMode == 2) serverFinished = true;
        timer.stop();
//        musi.clip.close();
//        musThread.interrupt();

        JOptionPane.showMessageDialog(null, "Game over!");
        myframe.dispose();
        Menu newMenu = new Menu();

        return;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        player.move();
        player.recountCoords(player.getHeadX(), player.getHeadY());

        if (gameMode >0) {
            player2.move();
            player2.recountCoords(player2.getHeadX(), player2.getHeadY());

        }
        refreshMap();

        testCrush();
    }

    //возвратщает карту с ништяками
    public int[][] getMap() {

        return map;
    }

    public void resetFlags() {
        delClientSegment = false;
        addClientSegment = false;
    }

    //обрабатываем данные на клиенте, полученые от сервера
    public void setClientData(ClientServerData inpData) {
        //загрузим в клиента получиенные от сервера данные
        //установим карту
        map = inpData.map;

//        //клиент съел ништяк - увеличим змейку
//        if (inpData.addClientSegment) player.addSegment(player.getHeadX(), player.getHeadY());
//        inpData.addClientSegment = false;
//        //съел закусь - удалим сегмент
//        if (inpData.delClientSegment) player.delLastSegment();
//        inpData.delClientSegment  = false;

        repaint();
    }

    //обрабатываем данные на сервере, полученные от клиента
    public void setServerData(ClientServerData inpData) {
        clientMap = inpData.map;

        //передаём нажатие на клавижу от второго игрока
        player2.keyPressed(new KeyEvent(this, 0, 0, 0, inpData.clientKeyCode));

//        repaint();

        //пробегаем карту сервера и смотрим где находится змейка второго игрока на карте клиента
        // может быть она съела ништяк ?
//        for(int row = 0; row < fSize; row++) {
//            for(int col = 0; col < fSize; col++) {
//                //проверим, не съел ли p2 чего...
//                if (clientMap[row][col] == PLAYER2 + 1) {
//                    switch(map[row][col]) {
//                        case 1: //бухло
//                            //добавить сегмент p2
//                            addClientSegment = true;
//                            break;
//                        case 2: // закусь
//                            // убрать сегмент p2
//                            delClientSegment = true;
//                            break;
//                        case PLAYER1: // тело p1
//                            // p2 врезался в p1
//                            break;
//                        case PLAYER1+1: // голова p1
//                            // p2 врезался в p1
//                            break;
//                        case PLAYER2: //тело p2
//                            // p2 врезался в себя
//                            break;
//                        default:
//                            break;
//                    }
//
//                } else
//                //это просто тушка p2 - нарисуем его на карте
//                if (clientMap[row][col] == PLAYER2) {
//                    map[row][col] = PLAYER2;
//                }
//            }
//        }
    }

    public void refreshMap() {

        //очистим карту полностью
        for(int y = 0; y < fSize; y++) {
            for (int x = 0; x < fSize; x++) {
                if (map[x][y] != 1 && map[x][y] != 2) {
                    map[x][y] = 0;
                }
            }
        }
    }

    public void testCrush() {
        int pX = player.getHeadX();
        int pY = player.getHeadY();

        int p2X = player2.getHeadX();
        int p2Y = player2.getHeadY();

        if (endOfField(player.getHeadX(),player.getHeadY())) return;
        if (endOfField(player2.getHeadX(),player2.getHeadY())) return;

        //змея съела ништяк - добавим сегмент
        if (map[pX][pY] == 1 ) {
            player.addSegment(pX, pY);
            account +=1;
            accLabel.setText(accText + account);
            if (account % speedUp == 0 ) {
                freqTimer -= timerUp;
                timer.setDelay(freqTimer);
                level +=1;
                levelLabel.setText(levelText + level);
            }
//            Thread sndThread = new Thread(sound);
//            sndThread.start();
        }

        //змея съела ништяк - добавим сегмент
        if (map[p2X][p2Y] == 1 ) {
            player2.addSegment(p2X, p2Y);
//            account +=1;
//            accLabel.setText(accText + account);
//            if (account % speedUp == 0 ) {
//                freqTimer -= timerUp;
//                timer.setDelay(freqTimer);
//                level +=1;
//                levelLabel.setText(levelText + level);
//            }
////            Thread sndThread = new Thread(sound);
////            sndThread.start();
        }

        //змея съела отраву - грохнем один сегмент
        if (map[pX][pY] == 2 ) {
            map[pX][pY] = 0;
            player.delLastSegment();
            if (player.length == 0) endGame(3);
        }


        if (map[p2X][p2Y] == 2 ) {
            map[p2X][p2Y] = 0;
            player2.delLastSegment();
            if (player2.length == 0) endGame(3);
        }

        //проверим, не врезалась ли змейка в саму себя
        if (player.length > 2)  {
            for (int i = 2; i< player.length; i++) {
                //запомним координаты X и Y текущего сегмента змейки
                segmentX = player.snakeBody.get(i).x;
                segmentY = player.snakeBody.get(i).y;

                if (segmentX == pX && segmentY == pY) endGame(3);
            }
        }

        map[pX][pY] = playerNum;

        setSnakeOnMap(player, playerNum);
        setSnakeOnMap(player2, playerNum2);
    }

    public void showMap() {
        for(int y = 0; y < fSize; y++) {
            for(int x = 0; x < fSize; x++) {
                System.out.print("" + map[x][y] + " ");
            }
            System.out.println();
        }
        System.out.println("---- map");
    }
}