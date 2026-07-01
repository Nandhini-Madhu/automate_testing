package com.student;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentOnboarding {

    static class StudentData {
        public String gmail, appPassword, newPassword, name;
        public String rollNo, phone, dob, dept, place, leetcode;

        public StudentData(String gmail, String appPassword, String newPassword,
                String name, String rollNo, String phone, String dob,
                String dept, String place, String leetcode) {
            this.gmail       = gmail;
            this.appPassword = appPassword;
            this.newPassword = newPassword;
            this.name        = name;
            this.rollNo      = rollNo;
            this.phone       = phone;
            this.dob         = dob;
            this.dept        = dept;
            this.place       = place;
            this.leetcode    = leetcode;
        }
    }

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    Actions actions;

    List<StudentData> students = Arrays.asList(
        new StudentData(
            "mahilan072@gmail.com",
            "umkbgyhdlnpqdodh",
            "Test@123",
            "mahilan",
            "201ec132",
            "9894913209",
            "2003-04-16",
            "ECE",
            "erode",
            "nandhini_madhu_"
        )
    );

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver  = new ChromeDriver(options);
        wait    = new WebDriverWait(driver, Duration.ofSeconds(40));
        js      = (JavascriptExecutor) driver;
        actions = new Actions(driver);
        System.out.println("Chrome browser opened");
    }

    @Test
    public void runOnboardingFlow() throws Exception {

        for (StudentData student : students) {

            try {

                System.out.println("\n====================================");
                System.out.println("PROCESSING : " + student.gmail);
                System.out.println("====================================");

                String tempPassword = fetchTempPasswordFromMail(
                    student.gmail, student.appPassword);
                System.out.println("TEMP PASSWORD : " + tempPassword);

    
                driver.get(
                    "https://beta.campushub.thebettertomorrow.in/login");
                sleep(4000);

                WebElement emailInput = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                        By.id("login-email")));
                emailInput.clear();
                emailInput.sendKeys(student.gmail);
                sleep(1000);

                driver.findElement(By.id("login-password"))
                    .sendKeys(tempPassword);
                sleep(1000);

                jsClick(wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Sign in')]"))));
                System.out.println("Sign In clicked");
                sleep(5000);

               
                jsClick(wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Next Step')]"))));
                System.out.println("Next Step clicked");
                sleep(3000);

            
                List<WebElement> pwFields = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//input[@type='password']")));
                reactType(pwFields.get(0), student.newPassword);
                reactType(pwFields.get(1), student.newPassword);
                System.out.println("New password entered");
                sleep(2000);

            
                jsClick(wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(
                        "//button[contains(.,'Proceed to Profile Setup')]"))));
                System.out.println("Proceed clicked");
                sleep(4000);

            
                wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@type='text']")));
                sleep(1500);

                typeIntoNthInput(0, student.name);
                System.out.println("Name entered");
                sleep(500);

              
                typeIntoNthInput(1, student.rollNo);
                System.out.println("Roll No entered");
                sleep(500);

              
                typeIntoNthInput(2, student.phone);
                System.out.println("Phone entered");
                sleep(500);

               
                WebElement dobEl = driver.findElement(
                    By.xpath("//input[@type='date']"));
                reactSetValue(dobEl, student.dob);
                System.out.println("DOB entered");
                sleep(500);

            
                typeIntoNthInput(3, student.dept);
                System.out.println("Department entered");
                sleep(500);

                typeIntoNthInput(4, student.place);
                System.out.println("Place entered");
                sleep(500);

                typeIntoNthInput(5, student.leetcode);
                System.out.println("LeetCode entered");
                sleep(1000);

                jsClick(wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(
                        "//button[contains(.,\"Let's Start Buddy\")]"))));

                System.out.println(
                    "Onboarding completed for : " + student.gmail);
                sleep(5000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void typeIntoNthInput(int index, String value)
            throws Exception {

        List<WebElement> inputs = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//input[@type='text']")));

        reactType(inputs.get(index), value);
    }



    private void reactType(WebElement el, String value)
            throws Exception {

  
        jsClick(el);
        sleep(300);

        js.executeScript(
            "var nativeInputValueSetter = Object.getOwnPropertyDescriptor("
            + "window.HTMLInputElement.prototype, 'value').set;"
            + "nativeInputValueSetter.call(arguments[0], arguments[1]);"
            + "arguments[0].dispatchEvent("
            + "  new Event('input', { bubbles: true }));"
            + "arguments[0].dispatchEvent("
            + "  new Event('change', { bubbles: true }));",
            el, value);

        sleep(200);

   
        String actual = el.getAttribute("value");
        if (actual == null || actual.isEmpty()) {
            System.out.println("  native setter failed, typing char by char");
            for (char c : value.toCharArray()) {
                el.sendKeys(String.valueOf(c));
                sleep(40);
            }
        }

        System.out.println(
            "  typed [" + el.getAttribute("value") + "] into field");
    }


    private void reactSetValue(WebElement el, String value) {
        js.executeScript(
            "var nativeInputValueSetter = Object.getOwnPropertyDescriptor("
            + "window.HTMLInputElement.prototype, 'value').set;"
            + "nativeInputValueSetter.call(arguments[0], arguments[1]);"
            + "arguments[0].dispatchEvent("
            + "  new Event('input', { bubbles: true }));"
            + "arguments[0].dispatchEvent("
            + "  new Event('change', { bubbles: true }));",
            el, value);
    }


    private String fetchTempPasswordFromMail(
            String gmail, String appPassword) throws Exception {

        System.out.println("Fetching temp password from Gmail...");

        Properties props = new Properties();
        props.put("mail.store.protocol",                "imaps");
        props.put("mail.imaps.host",                    "imap.gmail.com");
        props.put("mail.imaps.port",                    "993");
        props.put("mail.imaps.ssl.enable",              "true");
        props.put("mail.imaps.ssl.trust",               "*");
        props.put("mail.imaps.ssl.checkserveridentity", "false");
        props.put("mail.imaps.socketFactory.class",
                  "javax.net.ssl.SSLSocketFactory");
        props.put("mail.imaps.socketFactory.fallback",  "false");
        props.put("mail.imaps.socketFactory.port",      "993");

        Session session = Session.getInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", 993, gmail, appPassword);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();
        System.out.println("Total emails in inbox: " + messages.length);

        for (int i = messages.length - 1; i >= 0; i--) {

            Message message = messages[i];
            String subject  = message.getSubject();
            if (subject == null) continue;

            String sl = subject.toLowerCase();
            if (sl.contains("platform") || sl.contains("campus")
                    || sl.contains("welcome") || sl.contains("invited")) {

                System.out.println("Checking email : " + subject);
                String body = getTextFromMessage(message);

                Matcher matcher = Pattern.compile(
                    "Temporary login password[:\\s]+"
                    + "([A-Za-z0-9@#$%^&+=!_\\-]+)")
                    .matcher(body);

                if (matcher.find()) {
                    String tp = matcher.group(1).trim();
                    System.out.println("Found temp password : " + tp);
                    inbox.close(false);
                    store.close();
                    return tp;
                }
            }
        }

        inbox.close(false);
        store.close();
        throw new Exception("Temporary password not found in inbox");
    }



    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain"))
            return message.getContent().toString();
        if (message.isMimeType("text/html"))
            return stripHtml(message.getContent().toString());
        if (message.isMimeType("multipart/*"))
            return getTextFromMimeMultipart(
                (MimeMultipart) message.getContent());
        return "";
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mp) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mp.getCount(); i++) {
            BodyPart bp = mp.getBodyPart(i);
            if (bp.isMimeType("text/plain"))
                sb.append(bp.getContent().toString());
            else if (bp.isMimeType("text/html"))
                sb.append(stripHtml(bp.getContent().toString()));
            else if (bp.getContent() instanceof MimeMultipart)
                sb.append(getTextFromMimeMultipart(
                    (MimeMultipart) bp.getContent()));
        }
        return sb.toString();
    }

    private String stripHtml(String html) {
        return html
            .replaceAll("(?i)<br\\s*/?>", " ")
            .replaceAll("(?i)</p>",        " ")
            .replaceAll("(?i)</div>",      " ")
            .replaceAll("<[^>]+>",         "")
            .replaceAll("&nbsp;",          " ")
            .replaceAll("&amp;",           "&")
            .replaceAll("\\s{2,}",         " ")
            .trim();
    }


    private void jsClick(WebElement el) {
        js.executeScript(
            "arguments[0].scrollIntoView({block:'center'});", el);
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