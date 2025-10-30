package stepDefinitions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.Assert;

import java.time.Duration;

public class GmailGitHubSteps {
    WebDriver driver;
    WebDriverWait wait;
    
    // Store credentials in variables (in real project, use config files)
    String githubEmail = "your_github_email@example.com";
    String githubPassword = "your_github_password";

    @Given("I navigate to Gmail login page")
    public void i_navigate_to_gmail_login_page() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        
        driver.get("https://mail.google.com");
    }

    @When("I click on GitHub login option")
    public void i_click_on_github_login_option() {
        try {
            // Click on "Use another account" if multiple accounts shown
            try {
                WebElement useAnotherAccount = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Use another account']")));
                useAnotherAccount.click();
            } catch (Exception e) {
                System.out.println("'Use another account' not found, continuing...");
            }

            // Look for GitHub option - this might vary based on Google's UI
            WebElement githubOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'GitHub') or contains(text(),'github')]")));
            githubOption.click();
            
        } catch (Exception e) {
            // Alternative: Look for SSO options
            try {
                WebElement ssoButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'Sign in with')]")));
                ssoButton.click();
            } catch (Exception ex) {
                System.out.println("Neither GitHub nor SSO option found");
                ex.printStackTrace();
            }
        }
    }

    @When("I enter GitHub credentials")
    public void i_enter_github_credentials() {
        try {
            // Switch to GitHub login window
            String originalWindow = driver.getWindowHandle();
            
            // Wait for new window and switch to it
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            for (String windowHandle : driver.getWindowHandles()) {
                if (!originalWindow.equals(windowHandle)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            // Enter GitHub username/email
            WebElement githubEmailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("login_field")));
            githubEmailField.clear();
            githubEmailField.sendKeys(githubEmail);

            // Enter GitHub password
            WebElement githubPasswordField = driver.findElement(By.id("password"));
            githubPasswordField.clear();
            githubPasswordField.sendKeys(githubPassword);

            // Click sign in button
            WebElement signInButton = driver.findElement(By.name("commit"));
            signInButton.click();

            // If GitHub requires OTP/2FA
            try {
                WebElement otpField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("otp")));
                System.out.println("2FA detected - manual intervention required");
                // For 2FA, you might need to pause and enter manually
                Thread.sleep(30000); // Wait 30 seconds for manual 2FA entry
            } catch (Exception e) {
                System.out.println("No 2FA detected");
            }

            // Switch back to original window
            driver.switchTo().window(originalWindow);
            
        } catch (Exception e) {
            System.out.println("Error during GitHub login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Then("I should be logged into Gmail successfully")
    public void i_should_be_logged_into_gmail_successfully() {
        try {
            // Wait for Gmail inbox to load
            WebElement composeButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[text()='Compose' or contains(@role, 'button')]")));
            
            Assert.assertTrue(composeButton.isDisplayed(), "Compose button not found - login may have failed");
            System.out.println("✅ Successfully logged into Gmail!");
            
        } catch (Exception e) {
            System.out.println("❌ Login verification failed: " + e.getMessage());
            Assert.fail("Gmail login verification failed");
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}