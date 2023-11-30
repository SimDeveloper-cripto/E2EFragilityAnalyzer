package JUnit.JTrac; // Generated by Selenium IDE

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

public class SettingsTest {
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
  public void settings() {
    driver.get("http://127.0.0.1:8888/app");
    driver.findElement(By.linkText("OPZIONI")).click();
    driver.findElement(By.linkText("Gestione Impostazioni")).click();
    assertThat(driver.findElement(By.cssSelector(".heading")).getText(), is("Impostazioni di Configurazione"));
    driver.close();
  }
}