package com.taskmanager.backend.e2e.support;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;


public abstract class BaseE2ETest {

    protected static final String BASE_URL =
            System.getProperty("selenium.baseUrl", "http://localhost:5173");

    private static final boolean HEADLESS =
            Boolean.parseBoolean(System.getProperty("selenium.headless", "true"));

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUpDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if (HEADLESS) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,900");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDownDriver(TestInfo testInfo) {
        try {
            saveScreenshot(testInfo);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private void saveScreenshot(TestInfo testInfo) {
        if (!(driver instanceof TakesScreenshot)) {
            return;
        }
        try {
            Path dir = Path.of("target", "selenium-screenshots");
            Files.createDirectories(dir);
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path target = dir.resolve(testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9-_]", "_") + ".png");
            Files.copy(screenshot.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Impossible de sauvegarder la capture d'écran : " + e.getMessage());
        }
    }
}
