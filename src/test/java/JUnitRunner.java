import java.io.*;
import java.util.List;
import java.lang.reflect.InvocationTargetException;

/* [DESCRIPTION]
    - Project's start point
    - JUnitRunner uses two classes:
        - TestRunner: runs all tests
        - Log: logs results on terminal and also in the specified files
**/

// Dolibarr        [OK] (All versions provided are tested)
// Phormer         [OK] (All versions provided are tested)
// PasswordManager [OK] (All versions provided are tested)
// JTrac           [OK] (All versions provided are tested)
// MantisBT        [OK] (All versions provided are tested)

/* [HOW TO RUN MANTIS_BT]
    - Before starting the test, you'll need to set the italian language.
    - This is how you do it:
        1. From your browser, go to http://localhost:8989/login_page.php and login as Administrator (Name: Administrator, Pass: root)
        2. Then proceed to My Account > Preferences
        3. Logout and do the same for this other users: (Name: Chris95, Miranda23 -- Pass: root)
        4. Login with user (Name: Ivan52, Pass: root) > Report Issue for EasyManager2 (fill category dropdown menu, Summary: "Glitch grafico", Description: "Glitch grafico che prima non si verificava")
        5. Stop and Re-Run your docker container
**/

public class JUnitRunner {
    static String SoftwareUsed = "MantisBT";

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException {
        String directory = "src/test/java/JUnit/" + SoftwareUsed; // JUnit test directory

        Judge judge = new Judge(new DefaultSelectorComplexityEvaluator(), new DefaultPageComplexityEvaluator(), new DefaultPageAndSelectorComplexityEvaluator());

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