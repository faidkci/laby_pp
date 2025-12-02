package com.example.rpn_calc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("calculator.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 450);
        stage.setTitle("RPN Calc");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}