/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package compliance.framework.core;

// import compliance.rules.ruleOne;

import javassist.tools.rmi.ObjectNotFoundException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {
    String instanceModelPath = "src/main/java/compliance/instanceModel/motivating-scenario-1.json";
    String iedmmPath = "src/main/java/compliance/instanceModel/";

    /* Arguments when running program:
     * 0. path to instance model file
     * 1. path for data transfer location
     * 2. name of classpath to rule detector
     * 3. name of classpath to rule evaluator
     * 4. path to resulting iedmm
     */
    public static void main(String[] args) throws ObjectNotFoundException, IOException, ClassNotFoundException {
        InstanceModelRetriever getModel = new InstanceModelRetriever();
        JSONObject instanceModel = getModel.getInstance(args[0]);

        ClassLoader classLoader = Main.class.getClassLoader();
        String[] argsForRule = new String[1]; // parameters that must be passed to the method being called
        Class[] argclasses = new Class[1];
        argclasses[0] = String[].class;
        Class[] argclassesDetector = new Class[1];
        argclassesDetector[0] = JSONObject.class; // parameterTypes signature to find the correct method, array of the method's parameter types
        JSONObject paramForDetector = instanceModel;

        // System.out.println(System.getProperties().get("java.class.path"));
        //
        // JarFile jarFile = new JarFile("D:\\OneDrive - stud.uni-stuttgart.de\\MSc-Uni-Stuttgart-Main\\Master_thesis\\poc\\compliance-automation-framework\\out\\artifacts\\compliance_automation_framework_jar\\compliance-automation-framework.jar");
        // Enumeration<JarEntry> a = jarFile.entries();
        // JarEntry je = null;

        try {

            // while (a.hasMoreElements()) {
            //     je = a.nextElement();
            //     if (je.getName().contains("ruleTwo")) {
            //         break;
            //     }
            //
            //
            // }

            Class aClass = Class.forName(args[2]);
            // System.out.println(je.toString());
            // Class aClass = classLoader.loadClass(args[0]);
            // addPath(args[0]);
            System.out.println("aClass.getName() = " + aClass.getName());
            Method method = aClass.getMethod("main", argclasses);
            Object obj = aClass.getDeclaredConstructor().newInstance();
            method.invoke(obj, argsForRule);

            Class detector = Class.forName(args[3]);
            System.out.println("detector.getName() = " + detector.getName());
            Method detect = detector.getMethod("detectRule", argclassesDetector);
            Object newObject = detector.getDeclaredConstructor().newInstance();
            detect.invoke(newObject, paramForDetector); // equal to newObject.detectRule(instanceModel), where detect is method detectRule, newObject is instance and instanceModel is parameter


            for (int i = 0; i < args.length; i++) {
                System.out.println("Argument " + i + ": " + args[i]);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void addPath(String s) throws Exception {
        File f = new File(s);
        URI u = f.toURI();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{u.toURL()});
    }

    public static void getDescription(String s) {

    }
}
