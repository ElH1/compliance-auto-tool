/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package compliance.framework.core;

import javassist.tools.rmi.ObjectNotFoundException;
import org.json.JSONObject;

import java.io.IOException;

public class Main {
    // String instanceModelPath = "instanceModels/motivating-scenario-1-noncompliant.json";
    // String iedmmPath = "instanceModels/iedmm";

    /* Arguments when running program:
     * 0. path to instance model file
     * 1. path + file for data transfer between detector & evaluator
     * 2.-x. name of classpath to rule(s)
     */
    public static void main(String[] args) throws ObjectNotFoundException, IOException, ClassNotFoundException {
        String instanceModelPath = args[0];
        String dataTransferPath = args[1];
        JSONObject[] issues = new JSONObject[args.length - 2];
        int ctrForIssues = 0;

        try {

            /* get the instance model */
            InstanceModelRetriever getModel = new InstanceModelRetriever();
            JSONObject instanceModel = getModel.getInstance(instanceModelPath);

            /* for all rules that were passed as args, execute their detector and evaluator */
            for (int i = 2; i < args.length; i++) {
                String ruleClassPath = args[i];
                /* check if rule applies to instance model */
                RuleDetector detector = new RuleDetector();
                boolean applicable = detector.detectRule(instanceModel, ruleClassPath, dataTransferPath);


                /* if the rule applies, evaluate the rule and annotate the instance model */
                if (applicable) {
                    RuleEvaluator evaluator = new RuleEvaluator();
                    issues[ctrForIssues] = evaluator.evaluateRule(instanceModel, ruleClassPath, dataTransferPath);
                } else {
                    JSONObject result = new JSONObject();
                    result.put("rule not applicable at position", args[i]);
                    issues[ctrForIssues] = result;
                }
                System.out.println("issue at position " + ctrForIssues + " is " + issues[ctrForIssues]);
                ctrForIssues++;
            }

            /* after all rules have been evaluated + their results collected, annotate the instance model with this information */
            InstanceModelAnnotator annotator = new InstanceModelAnnotator();
            JSONObject iedmm = annotator.annotateModel(issues, instanceModel);
            if (iedmm != null) {
                annotator.saveToFile(iedmm, "src/main/java/compliance/instanceModel/", "motivating-scenario-1-iedmm", ".json");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
