package JUnit.NEW_MantisBT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class UpdateIssueSummaryTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void updateIssueSummary() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("View Issues")).click();
		driver.findElement(By.cssSelector("img[alt=\"Edit\"]")).click();
		driver.findElement(By.name("summary")).clear();
		driver.findElement(By.name("summary")).sendKeys("Summary002");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("View Issues")).click();
		assertEquals("Summary002", driver.findElement(By.xpath(".//*[@id='buglist']/tbody/tr[4]/td[10]")).getText());
		driver.findElement(By.linkText("Logout")).click();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
