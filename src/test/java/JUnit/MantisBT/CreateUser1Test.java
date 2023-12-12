package JUnit.MantisBT; // Generated by Selenium IDE

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;

public class CreateUser1Test {
  private  WebDriver driver = new ChromeDriver();
  JavascriptExecutor js = (JavascriptExecutor) driver;

  public void setUp(WebDriver driver) {
    this.driver.quit();
    this.driver=driver;
    js = (JavascriptExecutor) driver;
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  @Test
  public void createUser1() throws InterruptedException {
    driver.get("http://localhost:8989/login_page.php");
    driver.manage().window().setSize(new Dimension(766, 640));

    driver.findElement(By.name("username")).sendKeys("administrator");
    driver.findElement(By.cssSelector(".width-40")).click();
    driver.findElement(By.name("password")).sendKeys("root");
    driver.findElement(By.cssSelector(".width-40")).click();

    Thread.sleep(1000);
    driver.findElement(By.id("menu-toggler")).click();

    Thread.sleep(1000);
    driver.findElement(By.cssSelector("li:nth-child(7) .menu-text")).click();
    driver.findElement(By.linkText("Gestione utenti")).click();

    driver.findElement(By.xpath("//button[@type=\'submit\']")).click();
    driver.findElement(By.name("username")).click();
    driver.findElement(By.name("username")).sendKeys("asda");
    driver.findElement(By.name("realname")).click();
    driver.findElement(By.name("realname")).sendKeys("adsa");
    driver.findElement(By.name("email")).click();
    driver.findElement(By.name("email")).sendKeys("asdasd");
    driver.findElement(By.id("user-access-level")).click();
    {
      WebElement dropdown = driver.findElement(By.id("user-access-level"));
      dropdown.findElement(By.xpath("//option[. = 'aggiornatore']")).click();
    }
    driver.findElement(By.cssSelector("tr:nth-child(6) .lbl")).click();
    driver.findElement(By.cssSelector(".btn-white")).click();
    driver.close();
  }
}