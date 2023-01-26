import java.util.ArrayList;

public class ConnectionInfo {
    protected Thread tdRead, tdConnect;
    protected ArrayList<Connection> connections = new ArrayList<>();
    protected ArrayList<Connection> unpairedConnections = new ArrayList<>();
    
    protected static int id = 0;
    
    public ConnectionInfo(Thread tdRead, Thread tdConnect) {
        this.tdRead = tdRead;
        this.tdConnect = tdConnect;
        
        tdRead.setDaemon(true);
        tdConnect.setDaemon(true);
        
        tdRead.setName("Reader-" + id);
        tdConnect.setName("Connector-" + id);
        
        id++;
        
        tdRead.start();
        tdConnect.start();
    }
}
