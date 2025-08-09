#!/bin/bash

# VibeTDD Project Setup Script
# Based on VibeTDD Experiment 4.1: Project Setup and the Automation Reality Check
# This script adapts the template for a specific project following the lessons learned

set -e  # Exit on any error

# Configuration parameters
PROJECT_NAME=""
BASE_PACKAGE=""
MODULE_NAME=""

# Function to show usage
show_usage() {
    echo "Usage: $0 --project-name <name> --base-package <package> --module-name <name>"
    echo ""
    echo "Parameters:"
    echo "  --project-name   Project name (e.g., 'payouts-api')"
    echo "  --base-package   Base package name (e.g., 'dev.vibetdd')"
    echo "  --module-name    Module name (e.g., 'payouts')"
    echo ""
    echo "Example:"
    echo "  $0 --project-name payouts-api --base-package dev.vibetdd --module-name payouts"
}

# Function to convert first letter to uppercase
to_first_upper() {
    local word="$1"
    local first=$(printf "%s" "$word" | cut -c1 | tr '[:lower:]' '[:upper:]')
    local rest=$(printf "%s" "$word" | cut -c2-)
    echo "$first$rest"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --project-name)
            PROJECT_NAME="$2"
            shift 2
            ;;
        --base-package)
            BASE_PACKAGE="$2"
            shift 2
            ;;
        --module-name)
            MODULE_NAME="$2"
            shift 2
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            echo "Unknown parameter: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Validate required parameters
if [[ -z "$PROJECT_NAME" || -z "$BASE_PACKAGE" || -z "$MODULE_NAME" ]]; then
    echo "Error: Missing required parameters"
    show_usage
    exit 1
fi

# Get the script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

# Validate we're in the right directory (should contain module-template)
if [[ ! -d "module-template" ]]; then
    echo "Error: module-template directory not found in project root: $PROJECT_ROOT"
    echo "Expected project structure:"
    echo "  project-root/"
    echo "  ├── scripts/$(basename "$0")"
    echo "  ├── module-template/"
    echo "  └── other-modules/"
    exit 1
fi

echo "Script location: $SCRIPT_DIR"
echo "Project root: $PROJECT_ROOT"
echo "Starting project setup with:"
echo "  Project Name: $PROJECT_NAME"
echo "  Base Package: $BASE_PACKAGE"
echo "  Module Name: $MODULE_NAME"

# Convert module name to proper case for class names
MODULE_CLASS_NAME=$(to_first_upper "$MODULE_NAME")
echo "  Module Class Name: $MODULE_CLASS_NAME"

# Step 1: Initialize git repository
echo "Step 1: Initializing git repository..."
git init

# Step 2: Initial commit of template
echo "Step 2: Creating initial commit..."
git add --all
git commit -m "Initial template commit" || echo "Nothing to commit or git already initialized"

# Step 3: Make a copy of module-template to temp directory
echo "Step 3: Creating backup of module-template..."
cp -r module-template /tmp/module-template-backup

# Step 4: Rename module-template to the new module name
echo "Step 4: Renaming module-template to $MODULE_NAME..."
mv module-template "$MODULE_NAME"

# Step 5: Update project name in all pom.xml files
echo "Step 5: Updating project name to $PROJECT_NAME..."
find . -name "pom.xml" -type f -exec sed -i.bak "s/api-template-modular-basic/$PROJECT_NAME/g" {} \;

# Step 6: Rename package directories from com/company to new base package
echo "Step 6: Renaming package directories..."
# Convert package notation to directory path
OLD_PACKAGE_PATH="com/company"
NEW_PACKAGE_PATH=$(echo "$BASE_PACKAGE" | tr '.' '/')

# Find all directories with the old package structure and rename them
find . -type d -path "*src/main/kotlin/$OLD_PACKAGE_PATH*" -o -path "*src/test/kotlin/$OLD_PACKAGE_PATH*" | while read dir; do
    new_dir=$(echo "$dir" | sed "s|$OLD_PACKAGE_PATH|$NEW_PACKAGE_PATH|g")
    echo "  Renaming directory: $dir -> $new_dir"
    mkdir -p "$(dirname "$new_dir")"
    mv "$dir" "$new_dir"
done

# Clean up empty com/company directories
find . -type d -name "company" -empty -delete 2>/dev/null || true
find . -type d -name "com" -empty -delete 2>/dev/null || true

# Step 7: Update package declarations in source files
echo "Step 7: Updating package declarations in source files..."
find . -name "*.kt" -type f -exec sed -i.bak "s/package com\.company/package $BASE_PACKAGE/g" {} \;
find . -name "*.kt" -type f -exec sed -i.bak "s/import com\.company/import $BASE_PACKAGE/g" {} \;

# Step 8: Update ModuleTemplate references to new module name
echo "Step 8: Updating ModuleTemplate references to $MODULE_CLASS_NAME..."
find "$MODULE_NAME" -name "*.kt" -type f -exec sed -i.bak "s/ModuleTemplate/$MODULE_CLASS_NAME/g" {} \;
find "$MODULE_NAME" -name "*.xml" -type f -exec sed -i.bak "s/ModuleTemplate/$MODULE_CLASS_NAME/g" {} \;

# Step 9: Update module-template references in pom.xml files
echo "Step 9: Updating module references in pom.xml files..."
find . -name "pom.xml" -type f -exec sed -i.bak "s/module-template/$MODULE_NAME/g" {} \;

# Step 10: Clean up backup files
echo "Step 10: Cleaning up backup files..."
find . -name "*.bak" -type f -delete

# Step 11: Test compilation
echo "Step 11: Testing compilation..."
if mvn clean test -q; then
    echo "✓ Compilation successful!"
else
    echo "✗ Compilation failed. Please check the errors above."
    echo "You may need to manually fix some references."
    exit 1
fi

# Step 12: Restore module-template from backup
echo "Step 12: Restoring module-template from backup..."
mv /tmp/module-template-backup module-template

# Step 13: Final git commit
echo "Step 13: Creating final commit..."
git add --all
git commit -m "Setup project: $PROJECT_NAME with module: $MODULE_NAME"

echo ""
echo "✓ Project setup completed successfully!"
echo ""
echo "Summary of changes:"
echo "  - Project renamed to: $PROJECT_NAME"
echo "  - Base package changed to: $BASE_PACKAGE"
echo "  - New module created: $MODULE_NAME (with class names: $MODULE_CLASS_NAME)"
echo "  - module-template restored for future use"
echo "  - All changes committed to git"
echo ""
echo "Next steps:"
echo "  1. Review the generated code in the $MODULE_NAME module"
echo "  2. Update README.md with project-specific information"
echo "  3. Start implementing your business logic following VibeTDD conventions"