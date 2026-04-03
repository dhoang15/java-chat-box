package Client;

import View.ChatMain;
import View.Login;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientApp extends Application {
    private String username;
    private TCPClient tcpClient;
    private UDPClient udpClient;

    @Override
    public void start(Stage stage) {
        Login loginView = new Login();
        Scene scene = new Scene(loginView, 450, 680); 

        loginView.btnLogin.setOnAction(e -> {
            String user = loginView.txtUsername.getText().trim();
            if (!user.isEmpty()) {
                this.username = user;
                openChat(stage); 
            } else {
                loginView.lblError.setText("Vui lòng nhập tên!");
            }
        });

        stage.setTitle("ChatBox- Đăng nhập");
        stage.setScene(scene);
        stage.show();
    }

    private void openChat(Stage stage) {
        ChatMain chatView = new ChatMain();
        Scene chatScene = new Scene(chatView, 850, 600); 

        tcpClient = new TCPClient(username, chatView);
        tcpClient.connect("localhost", 5001); 
        udpClient = new UDPClient(username, "localhost", 5000); 
        
        // Luồng Keep-alive
        Thread hb = new Thread(() -> {
            while (true) {
                try { udpClient.sendOnlineHeartbeat(); Thread.sleep(2000); } 
                catch (InterruptedException e) { break; }
            }
        });
        hb.setDaemon(true);
        hb.start();

        // 1. Gửi tin nhắn Text
        chatView.btnSend.setOnAction(e -> {
            String content = chatView.txtInput.getText().trim();
            if(!content.isEmpty()) {
                tcpClient.sendMessage(content);
                chatView.txtInput.clear();
            }
        });
        chatView.txtInput.setOnAction(e -> chatView.btnSend.fire());
        chatView.txtInput.setOnKeyTyped(e -> udpClient.sendTyping());
        
        // 2. Gửi Ảnh
        chatView.btnImage.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                try {
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    String time = new SimpleDateFormat("HH:mm").format(new Date());
                    chatView.addImageMessage(username, bytes, time, true);
                    tcpClient.sendImage(file);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        // 3. Tạo nhóm & chọn thành viên
        chatView.btnCreateGroup.setOnAction(e -> {
            List<String> currentOnline = new ArrayList<>(chatView.listOnline.getItems());
            List<String> result = chatView.showCreateGroupDialog(currentOnline);
            if (result != null && result.size() > 0) {
                String gName = result.get(0);
                result.remove(0); // Chỉ còn member
                if (!result.contains(username)) result.add(username);
                
                tcpClient.createGroup(gName, result); 
                if (!chatView.listOnline.getItems().contains("👥 " + gName)) {
                    chatView.listOnline.getItems().add("👥 " + gName);
                }
            }
        });

        // 4. Click chọn mục tiêu Chat (Cực kỳ quan trọng)
        chatView.listOnline.setOnMouseClicked(e -> {
            String selected = chatView.listOnline.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String cleanName = selected.replace("🟢 ", "").replace("👥 ", "");
                chatView.targetUser = cleanName;
                chatView.lblTarget.setText(cleanName);
            }
        });

        stage.setScene(chatScene);
        stage.setTitle("ChatBox - " + username);
        stage.centerOnScreen();
    }

    public static void main(String[] args) { launch(args); }
}