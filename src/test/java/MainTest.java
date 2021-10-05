import compliance.framework.core.InstanceModelRetriever;
import compliance.framework.core.Main;
import compliance.framework.core.RuleDetector;
import compliance.framework.core.RuleEvaluator;
import compliance.rules.exampleRule;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class MainTest {
    Main main = new Main();
    InstanceModelRetriever retriever = new InstanceModelRetriever();
    JSONObject instanceModel = new JSONObject();
    RuleDetector detector = new RuleDetector();
    RuleEvaluator evaluator = new RuleEvaluator();
    exampleRule rule = new exampleRule();


    @Before
    public void init() {
        retriever = Mockito.mock(InstanceModelRetriever.class);
        instanceModel = Mockito.mock(JSONObject.class);
        detector = Mockito.mock(RuleDetector.class);
        evaluator = Mockito.mock(RuleEvaluator.class);
        rule = Mockito.mock(exampleRule.class);
        main = new Main();
    }

    @Test
    public void MainTest1() {


    }

    @Test
    public void MainTest2() {


    }

    @Test
    public void MainTest3() {


    }

}
