package com.student;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Student_Assignment_Test {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    Actions actions;

    public Student_Assignment_Test() {
    }

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        js = (JavascriptExecutor) driver;
        actions = new Actions(driver);
        System.out.println("Browser setup complete");
    }

    @AfterTest
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed");
        }
    }

    private void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    private void sleep(int millis) throws Exception {
        Thread.sleep(millis);
    }

    private void doLogin() throws Exception {
        System.out.println(">>> [STEP 1] Logging in...");
        driver.get("https://beta.campushub.thebettertomorrow.in/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-email")))
                .sendKeys("beta.bt.better.044@bt.in");
        driver.findElement(By.id("login-password")).sendKeys("Test@123");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Sign in')]"))).click();
        wait.until(ExpectedConditions.urlContains("/student"));
        sleep(2000);
        System.out.println("    Login successful");
    }

    private void navigateToAssignments() throws Exception {
        System.out.println(">>> [STEP 2] Navigating to Assignments...");
        WebElement assignmentsMenu = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'/assignments')]")
        ));
        jsClick(assignmentsMenu);
        wait.until(ExpectedConditions.urlContains("/assignments"));
        sleep(2000);
        System.out.println("    Navigated to Assignments page");
    }

    private void openAssignmentCard() throws Exception {
        System.out.println(">>> [STEP 3] Opening assignment card...");
        WebElement assignmentCard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[contains(text(),'bulk delete Assignment')]")
        ));
        jsClick(assignmentCard);
        sleep(2000);
        System.out.println("    Assignment card opened");
    }

    private void clickStartNow() throws Exception {
        System.out.println(">>> [STEP 4] Clicking Start Now...");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Start Now')]")
        )).click();
        sleep(2000);
        System.out.println("    Start Now clicked");
    }

    private void clickNext() throws Exception {
        System.out.println(">>> [STEP 5] Clicking Next...");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Next')]")
        )).click();
        sleep(2000);
        System.out.println("    Next clicked");
    }

    private void confirmNotificationOff() throws Exception {
        System.out.println(">>> [STEP 6] Confirming notification turned off...");
        WebElement notifInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[@placeholder='I TURNED OFF NOTIFICATION']")
        ));
        notifInput.click();
        sleep(300);
        notifInput.sendKeys("I TURNED OFF NOTIFICATION");
        sleep(1500);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Confirmed')]")
        ));
        sleep(1000);
        System.out.println("    Notification confirmation done");
    }

    private void clickStartTest() throws Exception {
        System.out.println(">>> [STEP 7] Clicking Start Test...");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Start Test')]")
        )).click();
        sleep(3000);
        System.out.println("    Start Test clicked");
    }

    private void handleFullscreenIfPresent() throws Exception {
        try {
            WebElement fullscreenText = driver.findElement(
                By.xpath("//*[contains(text(),'Please switch to Fullscreen')" +
                         " or contains(text(),'Fullscreen to continue')" +
                         " or contains(text(),'Full Screen to continue')]")
            );
            if (fullscreenText.isDisplayed()) {
                System.out.println("    [POPUP] Fullscreen prompt detected");
                try {
                    WebElement closeX = driver.findElement(
                        By.xpath("//button[normalize-space()='×' or normalize-space()='✕']")
                    );
                    jsClick(closeX);
                    sleep(500);
                } catch (Exception ex) {
                }
                js.executeScript("try{document.documentElement.requestFullscreen();}catch(e){}");
                sleep(1000);
                WebElement fullBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[normalize-space()='Full Screen']" +
                                 " or normalize-space()='Full Screen']")
                ));
                jsClick(fullBtn);
                sleep(2000);
                System.out.println("    [POPUP] Fullscreen handled");
            }
        } catch (Exception e) {
            System.out.println("    [POPUP] No fullscreen prompt");
        }
    }

    private void handleTabSwitchPopupIfPresent() throws Exception {
        try {
            WebElement popup = driver.findElement(
                By.xpath("//*[contains(text(),'Tab Switch')" +
                         " or contains(text(),'Focus lost')]")
            );
            if (popup.isDisplayed()) {
                System.out.println("    [POPUP] Tab switch warning detected");
                try {
                    WebElement closeX = driver.findElement(
                        By.xpath("//button[@aria-label='Close']" +
                                 " | //button[normalize-space()='×']")
                    );
                    jsClick(closeX);
                } catch (Exception e) {
                    actions.sendKeys(Keys.ESCAPE).perform();
                }
                sleep(1000);
                System.out.println("    [POPUP] Tab switch popup dismissed");
            }
        } catch (Exception e) {
            System.out.println("    [POPUP] No tab switch popup");
        }
    }

    private void handleBotDetectionPopupIfPresent() throws Exception {
        try {
            WebElement botPopup = driver.findElement(
                By.xpath("//*[contains(text(),'Only the bot can complete')" +
                         " or contains(text(),'activity is considered malpractice')]")
            );
            if (botPopup.isDisplayed()) {
                System.out.println("    [POPUP] Bot detection popup detected — clicking Continue");
                WebElement continueBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[normalize-space()='Continue']")
                ));
                jsClick(continueBtn);
                sleep(1500);
                System.out.println("    [POPUP] Bot detection popup dismissed via Continue");
            }
        } catch (Exception e) {
            System.out.println("    [POPUP] No bot detection popup");
        }
    }

    private void handleNetworkRetryIfPresent() throws Exception {
        try {
            WebElement retryBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Retry']")
                ));
            if (retryBtn.isDisplayed()) {
                System.out.println("    [POPUP] Network/Action Required popup — clicking Retry");
                jsClick(retryBtn);
                sleep(3000);
                wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Begin test'" +
                             " or normalize-space()='Begin Test']")
                ));
                System.out.println("    [POPUP] Retry successful — Begin test is now enabled");
            }
        } catch (Exception e) {
            System.out.println("    [POPUP] No network retry needed");
        }
    }

    private void handleOkPopupIfPresent() throws Exception {
        try {
            WebElement okBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='OK'" +
                             " or normalize-space()='Ok']")
                ));
            if (okBtn.isDisplayed()) {
                System.out.println("    [POPUP] OK popup detected — clicking OK");
                jsClick(okBtn);
                sleep(800);
                System.out.println("    [POPUP] OK popup dismissed");
            }
        } catch (Exception e) {
            System.out.println("    [POPUP] No OK popup");
        }
    }

    private void handleSubmitTestModalIfPresent() throws Exception {
        try {
            WebElement submitTestBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Submit test'" +
                             " or normalize-space()='Submit Test']")
                ));
            if (submitTestBtn.isDisplayed()) {
                System.out.println("    [POPUP] Submit Test modal detected — confirming");
                jsClick(submitTestBtn);
                sleep(2000);
                System.out.println("    [POPUP] Submit Test modal confirmed");
            }
        } catch (Exception e) {
            System.out.println("    [POPUP] No Submit Test modal");
        }
    }

    private void handleEndTestModalIfPresent() throws Exception {
        try {
            WebElement endSubmitBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Submit test'" +
                             " or normalize-space()='Submit Test']")
                ));
            if (endSubmitBtn.isDisplayed()) {
                System.out.println("    [POPUP] End Test confirmation modal — submitting");
                jsClick(endSubmitBtn);
                sleep(2000);
                System.out.println("    [POPUP] End Test submitted");
            }
        } catch (Exception e) {
            System.out.println("    [POPUP] No End Test modal");
        }
    }

    private void handleAllPopups() throws Exception {
        System.out.println("  [POPUPS] Checking all popups...");
        handleFullscreenIfPresent();
        sleep(500);
        handleTabSwitchPopupIfPresent();
        sleep(500);
        handleBotDetectionPopupIfPresent();
        sleep(500);
        handleNetworkRetryIfPresent();
        sleep(500);
        handleOkPopupIfPresent();
        sleep(300);
        System.out.println("  [POPUPS] All popup checks done");
    }

    private void openQuestion() throws Exception {
        System.out.println(">>> [STEP 8] Opening question card...");
        handleAllPopups();
        WebElement questionCard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//h3[contains(text(),'Largest Element in an Array')]")
        ));
        jsClick(questionCard);
        sleep(3000);
        handleAllPopups();
        System.out.println("    Question card opened");
    }

    private void resetCodeEditor() throws Exception {
        System.out.println("    Resetting code editor...");
        try {
            WebElement resetBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Reset Code']")
                ));
            jsClick(resetBtn);
            sleep(1500);
            try {
                WebElement confirmBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(.,'Reset')" +
                                 " or contains(.,'Confirm')" +
                                 " or contains(.,'Yes')]")
                    ));
                jsClick(confirmBtn);
                sleep(1000);
                System.out.println("    Reset confirmed");
            } catch (Exception e) {
                System.out.println("    No reset confirmation popup");
            }
        } catch (Exception e) {
            System.out.println("    Reset Code button not found: " + e.getMessage());
        }
        sleep(1500);
    }

    private void typeCodeInEditor(String code) throws Exception {
        System.out.println("    Typing code into editor...");
        WebElement cmContent = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'cm-content') and @contenteditable='true']")
        ));
        jsClick(cmContent);
        sleep(700);
        js.executeScript(
            "var editor = document.querySelector('.cm-content');" +
            "editor.focus();" +
            "document.execCommand('selectAll', false, null);" +
            "document.execCommand('insertText', false, arguments[0]);",
            code);
        sleep(1500);
        System.out.println("    Code injected successfully");
    }

    private void injectCode() throws Exception {
        System.out.println(">>> [STEP 9] Injecting solution code...");
        sleep(2000);

        String code =
            "import java.util.Scanner;\n\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        Scanner sc = new Scanner(System.in);\n" +
            "        int n = sc.nextInt();\n" +
            "        int[] arr = new int[n];\n" +
            "        for (int i = 0; i < n; i++) {\n" +
            "            arr[i] = sc.nextInt();\n" +
            "        }\n" +
            "        int max = arr[0];\n" +
            "        for (int i = 1; i < n; i++) {\n" +
            "            if (arr[i] > max) {\n" +
            "                max = arr[i];\n" +
            "            }\n" +
            "        }\n" +
            "        System.out.println(max);\n" +
            "    }\n" +
            "}";

        resetCodeEditor();
        typeCodeInEditor(code);
        System.out.println("    Code injection complete");
    }

    private void submitCode() throws Exception {
        System.out.println(">>> [STEP 10] Submitting code...");
        WebElement submitBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                            "//button[.//span[normalize-space()='SUBMIT']]" +
                            " | //button[normalize-space()='SUBMIT']" +
                            " | //button[contains(.,'SUBMIT') and not(contains(.,'RUN'))]"
                )));
        jsClick(submitBtn);
        sleep(2000);
        handleAllPopups();
        handleSubmitTestModalIfPresent();
        handleAllPopups();
        System.out.println("    Code submitted");
    }

    private void goBackToQuestionList() throws Exception {
        System.out.println(">>> [STEP 11] Going back to question list...");
        try {
            WebElement goBack = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath(
                        "//a[contains(normalize-space(),'Go Back')]" +
                        " | //button[contains(normalize-space(),'Go Back')]" +
                        " | //*[contains(@class,'go-back')]"
                )));
            jsClick(goBack);
            sleep(2000);
            System.out.println("    Go Back clicked");
        } catch (Exception e) {
            System.out.println("    Go Back not found — using browser back");
            driver.navigate().back();
            sleep(2000);
        }
        handleAllPopups();
        sleep(1500);
    }

    private void clickEndTest() throws Exception {
        System.out.println(">>> [STEP 12] Clicking End Test button on page...");
        WebElement endTestBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath(
                        "//button[normalize-space()='End Test']" +
                        " | //button[contains(.,'End Test')]"
                )));
        jsClick(endTestBtn);
        sleep(2000);
        System.out.println("    End Test button clicked");
    }

    private void confirmEndTestSubmission() throws Exception {

        System.out.println(">>> [STEP 13] Handling End Test flow...");
        System.out.println("    Waiting for confirmation Submit Test modal...");

        WebElement submitTestBtn = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath(
                    "//button[normalize-space()='Submit test']" +
                    " | //button[normalize-space()='Submit Test']"
                )));

        jsClick(submitTestBtn);
        System.out.println("    Confirmation modal Submit test clicked");
        sleep(3000);

        try {
            WebElement botPopup = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(
                        "//*[contains(text(),'Only the bot can complete')" +
                        " or contains(text(),'activity is considered malpractice')]"
                    )));

            System.out.println("    Bot modal detected");

            WebElement botEndTestBtn = botPopup.findElement(
                By.xpath(
                    "./ancestor::div[contains(@class,'fixed') or contains(@role,'dialog')]" +
                    "//button[normalize-space()='End Test']"
                ));

            jsClick(botEndTestBtn);
            System.out.println("    Bot modal End Test clicked");
            sleep(4000);

        } catch (Exception e) {
            System.out.println("    Bot modal not shown");
        }

        try {
            WebElement okBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(
                    By.xpath(
                        "//button[normalize-space()='OK']" +
                        " | //button[normalize-space()='Ok']"
                    )));

            jsClick(okBtn);
            System.out.println("    Success OK clicked");
            sleep(2000);
        } catch (Exception e) {
            System.out.println("    OK popup not shown");
        }

        System.out.println(">>> End Test flow completed");
    }

    private void exitFullscreen() throws Exception {
        System.out.println(">>> [STEP 14] Exiting fullscreen...");
        try {
            js.executeScript("if(document.exitFullscreen) document.exitFullscreen();");
            System.out.println("    Fullscreen exited");
        } catch (Exception e) {
            System.out.println("    Fullscreen exit skipped: " + e.getMessage());
        }
        sleep(2000);
    }

    @Test
    public void studentAssignmentTest() throws Exception {

        doLogin();
        navigateToAssignments();
        openAssignmentCard();
        clickStartNow();
        clickNext();
        confirmNotificationOff();
        clickStartTest();
        handleAllPopups();

        openQuestion();
        injectCode();
        submitCode();

        goBackToQuestionList();

        clickEndTest();
        confirmEndTestSubmission();

        exitFullscreen();

        System.out.println("=== ALL DONE! Final URL: " + driver.getCurrentUrl() + " ===");
    }
}