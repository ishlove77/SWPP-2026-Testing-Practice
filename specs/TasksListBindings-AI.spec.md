# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Binding adapter (`app:dueDateLabel`) that shows a task's D-day label (see `dueDateLabel`, using today's date from the device clock) in the given TextView, and hides the TextView when there is nothing to show. Because list rows are recycled, a row that is rebound to a task without a label must not keep the previous task's label.

**Pre-conditions**
- `textView` is a non-null, real Android `TextView` (needs the Android framework, so tests are instrumented).
- `task` may be null; `task.dueDate` may be null.

**Post-conditions**
- Success cases
  - Active task with a due date: the TextView text is the D-day label for today and it is visible. `[Integration]`
    - Tip: Given a TextView and an active Task with dueDate = LocalDate.now().plusDays(3) / When setDueDateLabel is called / Then text is "D-3" and visibility is View.VISIBLE
  - Active task due today: text is "D-Day" and the TextView is visible. `[Integration]`
    - Tip: Given a TextView and an active Task with dueDate = LocalDate.now() / When setDueDateLabel is called / Then text is "D-Day" and visibility is View.VISIBLE
  - Active overdue task: text is "D+N" and the TextView is visible. `[Integration]`
    - Tip: Given a TextView and an active Task with dueDate = LocalDate.now().minusDays(2) / When setDueDateLabel is called / Then text is "D+2" and visibility is View.VISIBLE
  - Completed task (with or without due date): text is "CLEAR" and the TextView is visible. `[Integration]`
    - Tip: Given a TextView and a completed Task with dueDate = null / When setDueDateLabel is called / Then text is "CLEAR" and visibility is View.VISIBLE
    - Tip: Given a TextView and a completed Task with dueDate = LocalDate.now().plusDays(5) / When setDueDateLabel is called / Then text is "CLEAR" and visibility is View.VISIBLE
  - Active task with no due date: nothing is shown; the TextView is hidden and its text is empty. `[Integration]`
    - Tip: Given a visible TextView and an active Task with dueDate = null / When setDueDateLabel is called / Then visibility is View.GONE and text is ""
  - Null task: nothing is shown; the TextView is hidden and its text is empty. `[Integration]`
    - Tip: Given a visible TextView with text "D-3" and task = null / When setDueDateLabel is called / Then visibility is View.GONE and text is ""
- Error cases
  - None. A null task is handled as "nothing to show" rather than rejected. `[Integration]`
- Edge cases
  - Recycled row, label then no label (boundary between "has label" and "no label"): after binding a task with a label, rebinding the same TextView to an active task with no due date hides it and clears the old label. `[Integration]`
    - Tip: Given a TextView already bound to an active Task with dueDate = LocalDate.now().plusDays(1) (text "D-1", visible) / When setDueDateLabel is called with an active Task with dueDate = null / Then visibility is View.GONE and text is ""
  - Recycled row, no label then label: after being hidden, rebinding to a task with a label shows it again. `[Integration]`
    - Tip: Given a TextView bound to task = null (GONE) / When setDueDateLabel is called with an active Task with dueDate = LocalDate.now() / Then text is "D-Day" and visibility is View.VISIBLE
  - Completed vs. active on the same due date: toggling completion switches between "CLEAR" and the day label. `[Integration]`
    - Tip: Given dueDate = LocalDate.now().plusDays(1) / When bound as a completed Task, then as an active Task / Then text is "CLEAR", then "D-1", visible both times
  - Day boundary around today: yesterday, today, tomorrow. `[Integration]`
    - Tip: Given an active Task / When dueDate is LocalDate.now().minusDays(1), LocalDate.now(), LocalDate.now().plusDays(1) / Then text is "D+1", "D-Day", "D-1"

**Undefined behavior** (optional)
- Behavior if the date changes between the moment the label is computed and the moment it is displayed (midnight rollover): the label is only computed at bind time and is not refreshed.
