package com.example.rpn_calc;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CalculatorController {

    @FXML private TableView<DataItem> dataTableView;
    @FXML private TableColumn<DataItem, String> column1;
    @FXML private TableColumn<DataItem, String> column2;
    @FXML private TableColumn<DataItem, String> column3;
    @FXML private TableColumn<DataItem, String> column4;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button loadButton;
    @FXML private Button sortButton;

    private ObservableList<DataItem> data = FXCollections.observableArrayList();

    public static class DataItem {
        private final String value1;
        private final String value2;
        private final String value3;
        private final String value4;

        public DataItem(String value1, String value2, String value3, String value4) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
            this.value4 = value4;
        }

        public String getValue1() { return value1; }
        public String getValue2() { return value2; }
        public String getValue3() { return value3; }
        public String getValue4() { return value4; }
    }

    @FXML
    public void initialize() {
        // Настройка столбцов таблицы
        column1.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getValue1()));
        column2.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getValue2()));
        column3.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getValue3()));
        column4.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getValue4()));

        // Заполнение комбобокса критериями сортировки
        sortComboBox.setItems(FXCollections.observableArrayList(
                "По колонке 1 (возрастание)",
                "По колонке 1 (убывание)",
                "По колонке 2 (возрастание)",
                "По колонке 2 (убывание)",
                "По колонке 3 (возрастание)",
                "По колонке 3 (убывание)",
                "По колонке 4 (возрастание)",
                "По колонке 4 (убывание)"
        ));

        dataTableView.setItems(data);
    }

    @FXML
    private void loadDataFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл с данными");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );

        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            try {
                loadDataFromFile(file.getAbsolutePath());
            } catch (IOException e) {
                showError("Ошибка загрузки файла: " + e.getMessage());
            } catch (Exception e) {
                showError("Ошибка обработки файла: " + e.getMessage());
            }
        }
    }

    private void loadDataFromFile(String filePath) throws IOException {
        data.clear();
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length >= 4) {
                DataItem item = new DataItem(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim()
                );
                data.add(item);
            }
        }

        if (data.isEmpty()) {
            showInfo("Файл загружен, но данные не найдены");
        } else {
            showInfo("Данные успешно загружены\nЗагружено записей: " + data.size());
        }
    }

    @FXML
    private void sortData() {
        String selectedSort = sortComboBox.getValue();
        if (selectedSort == null) {
            showError("Выберите критерий сортировки");
            return;
        }

        List<DataItem> sortedList = new ArrayList<>(data);

        switch (selectedSort) {
            case "По колонке 1 (возрастание)":
                sortedList.sort(Comparator.comparing(DataItem::getValue1));
                break;
            case "По колонке 1 (убывание)":
                sortedList.sort(Comparator.comparing(DataItem::getValue1).reversed());
                break;
            case "По колонке 2 (возрастание)":
                sortedList.sort(Comparator.comparing(DataItem::getValue2));
                break;
            case "По колонке 2 (убывание)":
                sortedList.sort(Comparator.comparing(DataItem::getValue2).reversed());
                break;
            case "По колонке 3 (возрастание)":
                sortedList.sort(Comparator.comparing(DataItem::getValue3));
                break;
            case "По колонке 3 (убывание)":
                sortedList.sort(Comparator.comparing(DataItem::getValue3).reversed());
                break;
            case "По колонке 4 (возрастание)":
                sortedList.sort(Comparator.comparing(DataItem::getValue4));
                break;
            case "По колонке 4 (убывание)":
                sortedList.sort(Comparator.comparing(DataItem::getValue4).reversed());
                break;
        }

        data.setAll(sortedList);
        showInfo("Данные отсортированы по критерию: " + selectedSort);
    }

    private Stage getStage() {
        return (Stage) loadButton.getScene().getWindow();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
