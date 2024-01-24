package JUnit.NewMantisBT;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;


public class AddUserTest {

    private  WebDriver driver = new ChromeDriver();
    JavascriptExecutor js = (JavascriptExecutor) driver;

    public void setUp(WebDriver driver) {
        this.driver.quit();
        this.driver=driver;
        js = (JavascriptExecutor) driver;
    }

    @Test
    public void addUser() {
        driver.get("http://localhost:8080/login_page.php");
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys("administrator");
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("root");
        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.linkText("Manage")).click();
        driver.findElement(By.linkText("Manage Users")).click();
        driver.findElement(By.cssSelector("td.form-title > form > input.button-small")).click();
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys("username001");
        driver.findElement(By.name("realname")).clear();
        driver.findElement(By.name("realname")).sendKeys("username");
        driver.findElement(By.name("email")).clear();
        driver.findElement(By.name("email")).sendKeys("username@username.it");
        driver.findElement(By.name("realname")).clear();
        driver.findElement(By.name("realname")).sendKeys("username001");
        driver.findElement(By.name("email")).clear();
        driver.findElement(By.name("email")).sendKeys("username001@username.it");
        new Select(driver.findElement(By.name("access_level"))).selectByVisibleText("updater");
        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.linkText("Proceed")).click();
        driver.findElement(By.linkText("Manage Users")).click();
        assertEquals("username001", driver.findElement(By.xpath("html/body/table[3]/tbody/tr[7]/td[1]/a")).getText());
        assertEquals("username001", driver.findElement(By.xpath("html/body/table[3]/tbody/tr[7]/td[2]")).getText());
        assertEquals("username001@username.it", driver.findElement(By.xpath("html/body/table[3]/tbody/tr[7]/td[3]")).getText());
        assertEquals("updater", driver.findElement(By.xpath("html/body/table[3]/tbody/tr[7]/td[4]")).getText());
        driver.findElement(By.linkText("Logout")).click();
    }

    public void tearDown() {
        driver.quit();
    }
}
