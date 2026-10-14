# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Binding adapter (`app:dueDateLabel`) for the due-date TextView of a task row. It shows the task's D-day label (`dueDateLabel` with today's date) and shows **no label** when there is nothing to show. The label must follow the task's current state every time the row is bound: checking or unchecking a task, or recycling the row for another task, must never leave a stale label (e.g. "CLEAR") behind.

**Pre-conditions**
- `textView` is a real Android `TextView` (needs the Android framework, so tests are instrumented).
- `task` may be null; `task.dueDate` may be null.
- In every test, `today` is `LocalDate.now()` captured once at the start of the test; due dates are written as `today ± N` and the expected strings are exact.

**Post-conditions**
- Success cases
  - Active task due in 3 days: the label "D-3" is shown. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, dueDate = today + 3) / When setDueDateLabel is called / Then text is "D-3" and visibility is View.VISIBLE
  - Active task due today: the label "D-Day" is shown. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, dueDate = today) / When setDueDateLabel is called / Then text is "D-Day" and visibility is View.VISIBLE
  - Active task overdue by 2 days: the label "D+2" is shown. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, dueDate = today - 2) / When setDueDateLabel is called / Then text is "D+2" and visibility is View.VISIBLE
  - Active task due in more than a month: the label uses the total number of days. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, dueDate = today + 40) / When setDueDateLabel is called / Then text is "D-40" and visibility is View.VISIBLE
  - Completed task without a due date: the label "CLEAR" is shown. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", true, dueDate = null) / When setDueDateLabel is called / Then text is "CLEAR" and visibility is View.VISIBLE
  - Completed task with a due date: the label "CLEAR" is shown. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", true, dueDate = today + 5) / When setDueDateLabel is called / Then text is "CLEAR" and visibility is View.VISIBLE
  - Active task with no due date: no label is shown (text is "" or the view is not VISIBLE). `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, dueDate = null) / When setDueDateLabel is called / Then the TextView shows no label (text is "" or visibility is not View.VISIBLE)
  - Null task: no label is shown. `[Integration]`
    - Tip: Given a new TextView and task = null / When setDueDateLabel is called / Then the TextView shows no label (text is "" or visibility is not View.VISIBLE)
- Error cases
  - None.
- Edge cases
  - Completed task without a due date is unchecked (label "CLEAR" → no label). `[Integration]`
    - Tip: Given a TextView bound to Task("T", "D", true, id = "id1", dueDate = null) (text "CLEAR") / When setDueDateLabel is called again with Task("T", "D", false, id = "id1", dueDate = null) / Then the TextView shows no label ("CLEAR" is not shown)
  - Same scenario on the real task list screen: checking then unchecking a task with no due date. `[Integration]`
    - Tip: Given FakeAndroidTestRepository with Task("TITLE1", "DESC1", false, "id1", dueDate = null) and TasksFragment launched / When complete_checkbox of the "TITLE1" row is clicked, then clicked again / Then after the first click the row's due_date_text shows "CLEAR", and after the second click no view with text "CLEAR" is displayed
  - Active task with a due date is checked then unchecked (label "D-1" → "CLEAR" → "D-1"). `[Integration]`
    - Tip: Given a TextView bound to Task("T", "D", false, dueDate = today + 1) / When rebound with the same task completed, then with it active again / Then text is "CLEAR", then "D-1"
  - Recycled row, label then no label: a row that showed "D-1" is rebound to an active task without a due date. `[Integration]`
    - Tip: Given a TextView bound to Task("A", "D", false, dueDate = today + 1) (text "D-1") / When setDueDateLabel is called with Task("B", "D", false, dueDate = null) / Then the TextView shows no label ("D-1" is not shown)
  - Recycled row, label then null task. `[Integration]`
    - Tip: Given a TextView bound to Task("A", "D", true, dueDate = null) (text "CLEAR") / When setDueDateLabel is called with task = null / Then the TextView shows no label
  - Recycled row, no label then label: the label is shown again. `[Integration]`
    - Tip: Given a TextView bound to Task("A", "D", false, dueDate = null) / When setDueDateLabel is called with Task("B", "D", false, dueDate = today) / Then text is "D-Day" and visibility is View.VISIBLE
  - Day boundary, due yesterday. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, dueDate = today - 1) / When setDueDateLabel is called / Then text is "D+1"
  - Day boundary, due tomorrow. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, dueDate = today + 1) / When setDueDateLabel is called / Then text is "D-1"

**Undefined behavior** (optional)
- Midnight rollover: the label is computed from the device clock at bind time and is not refreshed when the date changes; a test that runs across midnight may see a label for the next day.
- Whether "no label" is implemented as empty text, `INVISIBLE` or `GONE`: the intent only says no label is shown.
