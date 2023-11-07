import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/* JUnitRunner prende tutti i casi di test registrati e li esegue con JUnitCore */
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO: This class can be used when refactoring

public class XMLReader {
    public static void main(String[] args) {
        // String directoryPath = "src/test/java/XMLResult/JUnit.Dolibarr";
        // String directoryPath = "src/test/java/XMLResult/JUnit.MantisBT";
        String directoryPath = "src/test/java/XMLResult/JUnit.Magento";
        String csvFilePath = directoryPath + "/Result.csv";

        File directory = new File(directoryPath);
        File[] xmlFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

        FileWriter csvWriter;
        try {
            csvWriter = new FileWriter(csvFilePath);

            List<String> testNames = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();

            // Ottieni i nomi dei casi di test dal primo file XML
            if (xmlFiles.length > 0) {
                File firstXmlFile = xmlFiles[0];
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(firstXmlFile);

                    XPathFactory xPathFactory = XPathFactory.newInstance();
                    XPath xpath = xPathFactory.newXPath();
                    csvWriter.append("Nome del test,");

                    // Ottieni i nomi dei casi di test
                    String expression = "//test/@name";
                    NodeList testNameNodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
                    for (int i = 0; i < testNameNodes.getLength(); i++) {
                        csvWriter.append("\n");
                        String testName = testNameNodes.item(i).getNodeValue();
                        testNames.add(testName);
                        csvWriter.append(testName).append(",");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            csvWriter.append("\n");
            csvWriter.flush();
            csvWriter.close();

            // Itera sui file XML
            int leght = 0;
            for (File xmlFile : xmlFiles) {
                leght += 1;
                ArrayList<String> column = new ArrayList<>();
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(xmlFile);

                    XPathFactory xPathFactory = XPathFactory.newInstance();
                    XPath xpath = xPathFactory.newXPath();

                    String fileNameWithExtension = xmlFile.getName();
                    String fileNameWithoutExtension = getFileNameWithoutExtension(fileNameWithExtension);
                    column.add(fileNameWithoutExtension);

                    // Ottieni i valori degli attributi status
                    String expression = "//test/@status";
                    NodeList statusNodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

                    // Aggiungi i valori degli attributi status come righe nel file CSV
                    for (int i = 0; i < statusNodes.getLength(); i++) {
                        String status = statusNodes.item(i).getNodeValue();
                        column.add(status);
                    }
                    List<String> list = column;
                    String[] array = list.toArray(new String[0]);
                    appendCol(csvFilePath, array,",", leght);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("File CSV creato con successo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileNameWithoutExtension(String fileNameWithExtension) {
        int dotIndex = fileNameWithExtension.lastIndexOf(".");
        return fileNameWithExtension.substring(0, dotIndex);
    }
    public static void appendCol(String fileName, String[] newCol,String delimiter,int colPos) throws IOException {
        List<String> data = Files.readAllLines(Paths.get(fileName));
        PrintWriter pw = new PrintWriter(fileName);
        FileWriter fw = new FileWriter(fileName,true);

        for(int i = 0; i < data.size(); i++){
            String[] line = data.get(i).split((delimiter));
            List<String> record = new ArrayList<String>(Arrays.asList(line));
            record.add(colPos,newCol[i]);
            pw.println(String.join(delimiter, record));
        }
        pw.close();
    }
}