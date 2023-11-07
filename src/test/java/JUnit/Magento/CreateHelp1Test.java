package JUnit.Magento;// Generated by Selenium IDE
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.JavascriptExecutor;

import java.util.*;

public class CreateHelp1Test {
  private WebDriver driver=new ChromeDriver();
  private Map<String, Object> vars=new HashMap<String, Object>();
  JavascriptExecutor js= (JavascriptExecutor) driver;



  public void setUp(WebDriver driver) {
    this.driver.quit();
    this.driver = driver;
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();
  }
  @After
  public void tearDown() {
    driver.quit();
  }
  @Test
  public void createHelp1() throws InterruptedException {
    driver.get("http://localhost/");
    driver.manage().window().setSize(new Dimension(910, 1020));
    driver.findElement(By.linkText("Contact Us")).click();
    {
      WebElement element = driver.findElement(By.cssSelector(".submit > span"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    driver.findElement(By.id("name")).click();
    driver.findElement(By.id("name")).click();
    driver.findElement(By.id("name")).sendKeys("Mario");
    driver.findElement(By.id("email")).click();
    driver.findElement(By.id("email")).sendKeys("prova@prova.com");
    driver.findElement(By.id("comment")).click();
    driver.findElement(By.id("comment")).click();
    Thread.sleep(1000);
    driver.findElement(By.id("comment")).click();
    driver.findElement(By.id("comment")).sendKeys("Ho un problema");
    driver.findElement(By.cssSelector(".submit > span")).click();
    {
      WebElement element = driver.findElement(By.cssSelector(".submit > span"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    {
      WebElement element = driver.findElement(By.cssSelector(".submit"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    driver.findElement(By.id("comment")).click();
    driver.findElement(By.id("telephone")).click();
  }
}
