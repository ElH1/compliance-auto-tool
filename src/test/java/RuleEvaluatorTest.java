import compliance.framework.core.InstanceModelRetriever;
import compliance.framework.core.RuleEvaluator;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import static org.junit.Assert.*;

public class RuleEvaluatorTest {

    InstanceModelRetriever retriever = new InstanceModelRetriever();

    @Test(expected = ClassNotFoundException.class)
    public void nonexistentClassTest() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JSONObject model = retriever.retrieveInstance("instanceModels/motivating-scenario-1-compliant.json");
        String classLocation = "nonexistent.class";
        RuleEvaluator evaluator = new RuleEvaluator();
        JSONObject result = evaluator.evaluateRule(model, classLocation);
    }

    @Test
    public void compliantTest() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JSONObject model = retriever.retrieveInstance("instanceModels/motivating-scenario-1-compliant.json");
        String classLocation = "compliance.rules.exampleRule";
        RuleEvaluator evaluator = new RuleEvaluator();
        JSONObject result = evaluator.evaluateRule(model, classLocation);
        assertEquals(null, result);
    }

    @Test
    public void notCompliantTest() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JSONObject model = retriever.retrieveInstance("instanceModels/motivating-scenario-1-noncompliant.json");
        String classLocation = "compliance.rules.exampleRule";
        RuleEvaluator evaluator = new RuleEvaluator();
        JSONObject result = evaluator.evaluateRule(model, classLocation);
        assertNotEquals(null, result);
    }

    @Test
    public void notCompliantTest2() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JSONObject model = retriever.retrieveInstance("instanceModels/motivating-scenario-1-noncompliant.json");
        String classLocation = "compliance.rules.exampleRule";
        RuleEvaluator evaluator = new RuleEvaluator();
        JSONObject result = evaluator.evaluateRule(model, classLocation);
        System.out.println(result);
        Iterator<?> keys = result.keySet().iterator();
        while (keys.hasNext()) {
            String currentKey = (String) keys.next();
            assertTrue(result.getJSONObject(currentKey).has("type"));
        }
    }

    @Test(expected = NullPointerException.class)
    public void compliantTest2() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JSONObject model = retriever.retrieveInstance("instanceModels/motivating-scenario-1-compliant.json");
        String classLocation = "compliance.rules.exampleRule";
        RuleEvaluator evaluator = new RuleEvaluator();
        JSONObject result = evaluator.evaluateRule(model, classLocation);
        System.out.println(result);
        Iterator<?> keys = result.keySet().iterator();
        while (keys.hasNext()) {
            String currentKey = (String) keys.next();
            assertFalse(result.getJSONObject(currentKey).has("type"));
        }
    }

}
