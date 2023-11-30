package JUnit.JTrac; // Generated by Selenium IDE

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

public class BlackboardTest {
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
  public void blackboard() {
    driver.get("http://127.0.0.1:8888/app");
    driver.findElement(By.linkText("LAVAGNA")).click();
    assertThat(driver.findElement(By.cssSelector(".selected > span")).getText(), is("My space"));
    driver.close();
  }
}