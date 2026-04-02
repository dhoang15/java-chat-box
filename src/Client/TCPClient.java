package Client;

import common.MessengerModel;
import View.ChatMain;
import javafx.application.Platform;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TCPClient {
    private Socket socket; 
    private ObjectOutputStream out;
    private String username;
    private ChatMain view;

    public TCPClient(String username, ChatMain view) {
        this.username = username;
        this.view = view;
    }

    public void connect(String host, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                
                // Đăng ký với Server
                out.writeObject(new MessengerModel(username, "ALL", "", "REGISTER"));
                out.flush();

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    MessengerModel msg = (MessengerModel) in.readObject();
                    
                    Platform.runLater(() -> {
                        if (msg.type.equals("TEXT")) {
                            view.lblStatus.setText(""); 
                            view.addMessage(msg.sender, msg.content, msg.timestamp, msg.sender.equals(username));
                            
                        } else if (msg.type.equals("IMAGE")) {
                            view.lblStatus.setText("");
                            if (!msg.sender.equals(username)) {
                                view.addImageMessage(msg.sender, msg.fileData, msg.timestamp, false);
                            }
                            
                        } else if (msg.type.equals("ONLINE_LIST")) {
                            view.updateOnlineList(msg.onlineUsers);
                            
                        } else if (msg.type.equals("TYPING")) {
                            if (!msg.sender.equals(username)) {
                                view.lblStatus.setText(msg.sender + " đang soạn tin...");
                                // Tự xóa sau 2s
                                new Thread(() -> {
                                    try { Thread.sleep(2000); Platform.runLater(() -> view.lblStatus.setText("")); } 
                                    catch (InterruptedException ignored) {}
                                }).start();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> view.addMessage("Hệ thống", "Mất kết nối Server!", "", false));
            }
        }).start();
    }

    public void sendMessage(String content) {
        if (out != null && !content.trim().isEmpty()) {
            try {
                String time = new SimpleDateFormat("HH:mm").format(new Date());
                // Gửi tin nhắn đến targetUser (có thể là cá nhân hoặc nhóm)
                MessengerModel msg = new MessengerModel(username, view.targetUser, content, "TEXT", time);
                out.writeObject(msg);
                out.flush();
                out.reset(); 
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void sendImage(File file) {
        if (out != null) {
            try {
                String time = new SimpleDateFormat("HH:mm").format(new Date());
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                MessengerModel msg = new MessengerModel(username, view.targetUser, file.getName(), fileBytes, "IMAGE", time);
                out.writeObject(msg);
                out.flush();
                out.reset(); 
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void createGroup(String groupName, List<String> members) {
        if (out != null) {
            try {
                // Gói tin đặc biệt để Server tạo Map nhóm
                MessengerModel msg = new MessengerModel(username, "SERVER", groupName, "CREATE_GROUP");
                msg.onlineUsers = members; // Chứa danh sách thành viên được chọn
                out.writeObject(msg);
                out.flush();
                out.reset();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}