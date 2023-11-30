import java.io.*;
import java.util.List;
import java.lang.reflect.InvocationTargetException;

/* [DESCRIPTION]
    - Project's start point
    - JUnitRunner uses two classes:
        - TestRunner: runs all tests
        - Log: logs results on terminal and also in the specified files
**/

// TODO: Phormer Result.csv remains empty after execution
// TODO: At last row of Result.csv: update value of Point-BiserialCorrelation Coefficient (or remove it)

public class JUnitRunner {
    static String SoftwareUsed = "Phormer"; // Phormer, PasswordManager, MantisBT, JTrac

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException {
        String directory = "src/test/java/JUnit/" + SoftwareUsed; // JUnit test directory

        Judge judge = new Judge(new DefaultSelectorComplexityEvaluator(), new DefaultPageComplexityEvaluator(), new DefaultPageAndSelectorComplexityEvaluator());

        List<Test> dolibarrTests = Test.getAllTests(directory);

        TestRunner testRunner = new TestRunner(dolibarrTests, judge);
        List<Test> tests = testRunner.executeTests();

        List<Test> testsJudged = testRunner.assignScoreToEachTest(tests);

        Log log = new Log();
        log.logResult(testsJudged, testRunner);
    }
}