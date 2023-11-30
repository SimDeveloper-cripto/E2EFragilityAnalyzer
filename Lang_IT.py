from selenium import webdriver
from selenium.webdriver.common.by import By
from time import sleep
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.options import Options

options = Options()
options.add_argument('--headless')
options.add_argument('--disable-gpu') 
driver = webdriver.Chrome(options)

driver.get("http://127.0.0.1:8888/app")

driver.find_element(By.NAME,"loginName").send_keys("admin")
driver.find_element(By.NAME,"password").send_keys("admin")
driver.find_element(By.NAME,"password").send_keys(Keys.ENTER)

driver.find_element(By.XPATH,'//html/body/div[1]/table[2]/tbody/tr/td[1]/a').click()
driver.find_element(By.XPATH,'/html/body/table[2]/tbody/tr/td/div[5]').click()

table = driver.find_element(By.CSS_SELECTOR,'body > table:nth-child(7)')

trs = table.find_elements(By.TAG_NAME,'tr')
for tr in trs:
    if "locale.default" in tr.text:
        print(tr.text)
        tr.find_elements(By.TAG_NAME,'td')[2].click()
        break

driver.find_element(By.NAME, 'field:value').send_keys("it")
driver.find_element(By.XPATH, '//input[@value="Submit"]').click()

print("Lingua cambiata in IT")
driver.close()
