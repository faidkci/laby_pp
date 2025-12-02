package com.example.rpn_calc;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CalculatorController
{

    @FXML private TextField inputField;
    @FXML private TextField rpnField;
    @FXML private TextField resultField;
    @FXML private Button calculateButton;

    @FXML
    private void onCalculateClick()
    {
        String expr = inputField.getText();
        String rpn = RPNUtils.toRPN(expr);

        switch (rpn)
        {
            case "s" -> showError("Не хватает закрывающей скобки ')'");
            case "z" -> showError("Не хватает открывающей скобки '('");
            case "c" -> showError("Не хватает числа");
            case "o" -> showError("Не хватает оператора");
            case "empty" -> showError("Введите выражение");
            default ->
            {
                rpnField.setText(rpn);
                Double result = RPNUtils.calcRPN(rpn);
                if (result == null)
                {
                    showError("Ошибка вычисления: деление на ноль или некорректное выражение");
                    resultField.setText("Ошибка");
                }
                else
                {
                    resultField.setText(String.valueOf(result));
                }
            }
        }
    }

    private void showError(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}