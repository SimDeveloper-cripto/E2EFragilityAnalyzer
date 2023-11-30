package JUnit.PasswordManager; // Generated by Selenium IDE

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

public class ModifyEntry1Test {
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
  public void modifyEntry1() {
    driver.get("http://localhost:8000/");
    driver.findElement(By.id("user")).click();
    driver.findElement(By.id("user")).sendKeys("MikeFonseta");
    driver.findElement(By.id("pwd")).click();
    driver.findElement(By.id("pwd")).sendKeys("1231231");
    driver.findElement(By.id("chk")).click();
    driver.findElement(By.cssSelector(".datarow:nth-child(2) .glyphicon-wrench")).click();
    driver.findElement(By.id("edititeminputcomment")).click();
    driver.findElement(By.id("edititeminputcomment")).sendKeys("Password generata (Per google)");
    driver.findElement(By.id("editbtn")).click();
    driver.switchTo().alert().accept();
    driver.findElement(By.cssSelector(".datarow:nth-child(2) .glyphicon-wrench")).click();

    {
      String value = driver.findElement(By.id("edititeminputcomment")).getAttribute("value");
      assertThat(value, is("Password generata (Per google)"));
    }
    driver.close();
  }
}