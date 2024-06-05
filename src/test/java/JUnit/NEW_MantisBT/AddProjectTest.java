package JUnit.NEW_MantisBT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;


public class AddProjectTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void addProject() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Manage")).click();
		driver.findElement(By.linkText("Manage Projects")).click();
		driver.findElement(By.cssSelector("td.form-title > form > input.button-small")).click();
		driver.findElement(By.name("name")).clear();
		driver.findElement(By.name("name")).sendKeys("Project001 New");
		new Select(driver.findElement(By.name("status"))).selectByVisibleText("release");
		new Select(driver.findElement(By.name("view_state"))).selectByVisibleText("public");
		driver.findElement(By.name("description")).clear();
		driver.findElement(By.name("description")).sendKeys("description");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Proceed")).click();
		assertEquals("Project001 New", driver.findElement(By.xpath("html/body/table[3]/tbody/tr[3]/td[1]/a")).getText());
		assertEquals("release", driver.findElement(By.xpath("html/body/table[3]/tbody/tr[3]/td[2]")).getText());
		assertEquals("public", driver.findElement(By.xpath("html/body/table[3]/tbody/tr[3]/td[4]")).getText());
		assertEquals("description", driver.findElement(By.xpath("html/body/table[3]/tbody/tr[3]/td[5]")).getText());
		driver.findElement(By.linkText("Logout")).click();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
