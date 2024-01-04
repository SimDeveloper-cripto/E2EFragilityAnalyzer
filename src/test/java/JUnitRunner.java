
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.exceptions.CsvException;
import java.lang.reflect.InvocationTargetException;

/* [DESCRIPTION]
    - Project's start point
    - JUnitRunner uses two classes:
        - TestRunner: runs all tests
        - Log: logs results on terminal and also in the specified files
**/

// Remember to Edit Run Configuration specifying the version of the application you want to test!

// TODO: Update Readme.md file
// TODO: Find a way to define the implementations for each Evaluator (json file)

public class JUnitRunner {
    static String applicationVersion, SoftwareUsed;

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException, CsvException {
        JsonObject configuration = readConfigurationFile();

        JUnitRunner.SoftwareUsed = configuration.get("softwareUsed").getAsString();
        String directory = "src/test/java/JUnit/" + SoftwareUsed;

        JUnitRunner.applicationVersion = configuration.get("applicationVersion").getAsString();
        System.out.println("Testing Application Version: " + applicationVersion);

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

    private static JsonObject readConfigurationFile() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("src/test/java/config/configuration.json")));
        return new JsonParser().parse(content).getAsJsonObject();
    }
}