package JUnit.NewMantisBT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class DeleteMultipleUsersTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}
	@Test
	public void deleteMultipleUsers() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Manage")).click();
		driver.findElement(By.linkText("Manage Users")).click();
		driver.findElement(By.xpath("html/body/table[3]/tbody/tr[4]/td[1]/a")).click();
		driver.findElement(By.xpath("//input[@value='Delete User']")).click();
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Proceed")).click();
		driver.findElement(By.xpath("html/body/table[3]/tbody/tr[4]/td[1]/a")).click();
		driver.findElement(By.xpath("//input[@value='Delete User']")).click();
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Proceed")).click();
		driver.findElement(By.xpath("html/body/table[3]/tbody/tr[4]/td[1]/a")).click();
		driver.findElement(By.xpath("//input[@value='Delete User']")).click();
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Proceed")).click();
		assertEquals("Manage Accounts [2]",
				driver.findElement(By.xpath("html/body/table[3]/tbody/tr[1]/td[1]")).getText());
		assertFalse(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*user001[\\s\\S]*$"));
		assertFalse(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*user002[\\s\\S]*$"));
		assertFalse(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*user003[\\s\\S]*$"));
		driver.findElement(By.linkText("Logout")).click();
	}

	public void tearDown() throws Exception {
		driver.quit();

	}

}
