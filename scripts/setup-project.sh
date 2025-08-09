#!/bin/bash

# VibeTDD Project Setup Script
# Adapts the template project to a specific domain
#
# Usage: ./scripts/setup-project.sh PROJECT_NAME BASE_PACKAGE MODULE_NAME
# Example: ./scripts/setup-project.sh payouts-api dev.vibetdd payouts

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
show_usage() {
    echo "Usage: ./scripts/$0 PROJECT_NAME BASE_PACKAGE MODULE_NAME"
    echo ""
    echo "Parameters:"
    echo "  PROJECT_NAME  - The name of the project (e.g., payouts-api)"
    echo "  BASE_PACKAGE  - The base package name (e.g., dev.vibetdd)"
    echo "  MODULE_NAME   - The module name (e.g., payouts)"
    echo ""
    echo "Example:"
    echo "  ./scripts/$0 payouts-api dev.vibetdd payouts"
    echo ""
    echo "This will:"
    echo "  - Initialize git repository"
    echo "  - Rename module-template to the specified module name"
    echo "  - Update all package references"
    echo "  - Update project name references"
    echo "  - Convert ModuleTemplate class names to PascalCase module name"
    echo "  - Test compilation"
    echo "  - Stage all changes in git"
}

# Check if correct number of arguments provided
if [ $# -ne 3 ]; then
    print_error "Incorrect number of arguments"
    show_usage
    exit 1
fi

# Parse arguments
PROJECT_NAME="$1"
BASE_PACKAGE="$2"
MODULE_NAME="$3"

# Convert module name to PascalCase for class names
# Take first character, make it uppercase, then append the rest unchanged
first=$(printf "%s" "$MODULE_NAME" | cut -c1 | tr '[:lower:]' '[:upper:]')
rest=$(printf "%s" "$MODULE_NAME" | cut -c2-)
MODULE_CLASS_NAME="$first$rest"

print_status "Starting project setup with parameters:"
echo "  Project Name: $PROJECT_NAME"
echo "  Base Package: $BASE_PACKAGE"
echo "  Module Name: $MODULE_NAME"
echo "  Module Class Name: $MODULE_CLASS_NAME"
echo ""

# Check if we're in the project root (should contain both scripts directory and module-template)
if [ ! -d "module-template" ]; then
    print_error "module-template directory not found. Are you in the project root directory?"
    print_warning "This script should be run from the project root as: ./scripts/setup-project.sh"
    exit 1
fi

if [ ! -d "scripts" ]; then
    print_warning "scripts directory not found. Make sure you're running from project root."
fi

# Step 1: Initialize git repository
print_status "Initializing git repository..."
if [ ! -d ".git" ]; then
    git init
else
    print_warning "Git repository already exists, skipping git init"
fi

# Step 2: Add all files to git
print_status "Adding all files to git..."
git add --all

# Step 3: Change project name in all modules
print_status "Updating project name to '$PROJECT_NAME'..."

# Find and update project name in pom.xml files
find . -name "pom.xml" -type f -exec sed -i.bak "s/api-template-modular-basic/$PROJECT_NAME/g" {} \;

# Clean up backup files
find . -name "*.bak" -type f -delete

# Step 4: Replace package references
print_status "Updating package references from 'com.company' to '$BASE_PACKAGE'..."

# Convert package notation to directory structure
OLD_PACKAGE_DIR="com/company"
NEW_PACKAGE_DIR=$(echo "$BASE_PACKAGE" | tr '.' '/')

# Update package declarations in all Java/Kotlin files
find . \( -name "*.java" -o -name "*.kt" \) -exec sed -i.bak "s/com\.company/$BASE_PACKAGE/g" {} \;

# Update package references in pom.xml files
find . -name "pom.xml" -type f -exec sed -i.bak "s/com\.company/$BASE_PACKAGE/g" {} \;

# Rename package directories (handle both java and kotlin)
print_status "Renaming package directories..."

# Function to move package directories safely
move_package_dirs() {
    local src_type="$1"  # main or test
    local lang_type="$2" # java or kotlin

    find . -path "*src/$src_type/$lang_type/$OLD_PACKAGE_DIR" -type d | while read -r dir; do
        if [ -d "$dir" ]; then
            new_dir=$(echo "$dir" | sed "s|$OLD_PACKAGE_DIR|$NEW_PACKAGE_DIR|")
            mkdir -p "$(dirname "$new_dir")"
            print_status "Moving $src_type/$lang_type package: $dir -> $new_dir"
            mv "$dir" "$new_dir"
        fi
    done
}

# Move all package directories
move_package_dirs "main" "java"
move_package_dirs "test" "java"
move_package_dirs "main" "kotlin"
move_package_dirs "test" "kotlin"

# Clean up empty old package structure
print_status "Cleaning up empty old package directories..."
# Remove company directories first, then com directories
find . -path "*/src/*/java/com/company" -type d -delete 2>/dev/null || true
find . -path "*/src/*/kotlin/com/company" -type d -delete 2>/dev/null || true
find . -path "*/src/*/java/com" -type d -delete 2>/dev/null || true
find . -path "*/src/*/kotlin/com" -type d -delete 2>/dev/null || true

# More aggressive cleanup - remove any remaining com directories that are empty
find . -name "com" -type d -empty -delete 2>/dev/null || true
find . -path "*/com" -type d -empty -delete 2>/dev/null || true

# Clean up backup files
find . -name "*.bak" -type f -delete

# Step 5: Rename module-template to the new module name
print_status "Renaming module-template to '$MODULE_NAME'..."

if [ -d "module-template" ]; then
    mv "module-template" "$MODULE_NAME"
    print_status "Renamed module-template directory to $MODULE_NAME"
else
    print_error "module-template directory not found"
    exit 1
fi

# Step 6: Replace ModuleTemplate with the new class name
print_status "Updating class names from 'ModuleTemplate' to '$MODULE_CLASS_NAME'..."

# Update all Java/Kotlin files
find . \( -name "*.java" -o -name "*.kt" \) -exec sed -i.bak "s/ModuleTemplate/$MODULE_CLASS_NAME/g" {} \;

# Update pom.xml files for module references
find . -name "pom.xml" -type f -exec sed -i.bak "s/module-template/$MODULE_NAME/g" {} \;

# Update any other configuration files that might reference the module
find . \( -name "*.xml" -o -name "*.yml" -o -name "*.yaml" -o -name "*.properties" \) -exec sed -i.bak "s/module-template/$MODULE_NAME/g" {} \;
find . \( -name "*.xml" -o -name "*.yml" -o -name "*.yaml" -o -name "*.properties" \) -exec sed -i.bak "s/ModuleTemplate/$MODULE_CLASS_NAME/g" {} \;

# Clean up backup files
find . -name "*.bak" -type f -delete

# Final cleanup of any remaining empty com directories
print_status "Final cleanup of old package directories..."
find . -name "com" -type d -empty -delete 2>/dev/null || true
find . -path "*/com" -type d -empty -delete 2>/dev/null || true
# Also remove any company directories that might still exist
find . -name "company" -type d -empty -delete 2>/dev/null || true

# Step 7: Test compilation
print_status "Testing compilation with 'mvn clean test'..."
if mvn clean test -q; then
    print_status "Compilation successful!"
else
    print_error "Compilation failed. Please check the errors above and fix manually."
    print_warning "Common issues:"
    echo "  - Missing package imports after package rename"
    echo "  - Circular dependencies between modules"
    echo "  - Incorrect module references in pom.xml"
    exit 1
fi

# Step 8: Add all changes to git
print_status "Adding all changes to git..."
git add --all

# Final status
print_status "Project setup completed successfully!"
echo ""
echo "Summary of changes:"
echo "  ✓ Git repository initialized"
echo "  ✓ Project renamed to: $PROJECT_NAME"
echo "  ✓ Package changed to: $BASE_PACKAGE"
echo "  ✓ Module renamed to: $MODULE_NAME"
echo "  ✓ Class names updated to: $MODULE_CLASS_NAME"
echo "  ✓ Compilation tested successfully"
echo "  ✓ All changes staged in git"
echo ""
echo "Next steps:"
echo "  1. Review the changes: git status"
echo "  2. Commit the setup: git commit -m 'Initial project setup'"
echo "  3. Start developing your $MODULE_NAME module!"
echo ""
echo "To run the setup again or for other projects:"
echo "  ./scripts/setup-project.sh <project-name> <base-package> <module-name>"