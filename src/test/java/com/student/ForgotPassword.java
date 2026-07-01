package com.student;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForgotPassword {

    static final String GMAIL        = "nandhinimadhu599@gmail.com";
    static final String APP_PASSWORD = "bqwsfwugxknypqfr";
    static final String NEW_PASSWORD = "helloworld@123";
    static final String LOGIN_URL    = "https://campushub.thebettertomorrow.in/login";

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(40));
        js     = (JavascriptExecutor) driver;
        System.out.println("Chrome browser opened");
    }

    @Test
    public void forgotPasswordAndSignIn() throws Exception {

        driver.get(LOGIN_URL);
        sleep(2000);

      
        WebElement forgotLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Forgot password')]")));
        jsClick(forgotLink);
        System.out.println("Clicked 'Forgot password?'");
        sleep(2000);


        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("forgot-email")));
        emailField.clear();
        emailField.sendKeys(GMAIL);
        sleep(500);

        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.login-form__submit")));
        jsClick(submitBtn);
        System.out.println("Reset email requested for " + GMAIL);
        sleep(3000);

        String resetLink = fetchResetLinkFromMail(GMAIL, APP_PASSWORD);
        System.out.println("Reset link found: " + resetLink);

        driver.get(resetLink);
        sleep(3000);

        WebElement newPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("reset-password")));
        WebElement confirmPasswordField = driver.findElement(By.id("reset-confirm-password"));

        reactType(newPasswordField, NEW_PASSWORD);
        reactType(confirmPasswordField, NEW_PASSWORD);
        sleep(500);

        WebElement updateBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Update password')]")));
        jsClick(updateBtn);
        System.out.println("Password update submitted");
        sleep(3000);

        
        WebElement goToSignIn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Sign in')]")));
        jsClick(goToSignIn);
        System.out.println("Navigated back to login page");
        sleep(2000);

        WebElement loginEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("login-email")));
        loginEmail.clear();
        loginEmail.sendKeys(GMAIL);
        sleep(300);

        WebElement loginPassword = driver.findElement(By.id("login-password"));
        loginPassword.clear();
        loginPassword.sendKeys(NEW_PASSWORD);
        sleep(300);

        WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Sign in')]")));
        jsClick(signInBtn);
        System.out.println("Signed in with new password");
        sleep(4000);

    
        if (driver.getCurrentUrl().contains("/login")) {
            throw new AssertionError("Still on login page — sign-in with new password may have failed");
        }
        System.out.println("Forgot-password flow completed successfully");
    }

    private String fetchResetLinkFromMail(String gmail, String appPassword) throws Exception {

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.ssl.trust", "*");
        props.put("mail.imaps.ssl.checkserveridentity", "false");
        props.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.imaps.socketFactory.fallback", "false");
        props.put("mail.imaps.socketFactory.port", "993");

        Session session = Session.getInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", 993, gmail, appPassword);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        long deadline = System.currentTimeMillis() + 60000;
        String link = null;

        while (System.currentTimeMillis() < deadline && link == null) {
            Message[] messages = inbox.getMessages();

            for (int i = messages.length - 1; i >= 0; i--) {
                Message message = messages[i];
                String subject = message.getSubject();
                if (subject == null) continue;

                if (subject.toLowerCase().contains("reset your campushub password")) {
                    String body = getTextFromMessage(message);

                    Matcher matcher = Pattern.compile(
                        "https://campushub\\.thebettertomorrow\\.in/auth/action\\?[^\\s\"<]+"
                    ).matcher(body);

                    if (matcher.find()) {
                        link = matcher.group();
                        break;
                    }
                }
            }

            if (link == null) {
                System.out.println("Reset email not found yet, retrying...");
                Thread.sleep(5000);
            }
        }

        inbox.close(false);
        store.close();

        if (link == null) {
            throw new Exception("Reset password link not found in inbox");
        }
        return link;
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain"))
            return message.getContent().toString();
        if (message.isMimeType("text/html"))
            return stripHtml(message.getContent().toString());
        if (message.isMimeType("multipart/*"))
            return getTextFromMimeMultipart((MimeMultipart) message.getContent());
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mp) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mp.getCount(); i++) {
            BodyPart bp = mp.getBodyPart(i);
            if (bp.isMimeType("text/plain"))
                sb.append(bp.getContent().toString());
            else if (bp.isMimeType("text/html"))
                sb.append(stripHtml(bp.getContent().toString()));
            else if (bp.getContent() instanceof MimeMultipart)
                sb.append(getTextFromMimeMultipart((MimeMultipart) bp.getContent()));
        }
        return sb.toString();
    }

    private String stripHtml(String html) {
        return html
            .replaceAll("(?i)<br\\s*/?>", " ")
            .replaceAll("(?i)</p>", " ")
            .replaceAll("(?i)</div>", " ")
            .replaceAll("<[^>]+>", "")
            .replaceAll("&nbsp;", " ")
            .replaceAll("&amp;", "&")
            .replaceAll("\\s{2,}", " ")
            .trim();
    }

    private void reactType(WebElement el, String value) throws Exception {
        jsClick(el);
        sleep(200);
        js.executeScript(
            "var nativeInputValueSetter = Object.getOwnPropertyDescriptor("
            + "window.HTMLInputElement.prototype, 'value').set;"
            + "nativeInputValueSetter.call(arguments[0], arguments[1]);"
            + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
            + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            el, value);
        sleep(200);

        String actual = el.getAttribute("value");
        if (actual == null || actual.isEmpty()) {
            for (char c : value.toCharArray()) {
                el.sendKeys(String.valueOf(c));
                sleep(40);
            }
        }
    }

    private void jsClick(WebElement el) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        js.executeScript("arguments[0].click();", el);
    }

    private void sleep(int ms) throws Exception {
        Thread.sleep(ms);
    }

    @AfterTest
    public void closeBrowser() {
        if (driver != null) driver.quit();
    }
}