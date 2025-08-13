## Execution Mode

Execute all remaining tasks in the work queue continuously. For each task:
- Complete both Phase 1 (tests) and Phase 2 (implementation)
- Handle partial completion scenarios
- Continue to next task automatically
- Stop only on errors or queue completion

## Task State Detection

Before starting each task, check the current state:
- Run `mvn clean test` to see current test status
- If tests for current batch already exist and pass: mark as completed, move to next
- If tests exist but fail: skip to Phase 2 (implementation only)
- If no tests exist: start with Phase 1 (write tests)

## Read Work Queue and Get Current Task

Read `/docs/spec/work-queue.md`:
- Take the first line as current task to work on
- Parse: story = split('/')[0], task = split('/')[1], batch = split('/')[2]
- If work queue is empty, report "All tasks completed for this story"

Checkpoint: If work queue file missing or empty, stop here and report the issue.

## Read Convention Files

Read the attached convention files:
- `.template/vibetdd/conventions/testing/basic.md` - basic testing principles you must follow
- `.template/vibetdd/conventions/kotlin/basic.md` - kotlin conventions you must follow
- `.template/vibetdd/conventions/arch/config.md` - configuration patterns you must follow
- Depending on a task you must read code examples + specific descriptions: 
  - for `domain` part:
    - read `.template/vibetdd/conventions/testing/domain.md` file to understand the module specific testing principles 
    - read `.template/vibetdd/conventions/arch/module-domain.md` file to understand the module specific architecture principles 
    - read `.template/example-domain/src` folder ONLY to understand the code examples to follow
  - for `storage` part:
    - read `.template/vibetdd/conventions/testing/storage.md` file to understand the module specific testing principles 
    - read `.template/vibetdd/conventions/arch/module-storage.md` file to understand the module specific architecture principles
    - read `.template/example-domain/src` folder ONLY to understand the code examples to follow
  - for `api` part:
    - read `.template/vibetdd/conventions/testing/api.md` file to understand the module specific testing principles
    - read `.template/vibetdd/conventions/arch/module-api.md` file to understand the module specific architecture principles
    - read `.template/example-api/src` folder ONLY to understand the code examples to follow

Checkpoints:
- If at least one file is missing (you got an error like 'Error: File does not exist'), stop here and report the issue
- Ask yourself if you understood all conventions are read all examples. If you have doubts, stop here and ask your questions

## Read Story and Task Files

Read story and task details:
- Story file: `/docs/spec/stories/{story}/story.md`
- Task file: `/docs/spec/stories/{story}/tasks/{task}.md`
- Locate the specific batch section in the task file

Checkpoint: If you cannot find the story file, task file, or batch section, stop here and report missing files.

## Phase 1: Write Tests (if needed)

**Skip this phase if tests already exist for current batch**

Write tests for the current batch:
- Create minimal objects, interfaces, and exceptions needed for compilation
- Don't implement any business logic at this step - leave methods empty or with basic stubs
- Follow VibeTDD batching principles from conventions
- Use exact test names provided in the batch section
- Create only what's needed for this specific batch
- Focus on layer responsibilities as defined in conventions
- Use patterns and examples from attached conventions

Checkpoints for test writing:
- All convention and spec files remain unchanged
- Use exact test names from the task batch section
- Infer method signatures from layer conventions and behavioral descriptions
- Use Object Mother pattern with appropriate overrides for test scenarios
- Use data types and structures defined in the story file
- Follow assertion patterns from conventions (test error codes, not messages)
- Run `mvn clean test` - code must compile and all new tests must fail
- If any new test passes, stop and report the issue

After writing tests:
- Add new files to git: `git add --all`
- Commit tests: `git commit -m "Add tests for {story} / {task} / {batch}"`

## Phase 2: Implement Logic

Implement the minimal logic to make all tests in the current batch pass:
- Follow conventions for implementation patterns
- Keep logic simple and focused on making tests pass
- Don't over-engineer or add extra features not required by tests
- Ensure implementation matches the layer responsibilities from conventions

Checkpoints for implementation:
- Run `mvn clean test` - all tests in current batch must pass
- No existing tests should be broken by the implementation
- Implementation follows convention patterns and examples
- Logic is minimal and focused - no unnecessary complexity

After implementing logic:
- Add changes to git: `git add --all`
- Commit implementation: `git commit -m "Implement {story} / {task} / {batch}"`

## Update Work Queue and Continue

Update the work queue:
- Remove the completed line (first line) from `/docs/spec/work-queue.md`
- Save the updated work queue file

**Continue to next task:**
- If work queue still has tasks: automatically start next task (go back to "Task State Detection")
- If work queue is empty - finish

**Stop execution only if:**
- Work queue is empty (all tasks completed)
- Compilation fails (`mvn clean test` doesn't compile)
- Tests fail to pass after implementation
- Missing required files (story, task, or convention files)

## Final Summary

When all tasks are completed, provide summary:
- Total batches completed in this execution
- Any tasks skipped (and why)
- Final test status: `mvn clean test`
- Git commit history for this execution
- Next steps (if any manual intervention needed)