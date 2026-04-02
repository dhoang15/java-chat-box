package Client;
import java.net.*;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private String username;

    public UDPClient(String username, String serverIp, int serverPort) {
        this.username = username;
        this.serverPort = serverPort;
        try {
            this.socket = new DatagramSocket(); 
            this.serverAddress = InetAddress.getByName(serverIp);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Gửi tín hiệu đang gõ
    public void sendTyping() {
        try {
            String msg = "TYPING:" + username;
            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, serverPort);
            socket.send(packet);
        } catch (Exception e) {}
    }

    // HÀM MỚI: Gửi tín hiệu báo Online
    public void sendOnlineHeartbeat() {
        try {
            String msg = "ONLINE:" + username;
            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, serverPort);
            socket.send(packet);
        } catch (Exception e) {}
    }
}