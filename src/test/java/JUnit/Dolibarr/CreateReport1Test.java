package JUnit.Dolibarr;// Generated by Selenium IDE
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import java.util.*;

public class CreateReport1Test {
  private  WebDriver driver=new ChromeDriver();
  private Map<String, Object> vars=new HashMap<String, Object>();
  JavascriptExecutor js= (JavascriptExecutor) driver;

  public void setUp(WebDriver driver) {
    this.driver.quit();
    this.driver=driver;
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();
  }
  @After
  public void tearDown() {
    driver.quit();
  }
  @Test
  public void createReport1() {
    driver.get("http://localhost:8080/");
    driver.manage().window().setSize(new Dimension(945, 1020));
    driver.findElement(By.id("username")).sendKeys("admin");
    driver.findElement(By.id("password")).sendKeys("dolibarr");
    driver.findElement(By.id("password")).sendKeys(Keys.ENTER);
    driver.findElement(By.cssSelector(".billing")).click();
    driver.findElement(By.cssSelector(".menu")).click();
    //Assert testuale fallita
    assertThat(driver.findElement(By.linkText("Reportistiche")).getText(), is("Reportistiche"));
    driver.findElement(By.linkText("Reportistiche")).click();
    driver.findElement(By.cssSelector(".button")).click();
    driver.findElement(By.xpath("//div[@id=\'id-right\']/div/table[2]/tbody/tr/td")).click();
    driver.findElement(By.xpath("//div[@id=\'id-right\']/div/table/tbody/tr/td[2]/div")).click();
    driver.findElement(By.xpath("//div[@id=\'id-right\']/div/table[2]/tbody/tr/td[3]")).click();
    driver.findElement(By.xpath("//div[@id=\'id-right\']/div/table[2]/tbody/tr/td")).click();
    driver.findElement(By.xpath("//div[@id=\'id-right\']/div/table/tbody/tr/td[2]/div")).click();
    driver.findElement(By.xpath("//div[@id=\'id-right\']/div/table[2]/tbody/tr/td[3]")).click();

  }
}
