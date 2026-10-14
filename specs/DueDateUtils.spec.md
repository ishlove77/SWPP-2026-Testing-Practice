# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Builds the D-day label shown next to a task in the task list (PR feature/due-date). A completed task always shows "CLEAR"; an active task with no due date shows nothing (""); an active task with a due date shows "D-Day" if due today, "D-N" if due in N days, and "D+N" if overdue by N days, where N is the **total** number of calendar days between `today` and `dueDate` (a positive integer, no zero padding).

**Pre-conditions**
- `today` is a non-null `LocalDate` supplied by the caller (the function never reads the clock).
- `dueDate` may be null; `isCompleted` may be true or false.

**Post-conditions**
- Success cases
  - Completed task with a future due date returns "CLEAR". `[Unit]`
    - Tip: Given dueDate 2026-05-10, today 2026-05-01, isCompleted true / When dueDateLabel is called / Then it returns "CLEAR"
  - Completed task without a due date returns "CLEAR". `[Unit]`
    - Tip: Given dueDate null, today 2026-05-01, isCompleted true / When dueDateLabel is called / Then it returns "CLEAR"
  - Completed task that is overdue returns "CLEAR", not "D+N". `[Unit]`
    - Tip: Given dueDate 2026-04-20, today 2026-05-01, isCompleted true / When dueDateLabel is called / Then it returns "CLEAR"
  - Active task without a due date returns "" (no label). `[Unit]`
    - Tip: Given dueDate null, today 2026-05-01, isCompleted false / When dueDateLabel is called / Then it returns ""
  - Active task due today returns "D-Day". `[Unit]`
    - Tip: Given dueDate 2026-05-01, today 2026-05-01, isCompleted false / When dueDateLabel is called / Then it returns "D-Day"
  - Active task due later in the same month returns "D-N". `[Unit]`
    - Tip: Given dueDate 2026-05-04, today 2026-05-01, isCompleted false / When dueDateLabel is called / Then it returns "D-3"
  - Active task due in a later month returns "D-N" with N the total number of days, not the day-of-month difference. `[Unit]`
    - Tip: Given dueDate 2026-06-15, today 2026-05-01, isCompleted false / When dueDateLabel is called / Then it returns "D-45"
  - Active task due in three or more digits of days returns "D-N" without truncation or padding. `[Unit]`
    - Tip: Given dueDate 2026-04-11, today 2026-01-01, isCompleted false / When dueDateLabel is called / Then it returns "D-100"
  - Active overdue task in the same month returns "D+N". `[Unit]`
    - Tip: Given dueDate 2026-04-28, today 2026-05-01, isCompleted false / When dueDateLabel is called / Then it returns "D+3"
  - Active task overdue across months returns "D+N" with N the total number of days. `[Unit]`
    - Tip: Given dueDate 2026-01-31, today 2026-03-01, isCompleted false / When dueDateLabel is called / Then it returns "D+29"
- Error cases
  - None.
- Edge cases
  - Today boundary, day before (overdue by one day). `[Unit]`
    - Tip: Given today 2026-05-01, dueDate 2026-04-30, isCompleted false / When dueDateLabel is called / Then it returns "D+1"
  - Today boundary, day after (due tomorrow). `[Unit]`
    - Tip: Given today 2026-05-01, dueDate 2026-05-02, isCompleted false / When dueDateLabel is called / Then it returns "D-1"
  - End of a 31-day month to the first day of the next month. `[Unit]`
    - Tip: Given today 2026-01-31, dueDate 2026-02-01, isCompleted false / When dueDateLabel is called / Then it returns "D-1"
  - One day short of a full month ahead. `[Unit]`
    - Tip: Given today 2026-01-15, dueDate 2026-02-14, isCompleted false / When dueDateLabel is called / Then it returns "D-30"
  - Exactly one month ahead. `[Unit]`
    - Tip: Given today 2026-01-15, dueDate 2026-02-15, isCompleted false / When dueDateLabel is called / Then it returns "D-31"
  - One day past a full month ahead. `[Unit]`
    - Tip: Given today 2026-01-15, dueDate 2026-02-16, isCompleted false / When dueDateLabel is called / Then it returns "D-32"
  - One day short of a full month overdue. `[Unit]`
    - Tip: Given today 2026-02-15, dueDate 2026-01-16, isCompleted false / When dueDateLabel is called / Then it returns "D+30"
  - Exactly one month overdue. `[Unit]`
    - Tip: Given today 2026-02-15, dueDate 2026-01-15, isCompleted false / When dueDateLabel is called / Then it returns "D+31"
  - One day past a full month overdue. `[Unit]`
    - Tip: Given today 2026-02-15, dueDate 2026-01-14, isCompleted false / When dueDateLabel is called / Then it returns "D+32"
  - Year boundary, Dec 31 to Jan 1 ahead. `[Unit]`
    - Tip: Given today 2025-12-31, dueDate 2026-01-01, isCompleted false / When dueDateLabel is called / Then it returns "D-1"
  - Year boundary, Jan 1 back to Dec 31 overdue. `[Unit]`
    - Tip: Given today 2026-01-01, dueDate 2025-12-31, isCompleted false / When dueDateLabel is called / Then it returns "D+1"
  - One day short of a full year ahead. `[Unit]`
    - Tip: Given today 2025-01-01, dueDate 2025-12-31, isCompleted false / When dueDateLabel is called / Then it returns "D-364"
  - Exactly one year ahead (Period's day part would be 0). `[Unit]`
    - Tip: Given today 2025-01-01, dueDate 2026-01-01, isCompleted false / When dueDateLabel is called / Then it returns "D-365"
  - One day past a full year ahead. `[Unit]`
    - Tip: Given today 2025-01-01, dueDate 2026-01-02, isCompleted false / When dueDateLabel is called / Then it returns "D-366"
  - Leap year, Feb 28 to Feb 29. `[Unit]`
    - Tip: Given today 2024-02-28, dueDate 2024-02-29, isCompleted false / When dueDateLabel is called / Then it returns "D-1"
  - Leap year, Feb 28 to Mar 1 (crosses Feb 29). `[Unit]`
    - Tip: Given today 2024-02-28, dueDate 2024-03-01, isCompleted false / When dueDateLabel is called / Then it returns "D-2"
  - Non-leap year, Feb 28 to Mar 1. `[Unit]`
    - Tip: Given today 2023-02-28, dueDate 2023-03-01, isCompleted false / When dueDateLabel is called / Then it returns "D-1"
  - Leap year, Mar 1 back to Feb 29 overdue. `[Unit]`
    - Tip: Given today 2024-03-01, dueDate 2024-02-29, isCompleted false / When dueDateLabel is called / Then it returns "D+1"

**Undefined behavior** (optional)
- Time zones and clock: the function only compares the two dates it is given, so which zone `today` was derived from is the caller's concern.
