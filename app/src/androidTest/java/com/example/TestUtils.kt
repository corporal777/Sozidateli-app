package com.example

import android.app.Activity
import android.view.View
import androidx.test.espresso.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.ui.main.MainActivity
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException


object TestUtils {
    fun waitId(viewId: Int, millis: Long): ViewAction? {
        return object : ViewAction {
            override fun getDescription(): String {
                return "wait for a specific view with id <$viewId> during $millis millis."
            }

            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun perform(uiController: UiController?, view: View?) {
                uiController?.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + millis
                val viewMatcher: Matcher<View> = withId(viewId)
                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return
                        }
                    }
                    uiController?.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)
                throw PerformException.Builder()
                    .withActionDescription(description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException())
                    .build()
            }

        }

    }

    fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }

    fun currentActivity(rule : ActivityScenarioRule<MainActivity>): Activity? {
        var activity: MainActivity? = null
        rule.scenario.onActivity {
            activity = it
        }
        return activity
    }

    fun ViewInteraction.waitUntilVisible(timeout: Long): ViewInteraction {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + timeout

        do {
            try {
                check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                return this
            } catch (e: NoMatchingViewException) {
                Thread.sleep(50)
            }
        } while (System.currentTimeMillis() < endTime)

        throw TimeoutException()
    }

    fun DataInteraction.waitUntilVisible(timeout: Long): DataInteraction {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + timeout

        do {
            try {
                check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                return this
            } catch (e: NoMatchingViewException) {
                Thread.sleep(50)
            }
        } while (System.currentTimeMillis() < endTime)

        throw TimeoutException()
    }

}
