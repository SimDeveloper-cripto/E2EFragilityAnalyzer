package JUnit.AddressBook816;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class AddressBookEditMultipleGroupsTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}
	@Test
	public void addressBookEditMultipleGroups() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.linkText("gruppi")).click();
		driver.findElement(By.xpath(".//*[@id='content']/form/input[4]")).click();
		driver.findElement(By.xpath(".//*[@id='content']/form/input[9]")).click();
		driver.findElement(By.name("group_name")).clear();
		driver.findElement(By.name("group_name")).sendKeys("NewGroup1");
		driver.findElement(By.name("group_header")).clear();
		driver.findElement(By.name("group_header")).sendKeys("New Header1");
		driver.findElement(By.name("group_footer")).clear();
		driver.findElement(By.name("group_footer")).sendKeys("New Footer1");
		driver.findElement(By.name("update")).click();
		driver.findElement(By.linkText("group page")).click();
		driver.findElement(By.xpath(".//*[@id='content']/form/input[4]")).click();
		driver.findElement(By.xpath(".//*[@id='content']/form/input[9]")).click();
		driver.findElement(By.name("group_name")).clear();
		driver.findElement(By.name("group_name")).sendKeys("NewGroup2");
		driver.findElement(By.name("group_header")).clear();
		driver.findElement(By.name("group_header")).sendKeys("New Header2");
		driver.findElement(By.name("group_footer")).clear();
		driver.findElement(By.name("group_footer")).sendKeys("New Footer2");
		driver.findElement(By.name("update")).click();
		driver.findElement(By.linkText("group page")).click();	
		driver.findElement(By.xpath(".//*[@id='content']/form/input[4]")).click();
		driver.findElement(By.xpath(".//*[@id='content']/form/input[9]")).click();
		driver.findElement(By.name("group_name")).clear();
		driver.findElement(By.name("group_name")).sendKeys("NewGroup3");
		driver.findElement(By.name("group_header")).clear();
		driver.findElement(By.name("group_header")).sendKeys("New Header3");
		driver.findElement(By.name("group_footer")).clear();
		driver.findElement(By.name("group_footer")).sendKeys("New Footer3");
		driver.findElement(By.name("update")).click();
		driver.findElement(By.linkText("group page")).click();
		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/form")).getText().contains("NewGroup1"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/form")).getText().contains("NewGroup2"));
		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/form")).getText().contains("NewGroup3"));
		driver.findElement(By.linkText("homepage")).click();
		new Select(driver.findElement(By.name("group"))).selectByVisibleText("NewGroup1");
		assertTrue(driver.findElement(By.xpath("html/body/div[1]/div[4]")).getText().contains("New Header1"));
		assertTrue(driver.findElement(By.xpath("html/body/div[1]/div[4]")).getText().contains("New Footer1"));
		new Select(driver.findElement(By.name("group"))).selectByVisibleText("NewGroup2");
		assertTrue(driver.findElement(By.xpath("html/body/div[1]/div[4]")).getText().contains("New Header2"));
		assertTrue(driver.findElement(By.xpath("html/body/div[1]/div[4]")).getText().contains("New Footer2"));
		new Select(driver.findElement(By.name("group"))).selectByVisibleText("NewGroup3");
		assertTrue(driver.findElement(By.xpath("html/body/div[1]/div[4]")).getText().contains("New Header3"));
		assertTrue(driver.findElement(By.xpath("html/body/div[1]/div[4]")).getText().contains("New Footer3"));
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
