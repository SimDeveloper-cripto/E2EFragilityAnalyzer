package JUnit.NEW_MantisBT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;


public class AddIssueTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}
	@Test
	public void addIssue() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Report Issue")).click();
		new Select(driver.findElement(By.name("category_id"))).selectByVisibleText("Category001");
		new Select(driver.findElement(By.name("reproducibility"))).selectByVisibleText("random");
		new Select(driver.findElement(By.name("severity"))).selectByVisibleText("crash");
		new Select(driver.findElement(By.name("priority"))).selectByVisibleText("immediate");
		driver.findElement(By.name("summary")).clear();
		driver.findElement(By.name("summary")).sendKeys("Summary001");
		driver.findElement(By.name("description")).clear();
		driver.findElement(By.name("description")).sendKeys("description001");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.xpath("(//a[contains(text(),'View Issues')])[2]")).click();
		assertEquals("Category001", driver.findElement(By.xpath(".//*[@id='buglist']/tbody/tr[4]/td[6]")).getText());
		assertEquals("crash", driver.findElement(By.xpath(".//*[@id='buglist']/tbody/tr[4]/td[7]")).getText());
		assertEquals("Summary001", driver.findElement(By.xpath(".//*[@id='buglist']/tbody/tr[4]/td[10]")).getText());
		driver.findElement(By.linkText("Logout")).click();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
