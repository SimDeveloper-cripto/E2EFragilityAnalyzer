package JUnit.NewMantisBT;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;


public class UpdateIssuePriorityTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void updateIssuePriority() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");
		driver.findElement(By.cssSelector("input.button")).click();

		//create issue
		driver.findElement(By.linkText("Report Issue")).click();
		driver.findElement(By.cssSelector("input.button")).click();
		new Select(driver.findElement(By.name("category_id"))).selectByVisibleText("Category002Mod");
		new Select(driver.findElement(By.name("reproducibility"))).selectByVisibleText("random");
		new Select(driver.findElement(By.name("severity"))).selectByVisibleText("crash");
		new Select(driver.findElement(By.name("priority"))).selectByVisibleText("immediate");
		driver.findElement(By.name("summary")).clear();
		driver.findElement(By.name("summary")).sendKeys("Summary001");
		driver.findElement(By.name("description")).clear();
		driver.findElement(By.name("description")).sendKeys("description001");
		driver.findElement(By.cssSelector("input.button")).click();
		//driver.findElement(By.xpath("(//a[contains(text(),'View Issues')])[2]")).click();

		driver.findElement(By.linkText("View Issues")).click();
		driver.findElement(By.cssSelector("img[alt=\"Edit\"]")).click();
		new Select(driver.findElement(By.name("priority"))).selectByVisibleText("low");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("View Issues")).click();
		assertEquals("low",
				driver.findElement(By.xpath(".//*[@id='buglist']/tbody/tr[4]/td[3]/img")).getAttribute("title"));
		driver.findElement(By.linkText("Logout")).click();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
