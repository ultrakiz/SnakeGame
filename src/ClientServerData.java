import java.awt.event.KeyEvent;

public class ClientServerData {
    public int[][] map;
    public boolean clientFinished;  // флаг прекращения игры клиентом
    public boolean serverFinished;  // флаг прекращения игры сервером
    public boolean delClientSegment;      // флаг удаления сегмента на клиенте
    public boolean addClientSegment;      // флаг добавления сегмента на клиенте
    public int clientKeyCode;           // клавиша, которую нажали на клиенте

    public void resetFlags() {
        clientFinished = false;
        serverFinished = false;
        delClientSegment = false;
        addClientSegment = false;
    }
}
