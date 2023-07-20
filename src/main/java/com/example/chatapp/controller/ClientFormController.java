package com.example.chatapp.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class ClientFormController{

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vBox;

    @FXML
    private TextField txtMessage;

    Socket socket;

    String message = "";

    File file;

    DataInputStream inputStream;

    DataOutputStream outputStream;

    @FXML
    void btnSendOnAction(ActionEvent event) throws IOException {
        if (!txtMessage.getText().isEmpty()) {
            if (txtMessage.getText().equals("1 image selected")) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                outputStream.writeUTF("img");
                outputStream.writeInt(bytes.length);
                outputStream.write(bytes);
                outputStream.flush();
//                writer.println("img" + file.getPath());
                Image image = new Image(file.toURI().toString());
                ImageView imageView = new ImageView(image);

                imageView.setFitHeight(150);
                imageView.setFitWidth(180);

                HBox hBox = new HBox(10);
                hBox.getStyleClass().add("send-box");
                hBox.setMaxHeight(170);
                hBox.setMaxWidth(200);
                hBox.getChildren().add(imageView);

                StackPane stackPane = new StackPane(hBox);
                stackPane.setAlignment(Pos.CENTER_RIGHT);
                vBox.getChildren().addAll(stackPane);
                scrollPane.layout();
                scrollPane.setVvalue(2.0);

                txtMessage.setEditable(true);
                txtMessage.clear();
            } else {
//                writer.println(txtMessage.getText());
                outputStream.writeUTF(txtMessage.getText());
                TextFlow tempFlow = new TextFlow();
                Text text = new Text(txtMessage.getText());
                text.setWrappingWidth(200);
                text.getStyleClass().add("send-text");
                tempFlow.getChildren().add(text);
                tempFlow.setMaxWidth(150);

                HBox hBox = new HBox(12);
                hBox.getStyleClass().add("send-box");
                hBox.setMaxWidth(220);
                hBox.setMaxHeight(50);
                hBox.getChildren().add(tempFlow);
                StackPane stackPane = new StackPane(hBox);
                stackPane.setAlignment(Pos.CENTER_RIGHT);
                vBox.getChildren().addAll(stackPane);
                scrollPane.layout();
                scrollPane.setVvalue(2.0);
                txtMessage.clear();
            }
        }else{
            new Alert(Alert.AlertType.WARNING,"Please input your message").show();
        }
        txtMessage.clear();
    }

    @FXML
    void btnAttachmentOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the image");
        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);
        file = fileChooser.showOpenDialog(txtMessage.getScene().getWindow());

        if (file != null) {
            txtMessage.setText("1 image selected");
            txtMessage.setEditable(false);
        }
    }

    @FXML
    void txtMessageOnAction(ActionEvent event) throws IOException {
        btnSendOnAction(event);
    }

    public void initialize(){
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(2.0);

        new Thread(()->{
            try {
                socket = new Socket("localhost",4005);

                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());

                while (true){
                    message = inputStream.readUTF();
                    if (message.equalsIgnoreCase("img")){
                        int size = inputStream.readInt();
                        byte[] bytes = new byte[size];
                        inputStream.readFully(bytes);
                        ImageView imageView2 = new ImageView(new Image(new ByteArrayInputStream(bytes)));

                        imageView2.setFitHeight(150);
                        imageView2.setFitWidth(180);

                        HBox hBox = new HBox(10);
                        hBox.getStyleClass().add("receive-box");
                        hBox.setMaxHeight(170);
                        hBox.setMaxWidth(200);
                        hBox.getChildren().add(imageView2);

                        Platform.runLater(() -> {
                            vBox.getChildren().addAll(hBox);
                            scrollPane.layout();
                            scrollPane.setVvalue(2.0);
                        });
                    }else{
                        TextFlow tempFlow = new TextFlow();
                        Text text = new Text(message);
                        text.setWrappingWidth(200);
                        text.getStyleClass().add("receive-text");
                        tempFlow.getChildren().add(text);
                        tempFlow.setMaxWidth(150);

                        HBox hBox = new HBox(12);
                        hBox.getStyleClass().add("receive-box");
                        hBox.setMaxWidth(220);
                        hBox.setMaxHeight(50);
                        hBox.getChildren().add(tempFlow);

                        Platform.runLater(() -> {
                            vBox.getChildren().addAll(hBox);
                            scrollPane.layout();
                            scrollPane.setVvalue(2.0);
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
