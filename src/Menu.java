import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Menu {
    private JPanel menuPanel;
    private JButton singlePlayerButton;
    private JButton multiPlayerButton;
    private JButton exitButton;
    private JButton optionsButton;
    private JRadioButton serverFlag;
    private JRadioButton clientFlag;

    public Menu() {

        //получим разрешение экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        //создадим окно менюшки и разместим его посередине экрана
        JFrame frame = new JFrame("Main menu");
        frame.setSize(screenSize.width / 2, screenSize.height / 2);
        frame.setLocation(screenSize.width / 4,  screenSize.height / 4);
        frame.setContentPane(menuPanel);
        frame.setVisible(true);

        //одиночная игра
        singlePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame newFrame = new JFrame("Snake game - single");
                Game newGame = new Game(newFrame, 0);
                newFrame.add(newGame);
                frame.dispose();
            }
        });

        //мультиплеер
        multiPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverFlag.isSelected() ) {
                    Server newServer = new Server();
                    Thread serverThread = new Thread(newServer);
                    serverThread.start();
                    frame.dispose();
                } else if (clientFlag.isSelected()) {
                    Client newClient = new Client();
                    Thread clientThread = new Thread(newClient);
                    clientThread.start();
                    frame.dispose();
                }
            }
        });
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
