package com.example

import com.example.data.models.ApiError
import org.junit.Assert
import org.junit.Test

class ApiErrorTest {

    private fun createApiError(
            errors: List<String>
    ) = ApiError(
            code = 0,
            session = null,
            errors = errors
    )

    @Test
    fun `has error true`() {
        val error = "test_error"
        val apiError = createApiError(errors = listOf(error))

        Assert.assertTrue(apiError.hasError(error))
    }

    @Test
    fun `has error false`() {
        val error = "test_error"
        val apiError = createApiError(errors = listOf(error))

        Assert.assertFalse(apiError.hasError("error"))
    }

    @Test
    fun `has error of list true`() {
        val error = "test_error"
        val apiError = createApiError(errors = listOf(error, "error2"))

        Assert.assertTrue(apiError.hasError(error))
    }

    @Test
    fun `has error of list false`() {
        val error = "test_error"
        val apiError = createApiError(errors = listOf(error, "error2"))

        Assert.assertFalse(apiError.hasError("error"))
    }

    @Test
    fun `has any error true`() {
        val error1 = "test_error1"
        val error2 = "test_error2"
        val apiError = createApiError(errors = listOf(error1))

        Assert.assertTrue(apiError.hasError(error1, error2))
    }

    @Test
    fun `do not has any error false`() {
        val error1 = "test_error1"
        val error2 = "test_error2"
        val apiError = createApiError(errors = listOf("error"))

        Assert.assertFalse(apiError.hasError(error1, error2))
    }
}