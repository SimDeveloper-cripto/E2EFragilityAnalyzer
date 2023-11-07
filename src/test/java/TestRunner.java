
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.support.events.EventFiringDecorator;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TestRunner {
    List<Test> testsToValidate;
    WebDriverListener listenerDriver;
    SelectorWebDriver listener;
    WebDriver driver;
    int numberOfSuccessTests = 0;
    int numberOfFailedTests = 0;

    public TestRunner(List<Test> tests) { // Must be initialized with a list of tests
        this.testsToValidate = tests;
    }

    public List<Test> executeTests() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        List<Test> testsFinished = new ArrayList<>();

        for (int testNum = 1; testNum <= testsToValidate.size(); testNum++) {
            setupListner();
            testsFinished.add(executeTest(testNum));
        }
        return testsFinished;
    }

    private Test executeTest(int testNum) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Test test;
        Class testX;
        Object objPetX;
        Method method1, method2, method3;
        List<Selector> selectorFinished;
        List<Page> pageFinished;
        String nomeTest, nomeMetodo;
        boolean testFailed = false;

        Class[] arg = new Class[1];
        arg[0] = WebDriver.class;

        System.out.println("\nTest no." + testNum + " started!\n");

        // Get the test so it can be executed
        test = testsToValidate.get(testNum - 1);
        nomeTest = getClassName(test);
        nomeMetodo = getMethodName(nomeTest);

        // Execute it
        testX = Class.forName(nomeTest);
        objPetX = create(testX);

        method1 = testX.getDeclaredMethod("setUp", WebDriver.class);
        method1.invoke(objPetX, driver);
        method2 = testX.getDeclaredMethod(nomeMetodo);

        try {
            method2.invoke(objPetX);
            System.out.println("< Test: " + nomeTest + " >" + ", < Invoked method: " + nomeMetodo + " >\n");
        } catch(Exception e) {
            e.printStackTrace();
            Selector lastSelector = listener.getSelectorPages().get(listener.getSelectorPages().size() - 1);
            lastSelector.setSelectorScore(Judge.getBadElementScore(lastSelector));
            System.out.println("A Test stopped working, Judge will assign to it -100 points!");
            numberOfFailedTests += 1;
            testFailed = true;
        }

        if(!testFailed) numberOfSuccessTests += 1;

        method3 = testX.getDeclaredMethod("tearDown");
        method3.invoke(objPetX);

        System.out.println("\nTest no." + testNum + " ended!\n");
        selectorFinished = listener.getSelectorPages();
        test.setSelectors(selectorFinished);
        pageFinished = listener.getDocumentPages();
        test.setPage(pageFinished);
        return test;
    }

    private void setupListner() {
        ChromeDriverService service = new ChromeDriverService.Builder().withLogOutput(System.err).build();

        driver = new ChromeDriver(service);
        listener = new SelectorWebDriver();
        listenerDriver = listener;
        driver = new EventFiringDecorator(listenerDriver).decorate(driver);

        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
        listener.setSelectorPages(new ArrayList<>());
        listener.setDocumentPages(new ArrayList<>());
    }

    public static <T> T create(Class<T> someClass) {
        try {
            return someClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            throw new RuntimeException("Failed to create instance of class " + someClass.getName(), ex);
        }
    }

    private String getClassName(Test test) {
        String nomeTest;
        nomeTest = test.getClassName();
        nomeTest = nomeTest.replace(".java","");
        nomeTest = nomeTest.replace("src/test/java/","");
        nomeTest = nomeTest.replace("/",".");
        return nomeTest;
    }

    private String getMethodName(String nomeTest) {
        String nomeMetodo;
        nomeMetodo = nomeTest.replace("Test","");
        nomeMetodo = (nomeMetodo.substring(nomeMetodo.lastIndexOf(".") + 1).trim());
        nomeMetodo = decapitalize(nomeMetodo);
        return nomeMetodo;
    }

    public static String decapitalize(String string) {
        if (string == null || string.isEmpty()) return string;

        char c[] = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        c[1] = Character.toLowerCase(c[1]);
        return new String(c);
    }
}