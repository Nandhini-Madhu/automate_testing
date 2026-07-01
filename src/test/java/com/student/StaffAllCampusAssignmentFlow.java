package com.student;

import java.io.File;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
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

/**
 * Flow:
 *  1. Login as placement admin
 *  2. Open "All Campus Assignment" report
 *  3. Click the "Hashing level 1" assignment card
 *  4. Click Download -> choose CSV
 *  5. Click "View Not Attended"
 *  6. Click Download -> choose CSV
 */
public class StaffAllCampusAssignmentFlow {

    static final String LOGIN_URL = "https://campushub.thebettertomorrow.in/login";
    static final String EMAIL     = "placement@acetcbe.edu.in";
    static final String PASSWORD  = "acetcbe#123";

    static final String ASSIGNMENT_CARD_TEXT = "Hashing level 1";

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    Actions actions;

    /**
     * Dispatches a full native-style mouse event sequence (pointerdown ->
     * mousedown -> mouseup -> click) instead of a plain element.click().
     * Several widgets on this site (react-select, dropdown menus, reactour)
     * attach handlers to mousedown/pointerdown rather than click, so a bare
     * .click() call can silently do nothing even though it "succeeds".
     */
    private static final String FULL_CLICK_JS =
        "var el = arguments[0];" +
        "el.scrollIntoView({block:'center'});" +
        "function fire(type){" +
        "  var evt = new MouseEvent(type, {bubbles:true, cancelable:true, view:window});" +
        "  el.dispatchEvent(evt);" +
        "}" +
        "try { el.dispatchEvent(new PointerEvent('pointerdown', {bubbles:true})); } catch(e) {}" +
        "fire('mousedown');" +
        "fire('mouseup');" +
        "fire('click');";

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.setExperimentalOption("prefs", java.util.Map.of(
            "download.default_directory", System.getProperty("user.home") + File.separator + "Downloads",
            "download.prompt_for_download", false,
            "plugins.always_open_pdf_externally", true
        ));

        driver  = new ChromeDriver(options);
        wait    = new WebDriverWait(driver, Duration.ofSeconds(40));
        js      = (JavascriptExecutor) driver;
        actions = new Actions(driver);
        System.out.println("Chrome browser opened");
    }

    @Test
    public void allCampusAssignmentFlow() throws Exception {

        doLogin();
        openAllCampusAssignment();
        openAssignmentCard(ASSIGNMENT_CARD_TEXT);
        downloadFormat("CSV");
        clickViewNotAttended();
        downloadFormat("CSV");

        System.out.println("=== All Campus Assignment flow completed! Final URL: "
            + driver.getCurrentUrl() + " ===");
    }

    private void doLogin() throws Exception {
        System.out.println(">>> [STEP 1] Logging in...");
        driver.get(LOGIN_URL);
        sleep(2000);

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("login-email")));
        emailField.clear();
        emailField.sendKeys(EMAIL);
        sleep(300);

        driver.findElement(By.id("login-password")).sendKeys(PASSWORD);
        sleep(300);

        jsClick(wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Sign in')]"))));
        System.out.println("    Signed in as " + EMAIL);
        sleep(4000);
    }

    /**
     * Navigates to the "All Campus Assignment" report via the sidebar icon
     * link. This link is icon-only (no visible text -- labelled only via
     * aria-labelledby pointing at a flowbite tooltip), and its exact href
     * is "/admin/reports/assignments" (confirmed via DevTools inspection),
     * so we match on that href directly rather than guessing at link text.
     * Falls back to a broader href-contains match and finally a generic
     * text search, in case the exact path changes.
     */
    private void openAllCampusAssignment() throws Exception {
        System.out.println(">>> [STEP 2] Opening 'All Campus Assignment'...");

        waitAndDismissTourIfPresent();

        WebElement reportsLink = null;

        List<WebElement> exactHref = driver.findElements(
            By.cssSelector("a[href='/admin/reports/assignments']"));
        if (!exactHref.isEmpty()) {
            reportsLink = exactHref.get(0);
            System.out.println("    Found sidebar link via exact href '/admin/reports/assignments'");
        }

        if (reportsLink == null) {
            List<WebElement> hrefContains = driver.findElements(
                By.cssSelector("a[href*='/admin/reports/assignments']"));
            if (!hrefContains.isEmpty()) {
                reportsLink = hrefContains.get(0);
                System.out.println("    Found sidebar link via href containing '/admin/reports/assignments'");
            }
        }

        if (reportsLink == null) {
            List<WebElement> textLink = driver.findElements(
                By.xpath("//*[self::a or self::button][contains(translate(normalize-space(.),"
                    + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'all campus assignment')]"));
            if (!textLink.isEmpty()) {
                reportsLink = textLink.get(0);
                System.out.println("    Found sidebar link via text match fallback");
            }
        }

        if (reportsLink == null) {
            System.out.println("    WARNING: could not locate the assignment reports sidebar link at all");
            return;
        }

        // Wait for it to actually be clickable before clicking, since sidebar
        // icons can render slightly after the rest of the page.
        wait.until(ExpectedConditions.elementToBeClickable(reportsLink));
        jsClick(reportsLink);
        System.out.println("    Clicked assignment reports sidebar icon");

        sleep(3000);
        waitAndDismissTourIfPresent();
        System.out.println("    Navigated to: " + driver.getCurrentUrl());
    }

    /**
     * Clicks the assignment card matching the given visible text
     * (e.g. "Hashing level 1").
     */
    private void openAssignmentCard(String cardText) throws Exception {
        System.out.println(">>> [STEP 3] Opening assignment card: '" + cardText + "'...");

        dismissTourIfPresent();

        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[contains(normalize-space(text()),'" + cardText + "')]")));
        jsClick(card);
        System.out.println("    Clicked assignment card");
        sleep(2500);

        waitAndDismissTourIfPresent();
        System.out.println("    Card opened, URL: " + driver.getCurrentUrl());
    }

    /**
     * Clicks the Download icon button, waits for the "Choose format" panel
     * to appear, then clicks the option matching the given format
     * ("CSV" or "PDF"). Some pages (e.g. the main assignment view) show
     * both CSV and PDF; the "Not Attended" page only shows CSV -- this
     * works for either case since it just looks for the matching label.
     */
    private void downloadFormat(String format) throws Exception {
        System.out.println(">>> [STEP] Clicking Download and choosing " + format + "...");

        dismissTourIfPresent();

        WebElement downloadButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@aria-label='Download' or @title='Download']")));
        jsClick(downloadButton);
        System.out.println("    Clicked Download");
        sleep(1000);

        // Wait for the "Choose format" panel to appear
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[normalize-space(text())='Choose format']")));
            System.out.println("    'Choose format' panel visible");
        } catch (TimeoutException e) {
            System.out.println("    WARNING: 'Choose format' panel did not appear");
        }

        sleep(300);

        List<WebElement> formatOptions = driver.findElements(
            By.xpath("//button[contains(translate(normalize-space(.),"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'"
                + format.toLowerCase() + "')]"));

        WebElement target = null;
        for (WebElement opt : formatOptions) {
            if (opt.isDisplayed()) {
                target = opt;
                break;
            }
        }

        if (target != null) {
            jsClick(target);
            System.out.println("    Selected '" + format + "'");
        } else {
            System.out.println("    WARNING: '" + format + "' option not found in Choose format panel");
        }

        sleep(2000);
    }

    private void clickViewNotAttended() throws Exception {
        System.out.println(">>> [STEP] Clicking 'View Not Attended'...");

        dismissTourIfPresent();

        WebElement viewNotAttended = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'View Not Attended')]")));
        jsClick(viewNotAttended);
        System.out.println("    Clicked 'View Not Attended'");
        sleep(3000);

        waitAndDismissTourIfPresent();
        System.out.println("    URL now: " + driver.getCurrentUrl());
    }

    /**
     * Waits briefly for the reactour overlay to appear (it can render with
     * a short delay after navigation or clicks) and dismisses it if it
     * shows up. Safe to call even if the tour never appears.
     */
    private void waitAndDismissTourIfPresent() throws Exception {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(4))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("__reactour")));
            System.out.println("    Tour overlay appeared");
        } catch (TimeoutException e) {
            return;
        }
        dismissTourIfPresent();
    }

    /**
     * Dismisses the reactour guided-tour overlay if present. Loops up to 5
     * times, re-checking visibility each time, and tries close-button ->
     * "Next" button -> ESCAPE key as a fallback, since the close button
     * can require a full mousedown/mouseup/click sequence rather than a
     * plain click to register.
     */
    private void dismissTourIfPresent() throws Exception {
        for (int i = 0; i < 5; i++) {
            List<WebElement> tourRoot = driver.findElements(By.id("__reactour"));
            if (tourRoot.isEmpty() || !tourRoot.get(0).isDisplayed()) {
                return;
            }

            System.out.println("    Tour overlay detected -- dismissing (loop " + (i + 1) + ")");

            List<WebElement> closeButtons = driver.findElements(
                By.cssSelector("button[class*='reactour__close'], button[aria-label='Close']"));
            if (!closeButtons.isEmpty() && closeButtons.get(0).isDisplayed()) {
                jsClick(closeButtons.get(0));
                sleep(600);
                continue;
            }

            List<WebElement> nextButtons = driver.findElements(
                By.xpath("//button[contains(.,'Next')]"));
            if (!nextButtons.isEmpty() && nextButtons.get(0).isDisplayed()) {
                jsClick(nextButtons.get(0));
                sleep(600);
                continue;
            }

            actions.sendKeys(Keys.ESCAPE).perform();
            sleep(600);
        }
    }

    private void jsClick(WebElement el) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        js.executeScript(FULL_CLICK_JS, el);
    }

    private void sleep(int ms) throws Exception {
        Thread.sleep(ms);
    }

    @AfterTest
    public void closeBrowser() {
        if (driver != null) driver.quit();
    }
}