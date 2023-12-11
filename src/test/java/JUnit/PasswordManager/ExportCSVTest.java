package JUnit.PasswordManager; // Generated by Selenium IDE

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

public class ExportCSVTest {
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
  public void exportCSV() {
    driver.get("http://localhost:8000//");

    driver.findElement(By.id("user")).click();
    driver.findElement(By.id("user")).sendKeys("MikeFonseta");
    driver.findElement(By.id("pwd")).click();
    driver.findElement(By.id("pwd")).sendKeys("1231231");
    driver.findElement(By.id("chk")).click();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    assertThat(driver.findElement(By.linkText("Export CSV")).getText(), is("Export CSV"));
    driver.close();
  }
}