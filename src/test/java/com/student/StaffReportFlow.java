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
 *  2. Go to "All Student Course" -> open ACE cluster -> "Assignment Report" tab
 *  3. Change date filter to "Last 6 Months"
 *  4. Click the "C - Assessment Day 12" assignment card
 *  5. Click Download
 *  6. Search for "P .Nanthini"
 *  7. Click "View Not Attended"
 *  8. Click Download on the not-attended list
 */
public class StaffReportFlow {

    static final String LOGIN_URL = "https://campushub.thebettertomorrow.in/login";
    static final String EMAIL     = "placement@acetcbe.edu.in";
    static final String PASSWORD  = "acetcbe#123";

    static final String SEARCH_TERM = "P .Nanthini";

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    Actions actions;

    /**
     * Dispatches a full native-style mouse event sequence (pointerdown ->
     * mousedown -> mouseup -> click) on the target element, instead of a
     * plain element.click(). Several third-party widgets used on this site
     * (react-select options, the reactour close button) attach their
     * handlers to mousedown/pointerdown rather than click, so a bare
     * .click() call can silently do nothing even though it "succeeds".
     * This is used in place of the old jsClick everywhere in this file.
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
    public void assignmentReportFlow() throws Exception {

        // 1. Login
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
        System.out.println("Signed in as " + EMAIL);
        sleep(4000);

        // 2. Click the "Assignment Report" sidebar link
        WebElement assignmentReportLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a[href='/admin/assignment-report']")));
        jsClick(assignmentReportLink);
        System.out.println("Opened Assignment Report page");
        sleep(3000);

        // 2b. Tour can render with a short delay after navigation.
        waitAndDismissTourIfPresent();

        // 3. Change date filter to "Last 6 Months"
        selectDateFilter("Last 6 Months");

        dismissTourIfPresent();

        // 4. Click the "C - Assessment Day 12" assignment card
        WebElement assessmentCard = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[contains(text(),'C - Assessment Day 12')]")));
        jsClick(assessmentCard);
        System.out.println("Opened 'C - Assessment Day 12'");
        sleep(3000);

        waitAndDismissTourIfPresent();

        // 5. Click Download (the icon button next to "View Not Attended"), then CSV
        WebElement downloadButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@aria-label='Download' or @title='Download']")));
        jsClick(downloadButton);
        System.out.println("Clicked Download");
        sleep(1000);

        List<WebElement> csvOption = driver.findElements(
            By.xpath("//*[normalize-space(text())='CSV']"));
        if (!csvOption.isEmpty()) {
            jsClick(csvOption.get(csvOption.size() - 1));
            System.out.println("Clicked CSV");
        } else {
            System.out.println("WARNING: CSV option not found after clicking Download");
        }
        sleep(2000);

        dismissTourIfPresent();

        // 6. Search for the student
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[contains(@placeholder,'Search by name or roll number')]")));
        searchInput.clear();
        searchInput.sendKeys(SEARCH_TERM);
        System.out.println("Searched for: " + SEARCH_TERM);
        sleep(1500);

        // 7. Click "View Not Attended"
        WebElement viewNotAttended = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'View Not Attended')]")));
        jsClick(viewNotAttended);
        System.out.println("Clicked 'View Not Attended'");
        sleep(3000);

        waitAndDismissTourIfPresent();

        // 8. Click Download on the "Students Not Attended" page, then CSV
        WebElement notAttendedDownload = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@aria-label='Download' or @title='Download']")));
        jsClick(notAttendedDownload);
        System.out.println("Clicked Download on Students Not Attended page");
        sleep(1000);

        List<WebElement> notAttendedCsvOption = driver.findElements(
            By.xpath("//*[normalize-space(text())='CSV']"));
        if (!notAttendedCsvOption.isEmpty()) {
            jsClick(notAttendedCsvOption.get(notAttendedCsvOption.size() - 1));
            System.out.println("Clicked CSV on Students Not Attended page");
        } else {
            System.out.println("WARNING: CSV option not found on Students Not Attended page");
        }
        sleep(2000);

        System.out.println("Assignment report flow completed successfully");
    }

    /**
     * Opens the react-select "Date filter" dropdown inside the
     * data-tour="filter-dropdown" wrapper and selects the option matching
     * the given label (case-insensitive).
     *
     * Dismisses the tour overlay defensively before opening the dropdown
     * (it can reappear here), waits explicitly for option elements to
     * appear in the DOM rather than relying on a fixed sleep, logs what it
     * finds for debugging, falls back to a generic text search if the
     * option list still isn't found, and finally verifies the control's
     * displayed text afterwards -- retrying (up to 3 attempts total) if
     * the wrong option ended up applied or nothing changed at all, which
     * usually means either the tour intercepted the click or react-select
     * ignored a plain click event.
     */
    private void selectDateFilter(String optionLabel) throws Exception {
        boolean applied = false;

        for (int attempt = 1; attempt <= 3 && !applied; attempt++) {

            dismissTourIfPresent();

            WebElement dateFilterControl = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[data-tour='filter-dropdown'] div[class*='-control']")));

            jsClick(dateFilterControl);
            System.out.println("Clicked date filter dropdown (attempt " + attempt + ")");

            dismissTourIfPresent();

            // Wait until at least one option element actually appears in the DOM
            boolean menuOpened = true;
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div[id*='-option-']")));
                System.out.println("Dropdown menu opened, options visible");
            } catch (TimeoutException e) {
                System.out.println("WARNING: dropdown menu did not open after click");
                menuOpened = false;
            }

            if (!menuOpened) {
                // Retry the click once more before giving up this attempt --
                // sometimes the first click only focuses the control.
                jsClick(dateFilterControl);
                sleep(500);
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div[id*='-option-']")));
                    System.out.println("Dropdown menu opened on second click");
                } catch (TimeoutException e) {
                    System.out.println("WARNING: dropdown still did not open, moving to next attempt");
                    continue;
                }
            }

            sleep(300);

            // Primary strategy: react-select option elements (id contains "-option-")
            List<WebElement> reactSelectOptions = driver.findElements(
                By.cssSelector("div[id*='-option-']"));
            System.out.println("Found " + reactSelectOptions.size() + " option elements");

            WebElement match = null;
            for (WebElement opt : reactSelectOptions) {
                String text = opt.getText().trim();
                System.out.println("Option found: '" + text + "'");
                if (text.equalsIgnoreCase(optionLabel)) {
                    match = opt;
                    break;
                }
            }

            if (match != null) {
                jsClick(match);
                System.out.println("Selected '" + optionLabel + "' via react-select option id");
            } else {
                // Fallback strategy: case-insensitive text search across the whole page
                List<WebElement> textMatches = driver.findElements(
                    By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),'" + optionLabel.toLowerCase() + "')]"));

                if (!textMatches.isEmpty()) {
                    jsClick(textMatches.get(textMatches.size() - 1));
                    System.out.println("Selected '" + optionLabel + "' via text-match fallback");
                } else {
                    System.out.println("WARNING: '" + optionLabel + "' option not found by either strategy");
                }
            }

            sleep(1500);
            dismissTourIfPresent();

            // Verify the control now actually shows the selected label
            try {
                WebElement control = driver.findElement(
                    By.cssSelector("div[data-tour='filter-dropdown'] div[class*='-control']"));
                String appliedText = control.getText().trim();
                System.out.println("Date filter control now shows: '" + appliedText + "'");
                if (appliedText.equalsIgnoreCase(optionLabel)) {
                    applied = true;
                } else {
                    System.out.println("WARNING: expected '" + optionLabel
                        + "' but got '" + appliedText + "' -- retrying");
                }
            } catch (Exception e) {
                System.out.println("Could not read date filter control text: " + e.getMessage());
            }
        }

        if (!applied) {
            System.out.println("WARNING: could not confirm '" + optionLabel
                + "' was applied after 3 attempts, proceeding anyway");
        }
    }

    /**
     * Waits briefly for the reactour overlay to appear (it can render with
     * a short delay after navigation or clicks) and dismisses it if it
     * shows up. Safe to call even if the tour never appears -- it simply
     * times out quietly after a short wait.
     */
    private void waitAndDismissTourIfPresent() throws Exception {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("__reactour")));
            System.out.println("Tour overlay appeared");
        } catch (TimeoutException e) {
            System.out.println("No tour overlay appeared");
            return;
        }

        dismissTourIfPresent();
    }

    /**
     * The first time certain admin pages are visited (and sometimes again
     * after a filter change or card click triggers a fresh render), a
     * guided tour (reactour) overlay appears with "Back"/"Next" buttons
     * and a close ("X") button. It sits on top of the page and silently
     * intercepts clicks on elements underneath it.
     *
     * This loops (up to 5 iterations) rather than trying once, because a
     * single dismissal attempt was previously found to be unreliable --
     * the close button often listens for mousedown rather than click, so
     * a bare element.click() can appear to succeed while doing nothing.
     * Each iteration re-checks whether #__reactour is still visible before
     * trying again, so it naturally stops as soon as the tour is gone.
     */
    private void dismissTourIfPresent() throws Exception {
        for (int i = 0; i < 5; i++) {
            List<WebElement> tourRoot = driver.findElements(By.id("__reactour"));
            if (tourRoot.isEmpty() || !tourRoot.get(0).isDisplayed()) {
                return;
            }

            System.out.println("Tour overlay detected -- attempting to dismiss (loop " + (i + 1) + ")");

            List<WebElement> closeButtons = driver.findElements(
                By.cssSelector("button[class*='reactour__close'], button[aria-label='Close']"));
            if (!closeButtons.isEmpty() && closeButtons.get(0).isDisplayed()) {
                jsClick(closeButtons.get(0));
                System.out.println("Clicked tour close button");
                sleep(600);
                continue;
            }

            List<WebElement> nextButtons = driver.findElements(
                By.xpath("//button[contains(.,'Next')]"));
            if (!nextButtons.isEmpty() && nextButtons.get(0).isDisplayed()) {
                jsClick(nextButtons.get(0));
                System.out.println("Clicked tour 'Next'");
                sleep(600);
                continue;
            }

            // Last resort: press Escape, which reactour also listens for.
            System.out.println("No close/next button found -- pressing ESCAPE");
            actions.sendKeys(Keys.ESCAPE).perform();
            sleep(600);
        }

        List<WebElement> stillThere = driver.findElements(By.id("__reactour"));
        if (!stillThere.isEmpty() && stillThere.get(0).isDisplayed()) {
            System.out.println("WARNING: tour overlay still present after 5 dismissal attempts");
        } else {
            System.out.println("Tour overlay dismissed");
        }
    }

    /**
     * Dispatches a full mousedown/mouseup/click event sequence on the
     * element rather than calling element.click() directly. This is
     * required for react-select options and the reactour close button,
     * both of which can attach handlers to mousedown/pointerdown instead
     * of (or in addition to) click.
     */
    private void jsClick(WebElement el) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        sleep0();
        js.executeScript(FULL_CLICK_JS, el);
    }

    private void sleep0() {
        try { Thread.sleep(150); } catch (InterruptedException ignored) {}
    }

    private void sleep(int ms) throws Exception {
        Thread.sleep(ms);
    }

    @AfterTest
    public void closeBrowser() {
        if (driver != null) driver.quit();
    }
}