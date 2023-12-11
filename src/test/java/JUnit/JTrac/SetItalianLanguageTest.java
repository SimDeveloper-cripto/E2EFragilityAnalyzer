package JUnit.JTrac;

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class SetItalianLanguageTest {
    private WebDriver driver = new ChromeDriver();
    JavascriptExecutor js;

    public void setUp(WebDriver driver) {
        this.driver.quit();
        this.driver = driver;
        js = (JavascriptExecutor) this.driver;
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void setItalianLanguage() {
        driver.get("http://127.0.0.1:8888/app");

        // Login as Admin User
        driver.findElement(By.name("loginName")).sendKeys("admin");
        driver.findElement(By.name("password")).click();
        driver.findElement(By.name("password")).sendKeys("admin");
        driver.findElement(By.cssSelector("td:nth-child(3) > input")).click();

        // Set Italian Language
        driver.findElement(By.xpath("//html/body/div[1]/table[2]/tbody/tr/td[1]/a")).click();
        driver.findElement(By.xpath("//html/body/table[2]/tbody/tr/td/div[5]")).click();

        WebElement table = driver.findElement(By.cssSelector("body > table:nth-child(7)"));
        java.util.List<WebElement> trs = table.findElements(By.tagName("tr"));

        for(WebElement tr : trs) {
            if (tr.getText().contains("locale.default")) {
                java.util.List<WebElement> tds = tr.findElements(By.tagName("td"));
                tds.get(2).click();
                break;
            }
        }

        driver.findElement(By.name("field:value")).clear();
        driver.findElement(By.name("field:value")).sendKeys("it");
        driver.findElement(By.xpath("//input[@value=\"Submit\"]")).click();
        driver.close();
    }
}