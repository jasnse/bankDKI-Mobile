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
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import groovy.console.ui.ObjectBrowser
import groovy.json.StringEscapeUtils
import internal.GlobalVariable
import software.amazon.awssdk.auth.credentials.internal.WebIdentityCredentialsUtils

import org.openqa.selenium.WebElement
import org.openqa.selenium.Keys as SeleniumKeys   // kalau butuh tombol keyboard

import java.lang.runtime.SwitchBootstraps
import java.util.Arrays
import org.openqa.selenium.support.ui.Select

String link = "https://dki-crms-site.skyworx.co.id/login"

def pengelompokanAkun = [
	"Janji Bayar",
	"Kirim Email",
	"Meninggalkan Pesan",
	"Tidak Terjawab",
	"Nada Sibuk",
	"No Telpon Salah",
	"Partial Payment",
	"Sudah Bayar",
	"Lainnya"
]

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
	WebUI.waitForElementPresent(forceLoginBtn, 5)
	WebUI.click(forceLoginBtn)
	WebUI.comment("Force Login button found, clicking it...")
} catch (Exception e) {
	WebUI.comment("Force Login button not found within timeout, continue normal flow...")
}

for (int i = 0; i<pengelompokanAkun.size() ;i++) {
	
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


TestObject TasklistAlternative= findTestObject('Object Repository/xpath', ['xpath': "//a[@href='/tasklist-dki']"])
		
try {
	WebUI.waitForElementPresent(TasklistAlternative, 5)
	WebUI.click(TasklistAlternative)
} catch (Exception e) {
			WebUI.comment("lanjut")
}

//goto tasklisk menu -> new task

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

//submit dunning call

WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//select[@class='custom-select'])[3]"]), 0)
WebUI.delay(2)

String aktivitas = "Janji Bayar"
String pihakDihub = "Rekan Kerja"
String hasilAktivitas = "Janji Bayar"
String date = "09-30-2025"
String jumlahJanji = "33211955"
String alasanMenunggak = "Sakit"
String catatan = "dunning call submit by katalon"
String nomor = "085211821384"

WebUI.delay(2)
WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath',['xpath': "//select[@id='FIELD001-009-001']"]), aktivitas, false)
WebUI.delay(2)
WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath',['xpath': "//select[@id='FIELD001-009-002']"]), pihakDihub, false)
WebUI.delay(2)
WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath',['xpath': "//select[@id='FIELD001-009-003']"]), hasilAktivitas, false)
WebUI.delay(3)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD001-009-012']"]), date)
WebUI.delay(3)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD001-009-013']"]), jumlahJanji)
WebUI.delay(2)
WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath',['xpath': "//select[@id='FIELD001-009-004']"]), alasanMenunggak, false)
WebUI.delay(2)
WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath',['xpath': "//select[@id='FIELD001-009-005']"]), pengelompokanAkun[i], false)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//textarea[@id='FIELD001-009-006']"]), catatan)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD001-009-008']"]), nomor)
WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@type='button' and normalize-space(text())='Save']"]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@type='button' and normalize-space(text())='Yes, proceed']"]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@type='button' and normalize-space(text())='OK']"]), FailureHandling.STOP_ON_FAILURE)
	
	
	
	
}
