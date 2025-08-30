# Proposed Feature: Cross-Platform Script Validation

## Overview

This document outlines the proposed expansion of script validation capabilities to support Windows PowerShell and macOS 
platforms, moving beyond the current Unix-only shellcheck integration.

## Current State

The `ScriptValidator` class currently:
- Only supports Unix-like systems (Linux)
- Uses shellcheck for bash/sh script validation
- Throws `UnsupportedOperationException` for Windows and macOS
- Contradicts the library's "cross-platform" promise in README

```java
// Current limitations in ScriptValidator.validateScript()
if (os.contains("win")) {
    throw new UnsupportedOperationException("Script validation not supported on Windows.");
} else if (os.contains("mac")) {
    throw new UnsupportedOperationException("Script validation not supported on Mac.");
}
```

## Proposed Solution

### Windows PowerShell Support

**Tool:** PSScriptAnalyzer
- Microsoft's official static code analyzer for PowerShell
- Available via PowerShell Gallery: `Install-Module -Name PSScriptAnalyzer`
- Similar output format to shellcheck with rule-based validation

**Implementation Approach:**
```java
private static Map<String,String> validateScriptForWindowsPlatform(String script) {
    // Check if script is PowerShell (.ps1) or batch (.bat/.cmd)
    // For PowerShell: Use PSScriptAnalyzer via powershell.exe
    // For batch: Basic syntax checking or fallback validation
}
```

**Command Structure:**
```powershell
powershell.exe -Command "Invoke-ScriptAnalyzer -Path 'script.ps1' -Severity Error,Warning"
```

### macOS Support

**Tool:** shellcheck (available via Homebrew)
- Same tool as Linux, just enable it on macOS
- Most macOS developers already have Homebrew
- Consistent behavior across Unix-like platforms

**Simple Implementation:**
```java
} else if (os.contains("mac")) {
    responseMap = validateScriptForUnixLikePlatform(script);  // Same as Linux
}
```

## Development and Testing Challenges

### Testing on Ubuntu Development Environment

Since the primary development occurs on Ubuntu, testing Windows/PowerShell support requires alternative approaches:

#### Option A: Docker-based Testing (Recommended)

**Advantages:**
- Runs on Ubuntu via Docker
- Uses official Microsoft PowerShell runtime  
- Can test actual PSScriptAnalyzer integration
- Reproducible testing environment
- No need for Windows VM or hardware

**Implementation:**
```dockerfile
# PowerShell validation container
FROM mcr.microsoft.com/powershell:latest

# Install PSScriptAnalyzer
RUN pwsh -Command "Install-Module -Name PSScriptAnalyzer -Force -Scope AllUsers"

# Copy test scripts and validation logic
COPY test-scripts/ /tests/
WORKDIR /tests

# Test command
CMD ["pwsh", "-Command", "Invoke-ScriptAnalyzer -Path test.ps1"]
```

**Usage in Development:**
```bash
# Build test container
docker build -t exec-powershell-test .

# Run validation tests
docker run --rm -v $(pwd)/test-scripts:/tests exec-powershell-test
```

#### Option B: PowerShell Core on Ubuntu

**Advantages:**
- Native installation on Ubuntu
- No Docker overhead
- Direct integration testing

**Limitations:**
- May have subtle differences from Windows PowerShell
- Module availability might vary
- Not true Windows environment testing

**Installation:**
```bash
# Install PowerShell Core on Ubuntu
sudo snap install powershell --classic
# or via Microsoft repository
```

#### Option C: GitHub Actions CI Matrix

**Complete Platform Coverage:**
```yaml
name: Cross-Platform Tests
strategy:
  matrix:
    os: [ubuntu-latest, windows-latest, macos-latest]
    include:
      - os: windows-latest
        script-type: powershell
        validator: PSScriptAnalyzer
      - os: macos-latest  
        script-type: bash
        validator: shellcheck
      - os: ubuntu-latest
        script-type: bash
        validator: shellcheck
```

**Benefits:**
- Tests on actual target platforms
- Automated validation across all OSes
- Catches platform-specific issues
- No local environment setup needed

## Technical Implementation Details

### Script Type Detection

**File Extension Based:**
- `.ps1` → PowerShell validation (Windows)
- `.sh`, `.bash` → shellcheck validation (Unix-like)
- `.bat`, `.cmd` → Basic batch file validation (Windows)

**Shebang Detection:**
```java
private static ScriptType detectScriptType(String scriptPath) {
    // Read first line for shebang detection
    // #!/bin/bash → BASH
    // #!/usr/bin/env pwsh → POWERSHELL
    // File extension fallback
}
```

### Error Handling Strategy

**Tool Availability Checks:**
```java
private static void ensurePSScriptAnalyzerAvailable() throws IOException {
    try {
        List<String> checkCommand = Arrays.asList("powershell", "-Command", 
            "Get-Module -ListAvailable -Name PSScriptAnalyzer");
        Map<String,String> result = Exec.exec(checkCommand);
        
        if (!result.get("exitValue").equals("0")) {
            throw new IOException(createPSScriptAnalyzerUnavailableMessage());
        }
    } catch (IOException e) {
        throw new IOException(createPSScriptAnalyzerUnavailableMessage(), e);
    }
}
```

**Graceful Degradation:**
- If PSScriptAnalyzer unavailable → Provide installation instructions
- If shellcheck unavailable on macOS → Suggest Homebrew installation
- Consistent error message format across platforms

### Output Format Standardization

**Challenge:** Different tools have different output formats
- shellcheck: Plain text or JSON
- PSScriptAnalyzer: PowerShell objects or formatted text

**Solution:** Normalize to common format
```java
private static Map<String,String> normalizeValidationOutput(
    String rawOutput, int exitCode, ValidationTool tool) {
    // Convert tool-specific output to standardized format
    // Maintain consistency with existing ScriptValidationResult
}
```

## Integration with Existing Architecture

### Backward Compatibility

**Maintain Existing API:**
- All current `validateScript()` methods continue working
- Same return types and exception behavior
- No breaking changes to existing functionality

**Platform-Aware Validation:**
```java
public static Map<String,String> validateScript(String script) 
        throws IOException, UnsupportedOperationException {
    
    String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    ScriptType scriptType = detectScriptType(script);
    
    if (os.contains("win")) {
        return validateScriptForWindows(script, scriptType);
    } else if (os.contains("mac") || os.contains("nux") || os.contains("nix")) {
        return validateScriptForUnixLike(script, scriptType);
    } else {
        throw new UnsupportedOperationException("OS '" + os + "' not supported");
    }
}
```

### Testing Strategy

**Comprehensive Test Matrix:**
```
Platform    | Script Type | Validator       | Test Cases
------------|-------------|-----------------|------------------
Windows     | PowerShell  | PSScriptAnalyzer| Valid/Invalid .ps1
Windows     | Batch       | Basic Check     | Valid/Invalid .bat  
macOS       | Bash        | shellcheck      | Valid/Invalid .sh
Linux       | Bash        | shellcheck      | Valid/Invalid .sh
```

**Mock Testing for Development:**
- Mock PSScriptAnalyzer responses during Ubuntu development
- Docker integration tests for real validation
- CI pipeline tests for platform verification

## Future Considerations

### Additional Script Types
Once the architecture supports multiple validators:
- Python script validation (pylint, flake8)
- JavaScript validation (eslint)
- YAML validation (yamllint)

### Configuration Integration
Cross-platform validation options:
- Platform-specific rule configurations
- Tool preference settings
- Fallback validation strategies

## Risk Assessment

**High Risk:**
- Platform-specific bugs difficult to catch in development
- Tool availability variations across environments
- Performance differences between validation tools

**Mitigation:**
- Comprehensive CI testing across platforms
- Clear error messages for missing tools
- Consistent timeout and resource handling

**Medium Risk:**
- Output format differences between tools
- Path handling differences (Windows vs Unix)
- Character encoding issues

**Low Risk:**
- Breaking changes to existing functionality (good abstraction)
- Performance regression (similar tool execution patterns)

## Implementation Timeline

**Prerequisites:**
- Complete Phase 1-3 of main implementation plan
- Set up Docker-based testing infrastructure
- Establish CI pipeline for cross-platform testing

**Estimated Effort:** 2-3 weeks
- Week 1: PowerShell validation implementation and Docker testing
- Week 2: macOS enablement and platform detection improvements  
- Week 3: Integration testing, error handling, and documentation

This feature would significantly enhance the library's cross-platform capabilities and align implementation with the documented promises, making it truly useful across diverse development environments.