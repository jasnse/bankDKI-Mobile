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
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import groovy.console.ui.ObjectBrowser
import groovy.json.StringEscapeUtils
import internal.GlobalVariable as GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.Keys as SeleniumKeys   // kalau butuh tombol keyboard
import java.util.Arrays
import org.openqa.selenium.support.ui.Select

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
String jenisAgunan = "TABUNGAN"
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

// click edit last row 
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//button[@class='btn mr-1 btn-primary btn-sm'])[last()]"]), FailureHandling.STOP_ON_FAILURE)

WebUI.delay(3)

// Helper: bikin TestObject by XPath cepat
TestObject byXpath(String xp) {
	TestObject t = new TestObject(xp)
	t.addProperty('xpath', ConditionType.EQUALS, xp)
	return t
}

// Helper: get .value untuk input/textarea/select
String getDomValue(String xpath) {
	TestObject obj = byXpath(xpath)
	WebUI.waitForElementPresent(obj, 10)
	WebElement el = WebUiCommonHelper.findWebElement(obj, 10)
	return WebUI.executeJavaScript("return arguments[0].value;", Arrays.asList(el))
}

// Helper: kalau elemen select & butuh label teks option terpilih
String getSelectedText(String xpath) {
	TestObject obj = byXpath(xpath)
	WebElement el = WebUiCommonHelper.findWebElement(obj, 10)
	return WebUI.executeJavaScript(
		"var s=arguments[0]; var o=s.options[s.selectedIndex]; return o? o.text.trim(): null;",
		Arrays.asList(el)
	)
}

// SELECT: Jenis Agunan
String jenisAgunanValue = getDomValue("//select[@id='FIELD001-008-002-002']") // contoh: "010"
String jenisAgunanText  = getSelectedText("//select[@id='FIELD001-008-002-002']") // contoh: "GIRO"
println "Jenis Agunan => value=${jenisAgunanValue}, text=${jenisAgunanText}"

// INPUT: Nama Pemilik
String namaPemilikEdit = getDomValue("//input[@id='FIELD001-008-002-003']")
println "Nama Pemilik => ${namaPemilikEdit}"

// TEXTAREA: Alamat Agunan
String alamatAgunanEdit = getDomValue("//textarea[@id='FIELD001-008-002-004']")
println "Alamat Agunan => ${alamatAgunanEdit}"

// SELECT: Bukti Kepemilikan
String buktiKepValue = getDomValue("//select[@id='FIELD001-008-002-005']")
String buktiKepText  = getSelectedText("//select[@id='FIELD001-008-002-005']")
println "Bukti Kepemilikan => value=${buktiKepValue}, text=${buktiKepText}"

// TEXTAREA: Keterangan Agunan
String keteranganAgunanEdit = getDomValue("//textarea[@id='FIELD001-008-002-006']")
println "Keterangan Agunan => ${keteranganAgunanEdit}"

// INPUT (angka): Nilai Jaminan / Pasar / Likuidasi
String nilaiJaminanEdit   = getDomValue("//input[@id='FIELD001-008-002-007']")
String nilaiPasarEdit     = getDomValue("//input[@id='FIELD001-008-002-008']")
String nilaiLikuidasiEdit = getDomValue("//input[@id='FIELD001-008-002-009']")
println "Nilai Jaminan   => ${nilaiJaminanEdit}"
println "Nilai Pasar     => ${nilaiPasarEdit}"
println "Nilai Likuidasi => ${nilaiLikuidasiEdit}"

// helper normalisasi angka
Closure<String> normNum = { s -> s?.replaceAll("[.,\\s]", "") }

// helper assert dengan pesan rapi
void mustEqual(String fieldName, String expected, String actual) {
	if (expected != actual) {
		KeywordUtil.markFailed("Mismatch ${fieldName}: expected='${expected}', got='${actual}'")
	} else {
		KeywordUtil.logInfo("OK ${fieldName}: '${actual}'")
	}
}

// Dropdown bandingkan TEKS (bukan value kode)
mustEqual("Jenis Agunan (text)", jenisAgunan, jenisAgunanText)
mustEqual("Bukti Kepemilikan (text)", buktiKepemilikan, buktiKepText)

// Field teks biasa
mustEqual("Nama Pemilik", namaPemilik, namaPemilikEdit)
mustEqual("Alamat Agunan", alamat, alamatAgunanEdit)
mustEqual("Keterangan Agunan", keteranganAgunan, keteranganAgunanEdit)

// Angka: samakan dulu formatnya
mustEqual("Nilai Jaminan", normNum(nilaiJaminan), normNum(nilaiJaminanEdit))
mustEqual("Nilai Pasar",   normNum(NilaiPasar),   normNum(nilaiPasarEdit))
mustEqual("Nilai Likuidasi", normNum(NilaiLikuidasi), normNum(nilaiLikuidasiEdit))


// Flag untuk cek semua validasi berhasil
boolean allOk = true
allOk &= (jenisAgunan == jenisAgunanText)
allOk &= (buktiKepemilikan == buktiKepText)
allOk &= (namaPemilik == namaPemilikEdit)
allOk &= (alamat == alamatAgunanEdit)
allOk &= (keteranganAgunan == keteranganAgunanEdit)
allOk &= (normNum(nilaiJaminan)   == normNum(nilaiJaminanEdit))
allOk &= (normNum(NilaiPasar)     == normNum(nilaiPasarEdit))
allOk &= (normNum(NilaiLikuidasi) == normNum(nilaiLikuidasiEdit))

if (allOk) {
	// Kalau ada SweetAlert2 di web, pakai Swal.fire
	String js = """
      if (window.Swal && typeof Swal.fire === 'function') {
        Swal.fire({
          icon: 'success',
          title: 'Validasi Berhasil',
          text: 'Semua nilai pada form edit sama dengan inputan.',
          confirmButtonText: 'OK'
        });
      } else {
        alert('✅ Validasi Berhasil: Semua nilai sama dengan inputan.');
      }
    """
	WebUI.executeJavaScript(js, null)
} else {
	// Kalau ada mismatch, kasih popup gagal
	String js = """
      if (window.Swal && typeof Swal.fire === 'function') {
        Swal.fire({
          icon: 'error',
          title: 'Validasi Gagal',
          text: 'Ada nilai yang berbeda. Cek log Katalon untuk detail.',
          confirmButtonText: 'OK'
        });
      } else {
        alert('❌ Validasi Gagal: Ada nilai yang berbeda. Lihat log.');
      }
    """
	WebUI.executeJavaScript(js, null)
}
















