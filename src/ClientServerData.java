public class ClientServerData {
    public int[][] map;
    public boolean clientFinished;  // флаг прекращения игры клиентом
    public boolean serverFinished;  // флаг прекращения игры сервером
    public boolean delClientSegment;      // флаг удаления сегмента на клиенте
    public boolean addClientSegment;      // флаг добавления сегмента на клиенте

    public void OutputData() {
        map = map;
    }

    public void resetFlags() {
        clientFinished = false;
        serverFinished = false;
        delClientSegment = false;
        addClientSegment = false;
    }
}
