package JUnit.NEW_MantisBT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


// it was SummaryIssueTest
public class ViewSummaryIssueTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void viewSummaryIssue() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Summary")).click();
		assertEquals("1", driver
				.findElement(By.xpath("/html/body/table[3]/tbody/tr[2]/td[1]/table[1]/tbody/tr[2]/td[5]")).getText());
		driver.findElement(By.linkText("Logout")).click();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
