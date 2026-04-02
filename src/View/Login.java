package View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Login extends VBox {
    public TextField txtUsername = new TextField();
    public PasswordField txtPassword = new PasswordField();
    public Button btnLogin = new Button("Đăng nhập");
    public Label lblError = new Label();

    public Login() {
        // --- Setup tổng thể: Nền trắng sạch sẽ ---
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(40, 20, 40, 20));
        this.setStyle("-fx-background-color: white;");

        VBox loginCard = new VBox(15);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(30, 35, 30, 35));
        loginCard.setMaxWidth(380);
        // Viền mờ bao quanh khung đăng nhập
        loginCard.setStyle("-fx-background-color: white; -fx-border-color: #dbdbdb; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label lblLogo = new Label("Chat Box");
        lblLogo.setFont(Font.font("Serif", FontWeight.BOLD, 45));
        lblLogo.setTextFill(Color.web("#262626"));
        VBox.setMargin(lblLogo, new Insets(10, 0, 20, 0));

        // 2. Styling các ô nhập liệu
        styleInstagramField(txtUsername, "Số điện thoại, tên người dùng hoặc email");
        styleInstagramField(txtPassword, "Mật khẩu");

        // 3. Styling nút Đăng nhập
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(35);
        // Màu xanh Insta: #0095f6, bo góc nhẹ
        btnLogin.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        VBox.setMargin(btnLogin, new Insets(10, 0, 5, 0));

        // 6. Quên mật khẩu & Báo lỗi
        // hlForgotPassword.setStyle("-fx-text-fill: #385185; -fx-font-size: 12px; -fx-underline: false;");
        lblError.setTextFill(Color.RED);
        lblError.setFont(Font.font(12));

        loginCard.getChildren().addAll(lblLogo, txtUsername, txtPassword, btnLogin,lblError);

        this.getChildren().addAll(loginCard);
    }

    private void styleInstagramField(TextField tf, String prompt) {
        tf.setPromptText(prompt);
        tf.setPrefHeight(38);
        // Nền xám cực nhạt, viền mờ
        tf.setStyle("-fx-background-color: #fafafa; -fx-background-radius: 5; -fx-border-color: #dbdbdb; -fx-border-radius: 5; -fx-padding: 0 10; -fx-font-size: 12px;");
        
        // Hiệu ứng khi Click vào ô nhập (Focus)
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                tf.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #a8a8a8; -fx-border-radius: 5; -fx-padding: 0 10; -fx-font-size: 12px;");
            } else {
                tf.setStyle("-fx-background-color: #fafafa; -fx-background-radius: 5; -fx-border-color: #dbdbdb; -fx-border-radius: 5; -fx-padding: 0 10; -fx-font-size: 12px;");
            }
        });
    }
}