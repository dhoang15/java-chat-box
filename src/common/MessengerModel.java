package common;

import java.io.Serializable;
import java.util.List;

/**
 * MessengerModel là lớp định nghĩa cấu trúc gói tin truyền tải giữa Client và Server.
 * Cần implements Serializable để có thể gửi đối tượng qua Socket.
 */
public class MessengerModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String sender;
    public String receiver = "ALL"; // Mặc định gửi cho tất cả mọi người
    public String content;
    public String type;             // Các loại: TEXT, IMAGE, TYPING, ONLINE_LIST, REGISTER
    
    public byte[] fileData;        // Dữ liệu hình ảnh dưới dạng mảng byte
    public String fileName;
    public List<String> onlineUsers; // Danh sách người dùng đang hoạt động
    public String timestamp;        // Chuỗi thời gian hiển thị (VD: 10:30)

    // 1. Hàm khởi tạo đầy đủ cho Tin nhắn chữ (Có kèm thời gian)
    public MessengerModel(String sender, String receiver, String content, String type, String timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }

    // 2. Hàm khởi tạo cho Tin nhắn chữ (Không kèm thời gian - dùng cho logic cũ)
    public MessengerModel(String sender, String receiver, String content, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type;
    }

    // 3. Hàm khởi tạo cho Hình ảnh (Có kèm thời gian)
    public MessengerModel(String sender, String receiver, String fileName, byte[] fileData, String type, String timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.fileName = fileName;
        this.fileData = fileData;
        this.type = type;
        this.timestamp = timestamp;
    }
    
    // 4. Hàm khởi tạo cho Hình ảnh (Không kèm thời gian)
    public MessengerModel(String sender, String receiver, String fileName, byte[] fileData, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.fileName = fileName;
        this.fileData = fileData;
        this.type = type;
    }

    // 5. Hàm khởi tạo đơn giản (Dùng cho thông báo hệ thống hoặc TYPING)
    public MessengerModel(String sender, String content, String type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    // 6. Hàm khởi tạo cho Danh sách Online
    public MessengerModel(String type, List<String> onlineUsers) {
        this.type = type;
        this.onlineUsers = onlineUsers;
    }
}