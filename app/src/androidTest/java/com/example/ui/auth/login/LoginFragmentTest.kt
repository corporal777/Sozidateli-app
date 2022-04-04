package com.example.ui.auth.login

import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.R
import com.example.ui.main.MainActivity
import junit.framework.TestCase
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginFragmentTest : TestCase() {

    private lateinit var scenario: ActivityScenario<MainActivity>

    private var mLogin = "t_3@houseofapps.ru"
    private var mPassword = "Lera1801"


    @Before
    override fun setUp() {
        super.setUp()

        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun setLoginAndPassword() {

//        onView(withId(R.id.etLogin)).check(matches(isDisplayed()))
//        onView(withId(R.id.etPassword)).check(matches(isDisplayed()))
//        onView(withId(R.id.ibLogin)).check(matches(isDisplayed()))

//        val loginResult: ViewInteraction = onView(withId(R.id.etLogin))
//        mLogin = getText(loginResult)
//        val passwordResult : ViewInteraction = onView(withId(R.id.etPassword))
//        mPassword = getText(passwordResult)

        onView(withId(R.id.etLogin))
            .perform(typeText(mLogin))

        onView(withId(R.id.etPassword))
            .perform(typeText(mPassword))

        onView(withId(R.id.ibLogin)).perform(click())


    }

    private fun getText(matcher: ViewInteraction): String {
        var text = String()
        matcher.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "Text of the view"
            }

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView
                text = tv.text.toString()
            }
        })

        return text
    }

}