import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.List;
import java.lang.reflect.InvocationTargetException;

/* [DESCRIPTION]
    - Project's start point
    - JUnitRunner uses two classes:
        - TestRunner: runs all tests
        - Log: logs results on terminal and also in the specified files
**/

// Remember to Edit Run Configuration specifying the version of the application you want to test!

public class JUnitRunner {
    static String SoftwareUsed = "PasswordManager"; // src/test/java/JUnit/
    static String applicationVersion;

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException, CsvException {
        String directory = "src/test/java/JUnit/" + SoftwareUsed; // JUnit test directory

        JUnitRunner.applicationVersion = args[0];
        System.out.println("Application Version to test: " + applicationVersion);

        Judge judge = new Judge(new DefaultSelectorComplexityEvaluator(), new DefaultPageComplexityEvaluator(), new DefaultPageAndSelectorComplexityEvaluator());

        // Clean ErrorLog.txt file
        String filePath = "src/test/java/XMLResult/" + JUnitRunner.SoftwareUsed + "/ErrorLog.txt";
        PrintWriter writer = new PrintWriter(filePath);
        writer.close();

        List<Test> dolibarrTests = Test.getAllTests(directory);

        TestRunner testRunner = new TestRunner(dolibarrTests, judge);
        List<Test> tests = testRunner.executeTests();

        List<Test> testsJudged = testRunner.assignScoreToEachTest(tests);

        Log log = new Log();
        log.logResult(testsJudged, testRunner);
    }
}