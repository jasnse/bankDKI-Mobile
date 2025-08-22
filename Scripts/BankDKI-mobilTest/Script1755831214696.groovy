import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import java.util.concurrent.locks.Condition

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
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.testobject.ConditionType

// Start App
String apkPath = "C:\\2025\\Automation\\app-DKI-v12-1.apk"
Mobile.startApplication(apkPath, false)



//input username
TestObject usernameObj = new TestObject()
usernameObj.addProperty('xpath', ConditionType.EQUALS, "(//android.widget.EditText)[1]")
Mobile.setText(usernameObj, "RMDKS002_KONSUMERFC", 10)

//input password
TestObject passObj = new TestObject()
passObj.addProperty('xpath', ConditionType.EQUALS, "(//android.widget.EditText)[2]")
Mobile.setText(passObj, "123456", 10)

Mobile.delay(2)

//masuk
// TestObject pertama (Button 1) berdasarkan XPath pertama
TestObject btnMasuk1 = new TestObject()
btnMasuk1.addProperty('xpath', ConditionType.EQUALS, "//hierarchy/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.view.View[1]/android.view.View[1]/android.view.View[1]/android.view.View[1]/android.widget.Button[1]")

// TestObject kedua (Button 2) berdasarkan XPath kedua
TestObject btnMasuk2 = new TestObject()
btnMasuk2.addProperty('xpath', ConditionType.EQUALS, "//hierarchy/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.view.View[1]/android.view.View[1]/android.view.View[1]/android.view.View[1]/android.widget.Button[1]")

// --- 1. Cek apakah btnMasuk1 ada ---
boolean isBtnMasuk1Visible = Mobile.verifyElementVisible(btnMasuk1, 5)  // Timeout 5 detik

if (isBtnMasuk1Visible) {
	// --- 2. Jika Button 1 terlihat, tap Button 1 ---
	Mobile.tap(btnMasuk1, 10)  // Timeout 10 detik
} else {
	// --- 3. Jika Button 1 tidak terlihat, coba Button 2 ---
	boolean isBtnMasuk2Visible = Mobile.verifyElementVisible(btnMasuk2, 5)  // Timeout 5 detik
	if (isBtnMasuk2Visible) {
		Mobile.tap(btnMasuk2, 10)  // Timeout 10 detik
	} else {
		// --- 4. Jika kedua tombol tidak ada, lakukan scroll untuk mencari tombol ---
		Mobile.scrollToText('Login')  // Scroll mencari teks "Login"
		// Ulangi cek setelah scroll
		isBtnMasuk2Visible = Mobile.verifyElementVisible(btnMasuk2, 5)  // Timeout 5 detik
		if (isBtnMasuk2Visible) {
			Mobile.tap(btnMasuk2, 10)  // Timeout 10 detik
		} else {
			println("Tombol tidak ditemukan!")
		}
	}
}

// --- 5. Delay untuk memastikan aplikasi siap ---
Mobile.delay(5)

TestObject tasklistMenu = new TestObject()
tasklistMenu.addProperty('xpath', ConditionType.EQUALS, "//android.widget.ImageView[2]")
Mobile.tap(tasklistMenu, 10)
