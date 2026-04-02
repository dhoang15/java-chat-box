package View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatMain extends BorderPane {
    public VBox chatBox = new VBox(15);
    public ScrollPane scrollPane = new ScrollPane(chatBox);
    public TextField txtInput = new TextField();
    public Button btnSend = new Button("Gửi");
    public Button btnImage = new Button("📷"); 
    public Button btnCreateGroup = new Button("＋ Tạo nhóm");
    public Label lblStatus = new Label(); 
    public ListView<String> listOnline = new ListView<>(); 
    public String targetUser = "ALL";
    public Label lblTarget = new Label("Trực tiếp");

    public ChatMain() {
        this.setStyle("-fx-background-color: white;");

        // --- 1. SideBar TRÁI (Instagram Style) ---
        VBox leftBar = new VBox(15);
        leftBar.setPadding(new Insets(20, 10, 20, 10));
        leftBar.setPrefWidth(260);
        leftBar.setStyle("-fx-border-color: #dbdbdb; -fx-border-width: 0 1 0 0; -fx-background-color: white;");
        
        Label lblUserHeader = new Label("Tin nhắn");
        lblUserHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #262626;");
        
        btnCreateGroup.setMaxWidth(Double.MAX_VALUE);
        btnCreateGroup.setStyle("-fx-background-color: #fafafa; -fx-border-color: #dbdbdb; -fx-border-radius: 5; -fx-cursor: hand;");
        
        listOnline.setStyle("-fx-background-color: transparent; -fx-control-inner-background: white; -fx-background-insets: 0;");
        VBox.setVgrow(listOnline, Priority.ALWAYS);
        
        leftBar.getChildren().addAll(lblUserHeader, btnCreateGroup, listOnline);
        this.setLeft(leftBar);

        // --- 2. Khu vực Chat chính ---
        VBox mainArea = new VBox();
        HBox header = new HBox(15);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-border-color: #dbdbdb; -fx-border-width: 0 0 1 0;");
        
        lblTarget.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(lblTarget, spacer, lblStatus);

        chatBox.setPadding(new Insets(20));
        scrollPane.setContent(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        chatBox.heightProperty().addListener((obs, oldV, newV) -> scrollPane.setVvalue(1.0));

        HBox inputBar = new HBox(10, btnImage, txtInput, btnSend);
        inputBar.setPadding(new Insets(15, 20, 20, 20));
        inputBar.setAlignment(Pos.CENTER);
        inputBar.setStyle("-fx-border-color: #dbdbdb; -fx-border-radius: 30; -fx-border-width: 1; -fx-background-radius: 30;");
        HBox.setHgrow(txtInput, Priority.ALWAYS);
        
        txtInput.setPromptText("Nhắn tin...");
        txtInput.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        btnSend.setStyle("-fx-background-color: transparent; -fx-text-fill: #0095f6; -fx-font-weight: bold; -fx-cursor: hand;");
        btnImage.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        mainArea.getChildren().addAll(header, scrollPane, inputBar);
        this.setCenter(mainArea);
    }

    private StackPane createAvatar(String name) {
        LinearGradient instaGradient = new LinearGradient(0, 1, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#f9ce34")), new Stop(0.5, Color.web("#ee2a7b")), new Stop(1, Color.web("#6228d7")));
        Circle border = new Circle(20, instaGradient);
        Circle inner = new Circle(18, Color.WHITE);
        Circle bg = new Circle(16, Color.web("#dbdbdb"));
        Label lbl = new Label(name != null && !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "?");
        lbl.setStyle("-fx-font-weight: bold;");
        return new StackPane(border, inner, bg, lbl);
    }

    public void addMessage(String sender, String content, String time, boolean isMe) {
        HBox wrapper = new HBox(10);
        VBox container = new VBox(2);
        Label lblMsg = new Label(content);
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(300);
        lblMsg.setPadding(new Insets(10, 15, 10, 15));

        Label lblTime = new Label(time != null ? time : "");
        lblTime.setStyle("-fx-font-size: 10px; -fx-text-fill: #8e8e8e;");

        if (isMe) {
            wrapper.setAlignment(Pos.CENTER_RIGHT);
            container.setAlignment(Pos.TOP_RIGHT);
            lblMsg.setStyle("-fx-background-color: white; -fx-border-color: #dbdbdb; -fx-border-radius: 20; -fx-background-radius: 20;");
        } else {
            wrapper.setAlignment(Pos.CENTER_LEFT);
            container.setAlignment(Pos.TOP_LEFT);
            lblMsg.setStyle("-fx-background-color: #efefef; -fx-background-radius: 20;");
            wrapper.getChildren().add(createAvatar(sender));
        }
        container.getChildren().addAll(lblMsg, lblTime);
        wrapper.getChildren().add(container);
        chatBox.getChildren().add(wrapper);
    }

    public void addImageMessage(String sender, byte[] imageData, String time, boolean isMe) {
        HBox wrapper = new HBox(10);
        VBox container = new VBox(2);
        Image img = new Image(new ByteArrayInputStream(imageData));
        ImageView iv = new ImageView(img);
        iv.setFitWidth(250);
        iv.setPreserveRatio(true);

        Rectangle clip = new Rectangle(250, 100);
        clip.setArcWidth(25); clip.setArcHeight(25);
        if (img.getWidth() > 0) {
            clip.setHeight(250 * (img.getHeight() / img.getWidth()));
        }
        iv.setClip(clip);

        Label lblTime = new Label(time != null ? time : "");
        lblTime.setStyle("-fx-font-size: 10px; -fx-text-fill: #8e8e8e;");

        if (isMe) {
            wrapper.setAlignment(Pos.CENTER_RIGHT);
            container.setAlignment(Pos.TOP_RIGHT);
        } else {
            wrapper.setAlignment(Pos.CENTER_LEFT);
            container.setAlignment(Pos.TOP_LEFT);
            wrapper.getChildren().add(createAvatar(sender));
        }
        container.getChildren().addAll(iv, lblTime);
        wrapper.getChildren().add(container);
        chatBox.getChildren().add(wrapper);
    }

    public void updateOnlineList(List<String> users) { 
        listOnline.getItems().clear();
        for (String u : users) { listOnline.getItems().add(u); }
    }

    /**
     * Hộp thoại chọn thành viên tạo nhóm
     */
    public List<String> showCreateGroupDialog(List<String> allUsers) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Tạo nhóm mới");
        dialog.setHeaderText("Chọn thành viên và đặt tên nhóm");

        ButtonType createButtonType = new ButtonType("Tạo", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        
        TextField txtGroupName = new TextField();
        txtGroupName.setPromptText("Nhập tên nhóm...");

        VBox userListSelection = new VBox(5);
        ScrollPane sp = new ScrollPane(userListSelection);
        sp.setPrefHeight(200);
        sp.setFitToWidth(true);
        
        for (String user : allUsers) {
            if (!user.contains("👥")) { 
                CheckBox cb = new CheckBox(user.replace("🟢 ", ""));
                userListSelection.getChildren().add(cb);
            }
        }

        container.getChildren().addAll(new Label("Tên nhóm:"), txtGroupName, new Label("Chọn thành viên:"), sp);
        dialog.getDialogPane().setContent(container);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String groupName = txtGroupName.getText().trim();
                if (groupName.isEmpty()) return null;

                List<String> selected = new ArrayList<>();
                selected.add(groupName); // Vị trí 0 là tên nhóm
                for (javafx.scene.Node node : userListSelection.getChildren()) {
                    if (node instanceof CheckBox) {
                        CheckBox cb = (CheckBox) node;
                        if (cb.isSelected()) selected.add(cb.getText());
                    }
                }
                return selected;
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }
}