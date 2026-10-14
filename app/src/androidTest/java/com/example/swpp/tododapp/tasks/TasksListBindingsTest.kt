package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Integration tests for [setDueDateLabel], one per Tip in specs/TasksListBindings.spec.md. */
@SmallTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    private lateinit var today: LocalDate

    @Before
    fun captureToday() {
        today = LocalDate.now()
    }

    private fun newTextView(): TextView {
        lateinit var textView: TextView
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            textView = TextView(InstrumentationRegistry.getInstrumentation().targetContext)
        }
        return textView
    }

    private fun bind(textView: TextView, task: Task?) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { setDueDateLabel(textView, task) }
    }

    private fun assertLabelShown(textView: TextView, expected: String) {
        assertEquals(expected, textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    private fun assertNoLabel(textView: TextView) {
        assertTrue(
            "expected no label but saw \"${textView.text}\" (visibility=${textView.visibility})",
            textView.text.isEmpty() || textView.visibility != View.VISIBLE
        )
    }

    // Success cases

    @Test
    fun setDueDateLabel_activeDueIn3Days_showsDMinus3() {
        // GIVEN
        val textView = newTextView()
        val task = Task("T", "D", false, dueDate = today.plusDays(3))
        // WHEN
        bind(textView, task)
        // THEN
        assertLabelShown(textView, "D-3")
    }

    @Test
    fun setDueDateLabel_activeDueToday_showsDDay() {
        val textView = newTextView()
        bind(textView, Task("T", "D", false, dueDate = today))
        assertLabelShown(textView, "D-Day")
    }

    @Test
    fun setDueDateLabel_activeOverdue2Days_showsDPlus2() {
        val textView = newTextView()
        bind(textView, Task("T", "D", false, dueDate = today.minusDays(2)))
        assertLabelShown(textView, "D+2")
    }

    @Test
    fun setDueDateLabel_activeDueIn40Days_showsDMinus40() {
        val textView = newTextView()
        bind(textView, Task("T", "D", false, dueDate = today.plusDays(40)))
        assertLabelShown(textView, "D-40")
    }

    @Test
    fun setDueDateLabel_completedNoDue_showsClear() {
        val textView = newTextView()
        bind(textView, Task("T", "D", true, dueDate = null))
        assertLabelShown(textView, "CLEAR")
    }

    @Test
    fun setDueDateLabel_completedWithDue_showsClear() {
        val textView = newTextView()
        bind(textView, Task("T", "D", true, dueDate = today.plusDays(5)))
        assertLabelShown(textView, "CLEAR")
    }

    @Test
    fun setDueDateLabel_activeNoDue_showsNoLabel() {
        val textView = newTextView()
        bind(textView, Task("T", "D", false, dueDate = null))
        assertNoLabel(textView)
    }

    @Test
    fun setDueDateLabel_nullTask_showsNoLabel() {
        val textView = newTextView()
        bind(textView, null)
        assertNoLabel(textView)
    }

    // Edge cases

    @Test
    fun setDueDateLabel_completedNoDueThenUnchecked_clearsLabel() {
        // GIVEN - a row showing "CLEAR" for a completed task without a due date
        val textView = newTextView()
        bind(textView, Task("T", "D", true, "id1", dueDate = null))
        // WHEN - the same task is unchecked
        bind(textView, Task("T", "D", false, "id1", dueDate = null))
        // THEN - "CLEAR" is no longer shown
        assertNoLabel(textView)
    }

    @Test
    fun setDueDateLabel_activeWithDueCheckedThenUnchecked_showsClearThenDMinus1() {
        val textView = newTextView()
        bind(textView, Task("T", "D", false, "id1", dueDate = today.plusDays(1)))

        bind(textView, Task("T", "D", true, "id1", dueDate = today.plusDays(1)))
        assertLabelShown(textView, "CLEAR")

        bind(textView, Task("T", "D", false, "id1", dueDate = today.plusDays(1)))
        assertLabelShown(textView, "D-1")
    }

    @Test
    fun setDueDateLabel_recycledFromDMinus1ToNoDue_clearsLabel() {
        val textView = newTextView()
        bind(textView, Task("A", "D", false, dueDate = today.plusDays(1)))
        bind(textView, Task("B", "D", false, dueDate = null))
        assertNoLabel(textView)
    }

    @Test
    fun setDueDateLabel_recycledFromClearToNullTask_clearsLabel() {
        val textView = newTextView()
        bind(textView, Task("A", "D", true, dueDate = null))
        bind(textView, null)
        assertNoLabel(textView)
    }

    @Test
    fun setDueDateLabel_recycledFromNoLabelToDueToday_showsDDay() {
        val textView = newTextView()
        bind(textView, Task("A", "D", false, dueDate = null))
        bind(textView, Task("B", "D", false, dueDate = today))
        assertLabelShown(textView, "D-Day")
    }

    @Test
    fun setDueDateLabel_activeDueYesterday_showsDPlus1() {
        val textView = newTextView()
        bind(textView, Task("T", "D", false, dueDate = today.minusDays(1)))
        assertLabelShown(textView, "D+1")
    }

    @Test
    fun setDueDateLabel_activeDueTomorrow_showsDMinus1() {
        val textView = newTextView()
        bind(textView, Task("T", "D", false, dueDate = today.plusDays(1)))
        assertLabelShown(textView, "D-1")
    }
}
