package JUnit.NEW_MantisBT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class LoginNegativeTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void loginNegative() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("");
		driver.findElement(By.cssSelector("input.button")).click();
		assertEquals("Your account may be disabled or blocked or the username/password you entered is incorrect.",
				driver.findElement(By.xpath("html/body/div[2]/font")).getText());
	}


	public void tearDown() throws Exception {
		driver.quit();
	}

}
