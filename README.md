Copyright IBM 2021 under Apache 2.0 license

Author: Elena Heldwein

This applies to all code under src/main.

# Prototype for master thesis

Thesis Title:  "Automated Compliance Management of Heterogeneous Application Infrastructures at Runtime"

# Using the prototype

An already packaged, portable version of the prototype is available in the `portable` directory under the name `out.jar`
. Please note that all files in this directory are necessary for execution.

#### Prerequisites

- A rule description file must exist at a location of your choice. There is an example
  under `ruleDescriptions/exampleRuleDescription.json`. Please note that the paths provided in the rule description must
  either be absolute, or relative to the location the framework is executed from, and they must end with `/`.
- An instance model must exist at a location of your choice. There are some example instance models
  under `instanceModels`.
- Rule definitions implementing the interface `complianceRule.java` under `src/main/java/compliance/rules` in a
  directory that the rule description points to. These definitions must all start with `package compliance.rules;`
- Any dependencies the rule definitions require must be included in a `lib` folder at the same location as the rule
  definitions. This lib folder also requires a copy of the framework jar! So for the running example, the `lib` folder
  must include a) the json jar and b) the `out.jar` framework archive.

IMPORTANT: the directory the rule description points to must contain no files other than the rules which should be
evaluated! Otherwise, the framework will run into errors. It can contain any directories, but everything in this folder will be copied during execution.

#### Execution

Execute
using `java -cp "out.jar;json-20210307.jar;." compliance.framework.core.Main "PATH/TO/INSTANCEMODEL.JSON" "PATH/TO/RULEDESCRIPTION.JSON"`
from the location where you have stored the portable folder. If using a UNIX-based system instead of Windows, replace
the ; separator in the values for -cp by : as a separator.

The resulting I-EDMM will be placed in the same location as the initial instance model.

### A working example
Executed from the `./portable/` folder, and using the currently existent example rule description, as
well as an example rule, can be executed using this command:

```
java -cp "out.jar;json-20210307.jar;." \
compliance.framework.core.Main \
"../instanceModels/motivating-scenario-1-noncompliant.json" \
"../ruleDescriptions/exampleRuleDescription.json"
```

# For further development:

## Compiling

The project in this repository is an IntelliJ IDEA project that uses Maven to manage dependencies. It can be loaded into
an IDE and compiled from there. For example this works in IntelliJ IDEA (https://www.jetbrains.com/idea/) or
Eclipse (https://www.eclipse.org/downloads/), both of which are freely available IDEs. For Eclipse, it is recommended to
unpackage the zip file before opening it in Eclipse, as the zip contains an extra folder
level (`masterthesis-prototype-master\masterthesis-prototype-master`), which Eclipse handles incorrectly when loading
the project directly as a zip archive.

As an alternative that doesn't require an IDE, compilation can be done using
Ant (https://ant.apache.org/bindownload.cgi). To achieve this, add the following as a build.xml in the project root:

```
<project default="compile">
    <target name="compile">
        <mkdir dir="bin"/>
        <javac srcdir="src/main" destdir="bin">
	    <classpath>
  	        <pathelement location="portable/json-20210307.jar"/>
	    </classpath>
	</javac>
    </target>
</project>
```

Then build using `ant info` (assuming ant is installed and available as a command).

## Packaging compiled code

Should you wish to package the jar yourself, use the following
command: `jar cf out.jar ./compliance ./META-INF edmm-core-v1.0.18.jar json-20210307.jar
` from where your compiled `.class` files are stored after having built the source code. For example, if using IntelliJ,
that may be the directory `compliance-automation-framework/target/classes`. Note that the `edmm-core-v1.0.18.jar` file,
as well as `json-20210307.jar` are required in the current directory for this step. These are made available at the top
level of the project and in the portable folder.

# Project Structure

- `.idea`
    - contains IntelliJ-specific project files
- `instanceModels`
    - contains a number of examples for motivating scenario 1, in variants where it is compliant with CR 1 (defined
      in `exampleRule.java`), not compliant with CR 1 or where CR 1 is not applicable. Furthermore, there are examples
      of an I-EDMM file, and `.yml` versions identical to the corresponding `.json` versions.
- `portable`
    - portable version of the framework, with all required dependencies and the META-INF required to successfully
      execute the framework's `out.jar`
- `ruleDescriptions`
    - contains an example rule description to show the required structure
- `src/main`
    - `java/compliance`
        - `framework/core`
            - contains classes for Orchestrator (in Main), Retriever, Detector, Evaluator and Annotator.
        - `rules`
            - contains the interface for implementing custom rules in `complianceRule.java`
            - an example rule implementation is provided in `exampleRule.java`
- `compliance-automation-framework.iml` is an IntelliJ-produced project file
- `pom.xml` defines the Maven dependencies of the project
