package JUnit.AddressBook810;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AddressBookEditMultipleAddressBookTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}
	@Test
	public void addressBookEditMultipleAddressBook() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();

		 driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[7]/a/img")).click();

		driver.findElement(By.name("address")).clear();
		driver.findElement(By.name("address")).sendKeys("newaddress1");
		driver.findElement(By.name("home")).clear();
		driver.findElement(By.name("home")).sendKeys("111111");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("newmail1@mail.it");

		driver.findElement(By.xpath(".//*[@id='content']/form[1]/input[19]")).click();


		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/div")).getText()
				.contains("Rubrica"));
		driver.findElement(By.linkText("home page")).click();

		driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[7]/a/img")).click();

		driver.findElement(By.name("address")).clear();
		driver.findElement(By.name("address")).sendKeys("newaddress2");
		driver.findElement(By.name("home")).clear();
		driver.findElement(By.name("home")).sendKeys("222222");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("newmail2@mail.it");

		driver.findElement(By.xpath(".//*[@id='content']/form[1]/input[19]")).click();

		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/div")).getText()
				.contains("Rubrica"));
		driver.findElement(By.linkText("home page")).click();
		driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[4]/td[7]/a/img")).click();

		driver.findElement(By.name("address")).clear();
		driver.findElement(By.name("address")).sendKeys("newaddress3");
		driver.findElement(By.name("home")).clear();
		driver.findElement(By.name("home")).sendKeys("333333");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("newmail3@mail.it");

		 driver.findElement(By.xpath(".//*[@id='content']/form[1]/input[19]")).click();

		assertTrue(driver.findElement(By.xpath(".//*[@id='content']/div")).getText()
				.contains("Rubrica"));
		driver.findElement(By.linkText("home page")).click();

		assertEquals("newmail1@mail.it",
				driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[4]")).getText());
		assertEquals("111111", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[5]")).getText());
		assertEquals("newmail2@mail.it",
				driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[4]")).getText());
		assertEquals("222222", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[5]")).getText());
		assertEquals("newmail3@mail.it",
				driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[4]/td[4]")).getText());
		assertEquals("333333", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[4]/td[5]")).getText());

	}

	public void tearDown() throws Exception {
		driver.quit();
	}

}
