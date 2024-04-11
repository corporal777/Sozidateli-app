package com.example

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.runner.screenshot.Screenshot
import com.example.ui.main.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
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

    fun doScreenShot(name : String){
        Screenshot.capture().setName(name).setFormat(Bitmap.CompressFormat.JPEG).process();
    }

    fun findCustomEditText(id : Int): Matcher<View> {
        return Matchers.allOf(
            ViewMatchers.withClassName(Matchers.`is`("androidx.appcompat.widget.AppCompatEditText")),
            child(
                Matchers.allOf(
                    withId(R.id.clSearch),
                    child(
                        child(withId(id), 0),
                        1
                    )
                ),
                0
            ),
            ViewMatchers.isDisplayed()
        )
    }

    fun findCustomMenuText(id : Int): Matcher<View> {
        return Matchers.allOf(
            ViewMatchers.withClassName(Matchers.`is`("androidx.appcompat.widget.AppCompatAutoCompleteTextView")),
            child(
                child(
                    Matchers.allOf(
                        withId(R.id.tilInput),
                        child(
                            child(withId(id), 0),
                            1
                        )
                    ), 0
                ), 1
            ),
            ViewMatchers.isDisplayed()
        )
    }

    fun getChildFromView(parentMatcher: Matcher<View?>, childPosition: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("position $childPosition of parent ")
                parentMatcher.describeTo(description)
            }

            override fun matchesSafely(view: View): Boolean {
                if (view.parent !is ViewGroup) return false
                val parent = view.parent as ViewGroup
                return parentMatcher.matches(parent)
                        && parent.childCount > childPosition
                        && parent.getChildAt(childPosition) == view
            }
        }
    }

    private fun child(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
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
