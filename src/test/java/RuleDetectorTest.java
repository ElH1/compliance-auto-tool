import compliance.framework.core.InstanceModelRetriever;
import compliance.framework.core.RuleDetector;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class RuleDetectorTest {

    InstanceModelRetriever retriever = new InstanceModelRetriever();

    @Test(expected = ClassNotFoundException.class)
    public void nonexistentClassTest() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JSONObject model = retriever.retrieveInstance("instanceModels/motivating-scenario-1-compliant.json");
        String classLocation = "nonexistent.class";
        RuleDetector detector = new RuleDetector();
        boolean result = detector.detectRule(model, classLocation);
    }

    @Test
    public void notApplicableTest() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JSONObject model = retriever.retrieveInstance("instanceModels/motivating-scenario-1-notapplicable.json");
        String classLocation = "compliance.rules.exampleRule";
        RuleDetector detector = new RuleDetector();
        boolean result = detector.detectRule(model, classLocation);
        assertFalse(result);
    }

    @Test
    public void applicableTest() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JSONObject model = retriever.retrieveInstance("instanceModels/motivating-scenario-1-noncompliant.json");
        String classLocation = "compliance.rules.exampleRule";
        RuleDetector detector = new RuleDetector();
        boolean result = detector.detectRule(model, classLocation);
        assertTrue(result);
    }

}
