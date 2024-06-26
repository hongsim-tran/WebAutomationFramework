package simtran.evershop.tests.cucumber.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.TestNG;

import java.lang.reflect.InvocationTargetException;

@CucumberOptions ( features = "src/main/java/simtran/evershop/tests/cucumber/features",
        glue = "simtran/evershop/tests/cucumber/stepDefinitions",
        plugin = {"rerun:target/failed-scenarios.txt", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"}
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
