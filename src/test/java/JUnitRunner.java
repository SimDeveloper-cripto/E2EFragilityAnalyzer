import java.io.*;
import java.util.List;
import java.lang.reflect.InvocationTargetException;

/* [DESCRIPTION]
    - Project's start point
    - JUnitRunner uses two classes:
        - TestRunner: runs all tests
        - Log: logs results on terminal and also in the specified files
* */
public class JUnitRunner {
    static String SoftwareUsed = "Dolibarr"; // Change this as you like

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException {
        String directory = "src/test/java/JUnit/" + SoftwareUsed; // JUnit test directory

        Judge judge = new Judge(new DefaultSelectorComplexityEvaluator(), new DefaultPageComplexityEvaluator(), new DefaultPageAndSelectorEvaluator());

        // Step 1: Retrieve all tests and run them
        List<Test> dolibarrTests = Test.getAllTests(directory);

        TestRunner testRunner = new TestRunner(dolibarrTests, judge);
        List<Test> tests = testRunner.executeTests();

        // Step 3: For each test assign its score
        List<Test> testsJudged = testRunner.assignScoreToEachTest(tests);

        // Step 4: Calculate the correlation coefficient
        double result = PointBiserialCorrelationCoefficient.getCorrelation(testsJudged);
        System.out.println("The value of correlation coefficient is: " + result);

        // Step 5: Show results
        Log log = new Log();
        log.logResult(testsJudged, testRunner);
    }
}