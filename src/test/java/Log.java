
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/* [DESCRIPTION]
    - Log class is used to show the user Test results on terminal and also in the specified files
**/

public class Log {
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
                String index = "Type, Selector, SelectorScore, PageScore, PageAndSelectorScore, SelectorWeightedAverageResult \n\n";

                fileWriter.write(index);
                for (int i = 0; i < test.getSelectors().size(); i++) {
                    Selector selector = test.getSelectors().get(i);
                    Page page = test.getPages().get(i);

                    String nameSelector = selector.getSelector();
                    String typeSelector = selector.getType();
                    float selectorScore = selector.getSelectorScore();
                    float pageScore     = page.getPageComplexity();
                    float pageAndSelectorScore = selector.getSelectorCombinedScoreWithPageScore();
                    double selectorFinalScore  = selector.getSelectorWeightedAverageResultScore();

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

    public void logResult(List<Test> testsJudged, TestRunner testRunner) throws IOException, CsvException {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.000", symbols);

        System.out.println("============ Results =============");
        System.out.println("== Successes: " + testRunner.getNumberOfSuccessTests() + "\n== Failures: " + testRunner.getNumberOfFailedTests());
        System.out.println("==================================");

        logResultForEachTest(testsJudged, df);

        String directory = "src/test/java/XMLResult/" + JUnitRunner.SoftwareUsed + "/Result.csv";
        createScoreFileForEachTest(testsJudged, df);

        WriterCSV writerCSV = new WriterCSV(testsJudged, directory, df, JUnitRunner.applicationVersion);
        writerCSV.writeResultsToCSV();
    }

    public void logResultForEachTest(List<Test> testsJudged, DecimalFormat df) {
        System.out.println(" ");
        for (Test testJudged : testsJudged) {
            System.out.println("Test name: " + testJudged.getClassName());
            for (int i = 0; i < testJudged.getSelectors().size(); i++) {
                Selector selector = testJudged.getSelectors().get(i);
                Page page = testJudged.getPages().get(i);

                System.out.println("\tSelector: " + selector.getSelector());
                System.out.println("\t\tSelectorScore: " + df.format(selector.getSelectorScore()) + ", PageScore: " + df.format(page.getPageComplexity())
                        + ", PageAndSelectorScore: " + df.format(selector.getSelectorCombinedScoreWithPageScore()) + ", SelectorWeightedAverageResult: " + df.format(selector.getSelectorWeightedAverageResultScore()));
            }
            System.out.println("Test Score (by Harmonic Mean): " + testJudged.getTestScore());
        }
        System.out.println(" ");
        // double result = PointBiserialCorrelationCoefficient.getCorrelation(testsJudged);
        // System.out.println("The value of correlation coefficient is: " + result);
    }

    public static void fillErrorLog(String errorMessage, String filePath) {
        try {
            PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
            w.println(errorMessage);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}