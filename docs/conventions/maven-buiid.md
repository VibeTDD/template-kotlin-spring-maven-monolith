# Maven Build Configuration Conventions

This document defines the build configuration principles and conventions for creating multi-module Maven projects with clean dependency management.

## Core Build Principles

### Version Management
- **Use properties for ALL versions** - Never hardcode versions in dependencies
- **Define all versions in parent POM** - Child modules inherit versions without specifying them
- **Use latest LTS Java version** - Always target the most recent Long Term Support Java release
- **Use latest dependency versions** - Keep all external dependencies up to date
- **Auto-update dependencies** - Use versions-maven-plugin for automated version management

### Multi-Module Structure
- **Parent POM manages all versions** - Central version control through dependencyManagement
- **Child modules declare dependencies without versions** - Versions inherited from parent
- **Clean dependency boundaries** - Upward dependencies only, no circular references
- **Library vs Application modules** - Different packaging and plugin strategies

## Properties Section Organization

Properties must follow this exact order with comment separators:

### Required Order:
1. **Language Versions** - Java, Kotlin, Maven compiler settings
2. **Internal Dependencies** - Project module versions
3. **External Dependencies** - Third-party library versions
4. **Testing Dependencies** - Test framework versions
5. **Plugins** - Maven plugin versions

### Comment Separators:
```xml
<!--  LANGUAGE VERSIONS  -->
<!--  INTERNAL DEPENDENCIES  -->
<!--  EXTERNAL DEPENDENCIES  -->
<!--  TESTING DEPENDENCIES  -->
<!--  PLUGINS  -->
```

## Dependency Management Section Organization

Dependencies must follow this exact order with comment separators:

### Required Order:
1. **Internal Dependencies** - Project modules
2. **External Dependencies** - Third-party libraries and BOMs
3. **Testing Dependencies** - Test-specific libraries

### BOM Usage:
- Always use BOM imports for Spring Boot, Kotlin, and other frameworks
- Import BOMs before individual dependencies
- Use `<type>pom</type>` and `<scope>import</scope>` for BOMs

## Dependencies Section Organization

When declaring actual dependencies (not in dependencyManagement), follow the same order:

### Required Order:
1. **Internal Dependencies** - References to other project modules
2. **External Dependencies** - Third-party libraries
3. **Testing Dependencies** - Test-scoped dependencies

### Key Rules:
- Never specify versions in child modules - inherit from parent
- Use comment separators for clarity
- Group related dependencies together

## Plugin Configuration

### Plugin Management:
- Define all plugin versions in parent POM pluginManagement
- Child modules reference plugins without versions
- Use consistent plugin configuration across modules

### Essential Plugins:
- **versions-maven-plugin** - For automated dependency updates
- **maven-surefire-plugin** - For test execution
- **kotlin-maven-plugin** - For Kotlin compilation (if using Kotlin)
- **spring-boot-maven-plugin** - For Spring Boot applications (app module only)

## Module Dependency Rules

### Dependency Flow:
- Dependencies must flow upward only
- No circular dependencies between modules
- Commons module has no internal dependencies
- API/Application modules depend on domain modules
- Domain modules never depend on each other directly

### Communication Patterns:
- Use client interfaces for inter-module communication
- Domain modules expose simple, focused functionality
- API modules compose complex responses from multiple domains

## Best Practices Summary

### Version Management:
1. Never hardcode versions in dependencies
2. Use latest LTS Java (currently Java 21)
3. Keep dependencies current with versions-maven-plugin
4. Group properties logically with comment separators
5. Inherit all versions in child modules

### Multi-Module Organization:
1. Parent manages all versions through dependencyManagement
2. Child modules specify no versions - inherit from parent
3. Clean dependency flow - upward only, no cycles
4. Separate library and application packaging strategies
5. Use BOM imports for external dependency management

### Build Configuration:
1. Consistent property ordering across all POMs
2. Comment-separated sections for clarity
3. Plugin management in parent POM
4. Minimal child POMs with only necessary declarations
5. Automated version updates through Maven plugins

## Module Types

### Library Modules:
- Standard JAR packaging
- No executable plugins
- Dependencies without versions
- Minimal build configuration

### Application Modules:
- Executable JAR packaging
- Spring Boot plugin for repackaging
- Depends on other modules
- Contains main application class

### Parent Module:
- POM packaging only
- Defines all versions and plugin management
- No source code
- Declares all child modules