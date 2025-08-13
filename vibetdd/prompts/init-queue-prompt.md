Read all story and task files in the /docs/spec/stories/ folder structure.

For each story found:
- Read the story.md file to understand the story context
- Scan the tasks/ subfolder for all task files (domain.md, api.md, store.md, etc.)
- For each task file, extract all batch names from sections that start with "### Batch" or "## Batch"

Add lines to work queue file at `/docs/spec/work-queue.md` with the following format:

{story-name} / {task-name} / {batch-name}
{story-name} / {task-name} / {batch-name}
{story-name} / {task-name} / {batch-name}

Rules for queue creation:
- One line per batch
- Format: story / task / batch (separated by " / ")
- Order batches logically: domain batches first, then storage batches then api batches
- Within each task, preserve the order of batches as they appear in the task file
- Use exact batch names from the task files without modification
- No checkboxes, no status indicators, no additional sections

Example output format:

1-create-payout / domain / Amount Business Validation Batch
1-create-payout / domain / Currency Business Rules Batch
1-create-payout / storage / Payout Storage Batch
1-create-payout / storage / Payout Retrieval Batch
1-create-payout / api / Request Format Validation Batch
1-create-payout / api / Success Response Batch

Checkpoint before responding:
- Verify you found at least one story folder with task files
- Verify each task file contains identifiable batch sections
- Create the work queue with the exact format specified above
- Do not modify any existing files, only create the new work queue file