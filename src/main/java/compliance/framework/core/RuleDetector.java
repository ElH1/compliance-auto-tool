/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package compliance.framework.core;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class RuleDetector {

    /**
     * @param instanceModel
     */
    // classLocation = package name from execution args
    /* detects whether evaluation for a given rule has to be executed, i.e. if rule applies to given instance model */
    public boolean detectRule(JSONObject instanceModel, String classLocation) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, MalformedURLException {
        String dbKey;
        String webAppKey;


        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});
            Class<?> loadedClass = classLoader.loadClass(classLocation);

            /* set the args to identify the method signature */
            Class[] argclassesDetector = new Class[1];
            argclassesDetector[0] = JSONObject.class; // parameterTypes signature to find the correct method, array of the method's parameter types

            /* get the provided rule's class and detection method */
            Class detector = Class.forName(classLocation);
            // System.out.println("detector.getName() = " + detector.getName());
            Method detect = loadedClass.getMethod("detectRule", argclassesDetector);

            /* create new instance of class */
            Object newObject = loadedClass.getDeclaredConstructor().newInstance();

            /* invoke detection method */
            boolean detectionResult = (boolean) detect.invoke(newObject, instanceModel); // equal to newObject.detectRule(instanceModel, path), where detect is method detectRule, newObject is instance and instanceModel+path are parameters
            return detectionResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
