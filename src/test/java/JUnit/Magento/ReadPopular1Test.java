package JUnit.Magento;// Generated by Selenium IDE
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.*;

public class ReadPopular1Test {
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
  public void readPopular1() throws InterruptedException {
    driver.get("http://localhost/");
    driver.manage().window().setSize(new Dimension(910, 1020));
    Thread.sleep(1000);
    driver.findElement(By.cssSelector(".nav:nth-child(1) > a")).click();
    Thread.sleep(1000);
    //Assert testuale fallita
    assertThat(driver.findElement(By.cssSelector(".base")).getText(), is("Popular Search Terms"));
    driver.findElement(By.xpath("//form[@id=\'newsletter-validate-detail\']/div[2]/button/span")).click();
    driver.findElement(By.xpath("//form[@id=\'newsletter-validate-detail\']/div/div")).click();
    driver.findElement(By.xpath("//form[@id=\'newsletter-validate-detail\']/div[2]/button/span")).click();
    {
      WebElement element = driver.findElement(By.xpath("//form[@id=\'newsletter-validate-detail\']/div[2]/button/span"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
  }
}
