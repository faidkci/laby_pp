package org.example;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryService {
    private Document doc;
    private File xmlFile;

    public LibraryService(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public boolean loadLibrary() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            if (!xmlFile.exists()) {
                System.out.println("XML файл не существует, создаем новый...");
                return createNewLibrary();
            }

            // Проверяем, не пустой ли файл
            if (xmlFile.length() == 0) {
                System.out.println("XML файл пустой, создаем новую структуру...");
                return createNewLibrary();
            }

            System.out.println("Загружаем XML файл: " + xmlFile.getAbsolutePath());
            doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Проверяем структуру каждого элемента book
            NodeList bookNodes = doc.getElementsByTagName("book");
            System.out.println("Загружено элементов book: " + bookNodes.getLength());

            for (int i = 0; i < bookNodes.getLength(); i++) {
                Element bookElement = (Element) bookNodes.item(i);
                if (!validateBookStructure(bookElement)) {
                    System.err.println("Обнаружена ошибка в структуре книги с ID: " + bookElement.getAttribute("id"));
                    return false;
                }
            }

            System.out.println("Структура XML файла проверена успешно");
            return true;

        } catch (Exception e) {
            System.err.println("Ошибка загрузки библиотеки: " + e.getMessage());
            return createNewLibrary();
        }
    }

    private boolean validateBookStructure(Element bookElement) {
        try {
            // Проверяем обязательные атрибуты
            String id = bookElement.getAttribute("id");
            if (id == null || id.trim().isEmpty()) {
                System.err.println("Отсутствует обязательный атрибут 'id'");
                return false;
            }

            // Проверяем наличие всех обязательных элементов
            String[] requiredElements = {"title", "author", "year", "category", "price"};
            for (String elementName : requiredElements) {
                NodeList nodes = bookElement.getElementsByTagName(elementName);
                if (nodes.getLength() == 0) {
                    System.err.println("Отсутствует обязательный элемент: " + elementName + " в книге ID: " + id);
                    return false;
                }

                // Проверяем, что элемент не пустой
                String textContent = nodes.item(0).getTextContent();
                if (textContent == null || textContent.trim().isEmpty()) {
                    System.err.println("Элемент " + elementName + " пустой в книге ID: " + id);
                    return false;
                }
            }

            // Проверяем числовые атрибуты
            try {
                Integer.parseInt(bookElement.getAttribute("totalCopies"));
                Integer.parseInt(bookElement.getAttribute("availableCopies"));
            } catch (NumberFormatException e) {
                System.err.println("Ошибка в числовых атрибутах книги ID: " + id);
                return false;
            }

            // Проверяем числовые элементы
            try {
                Integer.parseInt(getElementText(bookElement, "year"));
                parseNumber(getElementText(bookElement, "price"));
            } catch (NumberFormatException e) {
                System.err.println("Ошибка в числовых элементах книги ID: " + id);
                return false;
            }

            return true;

        } catch (Exception e) {
            System.err.println("Ошибка валидации структуры книги: " + e.getMessage());
            return false;
        }
    }

    private boolean createNewLibrary() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();

            Element library = doc.createElement("library");
            doc.appendChild(library);

            saveLibrary();
            System.out.println("Создан новый файл библиотеки: " + xmlFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            System.err.println("Критическая ошибка создания библиотеки: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void saveLibrary() {
        if (doc == null) {
            System.err.println("Документ не инициализирован для сохранения");
            return;
        }

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

            System.out.println("Библиотека сохранена: " + xmlFile.getAbsolutePath());

        } catch (TransformerException e) {
            System.err.println("Ошибка сохранения библиотеки: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        if (doc == null) {
            System.err.println("Документ не загружен");
            return books;
        }

        NodeList bookNodes = doc.getElementsByTagName("book");
        System.out.println("Найдено элементов book: " + bookNodes.getLength());

        for (int i = 0; i < bookNodes.getLength(); i++) {
            Element bookElement = (Element) bookNodes.item(i);
            Book book = elementToBook(bookElement);
            if (book != null) {
                books.add(book);
            }
        }

        return books;
    }

    public Book findBookById(String bookId) {
        Element bookElement = findBookElementById(bookId);
        if (bookElement != null) {
            return elementToBook(bookElement);
        }
        return null;
    }

    public boolean addBook(Book book) {
        if (doc == null) {
            System.err.println("Документ не загружен для добавления книги");
            return false;
        }

        if (findBookElementById(book.getId()) != null) {
            System.err.println("Книга с ID " + book.getId() + " уже существует");
            return false;
        }

        try {
            Element bookElement = doc.createElement("book");
            bookElement.setAttribute("id", book.getId());
            bookElement.setAttribute("totalCopies", String.valueOf(book.getTotalCopies()));
            bookElement.setAttribute("availableCopies", String.valueOf(book.getAvailableCopies()));

            bookElement.appendChild(createElement("title", book.getTitle()));
            bookElement.appendChild(createElement("author", book.getAuthor()));
            bookElement.appendChild(createElement("year", String.valueOf(book.getYear())));
            bookElement.appendChild(createElement("category", book.getCategory()));
            bookElement.appendChild(createElement("price", String.format("%.2f", book.getPrice())));

            doc.getDocumentElement().appendChild(bookElement);
            saveLibrary();

            System.out.println("Успешно добавлена новая книга: " + book.getTitle());
            return true;

        } catch (Exception e) {
            System.err.println("Ошибка при добавлении книги: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Book> searchByAuthor(String author) {
        return searchBooks("//book[contains(translate(author, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + author.toLowerCase() + "')]");
    }

    public List<Book> searchByYear(int year) {
        return searchBooks("//book[year=" + year + "]");
    }

    public List<Book> searchByCategory(String category) {
        return searchBooks("//book[contains(translate(category, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + category.toLowerCase() + "')]");
    }

    public List<Book> searchByTitle(String title) {
        return searchBooks("//book[contains(translate(title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + title.toLowerCase() + "')]");
    }

    private List<Book> searchBooks(String xpathExpr) {
        List<Book> books = new ArrayList<>();
        if (doc == null) return books;

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expr = xpath.compile(xpathExpr);

            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            System.out.println("XPath поиск '" + xpathExpr + "' нашел: " + nodes.getLength() + " элементов");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element bookElement = (Element) nodes.item(i);
                Book book = elementToBook(bookElement);
                if (book != null) {
                    books.add(book);
                }
            }
        } catch (XPathExpressionException e) {
            System.err.println("Ошибка XPath: " + e.getMessage());
        }
        return books;
    }

    public boolean updatePrice(String bookId, double newPrice) {
        if (doc == null) return false;

        Element bookElement = findBookElementById(bookId);
        if (bookElement != null) {
            Node priceNode = bookElement.getElementsByTagName("price").item(0);
            if (priceNode != null) {
                priceNode.setTextContent(String.format("%.2f", newPrice));
                saveLibrary();
                System.out.println("Обновлена цена книги ID " + bookId + " на " + newPrice);
                return true;
            }
        }
        System.out.println("Книга с ID " + bookId + " не найдена для обновления цены");
        return false;
    }

    public boolean lendBook(String bookId) {
        if (doc == null) return false;

        Element bookElement = findBookElementById(bookId);
        if (bookElement != null) {
            try {
                int availableCopies = Integer.parseInt(bookElement.getAttribute("availableCopies"));
                if (availableCopies > 0) {
                    bookElement.setAttribute("availableCopies", String.valueOf(availableCopies - 1));
                    saveLibrary();
                    System.out.println("Выдана книга ID " + bookId + ". Осталось: " + (availableCopies - 1));
                    return true;
                } else {
                    System.out.println("Нет доступных экземпляров книги ID " + bookId);
                }
            } catch (NumberFormatException e) {
                System.err.println("Ошибка формата availableCopies для книги " + bookId);
            }
        }
        System.out.println("Книга с ID " + bookId + " не найдена для выдачи");
        return false;
    }

    public boolean returnBook(String bookId) {
        if (doc == null) return false;

        Element bookElement = findBookElementById(bookId);
        if (bookElement != null) {
            try {
                int availableCopies = Integer.parseInt(bookElement.getAttribute("availableCopies"));
                int totalCopies = Integer.parseInt(bookElement.getAttribute("totalCopies"));
                if (availableCopies < totalCopies) {
                    bookElement.setAttribute("availableCopies", String.valueOf(availableCopies + 1));
                    saveLibrary();
                    System.out.println("Возвращена книга ID " + bookId + ". Доступно: " + (availableCopies + 1));
                    return true;
                } else {
                    System.out.println("Все экземпляры книги ID " + bookId + " уже доступны");
                }
            } catch (NumberFormatException e) {
                System.err.println("Ошибка формата атрибутов для книги " + bookId);
            }
        }
        System.out.println("Книга с ID " + bookId + " не найдена для возврата");
        return false;
    }

    private Element findBookElementById(String bookId) {
        if (doc == null) return null;

        NodeList bookNodes = doc.getElementsByTagName("book");
        for (int i = 0; i < bookNodes.getLength(); i++) {
            Element bookElement = (Element) bookNodes.item(i);
            if (bookElement.getAttribute("id").equals(bookId)) {
                return bookElement;
            }
        }
        return null;
    }

    private Book elementToBook(Element bookElement) {
        try {
            String id = bookElement.getAttribute("id");
            String title = getElementText(bookElement, "title");
            String author = getElementText(bookElement, "author");

            String yearText = getElementText(bookElement, "year");
            int year = Integer.parseInt(yearText.trim());

            String category = getElementText(bookElement, "category");

            String priceText = getElementText(bookElement, "price").trim();
            double price = parseNumber(priceText);

            String totalCopiesText = bookElement.getAttribute("totalCopies").trim();
            int totalCopies = Integer.parseInt(totalCopiesText);

            String availableCopiesText = bookElement.getAttribute("availableCopies").trim();
            int availableCopies = Integer.parseInt(availableCopiesText);

            return new Book(id, title, author, year, category, price, totalCopies, availableCopies);

        } catch (Exception e) {
            System.err.println("Ошибка преобразования элемента в книгу: " + e.getMessage());
            System.err.println("Элемент: " + bookElement.getAttribute("id"));
            e.printStackTrace();
            return null;
        }
    }

    private double parseNumber(String numberText) {
        if (numberText == null || numberText.trim().isEmpty()) {
            return 0.0;
        }

        String normalized = numberText.replace(',', '.');

        normalized = normalized.trim();

        try {
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            System.err.println("Ошибка парсинга числа: '" + numberText + "' -> '" + normalized + "'");
            throw e;
        }
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            String text = nodes.item(0).getTextContent();
            return text != null ? text.trim() : "";
        }
        return "";
    }

    private Element createElement(String tagName, String textContent) {
        Element element = doc.createElement(tagName);


        if ("price".equals(tagName) || "year".equals(tagName)) {
            try {
                double number = parseNumber(textContent);
                if ("price".equals(tagName)) {
                    element.setTextContent(String.format("%.2f", number).replace(',', '.'));
                } else {
                    element.setTextContent(String.valueOf((int)number));
                }
            } catch (NumberFormatException e) {
                element.setTextContent(textContent);
            }
        } else {
            element.setTextContent(textContent);
        }

        return element;
    }
}