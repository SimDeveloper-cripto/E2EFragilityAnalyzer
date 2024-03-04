package JUnit.AddressBook810;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
public class AddressBookAddMultipleAddressBookTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}


	@Test
	public void addressBookAddMultipleAddressBook() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
		driver.findElement(By.linkText("nuovo")).click();
		driver.findElement(By.name("quickadd")).click();
		driver.findElement(By.name("firstname")).clear();
		driver.findElement(By.name("firstname")).sendKeys("firstname1");
		driver.findElement(By.name("lastname")).clear();
		driver.findElement(By.name("lastname")).sendKeys("lastname1");
		driver.findElement(By.name("address")).clear();
		driver.findElement(By.name("address")).sendKeys("address1");
		driver.findElement(By.name("home")).clear();
		driver.findElement(By.name("home")).sendKeys("01056321");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("mail1@mail.it");
		new Select(driver.findElement(By.name("bday"))).selectByVisibleText("11");
		new Select(driver.findElement(By.name("bmonth"))).selectByVisibleText("Giugno");
		driver.findElement(By.name("byear")).clear();
		driver.findElement(By.name("byear")).sendKeys("1981");
		driver.findElement(By.name("submit")).click();
		driver.findElement(By.linkText("add next")).click();

		driver.findElement(By.name("quickadd")).click();


		driver.findElement(By.name("firstname")).clear();
		driver.findElement(By.name("firstname")).sendKeys("firstname2");
		driver.findElement(By.name("lastname")).clear();
		driver.findElement(By.name("lastname")).sendKeys("lastname2");
		driver.findElement(By.name("address")).clear();
		driver.findElement(By.name("address")).sendKeys("address2");
		driver.findElement(By.name("home")).clear();
		driver.findElement(By.name("home")).sendKeys("01056322");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("mail2@mail.it");
		new Select(driver.findElement(By.name("bday"))).selectByVisibleText("12");
		new Select(driver.findElement(By.name("bmonth"))).selectByVisibleText("Giugno");
		driver.findElement(By.name("byear")).clear();
		driver.findElement(By.name("byear")).sendKeys("1982");
		driver.findElement(By.name("submit")).click();
		driver.findElement(By.linkText("add next")).click();

		driver.findElement(By.name("quickadd")).click();

		driver.findElement(By.name("firstname")).clear();
		driver.findElement(By.name("firstname")).sendKeys("firstname3");
		driver.findElement(By.name("lastname")).clear();
		driver.findElement(By.name("lastname")).sendKeys("lastname3");
		driver.findElement(By.name("address")).clear();
		driver.findElement(By.name("address")).sendKeys("address3");
		driver.findElement(By.name("home")).clear();
		driver.findElement(By.name("home")).sendKeys("01056323");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("mail3@mail.it");
		new Select(driver.findElement(By.name("bday"))).selectByVisibleText("13");
		new Select(driver.findElement(By.name("bmonth"))).selectByVisibleText("Giugno");
		driver.findElement(By.name("byear")).clear();
		driver.findElement(By.name("byear")).sendKeys("1983");
		driver.findElement(By.name("submit")).click();
		driver.findElement(By.linkText("home page")).click();
		assertEquals("Numero di risultati: 3",
				driver.findElement(By.xpath("html/body/div[1]/div[4]/label/strong")).getText());

		assertEquals("lastname1", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[2]")).getText());
		assertEquals("firstname1", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[3]")).getText());
		assertEquals("mail1@mail.it",
				driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[4]")).getText());
		assertEquals("01056321", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[5]")).getText());
		assertEquals("lastname2", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[2]")).getText());
		assertEquals("firstname2", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[3]")).getText());
		assertEquals("mail2@mail.it",
				driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[4]")).getText());
		assertEquals("01056322", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[5]")).getText());
		assertEquals("lastname3", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[4]/td[2]")).getText());
		assertEquals("firstname3", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[4]/td[3]")).getText());
		assertEquals("mail3@mail.it",
				driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[4]/td[4]")).getText());
		assertEquals("01056323", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[4]/td[5]")).getText());
	}


	public void tearDown() throws Exception {
		driver.quit();
	}
}
