package JUnit.NEW_MantisBT;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class DeleteMultipleSubprojectsTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}
	@Test
	public void deleteMultipleSubprojects() throws Exception {

			driver.get("http://localhost:8080/login_page.php");
			driver.findElement(By.name("username")).clear();
			driver.findElement(By.name("username")).sendKeys("administrator");
			driver.findElement(By.name("password")).clear();
			driver.findElement(By.name("password")).sendKeys("root");
			driver.findElement(By.cssSelector("input.button")).click();
			driver.findElement(By.linkText("Manage")).click();
			driver.findElement(By.linkText("Manage Projects")).click();
			driver.findElement(By.linkText("» sub1")).click();
			driver.findElement(By.cssSelector("form > input.button")).click();
			driver.findElement(By.cssSelector("input.button")).click();
			driver.findElement(By.linkText("» sub2")).click();
			driver.findElement(By.cssSelector("form > input.button")).click();
			driver.findElement(By.cssSelector("input.button")).click();
			driver.findElement(By.linkText("Project1")).click();
			driver.findElement(By.cssSelector("form > input.button")).click();
			driver.findElement(By.cssSelector("input.button")).click();
			//assertFalse(isElementPresent(By.xpath("html/body/table[3]/tbody/tr[6]/td[1]/a")));
			//assertFalse(isElementPresent(By.xpath("html/body/table[3]/tbody/tr[7]/td[1]/a")));
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
