# Proposed Enhancements for Exec Library

## Overview

This document outlines planned enhancements to the exec library to improve developer experience, error reporting capabilities, and configuration flexibility while maintaining backward compatibility.

## Phase 1: Foundation ✅ COMPLETED

### 1.1 Platform Detection Improvement ✅
- **Issue:** Using `toLowerCase()` without locale specification causes issues in Turkish locale
- **Solution:** Use `toLowerCase(Locale.ROOT)` for predictable behavior
- **Status:** COMPLETED

### 1.2 Thread Safety Documentation ✅  
- **Issue:** Thread safety guarantees not explicitly documented
- **Solution:** Added comprehensive JavaDoc to `Exec` and `ScriptValidator` classes
- **Status:** COMPLETED

## Phase 2: Structured Error Reporting

**Estimated Effort:** 3-5 days  
**Priority:** High - Significantly improves developer experience

### 2.1 Core Classes

**ValidationSeverity Enum:**
```java
public enum ValidationSeverity {
    ERROR,      // Syntax errors, critical issues
    WARNING,    // Potential problems, deprecated usage
    INFO,       // Informational suggestions
    STYLE       // Code style recommendations
}
```

**ScriptValidationIssue Class:**
```java
public final class ScriptValidationIssue {
    private final ValidationSeverity severity;
    private final int lineNumber;
    private final int columnNumber;
    private final String ruleId;          // e.g., "SC2086"
    private final String message;         // Human-readable description
    private final String suggestion;      // Optional fix suggestion
    private final String wikiUrl;         // Link to rule documentation
    
    // Methods: getSeverity(), getLineNumber(), getMessage(), etc.
    // toString() for debugging
    // equals()/hashCode() for collections
}
```

### 2.2 Enhanced ScriptValidationResult

**New Methods to Add:**
```java
public final class ScriptValidationResult {
    // Existing methods remain unchanged
    
    // New structured access methods
    public List<ScriptValidationIssue> getIssues() { ... }
    public List<ScriptValidationIssue> getErrors() { ... }
    public List<ScriptValidationIssue> getWarnings() { ... }
    public List<ScriptValidationIssue> getInfos() { ... }
    
    // Convenience counting methods
    public int getErrorCount() { ... }
    public int getWarningCount() { ... }
    public int getIssueCount() { ... }
    
    // Semantic checking methods
    public boolean hasSyntaxErrors() { ... }
    public boolean hasSecurityIssues() { ... }  // Based on rule categories
    public boolean hasStyleIssues() { ... }
}
```

### 2.3 Implementation Strategy

**Shellcheck JSON Integration:**
- Use `shellcheck --format=json` for structured output
- Parse JSON response into `ScriptValidationIssue` objects
- Map shellcheck severity levels to `ValidationSeverity` enum
- Extract rule IDs, line numbers, and messages

**Backward Compatibility:**
- All existing methods continue to work unchanged
- Existing `getValidationOutput()` remains as formatted text
- New structured methods provide additional access to the same data

**Fallback Strategy:**
- If JSON parsing fails, fall back to text parsing
- Graceful degradation for older shellcheck versions
- Clear error messages if structured data unavailable

### 2.4 Usage Examples

**Basic Structured Error Handling:**
```java
ScriptValidationResult result = ScriptValidator.validateScriptWithResult("script.sh");

if (!result.isValid()) {
    System.out.println("Found " + result.getErrorCount() + " errors:");
    
    for (ScriptValidationIssue issue : result.getErrors()) {
        System.out.printf("Line %d: %s (%s)%n", 
            issue.getLineNumber(), 
            issue.getMessage(), 
            issue.getRuleId());
    }
}
```

**IDE Integration Example:**
```java
// For IDE plugins, linters, etc.
List<ScriptValidationIssue> issues = result.getIssues();
for (ScriptValidationIssue issue : issues) {
    if (issue.getSeverity() == ValidationSeverity.ERROR) {
        editor.addErrorAnnotation(issue.getLineNumber(), issue.getMessage());
    } else if (issue.getSeverity() == ValidationSeverity.WARNING) {
        editor.addWarningAnnotation(issue.getLineNumber(), issue.getMessage());
    }
}
```

### 2.5 Testing Requirements

**JSON Parsing Tests:**
- Valid JSON responses from shellcheck
- Malformed JSON handling
- Empty results handling
- Different shellcheck versions

**Issue Classification Tests:**
- Severity level mapping accuracy
- Rule ID extraction
- Line number parsing
- Message formatting

**Backward Compatibility Tests:**
- Existing API methods return same results
- New methods provide consistent data
- Performance impact measurement

## Phase 3: Configuration Options

**Estimated Effort:** 5-7 days  
**Priority:** Medium - Provides flexibility for different use cases

### 3.1 Configuration Infrastructure

**ValidationOptions Class:**
```java
public final class ValidationOptions {
    // Builder pattern for easy configuration
    public static Builder builder() { return new Builder(); }
    
    // Configuration properties
    private final Set<ValidationSeverity> reportLevels;
    private final Set<String> enabledRules;
    private final Set<String> disabledRules;
    private final ShellDialect dialect;
    private final boolean followIncludes;
    private final boolean checkIncludes;
    
    public static final class Builder {
        public Builder reportLevel(ValidationSeverity... levels) { ... }
        public Builder enableRule(String... ruleIds) { ... }
        public Builder disableRule(String... ruleIds) { ... }
        public Builder shellDialect(ShellDialect dialect) { ... }
        public Builder followIncludes(boolean follow) { ... }
        public ValidationOptions build() { ... }
    }
}
```

**ShellDialect Enum:**
```java
public enum ShellDialect {
    AUTO_DETECT,    // Let shellcheck determine
    BASH,           // Bash-specific rules
    DASH,           // Dash/POSIX shell
    KSH,            // Korn shell
    ZSH,            // Z shell
    SH              // POSIX sh
}
```

### 3.2 New API Methods

**Overloaded Methods:**
```java
// All existing validateScriptWithResult variants get configuration overloads
public static ScriptValidationResult validateScriptWithResult(
    String script, ValidationOptions options) throws IOException;
    
public static ScriptValidationResult validateScriptWithResult(
    Path script, ValidationOptions options) throws IOException;
    
public static ScriptValidationResult validateScriptWithResult(
    File script, ValidationOptions options) throws IOException;
```

### 3.3 Shellcheck Command Generation

**Mapping to Shellcheck Options:**
```java
private static List<String> buildShellcheckCommand(String scriptPath, ValidationOptions options) {
    List<String> command = new ArrayList<>(Arrays.asList("shellcheck"));
    
    // Format for structured parsing
    command.add("--format=json");
    
    // Shell dialect
    if (options.getDialect() != ShellDialect.AUTO_DETECT) {
        command.add("--shell=" + options.getDialect().name().toLowerCase());
    }
    
    // Severity filtering
    if (!options.getReportLevels().equals(DEFAULT_LEVELS)) {
        String severities = options.getReportLevels().stream()
            .map(s -> s.name().toLowerCase())
            .collect(Collectors.joining(","));
        command.add("--severity=" + severities);
    }
    
    // Rule management
    if (!options.getDisabledRules().isEmpty()) {
        String rules = String.join(",", options.getDisabledRules());
        command.add("--exclude=" + rules);
    }
    
    if (!options.getEnabledRules().isEmpty()) {
        String rules = String.join(",", options.getEnabledRules());
        command.add("--enable=" + rules);
    }
    
    // Include handling
    if (options.isFollowIncludes()) {
        command.add("--external-sources");
    }
    
    command.add(scriptPath);
    return command;
}
```

### 3.4 Usage Examples

**Basic Configuration:**
```java
ValidationOptions options = ValidationOptions.builder()
    .reportLevel(ValidationSeverity.ERROR, ValidationSeverity.WARNING)
    .shellDialect(ShellDialect.BASH)
    .disableRule("SC2086")  // Disable word splitting warnings
    .build();

ScriptValidationResult result = ScriptValidator
    .validateScriptWithResult("script.sh", options);
```

**Advanced Configuration:**
```java
ValidationOptions strictOptions = ValidationOptions.builder()
    .reportLevel(ValidationSeverity.ERROR, ValidationSeverity.WARNING, ValidationSeverity.STYLE)
    .enableRule("SC1000", "SC1001")  // Enable additional rules
    .followIncludes(true)            // Check sourced files
    .shellDialect(ShellDialect.BASH)
    .build();

// Use for CI/CD strict checking
ScriptValidationResult result = ScriptValidator
    .validateScriptWithResult(Paths.get("deploy-script.sh"), strictOptions);

if (result.getErrorCount() > 0 || result.getWarningCount() > 5) {
    throw new RuntimeException("Script quality gate failed");
}
```

### 3.5 Implementation Details

**Option Validation:**
- Validate rule IDs exist in shellcheck
- Check shellcheck version compatibility for specific features
- Provide meaningful error messages for invalid configurations

**Command Generation:**
- Build shellcheck command line from options
- Handle option conflicts gracefully
- Support version-specific feature detection

**Error Handling:**
- Clear error messages when shellcheck doesn't support requested options
- Fallback behavior for unavailable features
- Version compatibility warnings

### 3.6 Testing Strategy

**Configuration Tests:**
- All option combinations work correctly
- Invalid configurations produce clear error messages
- Command line generation accuracy

**Integration Tests:**
- Different shellcheck versions
- Various script types and complexity levels
- Performance with complex configurations

**Compatibility Tests:**
- Existing API unchanged
- New methods integrate seamlessly
- No performance regression

## Future Enhancements (Not Planned)

### Cross-Platform Support
- See `docs/proposed-feature-powershell.md` for detailed analysis
- Windows PowerShell validation
- macOS shellcheck enablement
- Comprehensive platform testing strategy

### Additional Script Types
- Python validation (pylint, flake8)
- JavaScript validation (eslint)
- YAML validation (yamllint)

### Performance Optimizations
- Process pooling for batch operations
- Result caching based on content hash
- Async/reactive API support

## Implementation Timeline

**Phase 2: Structured Error Reporting**
- Week 1: Core classes and JSON parsing
- Week 2: Integration and testing
- **Total: ~1.5 weeks**

**Phase 3: Configuration Options**
- Week 1: Configuration infrastructure and API
- Week 2: Shellcheck integration and command generation  
- Week 3: Testing and documentation
- **Total: ~2-2.5 weeks**

**Overall Timeline: 3.5-4 weeks for both phases**

## Benefits Summary

**Phase 2 Benefits:**
- Rich, structured error reporting for better developer experience
- Better integration with IDEs, linters, and development tools
- Programmatic access to validation issues for automated processing
- Maintains full backward compatibility

**Phase 3 Benefits:**
- Flexible configuration for different coding standards
- Enterprise-ready validation options
- Full utilization of shellcheck's capabilities
- Support for complex development workflows

Both phases significantly enhance the library's capabilities while maintaining the core design principles of simplicity, reliability, and backward compatibility.