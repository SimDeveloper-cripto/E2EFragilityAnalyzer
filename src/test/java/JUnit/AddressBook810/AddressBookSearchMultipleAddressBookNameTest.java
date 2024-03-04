package JUnit.AddressBook810;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AddressBookSearchMultipleAddressBookNameTest {

	private  WebDriver driver = new ChromeDriver();
	JavascriptExecutor js = (JavascriptExecutor) driver;


	public void setUp(WebDriver driver) {
		this.driver.quit();
		this.driver=driver;
		js = (JavascriptExecutor) driver;
	}

	@Test
	public void addressBookSearchMultipleAddressBookName() throws Exception {
		driver.get("http://localhost:3000/index.php");
		//driver.findElement(By.name("user")).sendKeys("admin");
		//driver.findElement(By.name("pass")).sendKeys("secret");
		//driver.findElement(By.xpath(".//*[@id='content']/form/input[3]")).click();
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
		driver.findElement(By.name("searchstring")).clear();
		driver.findElement(By.name("searchstring")).sendKeys("lastname1");
		assertEquals("Numero di risultati: 1",
				driver.findElement(By.xpath("html/body/div[1]/div[4]/label/strong")).getText());
		assertEquals("lastname1", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[2]")).getText());
		assertEquals("firstname1", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[3]")).getText());
		assertEquals("mail1@mail.it",
				driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[4]")).getText());
		assertEquals("01056321", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[2]/td[5]")).getText());
		driver.findElement(By.name("searchstring")).clear();
		driver.findElement(By.name("searchstring")).sendKeys("lastname2");
		assertEquals("lastname2", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[2]")).getText());
		assertEquals("firstname2", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[3]")).getText());
		assertEquals("mail2@mail.it",
				driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[4]/a")).getText());
		assertEquals("01056322", driver.findElement(By.xpath(".//*[@id='maintable']/tbody/tr[3]/td[5]")).getText());
		driver.findElement(By.name("searchstring")).clear();
		driver.findElement(By.name("searchstring")).sendKeys("lastname3");
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
