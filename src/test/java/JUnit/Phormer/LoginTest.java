package JUnit.Phormer;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginTest {
  private WebDriver driver = new ChromeDriver();
  JavascriptExecutor js = (JavascriptExecutor) driver;

  public void setUp(WebDriver driver) {
    this.driver.quit();
    this.driver = driver;
    js = (JavascriptExecutor) driver;
  }

  public void tearDown() {
    driver.quit();
  }

  @Test
  public void login() {
    driver.get("http://localhost:80");
    driver.findElement(By.linkText("Admin")).click();
    driver.findElement(By.name("passwd")).click();
    driver.findElement(By.name("passwd")).sendKeys("passwd");
    driver.findElement(By.cssSelector(".submit")).click();
    driver.findElement(By.linkText("Logout")).click();
  }
}