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
    public boolean detectRule(JSONObject instanceModel, String classLocation, String transferLocation) throws ObjectNotFoundException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String dbKey;
        String webAppKey;

        try {
            /* set the args to identify the method signature */
            Class[] argclassesDetector = new Class[2];
            argclassesDetector[0] = JSONObject.class; // parameterTypes signature to find the correct method, array of the method's parameter types
            argclassesDetector[1] = String.class;

            /* set the params the method takes */
            JSONObject paramForDetector = instanceModel; // parameters that must be passed to the method being called
            String param2ForDetector = transferLocation;

            /* get the provided rule's class and detection method */
            Class detector = Class.forName(classLocation);
            // System.out.println("detector.getName() = " + detector.getName());
            Method detect = detector.getMethod("detectRule", argclassesDetector);

            /* create new instance of class */
            Object newObject = detector.getDeclaredConstructor().newInstance();

            /* invoke detection method */
            boolean detectionResult = (boolean) detect.invoke(newObject, paramForDetector, param2ForDetector); // equal to newObject.detectRule(instanceModel, path), where detect is method detectRule, newObject is instance and instanceModel+path are parameters
            // System.out.println("this is what I get from invoked detect method in reflection: " + detectionResult);
            return detectionResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
