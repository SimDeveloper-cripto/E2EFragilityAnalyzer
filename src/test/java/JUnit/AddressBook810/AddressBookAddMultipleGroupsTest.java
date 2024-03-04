package JUnit.AddressBook810;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AddressBookAddMultipleGroupsTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}
	@Test
	public void addressBookAddMultipleGroups() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.linkText("gruppi")).click();
		driver.findElement(By.name("new")).click();
		driver.findElement(By.name("group_name")).clear();
		driver.findElement(By.name("group_name")).sendKeys("Group1");
		driver.findElement(By.name("group_header")).clear();
		driver.findElement(By.name("group_header")).sendKeys("Header1");
		driver.findElement(By.name("group_footer")).clear();
		driver.findElement(By.name("group_footer")).sendKeys("Footer1");
		driver.findElement(By.name("submit")).click();
		driver.findElement(By.linkText("group page")).click();
		driver.findElement(By.name("new")).click();
		driver.findElement(By.name("group_name")).clear();
		driver.findElement(By.name("group_name")).sendKeys("Group2");
		driver.findElement(By.name("group_header")).clear();
		driver.findElement(By.name("group_header")).sendKeys("Header2");
		driver.findElement(By.name("group_footer")).clear();
		driver.findElement(By.name("group_footer")).sendKeys("Footer2");
		driver.findElement(By.name("submit")).click();
		driver.findElement(By.linkText("group page")).click();
		driver.findElement(By.name("new")).click();
		driver.findElement(By.name("group_name")).clear();
		driver.findElement(By.name("group_name")).sendKeys("Group3");
		driver.findElement(By.name("group_header")).clear();
		driver.findElement(By.name("group_header")).sendKeys("Header3");
		driver.findElement(By.name("group_footer")).clear();
		driver.findElement(By.name("group_footer")).sendKeys("Footer3");
		driver.findElement(By.name("submit")).click();
		driver.findElement(By.linkText("group page")).click();
		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/form")).getText()
				.contains("Group1"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/form")).getText()
				.contains("Group2"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/form")).getText()
				.contains("Group3"));
	}

	public void tearDown() throws Exception {
		driver.quit();
	}
}
