/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package compliance.framework.core;

import javassist.tools.rmi.ObjectNotFoundException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RuleDetector {

    /**
     * @param instanceModel
     * @throws ObjectNotFoundException
     */
    /* detects whether evaluation for a given rule has to be executed, i.e. if rule applies to given instance model */
    public boolean detectRule(JSONObject instanceModel, String classLocation) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String dbKey;
        String webAppKey;

        try {
            /* set the args to identify the method signature */
            Class[] argclassesDetector = new Class[1];
            argclassesDetector[0] = JSONObject.class; // parameterTypes signature to find the correct method, array of the method's parameter types

            /* get the provided rule's class and detection method */
            Class detector = Class.forName(classLocation);
            // System.out.println("detector.getName() = " + detector.getName());
            Method detect = detector.getMethod("detectRule", argclassesDetector);

            /* create new instance of class */
            Object newObject = detector.getDeclaredConstructor().newInstance();

            /* invoke detection method */
            boolean detectionResult = (boolean) detect.invoke(newObject, instanceModel); // equal to newObject.detectRule(instanceModel, path), where detect is method detectRule, newObject is instance and instanceModel+path are parameters
            return detectionResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
