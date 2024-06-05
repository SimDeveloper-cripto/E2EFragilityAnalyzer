package JUnit.NEW_MantisBT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class UpdateCategoryTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;

	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void updateCategory() throws Exception {
		driver.get("http://localhost:8080/login_page.php");
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("administrator");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("root");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.linkText("Manage")).click();
		driver.findElement(By.linkText("Manage Projects")).click();
		driver.findElement(By.linkText("Project002")).click();
		driver.findElement(By.cssSelector("td.center > form > input.button-small")).click();
		driver.findElement(By.xpath("/html/body/div[6]/a[1]/table/tbody/tr[4]/td/form/input[3]")).clear();
		driver.findElement(By.xpath("/html/body/div[6]/a[1]/table/tbody/tr[4]/td/form/input[3]")).sendKeys("Category002");
		driver.findElement(By.xpath("/html/body/div[6]/a[1]/table/tbody/tr[4]/td/form/input[4]")).click();
		driver.findElement(By.xpath("/html/body/div[6]/a[1]/table/tbody/tr[3]/td[3]/form[1]/input[2]")).click();
		driver.findElement(By.name("name")).clear();
		driver.findElement(By.name("name")).sendKeys("Category002Mod");
		driver.findElement(By.xpath("/html/body/div[3]/form/table/tbody/tr[4]/td[2]/input")).click();

		driver.findElement(By.linkText("Proceed")).click();
		assertEquals("Category002Mod",
				driver.findElement(By.xpath("html/body/div[6]/a[1]/table/tbody/tr[3]/td[1]")).getText());
		driver.findElement(By.linkText("Logout")).click();
	}


	public void tearDown() throws Exception {
		driver.quit();
	}

}
