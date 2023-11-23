import java.io.*;
import java.util.List;
import java.lang.reflect.InvocationTargetException;

/* [DESCRIPTION]
    - Project's start point
    - JUnitRunner uses two classes:
        - TestRunner: runs all tests
        - Log: logs results on terminal and also in the specified files
* */

// TODO: ADD IN XMLResult PACKAGE CALLED SCORES AND Phormer3.0.1.xml

public class JUnitRunner {
    static String SoftwareUsed = "MantisBT"; // Change this as you like (Dolibarr, MantisBT, MikeFonsetaPhormer)

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