import java.io.*;
import java.util.List;
import java.lang.reflect.InvocationTargetException;

/* [DESCRIPTION]
    - Project's start point
    - JUnitRunner uses two classes:
        - TestRunner: runs all tests
        - Log: logs results on terminal and also in the specified files
**/

// Dolibarr        [OK]
// Phormer         [OK]
// PasswordManager [OK]
// JTrac           [OK]
// MantisBT        []

/* [HOW TO RUN MANTIS_BT]
    - Before starting the test, you'll need to set the italian language.
    - This is how you do it:
        1. From your browser, go to http://localhost:8989/login_page.php and login as Administrator (Name: administrator, Pass: root)
        2. Then proceed to My Account > Preferences
        3. Logout and do the same for this other users: (Name: Chris95, Miranda23 -- Pass: root)
        3. Stop and Re-Run your docker container
**/

// What does not work in MantiBT test:
// Ogni riferimento a "Le fatture sono errate" è stato modificato
// Commented out Line 56 of aStopFilter1Test
// Commented out Line 94 of CreateFilter1Test
// zDeleteUser1Test.java Line 43, changed Umber93 with Mario (Umber93 does not exist)

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