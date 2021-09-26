package compliance.framework.core;

import javassist.tools.rmi.ObjectNotFoundException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RuleEvaluator {

    /* evaluates whether given rule is fulfilled for given instance model */
    public JSONObject evaluateRule(JSONObject instanceModel, String classLocation, String transferLocation) throws ObjectNotFoundException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try {
            /* set the args to identify the method signature */
            Class[] argclassesDetector = new Class[2];
            argclassesDetector[0] = JSONObject.class; // parameterTypes signature to find the correct method, array of the method's parameter types
            argclassesDetector[1] = String.class;

            /* set the params the method takes */
            JSONObject paramForDetector = instanceModel; // parameters that must be passed to the method being called
            String param2ForDetector = transferLocation;

            /* get the provided rule's class and evaluation method */
            Class evaluator = Class.forName(classLocation);
            System.out.println("evaluator.getName() = " + evaluator.getName());
            Method evaluate = evaluator.getMethod("evaluateRule", argclassesDetector);

            /* create new instance of class */
            Object newObject = evaluator.getDeclaredConstructor().newInstance();

            /* invoke evaluation method */
            JSONObject evaluationResult = (JSONObject) evaluate.invoke(newObject, paramForDetector, param2ForDetector); // equal to newObject.evaluateRule(instanceModel, path), where detect is method detectRule, newObject is instance and instanceModel+path are parameters
            // System.out.println("this is what I get from invoked method in eval reflection: " + evaluationResult.toString());
            return evaluationResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }
}
