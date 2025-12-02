package org.example;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class XMLValidator {

    public static boolean validateXML(File xmlFile, File xsdFile) {
        if (!xmlFile.exists()) {
            System.err.println("XML файл не найден: " + xmlFile.getAbsolutePath());
            return false;
        }

        if (!xsdFile.exists()) {
            System.err.println("XSD файл не найден: " + xsdFile.getAbsolutePath());
            return false;
        }

        try {
            System.out.println("Начинаем валидацию XML...");
            System.out.println("XML файл: " + xmlFile.getAbsolutePath());
            System.out.println("XSD файл: " + xsdFile.getAbsolutePath());

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsdFile);
            Validator validator = schema.newValidator();

            validator.setErrorHandler(new SimpleErrorHandler());

            validator.validate(new StreamSource(xmlFile));

            System.out.println("Валидация успешно пройдена!");
            return true;

        } catch (SAXException e) {
            System.err.println("Ошибка валидации: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
            return false;
        }
    }

    private static class SimpleErrorHandler implements org.xml.sax.ErrorHandler {
        @Override
        public void warning(org.xml.sax.SAXParseException exception) {
            System.out.println("Warning: " + exception.getMessage());
        }

        @Override
        public void error(org.xml.sax.SAXParseException exception) {
            System.err.println("Error: " + exception.getMessage());
            System.err.println("Line: " + exception.getLineNumber() + ", Column: " + exception.getColumnNumber());
        }

        @Override
        public void fatalError(org.xml.sax.SAXParseException exception) {
            System.err.println("Fatal Error: " + exception.getMessage());
            System.err.println("Line: " + exception.getLineNumber() + ", Column: " + exception.getColumnNumber());
        }
    }
}