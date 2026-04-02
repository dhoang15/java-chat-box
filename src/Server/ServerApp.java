package Server;

public class ServerApp {
    public static void main(String[] args) {
        new UDPServer().start(5000);
        new TCPServer().start(5001);
    }
}