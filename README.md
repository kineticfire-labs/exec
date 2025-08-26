# exec
[![Powered by KineticFire Labs](https://img.shields.io/badge/Powered_by-KineticFire_Labs-CDA519?link=https%3A%2F%2Flabs.kineticfire.com%2F)](https://labs.kineticfire.com/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
<p></p>
`exec` is a Java library for executing native system processes and validating shell scripts.  It provides flexible APIs 
to run commands and return results as strings, maps, or exceptions, plus utilities for script analysis.


## Table of Contents
1. [Key Capabilities](#key-capabilities)
2. [Installation](#installation)
3. [Usage](#usage)
4. [License](#license)


## Key Capabilities
Key capabilities of `exec` include:
1. Ease executing commands on the command line with flexible return types (exec.Exec)
1. Validating bash/sh scripts (exec.ScriptValidator)


## Installation

todo


## Usage

todo

## Building

### Test
Run tests with: `./gradlew test`

### Build and Release
To build, these properties must be defined:
- `project_release`: the version of the project release, which must be a valid semantic version
- `Exec_lib_version`: the version of the library, which must be a valid semantic version

Build with, assuming both versions above are `1.0.0`: 
`./gradlew -Pproject_release=1.0.0 -PjavaExec_lib_version=1.0.0 clean build`

## License
The `exec` project is released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
