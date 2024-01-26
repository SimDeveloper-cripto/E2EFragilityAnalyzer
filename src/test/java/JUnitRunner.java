
import java.io.*;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

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

public class JUnitRunner {
    static String applicationVersion, SoftwareUsed;

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException, CsvException {
        JsonObject configuration = readConfigurationFile();

        JUnitRunner.SoftwareUsed = configuration.get("softwareUsed").getAsString();
        String directory = "src/test/java/JUnit/" + SoftwareUsed;

        JUnitRunner.applicationVersion = configuration.get("applicationVersion").getAsString();
        System.out.println("Testing Application Version: " + applicationVersion);

        String selectorScoreStrategy        = configuration.get("selectorScoreStrategy").getAsString();
        String pageScoreStrategy            = configuration.get("pageScoreStrategy").getAsString();
        String pageAndSelectorScoreStrategy = configuration.get("pageAndSelectorScoreStrategy").getAsString();

        Object selectorScoreStrategyImpl = null, pageScoreStrategyImpl = null, pageAndSelectorScoreStrategyImpl = null;
        try {
            selectorScoreStrategyImpl        = instantiateScoreStrategy(selectorScoreStrategy);
            pageScoreStrategyImpl            = instantiateScoreStrategy(pageScoreStrategy);
            pageAndSelectorScoreStrategyImpl = instantiateScoreStrategy(pageAndSelectorScoreStrategy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Judge judge = new Judge((ISelectorScoreStrategy) selectorScoreStrategyImpl,
                (IPageScoreStrategy) pageScoreStrategyImpl, (IPageAndSelectorScoreStrategy) pageAndSelectorScoreStrategyImpl);

        // Clear ErrorLog.txt file
        String filePath = "src/test/java/XMLResult/" + JUnitRunner.SoftwareUsed + "/ErrorLog.txt";
        PrintWriter writer = new PrintWriter(filePath);
        writer.close();

        List<Test> testsToExecute = Test.getAllTests(directory);
        TestRunner testRunner     = new TestRunner(testsToExecute, judge);

        List<Test> tests       = testRunner.executeTests();
        List<Test> testsJudged = testRunner.assignScoreToEachTest(tests);

        Log log = new Log();
        log.logResult(testsJudged, testRunner);
    }

    private static JsonObject readConfigurationFile() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("src/test/java/config/configuration.json")));
        return new JsonParser().parse(content).getAsJsonObject();
    }

    private static Object instantiateScoreStrategy(String className) throws Exception {
        Class<?> c = Class.forName(className);
        return c.getDeclaredConstructor().newInstance();
    }
}