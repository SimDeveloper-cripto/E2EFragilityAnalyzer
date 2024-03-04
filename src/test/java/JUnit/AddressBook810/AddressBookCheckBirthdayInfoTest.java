package JUnit.AddressBook810;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AddressBookCheckBirthdayInfoTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}
	@Test
	public void addressBookCheckBirthdayInfo() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.linkText("compleanni")).click();
		assertEquals("19.", driver.findElement(By.xpath(".//*[@id='birthdays']/tbody/tr[2]/td[1]")).getText());
		assertEquals("lastname", driver.findElement(By.xpath(".//*[@id='birthdays']/tbody/tr[2]/td[2]")).getText());
		assertEquals("firstname", driver.findElement(By.xpath(".//*[@id='birthdays']/tbody/tr[2]/td[3]")).getText());
		assertEquals("mail@mail.it", driver.findElement(By.xpath(".//*[@id='birthdays']/tbody/tr[2]/td[5]")).getText());
		assertEquals("01056321", driver.findElement(By.xpath(".//*[@id='birthdays']/tbody/tr[2]/td[6]")).getText());
		assertTrue(driver.findElement(By.xpath(".//*[@id='birthdays']/tbody/tr[1]/th")).getText().contains("Giugno"));
//		assertEquals("Giugno 2019", driver.findElement(By.xpath(".//*[@id='birthdays']/tbody/tr[1]/th")).getText());
	}

	public void tearDown() throws Exception {
		driver.quit();
	}
}
