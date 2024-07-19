package simtran.core.wdm;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import simtran.core.utils.MyLogger;

import java.net.URI;
import java.time.Duration;

import static simtran.core.config.ConfigManager.config;
import static simtran.core.config.ConfigManager.envConfig;

/**
 * This class provides a singleton instance to manage the WebDriver instance and WebDriverWait throughout the test execution.
 *
 * @author simtran
 */
public class DriverManager {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();

    /**
     * Gets the currently running WebDriver instance.
     *
     * @return The WebDriver instance associated with the current thread, or null if not yet set.
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Sets the WebDriver instance for the current thread along with a WebDriverWait object
     * initialized with the timeout specified in the configuration for the given target environment.
     * If a WebDriver instance already exists for the current thread, it is first quit before setting the new one.
     *
     * @param driver The WebDriver instance to set.
     * @param target The target environment
     */
    public static void setDriver(WebDriver driver, String target) {
        if (getDriver() != null)
            quit();
        DriverManager.driver.set(driver);
        wait.set(new WebDriverWait(getDriver(), Duration.ofSeconds(envConfig(target).longTimeout())));
    }

    /**
     * Gets the WebDriverWait instance associated with the current thread's WebDriver instance.
     *
     * @return The WebDriverWait object for waiting during test execution.
     */
    public static WebDriverWait getWait() {
        return wait.get();
    }

    /**
     * Quits the currently running WebDriver instance and removes it from the ThreadLocal storage.
     */
    public static void quit() {
        DriverManager.driver.get().quit();
        driver.remove();
    }

    /**
     * Retrieves information about the currently running browser, including platform, browser name, and version
     *
     * @return A string containing platform, browser name, and version details.
     */
    public static String getBrowserInfo() {
        var cap = ((RemoteWebDriver) DriverManager.getDriver()).getCapabilities();

        String platform = cap.getPlatformName().toString();
        String browserName = cap.getBrowserName();
        String browserVersion = cap.getBrowserVersion();

        return String.format("Platform: %s, Browser: %s - version: %s", platform, browserName, browserVersion);
    }

    /**
     * Creates and returns a new WebDriver instance for the specified browser using a BrowserFactory.
     *
     * @param browser The name of the browser to be used (e.g., "CHROME", "FIREFOX").
     * @return A new WebDriver instance for the specified browser.
     */
    public WebDriver createDriverInstance(String browser) {
        if (config().gridEnabled()){
            return createRemoteInstance(BrowserFactory.valueOf(browser.toUpperCase()).getOptions());
        }
        return BrowserFactory.valueOf(browser.toUpperCase()).initDriver();
    }

    private RemoteWebDriver createRemoteInstance(MutableCapabilities capabilities){
        RemoteWebDriver remoteWebDriver = null;
        try{
            String gridUrl = String.format("http://%s:%s/wd/hub", config().gridUrl(), config().gridPort());
            remoteWebDriver = new RemoteWebDriver(URI.create(gridUrl).toURL(), capabilities);
        } catch (java.net.MalformedURLException e){
            MyLogger.error("Grid URL is invalid and Grid is not available");
            MyLogger.error(String.format("Browser: %s \n%s", capabilities.getBrowserName(), e));
        } catch (IllegalArgumentException e){
            MyLogger.error(String.format("Browser %s is invalid or recognized \n%s", capabilities.getBrowserName(), e));
        }
        return remoteWebDriver;
    }
}