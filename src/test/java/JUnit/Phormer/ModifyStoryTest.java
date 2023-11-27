package JUnit.Phormer; // Generated by Selenium IDE

import org.junit.Test;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

public class ModifyStoryTest {
  private WebDriver driver = new ChromeDriver();
  JavascriptExecutor js = (JavascriptExecutor) driver;

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
  public void modifyStory() {
    driver.get("http://localhost/");
    driver.findElement(By.linkText("Admin")).click();

    driver.findElement(By.name("passwd")).click();
    driver.findElement(By.name("passwd")).sendKeys("admin");
    driver.findElement(By.cssSelector(".submit")).click();

    driver.findElement(By.linkText("Manage Stories")).click();

    WebElement nameField = driver.findElement(By.id("name"));
    nameField.click();
    nameField.clear();
    nameField.sendKeys("Story Edit");
    // driver.findElement(By.id("name")).click();
    // driver.findElement(By.id("name")).sendKeys("Story Edit");

    WebElement descField = driver.findElement(By.name("desc"));
    descField.clear();
    descField.sendKeys("Description Edit");
    // driver.findElement(By.name("desc")).sendKeys("Description Edit");

    driver.findElement(By.cssSelector("tr:nth-child(4) .radio:nth-child(4)")).click();

    driver.findElement(By.name("list")).click();
    driver.findElement(By.id("public")).click();

    driver.findElement(By.cssSelector(".submit")).click();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
    WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".note_valid")));
    assertThat(successMessage.getText(), is("Story \"Story Edit\" added succesfully!"));
  }
}