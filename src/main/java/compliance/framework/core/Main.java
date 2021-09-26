/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package compliance.framework.core;

// import compliance.rules.ruleOne;

import javassist.tools.rmi.ObjectNotFoundException;
import org.json.JSONObject;

// import java.io.File;
import java.io.IOException;
// import java.lang.reflect.Method;
// import java.net.URI;
// import java.net.URL;
// import java.net.URLClassLoader;
// import java.util.Enumeration;
// import java.util.jar.JarEntry;
// import java.util.jar.JarFile;

public class Main {
    // String instanceModelPath = "src/main/java/compliance/instanceModel/motivating-scenario-1.json";
    // String iedmmPath = "src/main/java/compliance/instanceModel/";

    /* Arguments when running program:
     * 0. path to instance model file
     * 1. path + file for data transfer between detector & evaluator
     * 2. name of classpath to rule
     */
    public static void main(String[] args) throws ObjectNotFoundException, IOException, ClassNotFoundException {
        String instanceModelPath = args[0];
        String dataTransferPath = args[1];
        JSONObject[] issues = new JSONObject[args.length - 2];
        int ctrForIssues = 0;

        try {

            /* get the instance model */
            // InstanceModelRetriever getModel = new InstanceModelRetriever();
            // JSONObject instanceModel = getModel.getInstance(instanceModelPath);
            //
            // /* for all rules that were passed as args, execute their detector and evaluator */
            // for (int i = 2; i < args.length; i++) {
            //     String ruleClassPath = args[i];
            //     /* check if rule applies to instance model */
            //     RuleDetector detector = new RuleDetector();
            //     boolean applicable = detector.detectRule(instanceModel, ruleClassPath, dataTransferPath);
            //
            //
            //     /* if the rule applies, evaluate the rule and annotate the instance model */
            //     if (applicable) {
            //         RuleEvaluator evaluator = new RuleEvaluator();
            //         issues[ctrForIssues] = evaluator.evaluateRule(instanceModel, ruleClassPath, dataTransferPath);
            //     } else {
            //         JSONObject result = new JSONObject();
            //         result.put("rule not applicable at position", args[i]);
            //         issues[ctrForIssues] = result;
            //     }
            //     System.out.println("issue at position " + ctrForIssues + " is " + issues[ctrForIssues]);
            //     ctrForIssues++;
            // }
            //
            // /* after all rules have been evaluated + their results collected, annotate the instance model with this information */
            // InstanceModelAnnotator annotator = new InstanceModelAnnotator();
            // JSONObject iedmm = annotator.annotateModel(issues, instanceModel);
            // if (iedmm != null) {
            //     annotator.saveToFile(iedmm, "src/main/java/compliance/instanceModel/", "motivating-scenario-1-iedmm", ".json");
            // }
// bis hier alles obige relevant/working!

            // ClassLoader classLoader = Main.class.getClassLoader();
            // Class[] argclasses = new Class[1];
            // argclasses[0] = String[].class; // parameterTypes signature to find the correct method, array of the method's parameter types!
            // String[] argsForRule = new String[1]; // parameters that must be passed to the method being called


            // System.out.println(System.getProperties().get("java.class.path"));
            //
            // JarFile jarFile = new JarFile("D:\\OneDrive - stud.uni-stuttgart.de\\MSc-Uni-Stuttgart-Main\\Master_thesis\\poc\\compliance-automation-framework\\out\\artifacts\\compliance_automation_framework_jar\\compliance-automation-framework.jar");
            // Enumeration<JarEntry> a = jarFile.entries();
            // JarEntry je = null;


            // while (a.hasMoreElements()) {
            //     je = a.nextElement();
            //     if (je.getName().contains("ruleTwo")) {
            //         break;
            //     }
            //
            //
            // }

            // Class aClass = Class.forName(args[2]);
            // System.out.println(je.toString());
            // Class aClass = classLoader.loadClass(args[0]);
            // addPath(args[0]);
            // System.out.println("aClass.getName() = " + aClass.getName());
            // Method method = aClass.getMethod("main", argclasses);
            // Object obj = aClass.getDeclaredConstructor().newInstance();
            // method.invoke(obj, argsForRule);

            // for (int i = 0; i < args.length; i++) {
            //     System.out.println("Argument " + i + ": " + args[i]);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
