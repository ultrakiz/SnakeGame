import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;

public class waitFrame {
    private JPanel waitPanel;
    private JProgressBar progressBar1;
    private JButton cancelButton;

    public waitFrame(JFrame frame, ServerSocket ss) {
        //получим разрешение экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        frame.setSize(screenSize.width / 2, screenSize.height / 2);
        frame.setLocation(screenSize.width / 4,  screenSize.height / 4);
        frame.setContentPane(waitPanel);
        progressBar1.setIndeterminate(true);
        frame.setVisible(true);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            //если нажали на отмену - закроем сервер
            public void actionPerformed(ActionEvent e) {
                try {
                    ss.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                frame.dispose();
            }
        });
    }
}
