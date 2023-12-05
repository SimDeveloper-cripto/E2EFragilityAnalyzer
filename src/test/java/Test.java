import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import java.util.List;
import java.util.ArrayList;

public class Test {
    private final String className;
    private List<Selector> selectors;
    private List<Page> pages;
    private double testScore;
    private final boolean laterGetSuccess;

    public Test(String className) {
        this.className = className;
        this.testScore = 0;

        if (JUnitRunner.SoftwareUsed.equals("Phormer"))
            this.laterGetSuccess = getStatusResult(this.getFullTestName(),1);
        else
            this.laterGetSuccess = getStatusResult(this.getFullTestName(),5); // Dolibarr, Magento, MantisBT have 5 versions
    }

    @Override
    public String toString() {
        return "Test {" + "className = '" + className + '\'' + ", testScore = " + getTestScore() + ", laterGetSuccess = " + laterGetSuccess + '}';
    }

    private boolean getStatusResult(String testName, int column) {
        boolean status = false;
        String directory = "src/test/java/XMLResult/" + JUnitRunner.SoftwareUsed + "/Result.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(directory))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String currentTestName = values[0]; // Test's name first column

                if (currentTestName.equals(TestRunner.decapitalize(testName))) {
                    String statusString = values[column];
                    if(statusString.equals("passed")) status = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }

    public static List<Test> getAllTests(String directory) {
        List<Test> listOfTests = new ArrayList<>();
        List<String> fileNames = null;

        if (JUnitRunner.SoftwareUsed.equals("PasswordManager")) {
            fileNames = new ArrayList<>();
            fileNames.add("src/test/java/JUnit/PasswordManager/RegisterTest.java");
            fileNames.add("src/test/java/JUnit/PasswordManager/LoginTest.java");
            fileNames.add("src/test/java/JUnit/PasswordManager/AddEntry1Test.java");
            fileNames.add("src/test/java/JUnit/PasswordManager/AddEntry2Test.java");
            fileNames.add("src/test/java/JUnit/PasswordManager/ModifyEntry1Test.java");
            fileNames.add("src/test/java/JUnit/PasswordManager/ModifyEntry2Test.java");
            fileNames.add("src/test/java/JUnit/PasswordManager/ExportCSVTest.java");
            fileNames.add("src/test/java/JUnit/PasswordManager/DeleteEntry1Test.java");
            fileNames.add("src/test/java/JUnit/PasswordManager/DeleteEntry2Test.java");
            fileNames.add("src/test/java/JUnit/PasswordManager/ImportCSVTest.java");
        } else {
            fileNames = getFileNames(directory);
        }

        for (String className : fileNames) {
            Test newTest = new Test(className);
            listOfTests.add(newTest);
        }

        return listOfTests;
    }

    public static List<String> getFileNames(String directoryPath) {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(directoryPath + "/" + file.getName());
                    }
                }
            }
        }
        return fileNames;
    }

    public String getClassName() {
        return this.className;
    }

    public String getTestName() {
        int lastIndex = className.lastIndexOf("/");
        int endIndex  = className.lastIndexOf(".java");

        if (lastIndex >= 0 && lastIndex < className.length() - 1) {
            if (endIndex > lastIndex) {
                return className.substring(lastIndex + 1, endIndex);
            } else {
                return className.substring(lastIndex + 1);
            }
        } else {
            return className;
        }
    }

    public String getFullTestName() {
        int lastIndex = className.lastIndexOf("/");
        int endIndex = className.lastIndexOf("Test.java");

        if (lastIndex >= 0 && lastIndex < className.length() - 1) {
            if (endIndex > lastIndex) {
                return className.substring(lastIndex + 1, endIndex);
            } else {
                return className.substring(lastIndex + 1);
            }
        } else {
            return className;
        }
    }

    public double getTestScore() {
        return this.testScore;
    }

    public void setTestScore(double testScore) {
        this.testScore = testScore;
    }

    public List<Selector> getSelectors() {
        return this.selectors;
    }

    public void setSelectors(List<Selector> selectors) {
        this.selectors = selectors;
    }

    public List<Page> getPages() {
        return this.pages;
    }

    public void setPages(List<Page> page) {
        this.pages = page;
    }

    public boolean isLaterGetSuccess() {
        return this.laterGetSuccess;
    }
}