# exec
[![Powered by KineticFire Labs](https://img.shields.io/badge/Powered_by-KineticFire_Labs-CDA519?link=https%3A%2F%2Flabs.kineticfire.com%2F)](https://labs.kineticfire.com/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central Version](https://img.shields.io/maven-central/v/com.kineticfire/exec)](https://central.sonatype.com/artifact/com.kineticfire/exec)


<p></p>

`exec` is a Java library for executing native system processes and validating shell scripts.  It provides flexible APIs 
to run commands and return results as strings, maps, or exceptions, plus utilities for script analysis.


## Table of Contents
1. [Key Capabilities](#key-capabilities)
2. [Installation](#installation)
3. [Usage](#usage)
4. [Documentation](#documentation)
5. [Building](#building)
6. [License](#license)


## Key Capabilities

`exec` simplifies process execution and script validation with enterprise-grade reliability and developer-friendly APIs:

### **Process Execution**
- **Flexible Return Types**: Execute commands with string results, detailed maps (exit codes, stdout, stderr), or exception-based error handling
- **Robust Error Handling**: Built-in timeout management, exit code processing, and comprehensive error reporting
- **Environment Control**: Configure working directories, environment variables, and execution context
- **Cross-Platform**: Consistent behavior across different operating systems

### **Script Validation**
- **Automated Analysis**: Validate bash/sh scripts using shellcheck with formatted, actionable feedback
- **Multiple Input Types**: Work with script files, paths, or string content seamlessly
- **Dependency Management**: Clear error messages when shellcheck is unavailable with installation guidance

### **Developer Experience**
- **Clean APIs**: Intuitive interface that reduces boilerplate code for common process execution tasks
- **Comprehensive Documentation**: Well-documented with examples and best practices
- **Production Ready**: Built for reliability with proper exception handling and resource management


## Installation

Add `exec` to your project using your preferred build system as below.  The `exec` library is published at 
[Maven Central Repository](https://central.sonatype.com/artifact/com.kineticfire/exec).

### **Gradle**

**Kotlin DSL (build.gradle.kts):**
```kotlin
dependencies {
    implementation("com.kineticfire:exec:1.0.0")
}
```

**Groovy DSL (build.gradle):**
```groovy
dependencies {
    implementation 'com.kineticfire:exec:1.0.0'
}
```

### **Maven**

**pom.xml:**
```xml
<dependency>
    <groupId>com.kineticfire</groupId>
    <artifactId>exec</artifactId>
    <version>1.0.0</version>
</dependency>
```

### **Leiningen**

**project.clj:**
```clojure
:dependencies [[com.kineticfire/exec "1.0.0"]]
```

### **Boot**

**build.boot:**
```clojure
(set-env! :dependencies '[[com.kineticfire/exec "1.0.0"]])
```

### **Clojure CLI**

**deps.edn:**
```clojure
{:deps {com.kineticfire/exec {:mvn/version "1.0.0"}}}
```

### **Prerequisites**

For script validation functionality, ensure `shellcheck` is installed on your system:

- **Ubuntu/Debian**: `apt install shellcheck`
- **CentOS/RHEL/Fedora**: `yum install ShellCheck` (or `dnf install ShellCheck`)
- **macOS**: `brew install shellcheck`
- **From source**: https://github.com/koalaman/shellcheck#installing


## Usage

### **Process Execution**

The `Exec` class provides multiple ways to execute system commands with different return types and error handling approaches.

#### **Basic Command Execution (String Results)**

```java
import com.kineticfire.exec.Exec;

// Simple command execution returning stdout as string
String result = Exec.exec("echo", "Hello World");
System.out.println(result); // Outputs: Hello World

// Command with arguments as List
List<String> command = Arrays.asList("ls", "-la", "/tmp");
String output = Exec.exec(command);
```

#### **Detailed Execution Results (Map Results)**

```java
import com.kineticfire.exec.Exec;

// Execute with detailed results including exit codes and stderr
Map<String, Object> result = Exec.execExceptionOnTaskFail("ls", "-la", "/nonexistent");

// Access execution details
int exitCode = (Integer) result.get("exitValue");
String stdout = (String) result.get("out");
String stderr = (String) result.get("err");
boolean success = (Boolean) result.get("success");

System.out.println("Exit Code: " + exitCode);
System.out.println("Output: " + stdout);
System.out.println("Error: " + stderr);
```

#### **Exception-Based Error Handling**

```java
import com.kineticfire.exec.Exec;
import com.kineticfire.exec.TaskExecutionException;

try {
    // This will throw TaskExecutionException on non-zero exit codes
    String result = Exec.exec("false"); // Command that always fails
} catch (TaskExecutionException e) {
    System.out.println("Command failed with exit code: " + e.getExitValue());
    System.out.println("Error message: " + e.getMessage());
}
```

#### **Environment Variables and Working Directory**

```java
import com.kineticfire.exec.Exec;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

// Set custom environment variables
Map<String, String> env = new HashMap<>();
env.put("MY_VAR", "custom_value");

// Execute with custom working directory and environment
File workingDir = new File("/path/to/working/directory");
String result = Exec.exec(Arrays.asList("env"), workingDir, env);
```

### **Script Validation**

The `ScriptValidator` class provides automated shell script validation using shellcheck.

#### **Basic Script Validation**

```java
import com.kineticfire.exec.validation.ScriptValidator;
import com.kineticfire.exec.validation.ScriptValidationResult;

// Validate script from file path
ScriptValidationResult result = ScriptValidator.validateScript("/path/to/script.sh");

if (result.isValid()) {
    System.out.println("Script is valid!");
} else {
    System.out.println("Script validation failed:");
    System.out.println(result.getValidationOutput());
}
```

#### **Validate Script Content**

```java
import com.kineticfire.exec.validation.ScriptValidator;
import java.nio.file.Paths;
import java.io.File;

// Validate using File object
File scriptFile = new File("script.sh");
ScriptValidationResult result = ScriptValidator.validateScript(scriptFile);

// Validate using Path object
Path scriptPath = Paths.get("/path/to/script.sh");
ScriptValidationResult result2 = ScriptValidator.validateScript(scriptPath);

// Handle validation results
if (!result.isValid()) {
    System.out.println("Validation Issues Found:");
    System.out.println(result.getValidationOutput());
    
    // Get exit code for detailed error handling
    System.out.println("Shellcheck exit code: " + result.getExitCode());
}
```

#### **Error Handling for Missing Dependencies**

```java
import com.kineticfire.exec.validation.ScriptValidator;
import java.io.IOException;

try {
    ScriptValidationResult result = ScriptValidator.validateScript("script.sh");
    // Process results...
} catch (IOException e) {
    if (e.getMessage().contains("shellcheck")) {
        System.err.println("Shellcheck is not installed or not available in PATH");
        System.err.println("Please install shellcheck to use script validation");
    } else {
        System.err.println("Script validation failed: " + e.getMessage());
    }
}
```

## Documentation

Complete API documentation and examples are available for all `exec` functionality.

### **Package Structure**

The `exec` library is organized into focused packages:

#### **`com.kineticfire.exec`**
Core execution utilities for running system processes with flexible return types and comprehensive error handling.

**Key Classes:**
- `Exec` - Main utility class for process execution
- `TaskExecutionException` - Custom exception for process execution failures

#### **`com.kineticfire.exec.validation`**
Script validation tools using shellcheck for automated shell script analysis.

**Key Classes:**
- `ScriptValidator` - Main utility class for script validation
- `ScriptValidationResult` - Encapsulates validation results and metadata

### **API Documentation**

Full JavaDoc API documentation will be available at Maven Central upon release, providing:

- Complete method signatures and parameter descriptions
- Usage examples for all public APIs
- Exception handling guidelines
- Best practices and performance considerations

### **Additional Resources**

- **Source Code**: [GitHub Repository](https://github.com/kineticfire-labs/exec)
- **Issues and Support**: [GitHub Issues](https://github.com/kineticfire-labs/exec/issues)
- **Examples**: See [Usage](#usage) section for comprehensive code examples

## Building

### **Prerequisites**

- **Java 21** or later
- **Gradle 9.0** or later (wrapper included)
- **shellcheck** (for script validation functionality)

### **Testing**

Run the test suite:
```bash
./gradlew test
```

Run specific test classes:
```bash
./gradlew test --tests "*.ExecTest"
```

### **Building**

To build the project, these properties must be defined:
- `project_release`: The version of the project release (must be valid semantic version)
- `exec_lib_version`: The version of the library (must be valid semantic version)

**Build command** (example with version 1.0.0):
```bash
./gradlew -Pproject_release=1.0.0 -Pexec_lib_version=1.0.0 clean build
```

This produces:
- `exec-1.0.0.jar` - Main library JAR
- `exec-1.0.0-sources.jar` - Source code JAR  
- `exec-1.0.0-javadoc.jar` - JavaDoc JAR
- CycloneDX SBOM for dependency tracking

### **Publishing**

#### **Test Publishing**

**Generate Publication Files:**
```bash
./gradlew generatePomFileForMavenPublication -Pexec_lib_version=1.0.0  --no-configuration-cache
```
Generates POM file at `build/publications/maven/pom-default.xml` for inspection.

**Publish to Local Repository:**
```bash
./gradlew publishToMavenLocal -Pexec_lib_version=1.0.0  --no-configuration-cache
```
Publishes to `ls -al ~/.m2/repository/com/kineticfire/exec/1.0.0`.  Check there to verify:
- All artifacts generate correctly
- POM metadata is complete
- No build errors occur
- JAR contents are correct

#### **Publish to Maven Central Portal**

1. **Set up credentials** in `~/.gradle/gradle.properties` (**NEVER** commit to repository):
   ```properties
   # Central Portal Publishing Credentials
   centralPortalUsername=your-sonatype-username
   centralPortalPassword=your-sonatype-password
   
   # GPG Signing Configuration
   # GPG                                                                                                                                                              
   signing.gnupg.keyName=<private key long>
   signing.gnupg.passphrase=<passphrase>
   ```

2. **Publish to Central Portal:**

Stage:
`./gradlew publishToSonatype closeSonatypeStagingRepository -Pexec_lib_version=1.0.0 --no-configuration-cache --info`

Then check Central Portal.  Click the "Publish" button to publish, else "Drop" to drop.

Or combined: `./gradlew publishToSonatype closeSonatypeStagingRepository closeAndReleaseSonatypeStagingRepository -Pexec_lib_version=1.0.0 --no-configuration-cache --info`

### **Known Issues**

- **Configuration Cache**: The license plugin has compatibility issues with Gradle 9's configuration cache. 
Use `--no-configuration-cache` flag until the plugin is updated. This affects build performance only, not functionality.

## License
The `exec` project is released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
