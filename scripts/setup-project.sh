#!/bin/bash

# Setup script to prepare the template for a real project
# Usage: ./scripts/setup-project.sh PROJECT_NAME BASE_PACKAGE MODULE_NAME
# Example: ./scripts/setup-project.sh payouts-api dev.vibetdd payouts

set -e

# Check if correct number of arguments provided
if [ $# -ne 3 ]; then
    echo "Usage: $0 PROJECT_NAME BASE_PACKAGE MODULE_NAME"
    echo "Example: $0 payouts-api dev.vibetdd payouts"
    exit 1
fi

PROJECT_NAME="$1"
BASE_PACKAGE="$2"
MODULE_NAME="$3"

# Convert MODULE_NAME to PascalCase for class names
first=$(printf "%s" "$MODULE_NAME" | cut -c1 | tr '[:lower:]' '[:upper:]')
rest=$(printf "%s" "$MODULE_NAME" | cut -c2-)
MODULE_CLASS_NAME="$first$rest"

echo "Setting up project with:"
echo "  PROJECT_NAME: $PROJECT_NAME"
echo "  BASE_PACKAGE: $BASE_PACKAGE"
echo "  MODULE_NAME: $MODULE_NAME"
echo "  MODULE_CLASS_NAME: $MODULE_CLASS_NAME"
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

echo "Step 1: Initialize git repository"
git init
git add --all

echo "Step 2: Rename module-template directory to ${MODULE_NAME}"
if [ -d "module-template" ]; then
    cp -r module-template "${MODULE_NAME}"
fi

echo "Step 3: Replace all occurrences in files"
echo "  Replacing package names..."
find . -type f -name "*.xml" -o -name "*.kt" -o -name "*.java" | while read -r file; do
    # Skip the original module-template directory
    if [[ "$file" == "./module-template/"* ]]; then
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

echo "Step 4: Rename files with ModuleTemplate in their names"
find . -type f -name "*ModuleTemplate*" | while read -r file; do
    # Skip the original module-template directory
    if [[ "$file" == "./module-template/"* ]]; then
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

echo "Step 5: Rename directories with module_template in their names"
find . -type d -name "*module_template*" | while read -r dir; do
    # Skip the original module-template directory
    if [[ "$dir" == "./module-template" ]] || [[ "$dir" == "./module-template/"* ]]; then
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

echo "Step 6: Move source files to new package structure"
for module_dir in */; do
    module_dir=${module_dir%/}

    # Skip module-template
    if [ "$module_dir" = "module-template" ]; then
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

echo "Step 7: Test compilation"
echo "Running mvn clean test..."
if mvn clean test -q; then
    echo "✅ Build successful!"
else
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi

echo "Step 8: Add all changes to git"
git add --all

echo "Step 9: Initial commit"
git commit -m 'Initial project setup'

echo
echo "✅ Setup completed successfully!"
echo
echo "Summary of changes:"
echo "  - Project name: $PROJECT_NAME"
echo "  - Base package: $BASE_PACKAGE"
echo "  - New module: $MODULE_NAME (classes: ${MODULE_CLASS_NAME}*)"
echo "  - Template module: module-template (kept for future use)"