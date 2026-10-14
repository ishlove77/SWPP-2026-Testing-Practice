package com.example.swpp.tododapp.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.swpp.tododapp.R
import com.example.swpp.tododapp.ServiceLocator
import com.example.swpp.tododapp.data.Task
import com.example.swpp.tododapp.data.source.FakeAndroidTestRepository
import com.example.swpp.tododapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Screen-level test for the "check then uncheck" Tip in specs/TasksListBindings.spec.md. */
@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class TasksFragmentDueDateTest {
    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun tasksList_checkThenUncheckTaskWithoutDueDate_clearLabelDisappears() = runTest {
        // GIVEN - an active task without a due date on the task list
        repository.saveTask(Task("TITLE1", "DESC1", false, "id1"))
        launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)

        // WHEN - the task is checked
        onView(withId(R.id.tasks_list)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("TITLE1")), clickChildViewWithId(R.id.complete_checkbox)
            )
        )
        // THEN - the row shows "CLEAR"
        onView(allOf(withId(R.id.due_date_text), withText("CLEAR"))).check(matches(isDisplayed()))

        // WHEN - the task is unchecked again
        onView(withId(R.id.tasks_list)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("TITLE1")), clickChildViewWithId(R.id.complete_checkbox)
            )
        )
        // THEN - "CLEAR" is no longer shown anywhere
        onView(allOf(withId(R.id.due_date_text), withText("CLEAR"), isDisplayed()))
            .check(doesNotExist())
    }
}

/** Clicks the child view with [id] inside a RecyclerView item instead of the item itself. */
private fun clickChildViewWithId(id: Int) = object : ViewAction {
    override fun getConstraints(): Matcher<View> = isAssignableFrom(View::class.java)
    override fun getDescription() = "click child view with id $id"
    override fun perform(uiController: UiController, view: View) {
        view.findViewById<View>(id).performClick()
        uiController.loopMainThreadUntilIdle()
    }
}
