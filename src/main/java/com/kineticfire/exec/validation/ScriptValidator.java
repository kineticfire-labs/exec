/*
 * (c) Copyright 2023-2025 'exec' Contributors. All rights reserved.
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

import com.kineticfire.exec.Exec;

import java.nio.file.Path;
import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;


/**
 * Provides system utilities.
 *
 */
public final class ScriptValidator {

   /**
    * Validates the script using a native command line process to run an OS-specific validation utility and returns a
    * Map result, including any error output from the process.
    * <p>
    * Returns a Map (unless an exception is thrown) with key-value pairs:
    * <ul>
    *    <li>isValid - String "true" if the script validated and String "false" otherwise; a "false" return value could
    *    indicate that the script failed validation or that an error occurred before validation; check the "exitValue",
    *    "out", and "err" values</li>
    *    <li>exitValue - the String representation of the integer exit value returned by the process; OS-specific
    *    meaning, where for Unix-like platforms the value is on the range of [0,255]; 0 for success and other values
    *    indicate an error; always defined</li>
    *    <li>out - the output returned by the process as a trimmed String, which could be an empty String; always
    *    defined</li>
    *    <li>err - contains the error output returned by the process as a trimmed String; defined if an error occurred
    *    (e.g. exitValue is non-zero)</li>
    * </ul>
    *
    * @param script
    *    the path as a Path to the script to validate
    * @return result as a Map of the script validation
    * @throws IllegalArgumentException
    *    if an illegal or inappropriate argument was passed to this method
    * @throws IOException
    *    if an I/O error occurs, including if the required 'shellcheck' utility is not available on the system
    * @throws NullPointerException
    *    if an element in task list is null
    * @throws SecurityException
    *    if a security manager exists and
    *    <ul>
    *       <li>when attemping to start the process, its checkExec method doesn't allow creation of the subprocess,
    *       or</li>
    *       <li>its checkPermission method doesn't allow access to the process environment</li>
    *    </ul>
    * @throws UnsupportedOperationException
    *    <ul>
    *       <li>if the operating system does not support the creation of processes</li>
    *       <li>if the operating system is not supported by this method</li>
    *    </ul>
    */
   public static Map<String,String> validateScript( Path script )
      throws IOException {

      return( validateScript( script.toString( ) ) );

   }

   /**
    * Validates the script using a native command line process to run an OS-specific validation utility and returns a
    * Map result, including any error output from the process.
    * <p>
    * Returns a Map (unless an exception is thrown) with key-value pairs:
    * <ul>
    *    <li>isValid - String "true" if the script validated and String "false" otherwise; a "false" return value could
    *    indicate that the script failed validation or that an error occurred before validation; check the "exitValue",
    *    "out", and "err" values</li>
    *    <li>exitValue - the String representation of the integer exit value returned by the process; OS-specific
    *    meaning, where for Unix-like platforms the value is on the range of [0,255]; 0 for success and other values
    *    indicate an error; always defined</li>
    *    <li>out - the output returned by the process as a trimmed String, which could be an empty String; always
    *    defined</li>
    *    <li>err - contains the error output returned by the process as a trimmed String; defined if an error occurred
    *    (e.g. exitValue is non-zero)</li>
    * </ul>
    *
    * @param script
    *    the path as a File to the script to validate
    * @return result as a Map of the script validation
    * @throws IllegalArgumentException
    *    if an illegal or inappropriate argument was passed to this method
    * @throws IOException
    *    if an I/O error occurs, including if the required 'shellcheck' utility is not available on the system
    * @throws NullPointerException
    *    if an element in task list is null
    * @throws SecurityException
    *    if a security manager exists and
    *    <ul>
    *       <li>when attemping to start the process, its checkExec method doesn't allow creation of the subprocess, or</li>
    *       <li>its checkPermission method doesn't allow access to the process environment</li>
    *    </ul>
    * @throws UnsupportedOperationException
    *    <ul>
    *       <li>if the operating system does not support the creation of processes</li>
    *       <li>if the operating system is not supported by this method</li>
    *    </ul>
    */
   public static Map<String,String> validateScript( File script )
         throws IOException {

      return( validateScript( script.toString( ) ) );

   }

   /**
    * Validates the script using a native command line process to run an OS-specific validation utility and returns a
    * Map result, including any error output from the process.
    * <p>
    * Returns a Map (unless an exception is thrown) with key-value pairs:
    * <ul>
    *    <li>isValid - String "true" if the script validated and String "false" otherwise; a "false" return value could
    *    indicate that the script failed validation or that an error occurred before validation; check the "exitValue",
    *    "out", and "err" values</li>
    *    <li>exitValue - the String representation of the integer exit value returned by the process; OS-specific
    *    meaning, where for Unix-like platforms the value is on the range of [0,255]; 0 for success and other values
    *    indicate an error; always defined</li>
    *    <li>out - the output returned by the process as a trimmed String, which could be an empty String; always
    *    defined</li>
    *    <li>err - contains the error output returned by the process as a trimmed String; defined if an error occurred
    *    (e.g. exitValue is non-zero)</li>
    * </ul>
    *
    * @param script
    *    the path as a String to the script to validate
    * @return result as a Map of the script validation
    * @throws IllegalArgumentException
    *    if an illegal or inappropriate argument was passed to this method
    * @throws IOException
    *    if an I/O error occurs, including if the required 'shellcheck' utility is not available on the system
    * @throws NullPointerException
    *    if an element in task list is null
    * @throws SecurityException
    *    if a security manager exists and
    *    <ul>
    *       <li>when attemping to start the process, its checkExec method doesn't allow creation of the subprocess,
    *       or</li>
    *       <li>its checkPermission method doesn't allow access to the process environment</li>
    *    </ul>
    * @throws UnsupportedOperationException
    *    <ul>
    *       <li>if the operating system does not support the creation of processes</li>
    *       <li>if the operating system is not supported by this method</li>
    *    </ul>
    */
   public static Map<String,String> validateScript( String script )
         throws IOException, UnsupportedOperationException {

      Map<String,String> responseMap;

      String os = System.getProperty( "os.name" ).toLowerCase( );

      if (os.contains("win")) {
         throw new UnsupportedOperationException( "Script validation not supported on Windows." );
      } else if (os.contains("mac")) {
         throw new UnsupportedOperationException( "Script validation not supported on Mac." );
      } else if (os.contains("nux") || os.contains("nix")) {
         responseMap = validateScriptForUnixLikePlatform( script );
      } else if (os.contains("sunos")) {
         throw new UnsupportedOperationException( "Script validation is not supported on SunOS." );
      } else {
         throw new UnsupportedOperationException( "OS '" + System.getProperty( "os.name" ) + "' not supported by " +
                 "this method" );
      }

      return( responseMap );
   }

   /**
    * Validates the script using a native command line process to run the 'shellcheck' utility and returns a Map result,
    * including any error output from the process.
    * <p>
    * Uses the 'shellcheck' utility for static analysis and linting tool for sh/bash scripts.
    * <p>
    * Returns a Map (unless an exception is thrown) with key-value pairs:
    * <ul>
    *    <li>isValid - String "true" if the script validated and String "false" otherwise; a "false" return value could
    *    indicate that the script failed validation or that an error occurred before validation; check the "exitValue"
    *    and "err" values</li>
    *    <li>exitValue - the String representation of the integer exit value returned by the process on the range of
    *    [0,255]; 0 for success and other values indicate an error; always defined</li>
    *    <li>out - the output returned by the process as a trimmed String, which could be an empty String; always
    *    defined</li>
    *    <li>err - contains the error output returned by the process as a trimmed String; defined if an error occurred
    *    (e.g. exitValue is non-zero)</li>
    * </ul>
    * <p>
    * Requirements:
    * <ul>
    *    <li>Unix-like system</li>
    *    <li>'shellcheck' utility is installed</li>
    *    <li>script to validate is a bash/sh script</li>
    * </ul>
    *
    * @param script
    *    the path as a String to the sh/bash script to validate
    * @return result as a Map of the script validation
    * @throws IllegalArgumentException
    *    if an illegal or inappropriate argument was passed to this method
    * @throws IOException
    *    if an I/O error occurs, including if the required 'shellcheck' utility is not available on the system
    * @throws NullPointerException
    *    if an element in task list is null
    * @throws SecurityException
    *    if a security manager exists and
    *    <ul>
    *       <li>when attemping to start the process, its checkExec method doesn't allow creation of the subprocess,
    *       or</li>
    *       <li>its checkPermission method doesn't allow access to the process environment</li>
    *    </ul>
    * @throws UnsupportedOperationException
    *    if the operating system does not support the creation of processes
    */
   private static Map<String,String> validateScriptForUnixLikePlatform( String script )
      throws IOException {

      ensureShellcheckAvailable();

      List<String> task = Arrays.asList( "shellcheck", script );

      Map<String,String> responseMap = Exec.exec( task );

      if ( responseMap.get( "exitValue" ).equals( "0" ) ) {
         responseMap.put( "isValid", "true" );
      } else {
         responseMap.put( "isValid", "false" );
      }

      return( responseMap );

   }

   /**
    * Ensures that the 'shellcheck' utility is available on the system for script validation.
    * <p>
    * This method checks if the 'shellcheck' command is available in the system PATH by attempting to execute
    * 'which shellcheck'. If 'shellcheck' is not found, it throws an IOException with helpful installation
    * instructions.
    * 
    * @throws IOException
    *    if 'shellcheck' is not available on the system, with detailed installation instructions
    */
   private static void ensureShellcheckAvailable() throws IOException {
      try {
         List<String> whichTask = Arrays.asList( "which", "shellcheck" );
         Map<String,String> result = Exec.exec( whichTask );
         
         if ( !result.get( "exitValue" ).equals( "0" ) ) {
            throw new IOException( createShellcheckUnavailableMessage() );
         }
      } catch ( IOException e ) {
         throw new IOException( createShellcheckUnavailableMessage(), e );
      }
   }

   /**
    * Creates a helpful error message when shellcheck is not available.
    * 
    * @return detailed error message with installation instructions
    */
   private static String createShellcheckUnavailableMessage() {
      return "The 'shellcheck' utility is required for script validation but is not available on this system. " +
             "Please install shellcheck using one of the following methods:\n" +
             "  - Ubuntu/Debian: apt install shellcheck\n" +
             "  - CentOS/RHEL/Fedora: yum install ShellCheck (or dnf install ShellCheck)\n" +
             "  - macOS: brew install shellcheck\n" +
             "  - From source: https://github.com/koalaman/shellcheck#installing\n" +
             "After installation, ensure 'shellcheck' is available in your system PATH.";
   }


   private ScriptValidator( ) {
      throw new UnsupportedOperationException( "Class instantiation not supported" );
   }
}
