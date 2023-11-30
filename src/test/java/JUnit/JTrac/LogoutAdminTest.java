package JUnit.JTrac; // Generated by Selenium IDE

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import java.util.*;

public class LogoutAdminTest {
  private WebDriver driver = new ChromeDriver();
  JavascriptExecutor js;

  public void setUp(WebDriver driver) {
    this.driver.quit();
    this.driver = driver;
    js = (JavascriptExecutor) driver;
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  @Test
  public void logoutAdmin() {
    driver.get("http://127.0.0.1:8888/app");
    driver.findElement(By.linkText("LOGOUT")).click();
    driver.findElement(By.linkText("Login")).click();
    {
      List<WebElement> elements = driver.findElements(By.name("loginName"));
      assert(!elements.isEmpty());
    }
    driver.close();
  }
}