import java.io.*;
import java.util.List;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/* [DESCRIPTION]
    - Log class is used to show the user Test results on terminal and also in the specified files
* */
public class Log {
    private void fillResultFile(List<Test> testsJudge, String csvFileNamePath, String columnName, DecimalFormat df) {
        try {
            File inputFile = new File(csvFileNamePath);
            File tempFile  = new File("temp.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            String[] headers;
            int columnIdx = -1;
            boolean foundColumn = false;

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
                if (tempFile.renameTo(inputFile)) System.out.println("Result.csv file updated successfully! ( " + csvFileNamePath + " )");
            } else {
                throw new IOException("Failed to rename the temporary file to the original Result file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createScoreFileForEachTest(List<Test> testsJudged, DecimalFormat df) {
        String basePath = "src/test/java/XMLResult/" + JUnitRunner.SoftwareUsed + "/Scores/";
        File directory = new File(basePath);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.out.println("Error at JUnitRunner.java 'createSelectorScoreFiles(...)': could not create directory.");
            }
        }

        for (Test test : testsJudged) {
            String fileName = basePath + test.getTestName() + "_scores.csv";

            try (FileWriter fileWriter = new FileWriter(fileName)) {
                String index = "Type, Selector, SelectorScore, PageScore, PageAndSelectorScore, FinalScore \n\n";

                fileWriter.write(index);
                for (int i = 0; i < test.getSelectors().size(); i++) {
                    Selector selector = test.getSelectors().get(i);
                    Page page = test.getPages().get(i);

                    String nameSelector = selector.getSelector();
                    String typeSelector = selector.getType();
                    float selectorScore = selector.getSelectorScore();
                    float pageScore     = selector.getPageScore();
                    float pageAndSelectorScore = selector.getPageAndSelectorScore();
                    double selectorFinalScore  = selector.getSelectorFinalScore();

                    String line = typeSelector + ", " + nameSelector + ", " + df.format(selectorScore) +
                            ", " + df.format(pageScore) + ", " + df.format(pageAndSelectorScore) + ", " + df.format(selectorFinalScore) + "\n";

                    fileWriter.write(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("You can find each Test score in: " + basePath);
    }

    public void logResult(List<Test> testsJudged, TestRunner testRunner) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.000", symbols);

        System.out.println("============ Results =============");
        System.out.println("== Successes: " + testRunner.getNumberOfSuccessTests() + "\n== Failures: " + testRunner.getNumberOfFailedTests());
        System.out.println("==================================");

        logResultForEachTest(testsJudged, df);

        String directory = "src/test/java/XMLResult/" + JUnitRunner.SoftwareUsed + "/Result.csv";
        createScoreFileForEachTest(testsJudged, df);
        fillResultFile(testsJudged, directory,"Metric Result", df);
    }

    public void logResultForEachTest(List<Test> testsJudged, DecimalFormat df) {
        System.out.println(" ");
        for (Test testJudged : testsJudged) {
            System.out.println("Test name: " + testJudged.getClassName());
            for (Selector selector : testJudged.getSelectors()) {
                System.out.println("\tSelector: " + selector.getSelector());
                System.out.println("\t\tSelectorScore: " + df.format(selector.getSelectorScore()) + ", PageScore: " + df.format(selector.getPageScore())
                        + ", PageAndSelectorScore: " + df.format(selector.getPageAndSelectorScore()) + ", SelectorFinalScore: " + df.format(selector.getSelectorFinalScore()));
            }
            System.out.println("Test Score (by harmonic Mean): " + testJudged.getTestScore());
            System.out.println(" ");
        }
        System.out.println(" ");
        double result = PointBiserialCorrelationCoefficient.getCorrelation(testsJudged);
        System.out.println("The value of correlation coefficient is: " + result);
    }
}