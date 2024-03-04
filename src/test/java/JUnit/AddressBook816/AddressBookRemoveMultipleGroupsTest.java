package JUnit.AddressBook816;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AddressBookRemoveMultipleGroupsTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}
	@Test
	public void addressBookRemoveMultipleGroups() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.linkText("gruppi")).click();
		driver.findElement(By.xpath(".//*[@id='content']/form/input[4]")).click();
		driver.findElement(By.xpath(".//*[@id='content']/form/input[5]")).click();
		driver.findElement(By.xpath(".//*[@id='content']/form/input[6]")).click();
		driver.findElement(By.xpath(".//*[@id='content']/form/input[8]")).click();
		driver.findElement(By.linkText("group page")).click();
		assertFalse(driver.findElement(By.xpath(".//*[@id='content']/form")).getText().contains("Group1"));
		assertFalse(driver.findElement(By.xpath(".//*[@id='content']/form")).getText().contains("Group2"));
		assertFalse(driver.findElement(By.xpath(".//*[@id='content']/form")).getText().contains("Group3"));
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
