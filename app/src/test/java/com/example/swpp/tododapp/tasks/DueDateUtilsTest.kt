package com.example.swpp.tododapp.tasks

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for [dueDateLabel], one per Tip in specs/DueDateUtils.spec.md. */
class DueDateUtilsTest {

    @Test
    fun dueDateLabel_completedFutureDue_returnsClear() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-05-10")
        val today = LocalDate.parse("2026-05-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN
        assertEquals("CLEAR", label)
    }

    @Test
    fun dueDateLabel_completedNoDue_returnsClear() {
        // GIVEN
        val dueDate: LocalDate? = null
        val today = LocalDate.parse("2026-05-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN
        assertEquals("CLEAR", label)
    }

    @Test
    fun dueDateLabel_completedOverdue_returnsClear() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-04-20")
        val today = LocalDate.parse("2026-05-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN
        assertEquals("CLEAR", label)
    }

    @Test
    fun dueDateLabel_activeNoDue_returnsEmpty() {
        // GIVEN
        val dueDate: LocalDate? = null
        val today = LocalDate.parse("2026-05-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("", label)
    }

    @Test
    fun dueDateLabel_activeDueToday_returnsDDay() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-05-01")
        val today = LocalDate.parse("2026-05-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-Day", label)
    }

    @Test
    fun dueDateLabel_activeDueIn3DaysSameMonth_returnsDMinus3() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-05-04")
        val today = LocalDate.parse("2026-05-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-3", label)
    }

    @Test
    fun dueDateLabel_activeDueNextMonth_returnsDMinus45() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-06-15")
        val today = LocalDate.parse("2026-05-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-45", label)
    }

    @Test
    fun dueDateLabel_activeDueIn100Days_returnsDMinus100() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-04-11")
        val today = LocalDate.parse("2026-01-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-100", label)
    }

    @Test
    fun dueDateLabel_activeOverdue3DaysSameMonth_returnsDPlus3() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-04-28")
        val today = LocalDate.parse("2026-05-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D+3", label)
    }

    @Test
    fun dueDateLabel_activeOverdueAcrossMonths_returnsDPlus29() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-01-31")
        val today = LocalDate.parse("2026-03-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D+29", label)
    }

    @Test
    fun dueDateLabel_activeDueYesterday_returnsDPlus1() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-04-30")
        val today = LocalDate.parse("2026-05-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D+1", label)
    }

    @Test
    fun dueDateLabel_activeDueTomorrow_returnsDMinus1() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-05-02")
        val today = LocalDate.parse("2026-05-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-1", label)
    }

    @Test
    fun dueDateLabel_activeMonthEndToNextMonthFirst_returnsDMinus1() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-02-01")
        val today = LocalDate.parse("2026-01-31")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-1", label)
    }

    @Test
    fun dueDateLabel_activeOneDayShortOfMonthAhead_returnsDMinus30() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-02-14")
        val today = LocalDate.parse("2026-01-15")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-30", label)
    }

    @Test
    fun dueDateLabel_activeExactlyOneMonthAhead_returnsDMinus31() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-02-15")
        val today = LocalDate.parse("2026-01-15")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-31", label)
    }

    @Test
    fun dueDateLabel_activeOneDayPastMonthAhead_returnsDMinus32() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-02-16")
        val today = LocalDate.parse("2026-01-15")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-32", label)
    }

    @Test
    fun dueDateLabel_activeOneDayShortOfMonthOverdue_returnsDPlus30() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-01-16")
        val today = LocalDate.parse("2026-02-15")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D+30", label)
    }

    @Test
    fun dueDateLabel_activeExactlyOneMonthOverdue_returnsDPlus31() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-01-15")
        val today = LocalDate.parse("2026-02-15")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D+31", label)
    }

    @Test
    fun dueDateLabel_activeOneDayPastMonthOverdue_returnsDPlus32() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-01-14")
        val today = LocalDate.parse("2026-02-15")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D+32", label)
    }

    @Test
    fun dueDateLabel_activeDec31ToJan1_returnsDMinus1() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-01-01")
        val today = LocalDate.parse("2025-12-31")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-1", label)
    }

    @Test
    fun dueDateLabel_activeJan1BackToDec31_returnsDPlus1() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2025-12-31")
        val today = LocalDate.parse("2026-01-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D+1", label)
    }

    @Test
    fun dueDateLabel_activeOneDayShortOfYearAhead_returnsDMinus364() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2025-12-31")
        val today = LocalDate.parse("2025-01-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-364", label)
    }

    @Test
    fun dueDateLabel_activeExactlyOneYearAhead_returnsDMinus365() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-01-01")
        val today = LocalDate.parse("2025-01-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-365", label)
    }

    @Test
    fun dueDateLabel_activeOneDayPastYearAhead_returnsDMinus366() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2026-01-02")
        val today = LocalDate.parse("2025-01-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-366", label)
    }

    @Test
    fun dueDateLabel_activeLeapFeb28ToFeb29_returnsDMinus1() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2024-02-29")
        val today = LocalDate.parse("2024-02-28")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-1", label)
    }

    @Test
    fun dueDateLabel_activeLeapFeb28ToMar1_returnsDMinus2() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2024-03-01")
        val today = LocalDate.parse("2024-02-28")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-2", label)
    }

    @Test
    fun dueDateLabel_activeNonLeapFeb28ToMar1_returnsDMinus1() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2023-03-01")
        val today = LocalDate.parse("2023-02-28")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D-1", label)
    }

    @Test
    fun dueDateLabel_activeLeapMar1BackToFeb29_returnsDPlus1() {
        // GIVEN
        val dueDate: LocalDate? = LocalDate.parse("2024-02-29")
        val today = LocalDate.parse("2024-03-01")

        // WHEN
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN
        assertEquals("D+1", label)
    }
}
