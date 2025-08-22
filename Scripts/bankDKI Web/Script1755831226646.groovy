import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import javax.swing.JOptionPane

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import groovy.console.ui.ObjectBrowser
import groovy.json.StringEscapeUtils
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

String link = "https://dki-crms-site.skyworx.co.id/login"

// open web
WebUI.openBrowser(link)
WebUI.maximizeWindow()

// login
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='username']"]), GlobalVariable.username)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='password']"]), GlobalVariable.password)
	//captcha dialogbox
	String captchaInput = JOptionPane.showInputDialog(null, "Please solve the CAPTCHA and enter the code below:", "CAPTCHA Input", JOptionPane.INFORMATION_MESSAGE)
	WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@placeholder='Enter Captcha']"]), captchaInput)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[.//div[text()='Login']]"]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)

TestObject forceLoginBtn = findTestObject('Object Repository/xpath', ['xpath': "//button[contains(@class, 'swal2-confirm') and text()='Login here']"])
try {
	// Wait for the element to be present (for up to 10 seconds)
	WebUI.waitForElementPresent(forceLoginBtn, 10)
	// If found, click the "Force Login" button
	WebUI.click(forceLoginBtn)
	WebUI.comment("Force Login button found, clicking it...")
} catch (Exception e) {
	// If the element is not found within the timeout, continue the normal flow
	WebUI.comment("Force Login button not found within timeout, continue normal flow...")
}

def menuTaskList = [
	[key: "New Task", value: "(//div[@aria-expanded='false' and @role='tab'])[1]"],
	[key: "Whitelist", value: "(//div[@aria-expanded='false' and @role='tab'])[2]"],
	[key: "Janji Bayar", value: "(//div[@aria-expanded='false' and @role='tab'])[3]"],
	[key: "Meninggalkan Pesan", value: "(//div[@aria-expanded='false' and @role='tab'])[4]"],
	[key: "Tidak Terjawab", value: "(//div[@aria-expanded='false' and @role='tab'])[5]"],
	[key: "Kunjungan", value: "(//div[@aria-expanded='false' and @role='tab'])[6]"],
	[key: "Kirim Email", value: "(//div[@aria-expanded='false' and @role='tab'])[7]"],
	[key: "Lainnya", value: "(//div[@aria-expanded='false' and @role='tab'])[8]"],
	[key: "Nada Sibuk", value: "(//div[@aria-expanded='false' and @role='tab'])[9]"],
	[key: "Telepon Salah", value: "(//div[@aria-expanded='false' and @role='tab'])[10]"],
	[key: "Partial Payment", value: "(//div[@aria-expanded='false' and @role='tab'])[11]"],
	[key: "Sudah Bayar", value: "(//div[@aria-expanded='false' and @role='tab'])[12]"]
	]

//goto tasklisk menu -> new task
TestObject TasklistAlternative= findTestObject('Object Repository/xpath', ['xpath': "//a[@href='/tasklist-dki']"])

try {
	WebUI.waitForElementPresent(TasklistAlternative, 5)
	WebUI.click(TasklistAlternative)
} catch (Exception e) {
	WebUI.comment("lanjut")
}
	
WebUI.click(findTestObject('Object Repository/xpath',['xpath': "//span[contains(text(),'Tasklist - Collection Cabang')]"]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(10)

WebUI.click(findTestObject('Object Repository/xpath', ['xpath': menuTaskList.find { it.key == "New Task" }?.value]), FailureHandling.STOP_ON_FAILURE)

//choose debitur and retrive norek pinjaman and name
WebUI.delay(5)
String norekpinjaman = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//a[contains(@href, '/tasklist-dki/')])[1]"]), FailureHandling.STOP_ON_FAILURE)
String namadebitur = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//td[@aria-colindex='4'])[1]"]), FailureHandling.STOP_ON_FAILURE)
WebUI.comment(norekpinjaman +" "+ namadebitur)
WebUI.delay(2)
WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//input[@placeholder='Cari data...'])[1]"]), 0, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//a[contains(@href, '/tasklist-dki/')])[1]"]), FailureHandling.STOP_ON_FAILURE)

//input data agunan
String jenisAgunan = "GIRO"
String namaPemilik = "Jason Katalon"
String alamat = "Jl.Alamat No.123, Jakarta Pusat"
String buktiKepemilikan = "SHM"
String keteranganAgunan = "ini adlaah keterangan agunan"
String nilaiJaminan = "111111"
String NilaiPasar = "222222"
String NilaiLikuidasi = "333333"


WebUI.delay(5)
WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//select[@id='__BVID__940']"]), 0, FailureHandling.STOP_ON_FAILURE)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//button[@class='btn mr-1 btn-danger'])[3]"]), FailureHandling.STOP_ON_FAILURE)

WebUI.delay(2)
WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath', ['xpath': "//select[@id='FIELD001-008-001-002']"]), jenisAgunan, false, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "(//input[@type='text'])[2]"]), namaPemilik, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//textarea[@id='FIELD001-008-001-004']"]), alamat, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath', ['xpath': "//select[@id='FIELD001-008-001-005']"]), buktiKepemilikan, false, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//textarea[@id='FIELD001-008-001-006']"]), keteranganAgunan, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "(//input[@type='text'])[3]"]), nilaiJaminan, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "(//input[@type='text'])[4]"]), NilaiPasar, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "(//input[@type='text'])[5]"]), NilaiLikuidasi, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@class='btn mx-2 btn-danger']"]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[text() ='Yes, proceed']"]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[text()='OK']"]), FailureHandling.STOP_ON_FAILURE)

//compare Edit Anggunan dengan input
WebUI.delay(2)
// Define the XPath for the column header with aria-colindex='1' and aria-sort='none'
String xpath = "(//th[@aria-colindex='1' and @aria-sort='none'])[3]"

// JavaScript to set aria-sort to 'descending' and trigger the click event
String script = """
    var element = document.evaluate("${xpath}", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
    if (element) {
        // Change aria-sort to 'descending'
        element.setAttribute('aria-sort', 'descending');
        
        // Trigger a click event on the element to trigger sorting in the UI
        element.click();
        
        return 'Successfully changed to descending and clicked the header to sort.';
    } else {
        return 'Error: Element not found.';
    }
"""

// Execute the JavaScript to update aria-sort and click the column header
String result = WebUI.executeJavaScript(script, null)

// Log the result of the JavaScript execution
WebUI.comment(result)

// Wait for the sorting to be completed (optional: adjust the wait time if needed)
WebUI.delay(3)  // Wait for 3 seconds (adjust if necessary) for the table to sort

// After sorting, click the button in the row
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//button[@class='btn mr-1 btn-primary btn-sm'])[2]"]), FailureHandling.STOP_ON_FAILURE)

















