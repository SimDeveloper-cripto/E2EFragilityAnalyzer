package JUnit.JTrac; // Generated by Selenium IDE

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import java.util.*;

public class LoginUser1Test {
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
  public void loginUser1() {
    driver.get("http://127.0.0.1:8888/app");
    driver.findElement(By.linkText("LOGIN")).click();
    driver.findElement(By.name("loginName")).sendKeys("13Mike");
    driver.findElement(By.name("password")).click();
    driver.findElement(By.name("password")).sendKeys("987654321");
    driver.findElement(By.cssSelector("td:nth-child(3) > input")).click();
    driver.findElement(By.cssSelector("td > span")).click();

    {
      List<WebElement> elements = driver.findElements(By.name("loginName"));
      assert(!elements.isEmpty());
    }

    driver.close();
  }
}