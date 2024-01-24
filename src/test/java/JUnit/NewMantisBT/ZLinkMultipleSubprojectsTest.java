package JUnit.NewMantisBT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;


public class ZLinkMultipleSubprojectsTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void zlinkMultipleSubprojects() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Manage")).click();
		driver.findElement(By.linkText("Manage Projects")).click();
		driver.findElement(By.linkText("Project002")).click();
		/*driver.findElement(By.cssSelector("td.form-title > form > input.button-small")).click();
		driver.findElement(By.name("name")).clear();
		driver.findElement(By.name("name")).sendKeys("sub1");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Proceed")).click();
		driver.findElement(By.linkText("Project002")).click();
		driver.findElement(By.cssSelector("td.form-title > form > input.button-small")).click();
		driver.findElement(By.name("name")).clear();
		driver.findElement(By.name("name")).sendKeys("sub2");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Proceed")).click();
		driver.findElement(By.linkText("Project002")).click();*/
		new Select(driver.findElement(By.name("subproject_id"))).selectByVisibleText("sub12");
		driver.findElement(By.xpath("html/body/div[5]/table/tbody/tr[3]/td/form/input[3]")).click();
		driver.findElement(By.linkText("Proceed")).click();
		new Select(driver.findElement(By.name("subproject_id"))).selectByVisibleText("sub22");
		driver.findElement(By.xpath("html/body/div[5]/table/tbody/tr[5]/td/form/input[3]")).click();
		driver.findElement(By.linkText("Proceed")).click();
		assertEquals("sub12", driver.findElement(By.xpath("html/body/div[5]/table/tbody/tr[3]/td[1]/a")).getText());
		assertEquals("sub22", driver.findElement(By.xpath("html/body/div[5]/table/tbody/tr[4]/td[1]/a")).getText());
		driver.findElement(By.linkText("Logout")).click();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
