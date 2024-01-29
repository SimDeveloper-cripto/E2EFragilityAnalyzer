package JUnit.NewMantisBT;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class UnlinkMultipleSubprojectsTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void unlinkMultipleSubprojects() throws Exception {
		driver.get("http://localhost:8080/login_page.php");

		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");

		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Manage")).click();
		driver.findElement(By.linkText("Manage Projects")).click();
		driver.findElement(By.linkText("Project002")).click();

		driver.findElement(By.xpath("html/body/div[5]/table/tbody/tr[3]/td[7]/span[2]/a")).click();
		driver.findElement(By.linkText("Proceed")).click();
		driver.findElement(By.xpath("html/body/div[5]/table/tbody/tr[3]/td[7]/span[2]/a")).click();
		driver.findElement(By.linkText("Proceed")).click();
		driver.navigate().refresh();

		assertFalse(isElementPresent(By.xpath("html/body/div[6]/table/tbody/tr[3]/td[1]/a")));
		assertFalse(isElementPresent(By.xpath("html/body/div[6]/table/tbody/tr[4]/td[1]/a")));
		driver.findElement(By.linkText("Manage Projects")).click();

		assertEquals("sub12", driver.findElement(By.xpath("html/body/table[3]/tbody/tr[4]/td[1]/a")).getText());
		assertEquals("sub22", driver.findElement(By.xpath("html/body/table[3]/tbody/tr[5]/td[1]/a")).getText());

		driver.findElement(By.linkText("Logout")).click();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
}