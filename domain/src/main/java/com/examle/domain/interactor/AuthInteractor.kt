package com.examle.domain.interactor

import com.examle.domain.model.Optional
import com.examle.domain.model.auth.AuthModel
import com.examle.domain.model.auth.LoginModel
import com.examle.domain.model.auth.SnAuthModel
import com.examle.domain.model.auth.TokenStatusModel
import com.examle.domain.model.body.AuthBody
import com.examle.domain.model.body.VKAuthBody
import com.examle.domain.model.user.SnUser
import com.examle.domain.repository.AuthRepository
import com.example.common.util.Utils.validatePhoneBeforeSend
import kotlinx.coroutines.flow.Flow

class AuthInteractor(private val repository: AuthRepository) {

    fun checkUserAuth(): Flow<TokenStatusModel> {
        return repository.checkUserAuth()
    }

    fun authWithResult(type: String, login: String, password: String): Flow<AuthModel> {
        val body = createAuthBody(type, login, password)
        return repository.authEmailOrPhoneWithResult(body)
    }


    fun authWithInvite(code: Int, type: String, login: String, password: String): Flow<AuthModel> {
        val body = createAuthBody(type, login, password)
        return repository.authEmailOrPhoneWithInvite(code, body)
    }

    fun authWithSn(type: String, login: String, password: String, sn: SnAuthModel): Flow<AuthModel> {
        val body = createAuthBody(type, login, password)
        return repository.authEmailOrPhoneWithSn(body, sn)
    }

    fun authWithVk(sn: SnAuthModel) : Flow<Optional<SnUser>> {
        val body = VKAuthBody(sn.token, sn.uuid)
        return repository.authWithVk(body, sn)

    }

    fun getStories(): List<String> {
        val list = listOf(
            "Завязывайте новые знакомства и встречайте единомышленников",
            "Находите интересные события и выступайте на мероприятиях",
            "Будьте в курсе актуальных событий в вашем городе и во всей стране",
            "Создавайте интересные страницы своих мероприятий",
            "Рассказывайте о себе, обменивайтесь опытом и получайте новые знания",
        )
        return arrayListOf<String>().apply {
            add(list.last())
            addAll(list)
            add(list.first())
        }
    }

    private fun createAuthBody(type: String, login: String, password: String): AuthBody {
        val validatedLogin = if (type == "phone") validatePhoneBeforeSend(login) else login
        return AuthBody(
            LoginModel(type, validatedLogin),
            LoginModel("common", password),
            "",
            "",
            "",
            "",
            ""
        )
    }
}