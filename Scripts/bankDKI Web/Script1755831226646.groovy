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

Map<String, String> mapJenisAgunan = [
	"010": "GIRO",
	"020": "TABUNGAN",
	"041": "SIMPANAN BERJANGKA",
	"042": "SURAT BERHARGA SBI",
	"043": "SURAT BERHARGA SPN",
	"045": "SETORAN JAMINAN",
	"046": "EMAS",
	"047": "SERTIFIKAT BANK INDONESIA",
	"048": "SURAT BERHARGA BANK INDONESIA VALAS",
	"081": "SURAT BERHARGA REKSADANA",
	"086": "SURAT BERHARGA ON",
	"087": "SURAT BERHARGA ORI",
	"091": "SURAT BERHARGA SAHAM",
	"092": "RESI GUDANG",
	"161": "PROPERTI KOMERSIAL GEDUNG",
	"162": "PROPERTI KOMERSIAL GUDANG",
	"163": "PROPERTI KOMERSIAL RUKO/RUKAN/KIOS",
	"164": "PROPERTI KOMERSIAL HOTEL",
	"175": "PROPERTI KOMERSIAL LAINNYA",
	"176": "PROPERTI RESIDENSIAL RUMAH TINGGAL",
	"177": "PROPERTI RESIDENSIAL APARTEMEN/RUSUN",
	"187": "TANAH",
	"189": "KENDARAAN BERMOTOR",
	"190": "MESIN",
	"191": "PESAWAT UDARA",
	"192": "KAPAL LAUT",
	"193": "PERSEDIAAN",
	"250": "AGUNAN LAINNYA BERWUJUD",
	"251": "SB/LC",
	"252": "GARANSI",
	"253": "KREDIT DERIVATIF",
	"254": "ASURANSI KREDIT",
	"275": "AGUNAN LAINNYA TIDAK BERWUJUD",
	"300": "TIDAK ADA AGUNAN/JAMINAN",
	"T1" : "Test 11"
]

Map<String, String> mapBuktiKep = [
	"100": "SHM",
	"SHMT": "SHM Tanah",
	"SHMR": "SHM Rumah",
	"101": "SHGU",
	"102": "SHGB",
	"103": "SHP",
	"104": "SHM SRS",
	"105": "SKKMS",
	"106": "Girik",
	"107": "PPJB",
	"108": "AJB",
	"109": "Hak Sewa",
	"110": "STP",
	"111": "SIPTB",
	"112": "SIPTU",
	"200": "Bilyet T/D",
	"201": "Buku Tab.",
	"202": "Srt Tagihan",
	"203": "STP",
	"204": "N/D",
	"300": "STP Emas",
	"400": "BPKB",
	"401": "Faktur",
	"500": "D/O",
	"501": "Kwitansi"
]

// Helper konversi kode → label (fallback ke raw kalau tidak ada di map)
String mapValue(Map<String,String> mapping, String raw) {
	String v = raw?.trim()
	return mapping.containsKey(v) ? mapping[v] : v
}

// Helper: bikin TestObject by XPath
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

// helper normalisasi angka
Closure<String> normNum = { s -> s?.replaceAll("[.,\\s]", "") }

String cleanCurrency(String s) {
	if (s == null) return null
	return s.replaceAll("[^0-9]", "")  // buang semua kecuali angka
}

// helper assert
void mustEqual(String fieldName, String expected, String actual) {
	if (expected != actual) {
		KeywordUtil.markFailed("Mismatch ${fieldName}: expected='${expected}', got='${actual}'")
	} else {
		KeywordUtil.logInfo("OK ${fieldName}: '${actual}'")
	}
}

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

//input data agunan
String jenisAgunan = "TABUNGAN"
String namaPemilik = "Jason Katalon"
String alamat = "Jl.Alamat No.123, Jakarta Pusat"
String buktiKepemilikan = "SHM"
String keteranganAgunan = "ini adlaah keterangan agunan"
String nilaiJaminan = "111111"
String NilaiPasar = "222222"
String NilaiLikuidasi = "333333"

WebUI.delay(8)
WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//select[@class='custom-select'])[2]"]), 0, FailureHandling.STOP_ON_FAILURE)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//button[@class='btn mr-1 btn-danger'])[3]"]), FailureHandling.STOP_ON_FAILURE)

WebUI.delay(2)
WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath', ['xpath': "//select[@id='FIELD001-008-001-002']"]), jenisAgunan, false, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD001-008-001-003']"]), namaPemilik, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//textarea[@id='FIELD001-008-001-004']"]), alamat, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath', ['xpath': "//select[@id='FIELD001-008-001-005']"]), buktiKepemilikan, false, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//textarea[@id='FIELD001-008-001-006']"]), keteranganAgunan, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD001-008-001-007']"]), nilaiJaminan, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD001-008-001-008']"]), NilaiPasar, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD001-008-001-009']"]), NilaiLikuidasi, FailureHandling.STOP_ON_FAILURE)
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

// SELECT: Jenis Agunan
String jenisAgunanValue = getDomValue("//select[@id='FIELD001-008-002-002']") // contoh: "010"
String jenisAgunanText  = getSelectedText("//select[@id='FIELD001-008-002-002']") // contoh: "GIRO"
// INPUT: Nama Pemilik
String namaPemilikEdit = getDomValue("//input[@id='FIELD001-008-002-003']")
// TEXTAREA: Alamat Agunan
String alamatAgunanEdit = getDomValue("//textarea[@id='FIELD001-008-002-004']")
// SELECT: Bukti Kepemilikan
String buktiKepValue = getDomValue("//select[@id='FIELD001-008-002-005']")
String buktiKepText  = getSelectedText("//select[@id='FIELD001-008-002-005']")
// TEXTAREA: Keterangan Agunan
String keteranganAgunanEdit = getDomValue("//textarea[@id='FIELD001-008-002-006']")
// INPUT (angka): Nilai Jaminan / Pasar / Likuidasi
String nilaiJaminanEdit   = getDomValue("//input[@id='FIELD001-008-002-007']")
String nilaiPasarEdit     = getDomValue("//input[@id='FIELD001-008-002-008']")
String nilaiLikuidasiEdit = getDomValue("//input[@id='FIELD001-008-002-009']")
// compare field dropdown Edit
mustEqual("Jenis Agunan (text)", jenisAgunan, jenisAgunanText)
mustEqual("Bukti Kepemilikan (text)", buktiKepemilikan, buktiKepText)

// compare field text Edit
mustEqual("Nama Pemilik", namaPemilik, namaPemilikEdit)
mustEqual("Alamat Agunan", alamat, alamatAgunanEdit)
mustEqual("Keterangan Agunan", keteranganAgunan, keteranganAgunanEdit)

// compare num field Edit
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
	WebUI.delay(2)
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
	WebUI.delay(2)
	// Hentikan test case
	KeywordUtil.markFailedAndStop("Validasi gagal: Ada nilai yang berbeda dengan inputan")
}
WebUI.delay(2)
WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD001-008-002-009']"]), 0)
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//button[@type='button' and normalize-space(text())='Save'])[2]"]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@type='button' and normalize-space(text())='Yes, proceed']"]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@type='button' and normalize-space(text())='OK']"]), FailureHandling.STOP_ON_FAILURE)


//compare View Anggunan dengan input

WebUI.delay(2)
WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@class='table b-table table-bordered'])[3]//td[@aria-colindex='7']//button[contains(@class,'btn-warning')])[last()]"]), FailureHandling.STOP_ON_FAILURE)

String jenisAgunanViewRaw = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[38]"]))
String jenisAgunanView = mapValue(mapJenisAgunan, jenisAgunanViewRaw) 

String namaPemilikView = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[39]"]))
String alamatAgunanView = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[40]"]))

String buktiKepViewRaw = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[41]"]))
String buktiKepView = mapValue(mapBuktiKep, buktiKepViewRaw)

//String masaBerlakuView = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[42]"]))
String ketAgunanView = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]))
String nilaiJaminanView = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[44]"]))
String nilaiPasarView = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[45]"]))
String nilaiLikuidasiView = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[46]"]))


mustEqual("Jenis Agunan", jenisAgunan, jenisAgunanView)
mustEqual("Nama Pemilik", namaPemilik, namaPemilikView)
mustEqual("Alamat Agunan", alamat, alamatAgunanView)
mustEqual("Bukti Kepemilikan", buktiKepemilikan, buktiKepView)
mustEqual("Keterangan Agunan", keteranganAgunan, ketAgunanView)
mustEqual("Nilai Jaminan", cleanCurrency(nilaiJaminan), cleanCurrency(nilaiJaminanView))
mustEqual("Nilai Pasar", cleanCurrency(NilaiPasar), cleanCurrency(nilaiPasarView))
mustEqual("Nilai Likuidasi", cleanCurrency(NilaiLikuidasi), cleanCurrency(nilaiLikuidasiView))

// Flag untuk cek semua validasi berhasil
boolean allOkinView = true
allOkinView &= (jenisAgunan == jenisAgunanView)
allOkinView &= (buktiKepemilikan == buktiKepView)
allOkinView &= (namaPemilik == namaPemilikView)
allOkinView &= (alamat == alamatAgunanView)
allOkinView &= (keteranganAgunan == ketAgunanView)
allOkinView &= (cleanCurrency(nilaiJaminan)   == cleanCurrency(nilaiJaminanView) )
allOkinView &= (cleanCurrency(NilaiPasar)     == cleanCurrency(nilaiPasarView) )
allOkinView &= (cleanCurrency(NilaiLikuidasi) == cleanCurrency(nilaiLikuidasiView) )

if (allOkinView) {
	String js = """
      if (window.Swal && typeof Swal.fire === 'function') {
        Swal.fire({
          icon: 'success',
          title: 'Validasi Berhasil',
          text: 'Semua data input sama dengan data view.',
          confirmButtonText: 'OK'
        });
      } else {
        alert('✅ Validasi Berhasil: Semua data sama dengan view.');
      }
    """
	WebUI.executeJavaScript(js, null)
} else {
	String js = """
      if (window.Swal && typeof Swal.fire === 'function') {
        Swal.fire({
          icon: 'error',
          title: 'Validasi Gagal',
          text: 'Ada data yang berbeda, cek log untuk detail.',
          confirmButtonText: 'OK'
        });
      } else {
        alert('❌ Validasi Gagal: Ada data yang berbeda, lihat log.');
      }
    """
	WebUI.executeJavaScript(js, null)

}
WebUI.delay(3)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@type='button' and normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)


//submit dunning call

WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//select[@class='custom-select'])[3]"]), 0)
WebUI.delay(2)

String aktivitas = "Janji Bayar"
String pihakDihub = "Rekan Kerja"
String hasilAktivitas = "Janji Bayar"
String date = "08-30-2025"
String jumlahJanji = "33211955"
String alasanMenunggak = "Sakit"
String catatan = "dunning call submit by katalon"
String nomor = "085211821384"

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

//check bucket akun

switch (pengelompokanAkun[i]) {
	case "Janji Bayar":
	//goto menu janji bayar
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': menuTaskList.find { it.key == "Janji Bayar" }?.value]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(4)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//input[@placeholder='DPD'])[3]"]), 0, FailureHandling.STOP_ON_FAILURE)
	//cek masuk sesuai bucket
	WebUI.delay(10)
	String norekPinjamanDunning = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[3]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	println("actual in janji bayar: " +norekPinjamanDunning.trim() +" Expected: " + norekpinjaman.trim())
	
	if (norekPinjamanDunning.trim() == norekpinjaman.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + norekpinjaman.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + norekPinjamanDunning.trim() + " | Normal: " + norekpinjaman.trim() + "')", null)
	}
	
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[3]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD003-009-008']"]), 0, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@role='table'])[5]//button[@class='btn mr-1 btn-warning btn-sm'][1])[1]"]), FailureHandling.STOP_ON_FAILURE)
	
	//get dunningcall history after submit
	WebUI.delay(2)
	String nilaiJanjiBayarRaw = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]), FailureHandling.STOP_ON_FAILURE)
	String nilaiJanjiBayarDunning = cleanCurrency(nilaiJanjiBayarRaw)
	
	if (nilaiJanjiBayarDunning.trim() == jumlahJanji.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + jumlahJanji.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + nilaiJanjiBayarDunning.trim() + " | Normal: " + jumlahJanji.trim() + "')", null)
	}
	
	//close dunning history
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)
	
	//submit data agunan in bucket janji bayar
	
	WebUI.delay(2)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//select[@class='custom-select'])[2]"]), 0, FailureHandling.STOP_ON_FAILURE)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//button[@class='btn mr-1 btn-danger'])[3]"]), FailureHandling.STOP_ON_FAILURE)
	
	WebUI.delay(2)
	WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath', ['xpath': "//select[@id='FIELD003-008-001-002']"]), GlobalVariable.jenisAgunanInBucket, false, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD003-008-001-003']"]), GlobalVariable.namaPemilikInBucket, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//textarea[@id='FIELD003-008-001-004']"]), GlobalVariable.alamatInBucket, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.selectOptionByLabel(findTestObject('Object Repository/xpath', ['xpath': "//select[@id='FIELD003-008-001-005']"]), GlobalVariable.buktiKepemilikanInBucket, false, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//textarea[@id='FIELD003-008-001-006']"]), GlobalVariable.keteranganAgunanInBucket, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD003-008-001-007']"]), GlobalVariable.nilaiJaminanInBucket, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD003-008-001-008']"]), GlobalVariable.NilaiPasarInBucket, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.setText(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD003-008-001-009']"]), GlobalVariable.NilaiLikuidasiInBucket, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@class='btn mx-2 btn-danger']"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[text() ='Yes, proceed']"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[text()='OK']"]), FailureHandling.STOP_ON_FAILURE)
	
	
	//compare hasil input agunan
	//compare Edit Anggunan dengan input
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//button[@class='btn mr-1 btn-primary btn-sm'])[last()]"]), FailureHandling.STOP_ON_FAILURE)
	
	WebUI.delay(3)
	
	// SELECT: Jenis Agunan
	String jenisAgunanValueBucket = getDomValue("//select[@id='FIELD003-008-002-002']") // contoh: "010"
	String jenisAgunanTextBucket  = getSelectedText("//select[@id='FIELD003-008-002-002']") // contoh: "GIRO"
	// INPUT: Nama Pemilik
	String namaPemilikEditBucket = getDomValue("//input[@id='FIELD003-008-002-003']")
	// TEXTAREA: Alamat Agunan
	String alamatAgunanEditBucket = getDomValue("//textarea[@id='FIELD003-008-002-004']")
	// SELECT: Bukti Kepemilikan
	String buktiKepValueBucket = getDomValue("//select[@id='FIELD003-008-002-005']")
	String buktiKepTextBucket  = getSelectedText("//select[@id='FIELD003-008-002-005']")
	// TEXTAREA: Keterangan Agunan
	String keteranganAgunanEditBucket = getDomValue("//textarea[@id='FIELD003-008-002-006']")
	// INPUT (angka): Nilai Jaminan / Pasar / Likuidasi
	String nilaiJaminanEditBucket   = getDomValue("//input[@id='FIELD003-008-002-007']")
	String nilaiPasarEditBucket     = getDomValue("//input[@id='FIELD003-008-002-008']")
	String nilaiLikuidasiEditBucket = getDomValue("//input[@id='FIELD003-008-002-009']")
	
	// compare field dropdown Edit
	mustEqual("Jenis Agunan (text)", GlobalVariable.jenisAgunanInBucket, jenisAgunanTextBucket)
	mustEqual("Bukti Kepemilikan (text)",  GlobalVariable.buktiKepemilikanInBucket, buktiKepTextBucket)
	
	// compare field text Edit
	mustEqual("Nama Pemilik", GlobalVariable.namaPemilikInBucket, namaPemilikEditBucket)
	mustEqual("Alamat Agunan", GlobalVariable.alamatInBucket, alamatAgunanEditBucket)
	mustEqual("Keterangan Agunan", GlobalVariable.keteranganAgunanInBucket, keteranganAgunanEditBucket)
	
	// compare num field Edit
	mustEqual("Nilai Jaminan", normNum(GlobalVariable.nilaiJaminanInBucket), normNum(nilaiJaminanEditBucket))
	mustEqual("Nilai Pasar",   normNum(GlobalVariable.NilaiPasarInBucket),   normNum(nilaiPasarEditBucket))
	mustEqual("Nilai Likuidasi", normNum(GlobalVariable.NilaiLikuidasiInBucket), normNum(nilaiLikuidasiEditBucket))
	
	// Flag untuk cek semua validasi berhasil
	boolean allOkBucket = true
	allOkBucket &= (GlobalVariable.jenisAgunanInBucket == jenisAgunanTextBucket)
	allOkBucket &= (GlobalVariable.buktiKepemilikanInBucket == buktiKepTextBucket)
	allOkBucket &= (GlobalVariable.namaPemilikInBucket == namaPemilikEditBucket)
	allOkBucket &= (GlobalVariable.alamatInBucket == alamatAgunanEditBucket)
	allOkBucket &= (GlobalVariable.keteranganAgunanInBucket == keteranganAgunanEditBucket)
	allOkBucket &= (normNum(GlobalVariable.nilaiJaminanInBucket)   == normNum(nilaiJaminanEditBucket))
	allOkBucket &= (normNum(GlobalVariable.NilaiPasarInBucket)     == normNum(nilaiPasarEditBucket))
	allOkBucket &= (normNum(GlobalVariable.NilaiLikuidasiInBucket) == normNum(nilaiLikuidasiEditBucket))
	
	if (allOkBucket) {
		String js = """
      if (window.Swal && typeof Swal.fire === 'function') {
        Swal.fire({
          icon: 'success',
          title: 'Validasi Berhasil',
          text: 'Semua nilai pada form edit sama dengan inputan.',
          confirmButtonText: 'OK'
        });
      } else {
        alert('✅ Validasi Berhasil: Semua nilai pada edit agunan bucket sama dengan inputan.');
      }
    """
		WebUI.executeJavaScript(js, null)
		WebUI.delay(2)
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
        alert('❌ Validasi Gagal: Ada nilai yang berbeda pada edit agunan bucket. Lihat log.');
      }
    """
		WebUI.executeJavaScript(js, null)
		WebUI.delay(2)
		// Hentikan test case
		KeywordUtil.markFailedAndStop("Validasi gagal: Ada nilai yang berbeda dengan inputan")
	}
	WebUI.delay(2)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD003-008-002-009']"]), 0)
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//button[@type='button' and normalize-space(text())='Save'])[2]"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@type='button' and normalize-space(text())='Yes, proceed']"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@type='button' and normalize-space(text())='OK']"]), FailureHandling.STOP_ON_FAILURE)
	
	//compare View Anggunan dengan input
	WebUI.delay(2)
	WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@class='table b-table table-bordered'])[3]//td[@aria-colindex='7']//button[contains(@class,'btn-warning')])[last()]"]), FailureHandling.STOP_ON_FAILURE)
	
	String jenisAgunanViewRawBucket = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[38]"]))
	String jenisAgunanViewBucket = mapValue(mapJenisAgunan, jenisAgunanViewRawBucket)
	
	String namaPemilikViewBucket = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[39]"]))
	String alamatAgunanViewBucket = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[40]"]))
	
	String buktiKepViewRawBucket = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[41]"]))
	String buktiKepViewBucket = mapValue(mapBuktiKep, buktiKepViewRawBucket)
	
	//String masaBerlakuView = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[42]"]))
	String ketAgunanViewBucket = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]))
	String nilaiJaminanViewBucket = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[44]"]))
	String nilaiPasarViewBucket = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[45]"]))
	String nilaiLikuidasiViewBucket = WebUI.getText(findTestObject('Object Repository/xpath',['xpath': "(//div[@class='p-0 border-bottom value col'])[46]"]))
	
	
	mustEqual("Jenis Agunan", GlobalVariable.jenisAgunanInBucket, jenisAgunanViewBucket)
	mustEqual("Nama Pemilik", GlobalVariable.namaPemilikInBucket, namaPemilikViewBucket)
	mustEqual("Alamat Agunan", GlobalVariable.alamatInBucket, alamatAgunanViewBucket)
	mustEqual("Bukti Kepemilikan", GlobalVariable.buktiKepemilikanInBucket, buktiKepViewBucket)
	mustEqual("Keterangan Agunan", GlobalVariable.keteranganAgunanInBucket, ketAgunanViewBucket)
	mustEqual("Nilai Jaminan", cleanCurrency(GlobalVariable.nilaiJaminanInBucket), cleanCurrency(nilaiJaminanViewBucket))
	mustEqual("Nilai Pasar", cleanCurrency(GlobalVariable.NilaiPasarInBucket), cleanCurrency(nilaiPasarViewBucket))
	mustEqual("Nilai Likuidasi", cleanCurrency(GlobalVariable.NilaiLikuidasiInBucket), cleanCurrency(nilaiLikuidasiViewBucket))
	
	// Flag untuk cek semua validasi berhasil pada bukcett
	boolean allOkinViewBucket = true
	allOkinViewBucket &= (GlobalVariable.jenisAgunanInBucket == jenisAgunanViewBucket)
	allOkinViewBucket &= (GlobalVariable.buktiKepemilikanInBucket == buktiKepViewBucket)
	allOkinViewBucket &= (GlobalVariable.namaPemilikInBucket == namaPemilikViewBucket)
	allOkinViewBucket &= (GlobalVariable.alamatInBucket == alamatAgunanViewBucket)
	allOkinViewBucket &= (GlobalVariable.keteranganAgunanInBucket == ketAgunanViewBucket)
	allOkinViewBucket &= (cleanCurrency(GlobalVariable.nilaiJaminanInBucket)   == cleanCurrency(nilaiJaminanViewBucket) )
	allOkinViewBucket &= (cleanCurrency(GlobalVariable.NilaiPasarInBucket)     == cleanCurrency(nilaiPasarViewBucket) )
	allOkinViewBucket &= (cleanCurrency(GlobalVariable.NilaiLikuidasiInBucket) == cleanCurrency(nilaiLikuidasiViewBucket) )
	
	if (allOkinViewBucket) {
		String js = """
      if (window.Swal && typeof Swal.fire === 'function') {
        Swal.fire({
          icon: 'success',
          title: 'Validasi Berhasil',
          text: 'Semua data input sama dengan data view.',
          confirmButtonText: 'OK'
        });
      } else {
        alert('✅ Validasi Berhasil: Semua data sama dengan view.');
      }
    """
		WebUI.executeJavaScript(js, null)
	} else {
		String js = """
      if (window.Swal && typeof Swal.fire === 'function') {
        Swal.fire({
          icon: 'error',
          title: 'Validasi Gagal',
          text: 'Ada data yang berbeda, cek log untuk detail.',
          confirmButtonText: 'OK'
        });
      } else {
        alert('❌ Validasi Gagal: Ada data yang berbeda, lihat log.');
      }
    """
		WebUI.executeJavaScript(js, null)
	
	}
	WebUI.delay(3)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[@type='button' and normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)
	
	
	
	break
	
	case "Kirim Email":
	//goto menu kirim email
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': menuTaskList.find { it.key == "Kirim Email" }?.value]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(4) 
WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//input[@placeholder='DPD'])[7]"]), 0, FailureHandling.STOP_ON_FAILURE)

//cek masuk sesuai bucket
WebUI.delay(10)
String norekPinjamanDunning = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[7]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
println("actual in kirim email: " +norekPinjamanDunning.trim() +" Expected: " + norekpinjaman.trim())

if (norekPinjamanDunning.trim() == norekpinjaman.trim()) {
	WebUI.executeJavaScript("alert('✅ Data sama: " + norekpinjaman.trim() + "')", null)
} else {
	WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + norekPinjamanDunning.trim() + " | Normal: " + norekpinjaman.trim() + "')", null)
}


WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[7]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)
WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD007-009-008']"]), 0, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@role='table'])[5]//button[@class='btn mr-1 btn-warning btn-sm'][1])[1]"]), FailureHandling.STOP_ON_FAILURE)

//get dunningcall history after submit
WebUI.delay(2)
String nilaiJanjiBayarRaw = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]), FailureHandling.STOP_ON_FAILURE)
String nilaiJanjiBayarDunning = cleanCurrency(nilaiJanjiBayarRaw)

if (nilaiJanjiBayarDunning.trim() == jumlahJanji.trim()) {
	WebUI.executeJavaScript("alert('✅ Data sama: " + jumlahJanji.trim() + "')", null)
} else {
	WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + nilaiJanjiBayarDunning.trim() + " | Normal: " + jumlahJanji.trim() + "')", null)
}

//close
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)


break
	
	case "Meninggalkan Pesan":
	//goto menu meninggalkan pesan
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': menuTaskList.find { it.key == "Meninggalkan Pesan" }?.value]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(4)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//input[@placeholder='DPD'])[4]"]), 0, FailureHandling.STOP_ON_FAILURE)
	
	//cek masuk sesuai bucket
	WebUI.delay(10)
	String norekPinjamanDunning = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[4]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	println("actual in kirim email: " +norekPinjamanDunning.trim() +" Expected: " + norekpinjaman.trim())
	
	if (norekPinjamanDunning.trim() == norekpinjaman.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + norekpinjaman.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + norekPinjamanDunning.trim() + " | Normal: " + norekpinjaman.trim() + "')", null)
	}
	
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[4]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD004-009-008']"]), 0, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@role='table'])[6]//button[@class='btn mr-1 btn-warning btn-sm'][1])[1]"]), FailureHandling.STOP_ON_FAILURE)
	
	//get dunningcall history after submit
	WebUI.delay(2)
	String nilaiJanjiBayarRaw = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]), FailureHandling.STOP_ON_FAILURE)
	String nilaiJanjiBayarDunning = cleanCurrency(nilaiJanjiBayarRaw)
	
	if (nilaiJanjiBayarDunning.trim() == jumlahJanji.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + jumlahJanji.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + nilaiJanjiBayarDunning.trim() + " | Normal: " + jumlahJanji.trim() + "')", null)
	}
	
	//close
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)
	
	
	break
	
	case "Tidak Terjawab":
	//goto menu tidak terjawab
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': menuTaskList.find { it.key == "Tidak Terjawab" }?.value]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(4)
WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//input[@placeholder='DPD'])[5]"]), 0, FailureHandling.STOP_ON_FAILURE)

//cek masuk sesuai bucket
WebUI.delay(10)
String norekPinjamanDunning = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[5]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
println("actual in kirim email: " +norekPinjamanDunning.trim() +" Expected: " + norekpinjaman.trim())

if (norekPinjamanDunning.trim() == norekpinjaman.trim()) {
	WebUI.executeJavaScript("alert('✅ Data sama: " + norekpinjaman.trim() + "')", null)
} else {
	WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + norekPinjamanDunning.trim() + " | Normal: " + norekpinjaman.trim() + "')", null)
}

WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[5]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)
WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD005-009-008']"]), 0, FailureHandling.STOP_ON_FAILURE)
WebUI.delay(1)
WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@role='table'])[5]//button[@class='btn mr-1 btn-warning btn-sm'][1])[1]"]), FailureHandling.STOP_ON_FAILURE)

//get dunningcall history after submit
WebUI.delay(2)
String nilaiJanjiBayarRaw = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]), FailureHandling.STOP_ON_FAILURE)
String nilaiJanjiBayarDunning = cleanCurrency(nilaiJanjiBayarRaw)

if (nilaiJanjiBayarDunning.trim() == jumlahJanji.trim()) {
	WebUI.executeJavaScript("alert('✅ Data sama: " + jumlahJanji.trim() + "')", null)
} else {
	WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + nilaiJanjiBayarDunning.trim() + " | Normal: " + jumlahJanji.trim() + "')", null)
}

//close
WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)


break
	
	case "Nada Sibuk":
	//goto menu nada sibuk
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': menuTaskList.find { it.key == "Nada Sibuk" }?.value]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(4)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//input[@placeholder='DPD'])[9]"]), 0, FailureHandling.STOP_ON_FAILURE)
	
	//cek masuk sesuai bucket
	WebUI.delay(10)
	String norekPinjamanDunning = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[9]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	println("actual in kirim email: " +norekPinjamanDunning.trim() +" Expected: " + norekpinjaman.trim())
	
	if (norekPinjamanDunning.trim() == norekpinjaman.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + norekpinjaman.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + norekPinjamanDunning.trim() + " | Normal: " + norekpinjaman.trim() + "')", null)
	}
	
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[9]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD016-009-008']"]), 0, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@role='table'])[5]//button[@class='btn mr-1 btn-warning btn-sm'][1])[1]"]), FailureHandling.STOP_ON_FAILURE)
	
	//get dunningcall history after submit
	WebUI.delay(2)
	String nilaiJanjiBayarRaw = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]), FailureHandling.STOP_ON_FAILURE)
	String nilaiJanjiBayarDunning = cleanCurrency(nilaiJanjiBayarRaw)
	
	if (nilaiJanjiBayarDunning.trim() == jumlahJanji.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + jumlahJanji.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + nilaiJanjiBayarDunning.trim() + " | Normal: " + jumlahJanji.trim() + "')", null)
	}
	
	//close
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)
	
	break
	
	
	case "No Telpon Salah":
	//goto menu telpon salah
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': menuTaskList.find { it.key == "Telepon Salah" }?.value]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(4)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//input[@placeholder='DPD'])[10]"]), 0, FailureHandling.STOP_ON_FAILURE)
	
	//cek masuk sesuai bucket
	WebUI.delay(10)
	String norekPinjamanDunning = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[10]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	println("actual in kirim email: " +norekPinjamanDunning.trim() +" Expected: " + norekpinjaman.trim())
	
	if (norekPinjamanDunning.trim() == norekpinjaman.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + norekpinjaman.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + norekPinjamanDunning.trim() + " | Normal: " + norekpinjaman.trim() + "')", null)
	}
	
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[10]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD017-009-008']"]), 0, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@role='table'])[5]//button[@class='btn mr-1 btn-warning btn-sm'][1])[1]"]), FailureHandling.STOP_ON_FAILURE)
	
	//get dunningcall history after submit
	WebUI.delay(2)
	String nilaiJanjiBayarRaw = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]), FailureHandling.STOP_ON_FAILURE)
	String nilaiJanjiBayarDunning = cleanCurrency(nilaiJanjiBayarRaw)
	
	if (nilaiJanjiBayarDunning.trim() == jumlahJanji.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + jumlahJanji.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + nilaiJanjiBayarDunning.trim() + " | Normal: " + jumlahJanji.trim() + "')", null)
	}
	
	//close
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)
	
	
	break
	
	case "Partial Payment":
	//goto menu partial payment
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': menuTaskList.find { it.key == "Partial Payment" }?.value]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(4)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//input[@placeholder='DPD'])[11]"]), 0, FailureHandling.STOP_ON_FAILURE)
	
	//cek masuk sesuai bucket
	WebUI.delay(10)
	String norekPinjamanDunning = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[11]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	println("actual in kirim email: " +norekPinjamanDunning.trim() +" Expected: " + norekpinjaman.trim())
	
	if (norekPinjamanDunning.trim() == norekpinjaman.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + norekpinjaman.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + norekPinjamanDunning.trim() + " | Normal: " + norekpinjaman.trim() + "')", null)
	}
	
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[11]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD019-009-008']"]), 0, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@role='table'])[5]//button[@class='btn mr-1 btn-warning btn-sm'][1])[1]"]), FailureHandling.STOP_ON_FAILURE)
	
	//get dunningcall history after submit
	WebUI.delay(2)
	String nilaiJanjiBayarRaw = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]), FailureHandling.STOP_ON_FAILURE)
	String nilaiJanjiBayarDunning = cleanCurrency(nilaiJanjiBayarRaw)
	
	if (nilaiJanjiBayarDunning.trim() == jumlahJanji.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + jumlahJanji.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + nilaiJanjiBayarDunning.trim() + " | Normal: " + jumlahJanji.trim() + "')", null)
	}
	
	//close
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)
	
	
	break
	
	case "Sudah Bayar":
	//goto menu sudah bayar
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': menuTaskList.find { it.key == "Sudah Bayar" }?.value]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(4)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//input[@placeholder='DPD'])[12]"]), 0, FailureHandling.STOP_ON_FAILURE)
	
	//cek masuk sesuai bucket
	WebUI.delay(10)
	String norekPinjamanDunning = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[12]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	println("actual in kirim email: " +norekPinjamanDunning.trim() +" Expected: " + norekpinjaman.trim())
	
	if (norekPinjamanDunning.trim() == norekpinjaman.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + norekpinjaman.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + norekPinjamanDunning.trim() + " | Normal: " + norekpinjaman.trim() + "')", null)
	}
	
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[12]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD025-009-008']"]), 0, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@role='table'])[5]//button[@class='btn mr-1 btn-warning btn-sm'][1])[1]"]), FailureHandling.STOP_ON_FAILURE)
	
	//get dunningcall history after submit
	WebUI.delay(2)
	String nilaiJanjiBayarRaw = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]), FailureHandling.STOP_ON_FAILURE)
	String nilaiJanjiBayarDunning = cleanCurrency(nilaiJanjiBayarRaw)
	
	if (nilaiJanjiBayarDunning.trim() == jumlahJanji.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + jumlahJanji.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + nilaiJanjiBayarDunning.trim() + " | Normal: " + jumlahJanji.trim() + "')", null)
	}
	
	//close
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)
	
	
	break
	
	case "Lainnya":
	//goto menu lainnya
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': menuTaskList.find { it.key == "Lainnya" }?.value]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(4)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "(//input[@placeholder='DPD'])[8]"]), 0, FailureHandling.STOP_ON_FAILURE)
	
	//cek masuk sesuai bucket
	WebUI.delay(10)
	String norekPinjamanDunning = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[8]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	println("actual in kirim email: " +norekPinjamanDunning.trim() +" Expected: " + norekpinjaman.trim())
	
	if (norekPinjamanDunning.trim() == norekpinjaman.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + norekpinjaman.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + norekPinjamanDunning.trim() + " | Normal: " + norekpinjaman.trim() + "')", null)
	}
	
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "(//table[@role='table'])[8]//tbody//tr[1]//td[@aria-colindex='3']//a"]), FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.scrollToElement(findTestObject('Object Repository/xpath', ['xpath': "//input[@id='FIELD008-009-008']"]), 0, FailureHandling.STOP_ON_FAILURE)
	WebUI.delay(1)
	WebUI.click(findTestObject('Object Repository/xpath',['xpath': "((//table[@role='table'])[5]//button[@class='btn mr-1 btn-warning btn-sm'][1])[1]"]), FailureHandling.STOP_ON_FAILURE)
	
	//get dunningcall history after submit
	WebUI.delay(2)
	String nilaiJanjiBayarRaw = WebUI.getText(findTestObject('Object Repository/xpath', ['xpath': "(//div[@class='p-0 border-bottom value col'])[43]"]), FailureHandling.STOP_ON_FAILURE)
	String nilaiJanjiBayarDunning = cleanCurrency(nilaiJanjiBayarRaw)
	
	if (nilaiJanjiBayarDunning.trim() == jumlahJanji.trim()) {
		WebUI.executeJavaScript("alert('✅ Data sama: " + jumlahJanji.trim() + "')", null)
	} else {
		WebUI.executeJavaScript("alert('❌ Data beda! Dunning: " + nilaiJanjiBayarDunning.trim() + " | Normal: " + jumlahJanji.trim() + "')", null)
	}
	
	//close
	WebUI.click(findTestObject('Object Repository/xpath', ['xpath': "//button[normalize-space(text())='Tutup']"]), FailureHandling.STOP_ON_FAILURE)
	
	
	break
	
default:
WebUI.comment("end")
}


}//end of file







































