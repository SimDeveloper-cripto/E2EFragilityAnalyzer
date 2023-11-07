
import java.io.*;
import java.util.List;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.lang.reflect.InvocationTargetException;

// PROJECT'S START POINT

public class JUnitRunner {
    static String SoftwareUsed = "Dolibarr";
    public static List<Test> createTestScore(List<Test> testWithSelector) {
        double scoreTest;

        for (Test testToJudge: testWithSelector) {
            scoreTest = Judge.getTestScore(testToJudge);
            testToJudge.setTestScore(scoreTest);
        }
        return testWithSelector;
    }
    private static void showResult(List<Test> testsJudged, TestRunner testRunner) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.00", symbols);

        System.out.println("============ Results =============");
        System.out.println("== Successes: " + testRunner.numberOfSuccessTests + "\n== Failures: " + testRunner.numberOfFailedTests);
        System.out.println("==================================");

        for (Test testJudged:testsJudged) {
            System.out.println("Test name: " + testJudged.getClassName() + " Score: " + df.format(testJudged.getTestScore()));
        }
        String directory = "src/test/java/XMLResult/" + SoftwareUsed + "/Result.csv";
        createSelectorScoreFiles(testsJudged,df);
        writeInResult(testsJudged, directory,"Metric", df);
    }
    public static void writeInResult(List<Test> testsJudge, String csvFileName, String columnName, DecimalFormat df) {
        try {
            File inputFile = new File(csvFileName);
            File tempFile = new File("temp.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            String[] headers;
            boolean foundColumn = false;
            int columnIdx = -1;

            // Process the header row separately
            if ((line = reader.readLine()) != null) {
                headers = line.split(",");
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].equals(columnName)) {
                        columnIdx = i;
                        foundColumn = true;
                        break;
                    }
                }
                writer.write(line);
                writer.newLine();
            }

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (foundColumn && !testsJudge.isEmpty()) {
                    // Update the values in the column with scores from testsJudge
                    if (columnIdx >= 0 && columnIdx < data.length) {
                        String newValue = df.format(testsJudge.remove(0).getTestScore());
                        data[columnIdx] = newValue;
                    }
                }

                // Reconstruct the line with updated data
                String newLine = String.join(",", data);
                writer.write(newLine);
                writer.newLine();
            }

            reader.close();
            writer.close();

            // Rename tempFile to inputFile
            if (inputFile.delete()) {
                boolean ret =  tempFile.renameTo(inputFile);

                if (ret)
                    System.out.println("Successfully renamed the temporary file to the original file.");
            } else {
                throw new IOException("Failed to rename the temporary file to the original file.");
            }

            System.out.println("Result.csv file updated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void createSelectorScoreFiles(List<Test> testsJudged, DecimalFormat df) {
        String basePath = "src/test/java/XMLResult/"+SoftwareUsed+"/Scores/";
        File directory = new File(basePath);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
             System.out.println("Error at JUnitRunner.java 'createSelectorScoreFiles(...)': could not create directory.");
            }
        }

        for (Test test : testsJudged) {
            String fileName = basePath + test.getTestName() + "_scores.csv";

            // TODO: This must be modified
            try (FileWriter fileWriter = new FileWriter(fileName)) {
                String index = "Tipo, Selettore, Punteggio Selettore, Punteggio pagina, Punteggio finale \n";

                fileWriter.write(index);
                for (int i = 0; i < test.getSelectors().size(); i++) {
                    Selector selector = test.getSelectors().get(i);
                    Page page = test.getPage().get(i);

                    String nameSelector = selector.getSelector();
                    String typeSelector = selector.getType();
                    float selectorScore = selector.getSelectorScore();
                    float pageScore=page.getPageScore();
                    float selectorFinalScore = selector.getSelectorFinalScore();

                    String line = typeSelector + "," + nameSelector + "," + df.format(selectorScore) +
                            "," + df.format(pageScore) + "," + df.format(selectorFinalScore)+"\n";

                    fileWriter.write(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException {
        String directory = "src/test/java/JUnit/" + SoftwareUsed; // JUnit test directory

        // Step 1: retrieve all tests
        List<Test> dolibarrTests = Test.getAllTests(directory);
        TestRunner testRunner = new TestRunner(dolibarrTests); // Believe it or not, the TestRunner runs all tests :)

        // Step 2: run tests
        List<Test> testsWithSelector = testRunner.executeTests();

        // Step 3: for each test assign its score
        List<Test> testsJudged = createTestScore(testsWithSelector);

        PointBiserialCorrelationCoefficient.getCorrelation(testsJudged);

        // Step 4: show results
        showResult(testsJudged, testRunner);
    }
}