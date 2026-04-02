package common;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    public String username;
    public String password;
    public String avatarPath; // Lưu đường dẫn ảnh đại diện

    public User(String username, String password, String avatarPath) {
        this.username = username;
        this.password = password;
        this.avatarPath = avatarPath;
    }
}