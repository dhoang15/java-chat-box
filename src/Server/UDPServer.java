package Server;
import common.MessengerModel;
import java.net.*;

public class UDPServer {
    public void start(int port) {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                System.out.println("UDP Server đang nghe ở cổng " + port);
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String data = new String(packet.getData(), 0, packet.getLength()).trim();
                    
                    if (data.startsWith("TYPING:")) {
                        String username = data.split(":")[1];
                        TCPServer.broadcast(new MessengerModel(username, "đang gõ...", "TYPING"));
                    } 
                    // BẮT BẮT TÍN HIỆU ONLINE VÀ GHI VÀO SỔ
                    else if (data.startsWith("ONLINE:")) {
                        String username = data.split(":")[1];
                        // Ghi lại thời gian mới nhất vào cuốn sổ bên TCPServer
                        TCPServer.onlineUsersMap.put(username, System.currentTimeMillis());
                        System.out.println("Đã nhận được nhịp tim của: " + username);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
}