package com.example

import com.example.common.util.AuthValidateUtil
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidateUtilTest {

    // email tests

    @Test
    fun correctEmailSimpleReturnsTrue(email : String) {
        //assertTrue(AuthValidateUtil.isValidEmail("name@email.com"))
        assertTrue(AuthValidateUtil.isValidEmail(email))
    }

    @Test
    fun correctEmailSubDomainReturnsTrue() {
        assertTrue(AuthValidateUtil.isValidEmail("name@email.io.com"))
    }

    @Test
    fun invalidEmailNoTldReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidEmail("name@email"))
    }

    @Test
    fun invalidEmailNoNameReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidEmail("@email.com"))
    }

    @Test
    fun invalidEmailNoDomainReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidEmail("name@"))
    }

    @Test
    fun invalidEmailDoubleDotReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidEmail("name@email..com"))
    }

    @Test
    fun invalidEmailEmptyReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidEmail(""))
    }

    //phone number test

    @Test
    fun correctPhoneNumberSimpleReturnsTrue() {
        assertTrue("Correct phone", AuthValidateUtil.isValidPhone("+79267806176"))
    }

    @Test
    fun invalidPhoneNumberNotFirstNumberIsNineReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidPhone("+78267806176"))
    }

    @Test
    fun invalidPhoneNumberLengthReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidPhone("+7926780617"))
    }

    @Test
    fun invalidPhoneNumberEmptyReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidPhone(""))
    }

    // password tests

    @Test
    fun correctPassword6SymbolsReturnsTrue() {
        assertTrue(AuthValidateUtil.isValidPassword("123456"))
    }

    @Test
    fun correctPassword7SymbolsReturnsTrue() {
        assertTrue(AuthValidateUtil.isValidPassword("1234567"))
    }

    @Test
    fun invalidPassword5SymbolsReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidPassword("12345"))
    }

    @Test
    fun invalidPasswordEmptyReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidPassword(""))
    }

    @Test
    fun invalidPasswordNewLineReturnsFalse() {
        assertFalse(AuthValidateUtil.isValidPassword("123\n456"))
    }
}