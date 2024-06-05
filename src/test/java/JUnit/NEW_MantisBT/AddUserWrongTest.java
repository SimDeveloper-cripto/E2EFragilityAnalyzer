package JUnit.NEW_MantisBT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;


public class AddUserWrongTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void addUserWrong() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Manage")).click();
		driver.findElement(By.linkText("Manage Users")).click();
		driver.findElement(By.xpath("//input[@value='Create New Account']")).click();
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("username001");
		driver.findElement(By.name("realname")).clear();
		driver.findElement(By.name("realname")).sendKeys("username001");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("username001@username,it");
		new Select(driver.findElement(By.name("access_level"))).selectByVisibleText("updater");
		driver.findElement(By.cssSelector("input.button")).click();
		assertEquals("That username is already being used. Please go back and select another one.",
				driver.findElement(By.xpath("html/body/div[2]/table/tbody/tr[2]/td/p")).getText());
		driver.findElement(By.linkText("Logout")).click();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
