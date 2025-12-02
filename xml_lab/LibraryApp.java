package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Locale;

public class LibraryApp extends JFrame {
    private LibraryService libraryService;
    private JTable table;
    private DefaultTableModel tableModel;
    private File xmlFile;
    private File xsdFile;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryApp().setVisible(true));
    }

    public LibraryApp() {
        initializeFiles();
        initializeUI();
        initializeLibraryService();
    }

    private void initializeFiles() {
        xmlFile = new File("library.xml");
        xsdFile = new File("library.xsd");

        System.out.println("XML файл: " + xmlFile.getAbsolutePath());
        System.out.println("XSD файл: " + xsdFile.getAbsolutePath());
    }

    private void initializeLibraryService() {
        libraryService = new LibraryService(xmlFile);

        System.out.println("Инициализация библиотеки...");
        if (!libraryService.loadLibrary()) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка загрузки библиотеки!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        refreshTable();
    }

    private void initializeUI() {
        setTitle("Управление библиотекой");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        createMenuBar();
        createTable();
        createToolBar();
        createControlPanel();

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu operationsMenu = new JMenu("Операции");
        String[] menuItems = {
                "Показать все книги", "Добавить книгу", "Поиск по автору",
                "Поиск по году", "Поиск по категории", "Изменить цену", "Выдать книгу", "Вернуть книгу"
        };

        JMenuItem loadFromXmlItem = new JMenuItem("Загрузить из XML файла");
        loadFromXmlItem.addActionListener(e -> loadBooksFromXML());
        operationsMenu.add(loadFromXmlItem);

        for (String item : menuItems) {
            JMenuItem menuItem = new JMenuItem(item);
            menuItem.addActionListener(e -> handleMenuAction(item));
            operationsMenu.add(menuItem);
        }

        menuBar.add(operationsMenu);
        setJMenuBar(menuBar);
    }

    private void createToolBar() {
        JToolBar toolBar = new JToolBar();


        String[][] buttons = {
                {"📚", "Показать все", "Показать все книги"},
                {"➕", "Добавить", "Добавить книгу"},
                {"👤", "Поиск автора", "Поиск по автору"},
                {"📅", "Поиск года", "Поиск по году"},
                {"📂", "Поиск категории", "Поиск по категории"},
                {"💰", "Изменить цену", "Изменить цену"},
                {"📖", "Выдать", "Выдать книгу"},
                {"↩️", "Вернуть", "Вернуть книгу"},
                {"📥", "Загрузить XML", "Загрузить из XML файла"}
        };

        for (String[] buttonInfo : buttons) {
            JButton button = new JButton(buttonInfo[0] + " " + buttonInfo[1]);
            button.setToolTipText(buttonInfo[2]);
            button.addActionListener(e -> handleMenuAction(buttonInfo[2]));
            toolBar.add(button);
        }

        add(toolBar, BorderLayout.NORTH);
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());


        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("🔍 Поиск");


        String[] searchTypes = {"По автору", "По году", "По категории", "По названию"};
        JComboBox<String> searchTypeCombo = new JComboBox<>(searchTypes);

        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            String searchType = (String) searchTypeCombo.getSelectedItem();

            if (!searchText.isEmpty()) {
                switch (searchType) {
                    case "По автору":
                        displaySearchResults(libraryService.searchByAuthor(searchText));
                        break;
                    case "По году":
                        try {
                            int year = Integer.parseInt(searchText);
                            displaySearchResults(libraryService.searchByYear(year));
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Введите корректный год!");
                        }
                        break;
                    case "По категории":
                        displaySearchResults(libraryService.searchByCategory(searchText));
                        break;
                    case "По названию":
                        displaySearchResults(libraryService.searchByTitle(searchText));
                        break;
                }
            }
        });

        controlPanel.add(new JLabel("Поиск:"));
        controlPanel.add(searchTypeCombo);
        controlPanel.add(searchField);
        controlPanel.add(searchButton);


        JButton refreshButton = new JButton("🔄 Обновить");
        refreshButton.addActionListener(e -> refreshTable());
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void loadBooksFromXML() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите XML файл для загрузки");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML files", "xml"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedXmlFile = fileChooser.getSelectedFile();
            File selectedXsdFile = findXsdFile(selectedXmlFile);

            // Строгая валидация XML перед загрузкой
            if (selectedXsdFile != null) {
                if (!XMLValidator.validateXML(selectedXmlFile, selectedXsdFile)) {
                    JOptionPane.showMessageDialog(this,
                            "XML файл не прошел валидацию по XSD схеме!\n" +
                                    "Убедитесь, что:\n" +
                                    "- Все обязательные элементы присутствуют\n" +
                                    "- Типы данных соответствуют схеме\n" +
                                    "- Структура XML корректна",
                            "Ошибка валидации",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                int choice = JOptionPane.showConfirmDialog(this,
                        "XSD схема не найдена. Продолжить загрузку без валидации?",
                        "Предупреждение",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                // Создаем временный сервис для загрузки данных
                LibraryService tempService = new LibraryService(selectedXmlFile);
                if (tempService.loadLibrary()) {
                    List<Book> loadedBooks = tempService.getAllBooks();

                    if (loadedBooks.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "В выбранном файле не найдено книг или структура данных некорректна!",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Показываем превью загружаемых книг
                    showImportPreview(loadedBooks, selectedXmlFile);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Ошибка загрузки данных из файла!\n" +
                                    "Проверьте структуру XML файла.",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка при чтении файла: " + e.getMessage() +
                                "\n\nВозможные причины:\n" +
                                "- Неправильная структура XML\n" +
                                "- Отсутствуют обязательные элементы\n" +
                                "- Неверные типы данных",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private File findXsdFile(File xmlFile) {
        String xmlPath = xmlFile.getAbsolutePath();
        String xsdPath = xmlPath.replace(".xml", ".xsd");
        File xsdFile = new File(xsdPath);

        if (xsdFile.exists()) {
            return xsdFile;
        }

        xsdPath = xmlFile.getParent() + File.separator + "library.xsd";
        xsdFile = new File(xsdPath);

        return xsdFile.exists() ? xsdFile : null;
    }

    private void showImportPreview(List<Book> books, File sourceFile) {
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "В выбранном файле не найдено книг!",
                    "Информация",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog previewDialog = new JDialog(this, "Превью загрузки", true);
        previewDialog.setSize(800, 500);
        previewDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Файл: " + sourceFile.getName()));
        infoPanel.add(new JLabel("Найдено книг: " + books.size()));

        String[] columns = {"ID", "Название", "Автор", "Год", "Категория", "Цена"};
        DefaultTableModel previewModel = new DefaultTableModel(columns, 0);
        JTable previewTable = new JTable(previewModel);

        for (Book book : books) {
            previewModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getYear(),
                    book.getCategory(),
                    String.format("%.2f", book.getPrice())
            });
        }

        JPanel buttonPanel = new JPanel();
        JButton importButton = new JButton("Импортировать");
        JButton cancelButton = new JButton("Отмена");

        importButton.addActionListener(e -> {
            performImport(books);
            previewDialog.dispose();
        });

        cancelButton.addActionListener(e -> previewDialog.dispose());

        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(previewTable), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        previewDialog.setContentPane(mainPanel);
        previewDialog.setVisible(true);
    }

    private void performImport(List<Book> booksToImport) {
        int importedCount = 0;
        int skippedCount = 0;

        for (Book book : booksToImport) {
            if (libraryService.findBookById(book.getId()) == null) {
                if (libraryService.addBook(book)) {
                    importedCount++;
                } else {
                    skippedCount++;
                }
            } else {
                skippedCount++;
            }
        }

        refreshTable();

        JOptionPane.showMessageDialog(this,
                String.format("Импорт завершен!\nУспешно импортировано: %d\nПропущено (дубликаты): %d",
                        importedCount, skippedCount),
                "Результат импорта",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void createTable() {
        String[] columns = {"ID", "Название", "Автор", "Год", "Категория", "Цена", "Всего", "Доступно"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);
    }

    private void handleMenuAction(String action) {
        switch (action) {
            case "Показать все книги":
                refreshTable();
                break;
            case "Добавить книгу":
                addNewBook();
                break;
            case "Поиск по автору":
                searchByAuthor();
                break;
            case "Поиск по году":
                searchByYear();
                break;
            case "Поиск по категории":
                searchByCategory();
                break;
            case "Изменить цену":
                changePrice();
                break;
            case "Выдать книгу":
                lendBook();
                break;
            case "Вернуть книгу":
                returnBook();
                break;
            case "Загрузить из XML файла":
                loadBooksFromXML();
                break;
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Book> books = libraryService.getAllBooks();
        System.out.println("Загружено книг для отображения: " + books.size());

        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getYear(),
                    book.getCategory(),
                    formatPrice(book.getPrice()),
                    book.getTotalCopies(),
                    book.getAvailableCopies()
            });
        }

        setTitle("Управление библиотекой - Книг: " + books.size());
    }

    private void addNewBook() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField totalField = new JTextField("1");
        JTextField availableField = new JTextField("1");

        Object[] message = {
                "ID (уникальный):", idField,
                "Название книги:", titleField,
                "Автор:", authorField,
                "Год издания:", yearField,
                "Категория:", categoryField,
                "Цена:", priceField,
                "Всего экземпляров:", totalField,
                "Доступно экземпляров:", availableField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Добавить книгу", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                if (idField.getText().trim().isEmpty() || titleField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Заполните ID и название книги!");
                    return;
                }

                Book book = new Book(
                        idField.getText().trim(),
                        titleField.getText().trim(),
                        authorField.getText().trim(),
                        yearField.getText().trim().isEmpty() ? 2023 : Integer.parseInt(yearField.getText().trim()),
                        categoryField.getText().trim(),
                        priceField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(priceField.getText().trim()),
                        totalField.getText().trim().isEmpty() ? 1 : Integer.parseInt(totalField.getText().trim()),
                        availableField.getText().trim().isEmpty() ? 1 : Integer.parseInt(availableField.getText().trim())
                );

                boolean success = libraryService.addBook(book);
                if (success) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Книга добавлена успешно!");
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при добавлении книги!");
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ошибка в числовых полях!");
            }
        }
    }

    private void searchByAuthor() {
        String author = JOptionPane.showInputDialog(this, "Введите автора:");
        if (author != null && !author.trim().isEmpty()) {
            displaySearchResults(libraryService.searchByAuthor(author.trim()));
        }
    }

    private void searchByYear() {
        String yearStr = JOptionPane.showInputDialog(this, "Введите год:");
        if (yearStr != null && !yearStr.trim().isEmpty()) {
            try {
                int year = Integer.parseInt(yearStr.trim());
                displaySearchResults(libraryService.searchByYear(year));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Некорректный год!");
            }
        }
    }

    private void searchByCategory() {
        String category = JOptionPane.showInputDialog(this, "Введите категорию:");
        if (category != null && !category.trim().isEmpty()) {
            displaySearchResults(libraryService.searchByCategory(category.trim()));
        }
    }

    private void displaySearchResults(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getYear(),
                    book.getCategory(),
                    formatPrice(book.getPrice()),
                    book.getTotalCopies(),
                    book.getAvailableCopies()
            });
        }

        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Книги не найдены!");
        } else {
            JOptionPane.showMessageDialog(this, "Найдено книг: " + books.size());
        }
    }

    private void changePrice() {
        String bookId = JOptionPane.showInputDialog(this, "Введите ID книги:");
        if (bookId != null && !bookId.trim().isEmpty()) {
            String newPriceStr = JOptionPane.showInputDialog(this, "Введите новую цену:");
            if (newPriceStr != null) {
                try {
                    double newPrice = Double.parseDouble(newPriceStr.trim());
                    if (libraryService.updatePrice(bookId.trim(), newPrice)) {
                        refreshTable();
                        JOptionPane.showMessageDialog(this, "Цена изменена успешно!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Книга с указанным ID не найдена!");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Некорректная цена!");
                }
            }
        }
    }

    private void lendBook() {
        String bookId = JOptionPane.showInputDialog(this, "Введите ID книги для выдачи:");
        if (bookId != null && !bookId.trim().isEmpty()) {
            if (libraryService.lendBook(bookId.trim())) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Книга выдана успешно!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Книга не найдена или нет доступных экземпляров!",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void returnBook() {
        String bookId = JOptionPane.showInputDialog(this, "Введите ID книги для возврата:");
        if (bookId != null && !bookId.trim().isEmpty()) {
            if (libraryService.returnBook(bookId.trim())) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Книга возвращена успешно!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Книга не найдена или все экземпляры уже доступны!",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%.2f", price);
    }
}