/*
 * (c) Copyright 2023-2025 exec Contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * KineticFire Labs: https://labs.kineticfire.com/
 *     project site: https://github.com/kineticfire-labs/exec/
 *
 */
package com.kineticfire.exec.validation;

import java.util.Map;


/**
 * Encapsulates the result of a script validation operation.
 * <p>
 * This class provides a type-safe, object-oriented interface for accessing script validation results,
 * offering better IDE support and compile-time safety compared to raw Map access.
 * <p>
 * Instances are typically created by calling one of the {@code ScriptValidator.validateScript()} methods
 * that return {@code ScriptValidationResult}.
 *
 */
public final class ScriptValidationResult {

    private final boolean valid;
    private final int exitCode;
    private final String validationOutput;
    private final String errorOutput;

    /**
     * Creates a new ScriptValidationResult from a validation result map.
     * <p>
     * This constructor is primarily intended for internal use by the ScriptValidator class.
     *
     * @param resultMap
     *    a Map containing validation results with keys: "isValid", "exitValue", "out", "err"
     * @throws IllegalArgumentException
     *    if the resultMap is null or missing required keys
     * @throws NumberFormatException
     *    if the exitValue cannot be parsed as an integer
     */
    public ScriptValidationResult( Map<String,String> resultMap ) {
        if ( resultMap == null ) {
            throw new IllegalArgumentException( "Result map cannot be null" );
        }

        if ( resultMap.get( "isValid" ) == null ) {
            throw new IllegalArgumentException( "Result map must contain 'isValid' key" );
        }

        if ( resultMap.get( "exitValue" ) == null ) {
            throw new IllegalArgumentException( "Result map must contain 'exitValue' key" );
        }

        this.valid = "true".equals( resultMap.get( "isValid" ) );

        try {
            this.exitCode = Integer.parseInt( resultMap.get( "exitValue" ) );
        } catch ( NumberFormatException e ) {
            throw new NumberFormatException( "Invalid exit value: " + resultMap.get( "exitValue" ) );
        }

        this.validationOutput = resultMap.get( "out" ) != null ? resultMap.get( "out" ) : "";
        this.errorOutput = resultMap.get( "err" ) != null ? resultMap.get( "err" ) : "";
    }

    /**
     * Returns whether the script validation was successful.
     *
     * @return true if the script is valid (no validation errors found), false otherwise
     */
    public boolean isValid( ) {
        return valid;
    }

    /**
     * Returns the exit code from the validation process.
     * <p>
     * For shellcheck validation on Unix-like systems, this is typically:
     * <ul>
     *    <li>0 - No issues found</li>
     *    <li>1 - Syntax or usage errors found</li>
     *    <li>2 - File access problems</li>
     *    <li>3 - No files specified or all files ignored</li>
     *    <li>4 - Interrupted by signal</li>
     * </ul>
     *
     * @return the exit code from the validation tool
     */
    public int getExitCode( ) {
        return exitCode;
    }

    /**
     * Returns the standard output from the validation process.
     * <p>
     * For shellcheck, this typically contains the validation results including warnings,
     * suggestions, and error messages in a human-readable format.
     *
     * @return the validation output as a string, or an empty string if no output was produced
     */
    public String getValidationOutput( ) {
        return validationOutput;
    }

    /**
     * Returns the standard error output from the validation process.
     * <p>
     * This typically contains error messages from the validation tool itself, such as
     * file access errors or tool-specific error messages.
     *
     * @return the error output as a string, or an empty string if no error output was produced
     */
    public String getErrorOutput( ) {
        return errorOutput;
    }

    /**
     * Returns a string representation of this validation result.
     *
     * @return a string containing the validation status, exit code, and any output
     */
    @Override
    public String toString( ) {
        StringBuilder sb = new StringBuilder( );
        sb.append( "ScriptValidationResult{" );
        sb.append( "valid=" ).append( valid );
        sb.append( ", exitCode=" ).append( exitCode );

        if ( !validationOutput.isEmpty( ) ) {
            sb.append( ", validationOutput='" ).append( validationOutput ).append( "'" );
        }

        if ( !errorOutput.isEmpty( ) ) {
            sb.append( ", errorOutput='" ).append( errorOutput ).append( "'" );
        }

        sb.append( "}" );
        return sb.toString( );
    }
}