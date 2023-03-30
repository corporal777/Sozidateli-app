package com.example

import com.example.data.AppData
import com.example.data.prefs.AppPrefs
import org.junit.Assert
import org.junit.Test

class AppDataTest {

    private fun createAppData(userToken: String? = null): AppData {
        return AppData(object : AppPrefs {
            override var selectedEvent: String? = null
            override var userToken: String? = userToken
            override var isFCMTokenSent: Boolean = false
            override var userId: Int = -1
            override var isStoriesShown: Boolean = false
            override var uniqueDeviceId: String? = ""
            override var attemptsOfChangePassword: Int = 0
            override var updateTime: Long = 0
        })
    }

    @Test
    fun `login save token`() {
        val appData = createAppData()

        val token = "test_token"

        appData.login(token)

        Assert.assertEquals(token, appData.token)
    }

    @Test
    fun `login set isLoggedOut to false`() {
        val appData = createAppData()

        val token = "test_token"

        appData.login(token)

        Assert.assertFalse(appData.isLoggedOut)
    }

    @Test
    fun `token is set on init`() {
        val token = "test_token"
        val appData = createAppData(token)

        Assert.assertEquals(token, appData.token)
    }

    @Test
    fun `token is set on init isLoggedOut is false`() {
        val token = "test_token"
        val appData = createAppData(token)

        Assert.assertFalse(appData.isLoggedOut)
    }

    @Test
    fun `logout set token to null`() {
        val token = "test_token"
        val appData = createAppData(token)
        appData.logout()

        Assert.assertNull(appData.token)
    }

    @Test
    fun `logout set isLoggedOut to true`() {
        val token = "test_token"
        val appData = createAppData(token)
        appData.logout()

        Assert.assertTrue(appData.isLoggedOut)
    }

    @Test
    fun `set token to null set isLoggedOut to true`() {
        val token = "test_token"
        val appData = createAppData(token)
        appData.token = null

        Assert.assertTrue(appData.isLoggedOut)
    }

    @Test
    fun `set token when isLoggedOut is true token is null`() {
        val token = "test_token"
        val appData = createAppData()
        appData.token = token

        Assert.assertNull(appData.token)
    }

    @Test
    fun `set token to null when isLoggedOut is false token is null`() {
        val token = "test_token"
        val appData = createAppData(token)
        appData.token = null

        Assert.assertNull(appData.token)
    }
}