Your task is to prepare the template to real project. Follow the steps:

- execute `git init`
- execute `git add --all`
- copy module-template to {MODULE_NAME}
- in the copied module rename all occurrences ModuleTemplate to {DOMAIN_NAME}
- change the project name to {PROJECT_NAME} and update the reference in all modules
- rename the base package com.company with {PACKAGE_NAME}
- don't touch anything else, don't assume it would be helpful
- test that code is compilable: `mvn clean test`. If not then fix issues
- execute `git add --all`