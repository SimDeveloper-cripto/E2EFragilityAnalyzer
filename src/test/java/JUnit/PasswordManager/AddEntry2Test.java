package JUnit.PasswordManager; // Generated by Selenium IDE

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;


public class AddEntry2Test {
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
  public void addEntry2() {
    driver.get("http://localhost:8000/");

    driver.findElement(By.id("user")).click();
    driver.findElement(By.id("user")).sendKeys("MikeFonseta");
    driver.findElement(By.id("pwd")).click();
    driver.findElement(By.id("pwd")).sendKeys("1231231");
    driver.findElement(By.id("chk")).click();

    driver.findElement(By.linkText("Add Entry")).click();
    driver.findElement(By.id("newiteminput")).click();
    driver.findElement(By.id("newiteminput")).sendKeys("Youtube");
    driver.findElement(By.id("newiteminputuser")).click();
    driver.findElement(By.id("newiteminputuser")).sendKeys("m.fonseta@gmail.com");
    driver.findElement(By.id("newiteminputurl")).click();
    driver.findElement(By.id("newiteminputurl")).sendKeys("https.//www.youtube.it");
    driver.findElement(By.id("newiteminputcomment")).click();
    driver.findElement(By.id("newiteminputcomment")).sendKeys("Password generata");
    driver.findElement(By.id("newbtn")).click();
    driver.switchTo().alert().accept();
    assertThat(driver.findElement(By.cssSelector(".datarow:nth-child(3) .accountname")).getText(), is("Youtube"));
    driver.close();
  }
}