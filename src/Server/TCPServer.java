package Server;

import common.MessengerModel;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.text.SimpleDateFormat;

public class TCPServer {
    // Tên người dùng -> ObjectOutputStream
    public static ConcurrentHashMap<String, ObjectOutputStream> clientMap = new ConcurrentHashMap<>();
    // Thời gian hoạt động cuối cùng
    public static ConcurrentHashMap<String, Long> onlineUsersMap = new ConcurrentHashMap<>();
    // Tên nhóm -> Danh sách các ObjectOutputStream của thành viên
    private static Map<String, List<ObjectOutputStream>> groupMap = new ConcurrentHashMap<>();

    public void start(int port) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("TCP Server đang chạy cổng " + port);
                
                // Luồng gửi danh sách Online + Group định kỳ
                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(3000);
                            long now = System.currentTimeMillis();
                            onlineUsersMap.entrySet().removeIf(entry -> (now - entry.getValue()) > 6000);
                            
                            List<String> activeList = new ArrayList<>(onlineUsersMap.keySet());
                            // Thêm icon 👥 để Client phân biệt được đâu là nhóm
                            for (String groupName : groupMap.keySet()) {
                                activeList.add("👥 " + groupName);
                            }
                            
                            broadcast(new MessengerModel("ONLINE_LIST", activeList));
                        } catch (InterruptedException e) {}
                    }
                }).start();

                while (true) {
                    Socket socket = serverSocket.accept();
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    new Thread(() -> handleClient(socket, out)).start();
                }
            } catch (IOException e) { e.printStackTrace(); }
        }).start();
    }

    private void handleClient(Socket socket, ObjectOutputStream out) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                MessengerModel msg = (MessengerModel) in.readObject();
                
                if (msg.sender != null) {
                    clientMap.put(msg.sender, out); 
                }

                msg.timestamp = new SimpleDateFormat("HH:mm").format(new Date());

                // --- PHÂN LUỒNG XỬ LÝ ---
                if (msg.type.equals("CREATE_GROUP")) {
                    handleCreateGroup(msg, out);
                } 
                else if (msg.type.equals("JOIN_GROUP")) {
                    handleJoinGroup(msg, out);
                }
                else if (msg.receiver != null && msg.receiver.equals("ALL")) {
                    broadcast(msg);
                } 
                else if (msg.receiver != null && groupMap.containsKey(msg.receiver)) {
                    sendToGroup(msg);
                } 
                else if (msg.receiver != null) {
                    sendPrivate(msg);
                }
            }
        } catch (Exception e) {
            clientMap.values().remove(out);
            groupMap.values().forEach(list -> list.remove(out));
            System.out.println("Một người dùng đã thoát.");
        }
    }

    // Logic tạo nhóm với danh sách thành viên được chọn
    private void handleCreateGroup(MessengerModel msg, ObjectOutputStream out) {
        String groupName = msg.content; // Tên nhóm gửi từ Client
        List<ObjectOutputStream> memberStreams = new ArrayList<>();
        
        // 1. Thêm người tạo nhóm
        memberStreams.add(out); 
        
        // 2. Thêm các thành viên được tích chọn
        if (msg.onlineUsers != null) {
            for (String memberName : msg.onlineUsers) {
                ObjectOutputStream mOut = clientMap.get(memberName);
                if (mOut != null && !memberStreams.contains(mOut)) {
                    memberStreams.add(mOut);
                }
            }
        }
        
        groupMap.put(groupName, memberStreams);
        System.out.println("Nhóm [" + groupName + "] đã tạo với " + memberStreams.size() + " thành viên.");
    }

    private void handleJoinGroup(MessengerModel msg, ObjectOutputStream out) {
        String groupName = msg.content;
        if (groupMap.containsKey(groupName) && !groupMap.get(groupName).contains(out)) {
            groupMap.get(groupName).add(out);
        }
    }

    public static void sendToGroup(MessengerModel msg) {
        List<ObjectOutputStream> members = groupMap.get(msg.receiver);
        if (members != null) {
            for (ObjectOutputStream memberOut : members) {
                try {
                    memberOut.writeObject(msg);
                    memberOut.flush();
                    memberOut.reset();
                } catch (IOException e) {}
            }
        }
    }

    public static void broadcast(MessengerModel msg) {
        for (ObjectOutputStream out : clientMap.values()) {
            try {
                out.writeObject(msg);
                out.flush();
                out.reset(); 
            } catch (IOException e) {}
        }
    }

    public static void sendPrivate(MessengerModel msg) {
        ObjectOutputStream receiverOut = clientMap.get(msg.receiver); 
        ObjectOutputStream senderOut = clientMap.get(msg.sender);     
        try {
            if (receiverOut != null) {
                receiverOut.writeObject(msg);
                receiverOut.flush();
                receiverOut.reset();
            }
            if (senderOut != null && receiverOut != senderOut) {
                senderOut.writeObject(msg);
                senderOut.flush();
                senderOut.reset();
            }
        } catch (IOException e) {}
    }
}