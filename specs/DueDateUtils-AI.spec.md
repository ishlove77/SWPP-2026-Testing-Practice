# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Builds the D-day label shown next to a task: "CLEAR" for a completed task, "" for an active task without a due date, otherwise "D-Day", "D-N" or "D+N", where N is the total number of calendar days between `today` and `dueDate`.

**Pre-conditions**
- `today` is a non-null `LocalDate` (the caller passes the current date; the function never reads the clock).
- `dueDate` may be null; `isCompleted` may be true or false.

**Post-conditions**
- Success cases
  - Completed task with a due date returns "CLEAR". `[Unit]`
    - Tip: Given dueDate 2026-05-10, today 2026-05-01, isCompleted true / When dueDateLabel is called / Then it returns "CLEAR"
  - Completed task without a due date returns "CLEAR". `[Unit]`
    - Tip: Given dueDate null, today 2026-05-01, isCompleted true / When dueDateLabel is called / Then it returns "CLEAR"
  - Completed task that is overdue returns "CLEAR", not "D+N". `[Unit]`
    - Tip: Given dueDate 2026-04-20, today 2026-05-01, isCompleted true / When dueDateLabel is called / Then it returns "CLEAR"
  - Active task without a due date returns the empty string. `[Unit]`
    - Tip: Given dueDate null, today 2026-05-01, isCompleted false / When dueDateLabel is called / Then it returns ""
  - Active task due today returns "D-Day". `[Unit]`
    - Tip: Given dueDate 2026-05-01, today 2026-05-01, isCompleted false / When dueDateLabel is called / Then it returns "D-Day"
  - Active task due later within the same month returns "D-N". `[Unit]`
    - Tip: Given dueDate 2026-05-04, today 2026-05-01, isCompleted false / When dueDateLabel is called / Then it returns "D-3"
  - Active task due in a later month returns "D-N" with N the total number of days. `[Unit]`
    - Tip: Given dueDate 2026-06-15, today 2026-05-01, isCompleted false / When dueDateLabel is called / Then it returns "D-45"
  - Active overdue task returns "D+N". `[Unit]`
    - Tip: Given dueDate 2026-04-28, today 2026-05-01, isCompleted false / When dueDateLabel is called / Then it returns "D+3"
  - Active task overdue by more than one month returns "D+N" with N the total number of days. `[Unit]`
    - Tip: Given dueDate 2026-01-31, today 2026-03-01, isCompleted false / When dueDateLabel is called / Then it returns "D+29"
- Error cases
  - None. Every combination of inputs is valid. `[Unit]`
- Edge cases
  - Completed vs. active (boundary on `isCompleted`): the same dates give "CLEAR" when completed and a day label when active. `[Unit]`
    - Tip: Given dueDate 2026-05-02, today 2026-05-01 / When isCompleted is true, then false / Then it returns "CLEAR", then "D-1"
  - Null vs. non-null due date (boundary on `dueDate`), active task: null gives "", a date equal to today gives "D-Day". `[Unit]`
    - Tip: Given today 2026-05-01, isCompleted false / When dueDate is null, then 2026-05-01 / Then it returns "", then "D-Day"
  - Today boundary: the day before, the day itself and the day after. `[Unit]`
    - Tip: Given today 2026-05-01, isCompleted false / When dueDate is 2026-04-30, 2026-05-01, 2026-05-02 / Then it returns "D+1", "D-Day", "D-1"
  - Month boundary (end of a 31-day month): due the last day, the first day of next month. `[Unit]`
    - Tip: Given today 2026-01-31, isCompleted false / When dueDate is 2026-01-31, 2026-02-01 / Then it returns "D-Day", "D-1"
  - Exactly one month apart and one day either side (Period's day part would wrongly be 0 or small). `[Unit]`
    - Tip: Given today 2026-01-15, isCompleted false / When dueDate is 2026-02-14, 2026-02-15, 2026-02-16 / Then it returns "D-30", "D-31", "D-32"
  - Overdue across a month boundary, one month apart and one day either side. `[Unit]`
    - Tip: Given today 2026-02-15, isCompleted false / When dueDate is 2026-01-14, 2026-01-15, 2026-01-16 / Then it returns "D+32", "D+31", "D+30"
  - Year boundary: Dec 31 to Jan 1 in both directions, and exactly one year apart with one day either side. `[Unit]`
    - Tip: Given isCompleted false / When (today 2025-12-31, dueDate 2026-01-01), (today 2026-01-01, dueDate 2025-12-31) / Then it returns "D-1", "D+1"
    - Tip: Given today 2025-01-01, isCompleted false / When dueDate is 2025-12-31, 2026-01-01, 2026-01-02 / Then it returns "D-364", "D-365", "D-366"
  - Leap day: Feb 28 to Mar 1 in a leap year vs. a non-leap year, and across Feb 29. `[Unit]`
    - Tip: Given today 2024-02-28, isCompleted false / When dueDate is 2024-02-29, 2024-03-01 / Then it returns "D-1", "D-2"
    - Tip: Given today 2023-02-28, isCompleted false / When dueDate is 2023-03-01 / Then it returns "D-1"
    - Tip: Given today 2024-03-01, isCompleted false / When dueDate is 2024-02-29 / Then it returns "D+1"

**Undefined behavior** (optional)
- Time zones and clock: the function only compares the two dates it is given, so which zone `today` was derived from is the caller's concern.
