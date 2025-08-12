#!/bin/bash

# Setup script to prepare the template for a real project
# Usage: ./scripts/setup-project.sh PROJECT_NAME BASE_PACKAGE MODULE_NAME TEMPLATE_DIR
# Example: ./scripts/setup-project.sh payouts-api dev.vibetdd payouts ../api-template-modular-basic

set -e

# Check if correct number of arguments provided
if [ $# -ne 4 ]; then
    echo "Usage: $0 PROJECT_NAME BASE_PACKAGE MODULE_NAME TEMPLATE_DIR"
    echo "Example: $0 payouts-api dev.vibetdd payouts ../api-template-modular-basic"
    exit 1
fi

PROJECT_NAME="$1"
BASE_PACKAGE="$2"
MODULE_NAME="$3"
TEMPLATE_DIR="$4"

# Convert to absolute path
TEMPLATE_DIR=$(cd "$TEMPLATE_DIR" && pwd)

# Verify template directory exists
if [ ! -d "$TEMPLATE_DIR" ]; then
    echo "Error: Template directory $TEMPLATE_DIR does not exist"
    exit 1
fi

# Convert MODULE_NAME to PascalCase for class names
first=$(printf "%s" "$MODULE_NAME" | cut -c1 | tr '[:lower:]' '[:upper:]')
rest=$(printf "%s" "$MODULE_NAME" | cut -c2-)
MODULE_CLASS_NAME="$first$rest"

echo "Setting up project with:"
echo "  PROJECT_NAME: $PROJECT_NAME"
echo "  BASE_PACKAGE: $BASE_PACKAGE"
echo "  MODULE_NAME: $MODULE_NAME"
echo "  MODULE_CLASS_NAME: $MODULE_CLASS_NAME"
echo "  TEMPLATE_DIR: $TEMPLATE_DIR"
echo

# Function to perform cross-platform sed replacement
sed_replace() {
    local pattern="$1"
    local file="$2"
    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "$pattern" "$file"
    else
        sed -i "$pattern" "$file"
    fi
}

echo "Step 1: Remove example modules"
# Remove all example modules
for dir in example-*/; do
    if [ -d "$dir" ]; then
        echo "  Removing example module: $dir"
        rm -rf "$dir"
    fi
done

echo "Step 2: Update main pom.xml to remove example modules"
if [ -f "pom.xml" ]; then
    echo "  Removing example module references from pom.xml"
    sed_replace '/<module>example-[^<]*<\/module>/d' "pom.xml"
fi

echo "Step 3: Create .template symlink"
# Remove vibetdd if it exists
if [ -d "vibetdd" ]; then
    echo "  Removing vibetdd directory"
    rm -rf vibetdd
fi

# Create symlink to entire template directory
echo "  Creating .template symlink to: $TEMPLATE_DIR"
ln -s "$TEMPLATE_DIR" ./.template

# Add symlink to gitignore
echo ".template/" >> .gitignore

echo "Step 4: Initialize git repository"
git init
git add --all

echo "Step 5: Remove template-specific files"
# Remove setup script from the new project
rm -f scripts/setup-project.sh
# Remove template README if it exists
rm -f TEMPLATE_README.md

echo "Step 6: Rename module-template directory to ${MODULE_NAME}"
if [ -d "module-template" ]; then
    cp -r module-template "${MODULE_NAME}"
    # Remove original module-template
    rm -rf module-template
fi

echo "Step 7: Replace all occurrences in files"
echo "  Replacing package names..."
find . -type f -name "*.xml" -o -name "*.kt" -o -name "*.java" | while read -r file; do
    # Skip the .template symlink
    if [[ "$file" == "./.template/"* ]]; then
        continue
    fi

    # Replace package names
    sed_replace "s/com\.company/$BASE_PACKAGE/g" "$file"

    # Replace project names
    sed_replace "s/<artifactId>api<\/artifactId>/<artifactId>$PROJECT_NAME<\/artifactId>/g" "$file"

    # Replace module names
    sed_replace "s/module-template/$MODULE_NAME/g" "$file"
    sed_replace "s/<module>module-template<\/module>/<module>$MODULE_NAME<\/module>/g" "$file"

    # Replace class names
    sed_replace "s/ModuleTemplate/$MODULE_CLASS_NAME/g" "$file"
    sed_replace "s/module_template/$MODULE_NAME/g" "$file"
done

echo "Step 8: Rename files with ModuleTemplate in their names"
find . -type f -name "*ModuleTemplate*" | while read -r file; do
    # Skip the .template symlink
    if [[ "$file" == "./.template/"* ]]; then
        continue
    fi

    # Get directory and filename
    dir=$(dirname "$file")
    filename=$(basename "$file")

    # Replace ModuleTemplate with MODULE_CLASS_NAME in filename
    new_filename=$(echo "$filename" | sed "s/ModuleTemplate/$MODULE_CLASS_NAME/g")

    # Only rename if the filename actually changed
    if [ "$filename" != "$new_filename" ]; then
        echo "  Renaming $file -> $dir/$new_filename"
        mv "$file" "$dir/$new_filename"
    fi
done

echo "Step 9: Rename directories with module_template in their names"
find . -type d -name "*module_template*" | while read -r dir; do
    # Skip the .template symlink
    if [[ "$dir" == "./.template"* ]]; then
        continue
    fi

    # Get parent directory and directory name
    parent_dir=$(dirname "$dir")
    dir_name=$(basename "$dir")

    # Replace module_template with MODULE_NAME in directory name
    new_dir_name=$(echo "$dir_name" | sed "s/module_template/$MODULE_NAME/g")

    # Only rename if the directory name actually changed
    if [ "$dir_name" != "$new_dir_name" ]; then
        echo "  Renaming directory $dir -> $parent_dir/$new_dir_name"
        mv "$dir" "$parent_dir/$new_dir_name"
    fi
done

echo "Step 10: Move source files to new package structure"
for module_dir in */; do
    module_dir=${module_dir%/}

    # Skip .template symlink and scripts
    if [ "$module_dir" = ".template" ] || [ "$module_dir" = "scripts" ]; then
        continue
    fi

    # Check if this module has source files
    if [ ! -d "$module_dir/src" ]; then
        continue
    fi

    echo "  Processing $module_dir"

    # Create new package directory structure
    base_package_path=$(echo "$BASE_PACKAGE" | tr '.' '/')

    # Handle main source directory
    if [ -d "$module_dir/src/main/kotlin/com/company" ]; then
        mkdir -p "$module_dir/src/main/kotlin/$base_package_path"

        # Move all content from com/company to new package, handling special cases
        find "$module_dir/src/main/kotlin/com/company" -mindepth 1 -maxdepth 1 | while read -r item; do
            item_name=$(basename "$item")

            # If this is module_template directory in the new module, rename it
            if [ "$item_name" = "module_template" ] && [ "$module_dir" = "$MODULE_NAME" ]; then
                cp -r "$item" "$module_dir/src/main/kotlin/$base_package_path/$MODULE_NAME"
            else
                cp -r "$item" "$module_dir/src/main/kotlin/$base_package_path/"
            fi
        done

        # Remove old directory structure
        rm -rf "$module_dir/src/main/kotlin/com"
    fi

    # Handle test source directory
    if [ -d "$module_dir/src/test/kotlin/com/company" ]; then
        mkdir -p "$module_dir/src/test/kotlin/$base_package_path"

        # Move all content from com/company to new package
        find "$module_dir/src/test/kotlin/com/company" -mindepth 1 -maxdepth 1 -exec cp -r {} "$module_dir/src/test/kotlin/$base_package_path/" \;

        # Remove old directory structure
        rm -rf "$module_dir/src/test/kotlin/com"
    fi
done

echo "Step 11: Test compilation"
echo "Running mvn clean test..."
if mvn clean test -q; then
    echo "✅ Build successful!"
else
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi

echo "Step 12: Add all changes to git"
git add --all

echo "Step 13: Initial commit"
git commit -m "Initial project setup from template

- Project: $PROJECT_NAME
- Package: $BASE_PACKAGE
- Module: $MODULE_NAME
- Template: $TEMPLATE_DIR"

echo
echo "✅ Setup completed successfully!"
echo
echo "Summary of changes:"
echo "  - Project name: $PROJECT_NAME"
echo "  - Base package: $BASE_PACKAGE"
echo "  - New module: $MODULE_NAME (classes: ${MODULE_CLASS_NAME}*)"
echo "  - Template reference: ./.template/ -> $TEMPLATE_DIR"
echo "  - Removed all example modules"
echo
echo "Available resources:"
echo "  - Conventions: ./.template/vibetdd/conventions/"
echo "  - Examples: ./.template/vibetdd/examples/"
echo "  - AI Prompts: ./.template/vibetdd/prompts/"
echo
echo "Next steps:"
echo "  1. Review generated code in $MODULE_NAME/"
echo "  2. Use 'claude' with template resources"