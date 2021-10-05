/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

/*  TODO: integration test(s)
 *  TODO: allow loading of class file from a given path, compile + load at runtime
 *  TODO: readme
 *  TODO: modify execution args! allow selection of whether to input paths directly, or file with rule description
 */

package compliance.framework.core;

import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    // String instanceModelPath = "instanceModels/motivating-scenario-1-noncompliant.json";
    // String iedmmPath = "instanceModels/iedmm";


    /* Arguments when running program:
     * 0. path to instance model FILE (execution config)
     * 1. path to description FILE (execution config)
     */
    // CLASSPATH = compliance.rules.FILENAME
    public static void main(String[] args) throws IOException {
        String instanceModelPath = args[0];
        String ruleDescriptionLocation = args[1];
        String[] ruleDescription = parseRule(ruleDescriptionLocation);
        String ruleType = ruleDescription[0];
        String ruleLocationDetector = ruleDescription[1];
        String ruleLocationEvaluator = ruleDescription[2];
        int ctrForIssues = 0;


        try {
            if (ruleType.equals("programme")) {
                /* copy all detector files from source to path required by framework */
                ProcessBuilder copyProcess = new ProcessBuilder();
                copyProcess.command("cp", "-RT", ruleLocationDetector, "compliance/rules/");
                Process copy = copyProcess.start();
                int copyDetDone = copy.waitFor();
                int copyEvalDone;
                /* copy all evaluator files from source to path required by framework, if at a different location than detectors */
                if (!ruleLocationDetector.equals(ruleLocationEvaluator)) {
                    ProcessBuilder copyEvalProcess = new ProcessBuilder();
                    copyEvalProcess.command("cp", "-RT", ruleLocationEvaluator, "compliance/rules/");
                    Process copyEval = copyEvalProcess.start();
                    copyEvalDone = copyEval.waitFor();
                } else {
                    copyEvalDone = 0;
                }

                /* copying was successful, compile all files at target */
                if (copyEvalDone == 0 && copyDetDone == 0) {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    // processBuilder.command("javac -cp 'ownRules/lib/*' ownRules/*.java");
                    processBuilder.command("javac", "-cp", "compliance/rules/lib/*", "compliance/rules/*.java");
                    // processBuilder.command("dir");
                    Process process = processBuilder.start();
                    int exitVal = process.waitFor();

                    /* compilation was successful, execute all detection + evaluation methods as required */
                    if (exitVal == 0) {
                        /* get the instance model */
                        InstanceModelRetriever getModel = new InstanceModelRetriever();
                        JSONObject instanceModel = getModel.retrieveInstance(instanceModelPath);
                        RuleDetector detector = new RuleDetector();
                        RuleEvaluator evaluator = new RuleEvaluator();

                        /* for all rules that are in the directory, execute their detector and evaluator */
                        File dir = new File("compliance/rules");
                        File[] directoryListing = dir.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.toLowerCase().endsWith(".class");
                            }
                        });

                        // object to collect issues in
                        JSONObject[] issues = new JSONObject[directoryListing.length];

                        if (directoryListing != null) {
                            /* iterate through all .class files in directory */
                            for (File child : directoryListing) {
                                String fileName = child.getName();
                                String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
                                String ruleClassPath = "compliance.rules." + tokens[0];

                                /* check if rule applies to instance model */
                                boolean applicable = detector.detectRule(instanceModel, ruleClassPath);

                                /* if the rule applies, evaluate the rule and annotate the instance model */
                                if (applicable) {
                                    JSONObject result = evaluator.evaluateRule(instanceModel, ruleClassPath);
                                    if (result != null) {
                                        issues[ctrForIssues] = result;
                                    } else {
                                        issues[ctrForIssues] = null;
                                        System.out.println("rule is fulfilled with name " + tokens[0]);
                                    }
                                } else {
                                    System.out.println("rule not applicable with name: " + tokens[0]);
                                    issues[ctrForIssues] = null;
                                }
                                // System.out.println("issue at position " + ctrForIssues + " is " + issues[ctrForIssues]);
                                ctrForIssues++;
                            }
                        } else {
                            System.out.println("compliance/rules is not a directory.");
                        }

                        /* after all rules have been evaluated + their results collected, annotate the instance model with this information */
                        InstanceModelAnnotator annotator = new InstanceModelAnnotator();
                        JSONObject iedmm = annotator.annotateModel(issues, instanceModel);
                        if (iedmm != null) {
                            String[] filename = instanceModelPath.split("\\.(?=[^\\.]+$)");
                            // annotator.saveToFile(iedmm, "instanceModels/iedmm/", "motivating-scenario-1-iedmm", ".json");
                            annotator.saveToFile(iedmm, filename[0], "-iedmm." + filename[1]);
                        }
                    } else {
                        System.out.println("Couldn't compile the rule files.");
                    }
                } else {
                    System.out.println("Couldn't copy the files.");
                }
            } else {
                // do nothing, no other rule types defined --> later make the framework match rule type to plugin identifiers
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static String[] parseRule(String ruleDescriptionPath) throws IOException {
        String[] result = new String[3];
        try {
            String stringModel = new String(Files.readAllBytes(Paths.get(ruleDescriptionPath)));
            JSONObject ruleDescriptionTemp = new JSONObject(stringModel);
            result[0] = ruleDescriptionTemp.get("type").toString();
            result[1] = ruleDescriptionTemp.get("detector").toString();
            result[2] = ruleDescriptionTemp.get("evaluator").toString();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
