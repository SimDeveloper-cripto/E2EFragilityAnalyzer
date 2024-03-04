package JUnit.AddressBook810;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class AddressBookAssignToMultipleGroupsTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}
	@Test
	public void addressBookAssignToMultipleGroups() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.xpath("html/body/div[1]/div[4]/form[2]/table/tbody/tr[2]/td[1]/input")).click();
		driver.findElement(By.name("add")).click();
		assertTrue(driver.findElement(By.xpath("html/body/div[1]/div[4]/div")).getText().contains("Users added."));
		driver.findElement(By.linkText("homepage")).click();
		driver.findElement(By.xpath("html/body/div[1]/div[4]/form[2]/table/tbody/tr[3]/td[1]/input")).click();
		new Select(driver.findElement(By.name("to_group"))).selectByVisibleText("Group2");
		driver.findElement(By.name("add")).click();
		assertTrue(driver.findElement(By.xpath("html/body/div[1]/div[4]/div")).getText().contains("Users added."));
		driver.findElement(By.linkText("homepage")).click();
		driver.findElement(By.xpath("html/body/div[1]/div[4]/form[2]/table/tbody/tr[4]/td[1]/input")).click();
		new Select(driver.findElement(By.name("to_group"))).selectByVisibleText("Group3");
		driver.findElement(By.name("add")).click();
		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/div")).getText().contains("Users added."));
	}

	public void tearDown() throws Exception {
		driver.quit();
	}
}
