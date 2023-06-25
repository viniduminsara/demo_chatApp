package com.example.chatapp.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormController{

    @FXML
    private TextField txtMessage;

    @FXML
    private VBox vBox;

    @FXML
    private ScrollPane scrollPane;

    private ServerSocket serverSocket;

    private Socket socket;

    private BufferedReader reader;

    private PrintWriter writer;

    private String message = "";

    private File file;

    @FXML
    void btnSendOnAction(ActionEvent event) {
        if (!txtMessage.getText().isEmpty()) {
            if (txtMessage.getText().equals("1 image selected")) {
                writer.println("img" + file.getPath());
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

                Platform.runLater(() -> {
                    vBox.getChildren().addAll(stackPane);
                    scrollPane.layout();
                    scrollPane.setVvalue(2.0);
                });
                txtMessage.setEditable(true);
                txtMessage.clear();
            } else {
                writer.println(txtMessage.getText());
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

                Platform.runLater(() -> {
                    vBox.getChildren().addAll(stackPane);
                    scrollPane.layout();
                    scrollPane.setVvalue(2.0);
                });
                txtMessage.clear();
            }
        }else{
            new Alert(Alert.AlertType.WARNING,"Please enter your message").show();
        }
    }

    @FXML
    void attachmentOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the image");
        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);
        file = fileChooser.showOpenDialog(txtMessage.getScene().getWindow());
        if (file != null){
            txtMessage.setText("1 image selected");
            txtMessage.setEditable(false);
        }
    }

    @FXML
    void txtMessageOnAction(ActionEvent event) {
        btnSendOnAction(event);
    }

    public void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(2.0);

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(4005);

                socket = serverSocket.accept();

                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    message = reader.readLine();
                    String firstChars = "";
                    if (message.length() > 3) {
                        firstChars = message.substring(0, 3);

                    }
                    if (firstChars.equalsIgnoreCase("img")){
                        message = message.substring(3);
                        File receives = new File(message);
                        Image image = new Image(receives.toURI().toString());
                        ImageView imageView2 = new ImageView(image);

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

                        HBox hBox = new HBox();
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