/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package compliance.framework.core;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RuleEvaluator {

    /* evaluates whether given rule is fulfilled for given instance model */
    public JSONObject evaluateRule(JSONObject instanceModel, String classLocation) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try {
            /* set the args to identify the method signature */
            Class[] argclassesDetector = new Class[1];
            argclassesDetector[0] = JSONObject.class; // parameterTypes signature to find the correct method, array of the method's parameter types

            /* get the provided rule's class and evaluation method */
            Class evaluator = Class.forName(classLocation);
            System.out.println("evaluator.getName() = " + evaluator.getName());
            Method evaluate = evaluator.getMethod("evaluateRule", argclassesDetector);

            /* create new instance of class */
            Object newObject = evaluator.getDeclaredConstructor().newInstance();

            /* invoke evaluation method */
            JSONObject evaluationResult = (JSONObject) evaluate.invoke(newObject, instanceModel); // equal to newObject.evaluateRule(instanceModel, path), where detect is method detectRule, newObject is instance and instanceModel+path are parameters
            return evaluationResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }
}
