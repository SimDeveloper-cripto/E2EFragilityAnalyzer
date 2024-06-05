package JUnit.OLD_MantisBT; // Generated by Selenium IDE

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.time.format.DateTimeFormatter;

public class CreateProject1Test {
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
  public void createProject1() throws InterruptedException {
    driver.get("http://localhost:8989/login_page.php");
    driver.manage().window().setSize(new Dimension(1936, 1056));

    driver.findElement(By.id("username")).sendKeys("administrator");
    driver.findElement(By.cssSelector(".width-40")).click();
    driver.findElement(By.id("password")).sendKeys("root");

    driver.findElement(By.cssSelector(".clearfix:nth-child(5) .lbl")).click();
    driver.findElement(By.cssSelector(".clearfix:nth-child(4) .lbl")).click();
    driver.findElement(By.cssSelector(".width-40")).click();
    driver.findElement(By.cssSelector(".fa-gears")).click();

    driver.findElement(By.linkText("Gestione progetti")).click();
    driver.findElement(By.cssSelector(".widget-toolbox > .form-inline .btn")).click();
    driver.findElement(By.id("project-name")).click();

    LocalDateTime currentDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
    String currentDateAndTime = currentDateTime.format(formatter);

    String projectName = "Prova" + currentDateAndTime; // Old value was "Prova1"
    driver.findElement(By.id("project-name")).sendKeys(projectName);
    driver.findElement(By.id("project-view-state")).click();

    {
      WebElement dropdown = driver.findElement(By.id("project-view-state"));
      dropdown.findElement(By.xpath("//option[. = 'privato']")).click();
    }

    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-description")).sendKeys("Prova");
    driver.findElement(By.cssSelector(".btn-white")).click();

    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    driver.findElement(By.linkText("Continua")).click();
    driver.findElement(By.linkText(projectName)).click();
    driver.findElement(By.name("version")).click();
    driver.findElement(By.name("version")).sendKeys("1.0");
    driver.findElement(By.name("add_version")).click();

    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-view-state")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-name")).click();
    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-view-state")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-name")).click();
    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-view-state")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-name")).click();
    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-view-state")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-name")).click();
    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-view-state")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-name")).click();
    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-view-state")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-name")).click();
    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-view-state")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-name")).click();
    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-view-state")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-name")).click();
    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-view-state")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-name")).click();
    driver.findElement(By.id("project-description")).click();
    driver.findElement(By.id("project-view-state")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-status")).click();
    driver.findElement(By.id("project-name")).click();

    driver.close();
  }
}