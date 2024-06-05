package JUnit.NEW_MantisBT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class UpdateUserEmptyTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}
	@Test
	public void updateUserEmpty() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");
		driver.findElement(By.cssSelector("input.button")).click();


		driver.findElement(By.linkText("Manage")).click();
		driver.findElement(By.linkText("Manage Users")).click();
		/*driver.findElement(By.cssSelector("td.form-title > form > input.button-small")).click();
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("username001");
		driver.findElement(By.name("realname")).clear();
		driver.findElement(By.name("realname")).sendKeys("username");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("username@username.it");
		driver.findElement(By.name("realname")).clear();
		driver.findElement(By.name("realname")).sendKeys("username001");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("username001@username.it");
		new Select(driver.findElement(By.name("access_level"))).selectByVisibleText("updater");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.xpath("html/body/div[2]/span[3]/a")).click();
		driver.findElement(By.linkText("Manage Users")).click();
		*/

		driver.findElement(By.linkText("username001")).click();
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("");
		driver.findElement(By.cssSelector("input.button")).click();
		assertEquals(
				"The username is invalid. Usernames may only contain Latin letters, numbers, spaces, hyphens, dots, plus signs and underscores.",
				driver.findElement(By.xpath("html/body/div[2]/table/tbody/tr[2]/td/p")).getText());
		driver.findElement(By.linkText("Logout")).click();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
