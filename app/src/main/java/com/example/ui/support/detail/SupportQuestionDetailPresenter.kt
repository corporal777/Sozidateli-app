package com.example.ui.support.detail

import com.example.data.AppData
import com.example.repository.CommonRepository
import com.example.ui.base.BasePresenter
import moxy.InjectViewState
import java.nio.charset.Charset
import javax.inject.Inject

@InjectViewState
class SupportQuestionDetailPresenter
@Inject constructor(
    private val appData: AppData,
    private val commonRepository: CommonRepository
) : BasePresenter<SupportQuestionDetailContract.View>(appData),
    SupportQuestionDetailContract.Presenter {

    var questionTitle = ""
    var questionAnswer = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val str = "# Ordered lists\n\n" +
                "1. **Lorem Ipsum**\n" +
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                "when an unknown printer took\n\u200C\n2. **Lorem Ipsum**\n" +
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n" +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,\n" +
                "when an unknown printer took   \n\u200C\n" +

                "3. **Lorem Ipsum**\n" +
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n" +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,\n" +
                "when an unknown printer took"

        val test = "1. **Используйте сложный пароль**\nПридумайте сложный пароль, содержащий не менее 8 символов: цифр, латинские заглавные и строчные буквы, например: Do78b15R9oyE0U3t19ro.\n\n   Используйте разные пароли для профиля ВКонтакте, почтового ящика и остальных сайтов.\n**Важно!** Если вы получаете подозрительные сообщения, заметили неизвестные попытки входа в профиль или подозреваете, что ваш аккаунт взломан, сразу же смените пароль в разделе [**Настройки аккаунта**](https://alfa.sozidateli.ru/settings)→ **Пароль**.\n\u200C\n2. **Проверяйте актуальность данных**\nСледите, чтобы к профилю был привязан актуальный номер телефона и email. Никогда и никому не сообщайте коды, которые вы получили от нас в SMS или посредством звонка.\n\n   Если вы пытаетесь подтвердить актуальный email, но не видите письмо от Созидателей с просьбой подтвердить email, проверьте все папки в почтовом ящике (в том числе «Спам» и «Социальные сети»). Также убедитесь, что адрес [noreply@sozidateli.ru](mailto:noreply@sozidateli.ru) не добавлен в черный список и не блокируется спам-фильтром. Если все в порядке, но письма нет, напишите нам в [Поддержку](mailto:support@sozidateli.ru).\n\u200C\n3. **Периодически проверяйте историю активности профиля**\nНайти ее можно в разделе [**Настройки аккаунта**](https://alfa.sozidateli.ru/settings )→ **Устройства и сессии**. Если заметите там что-то подозрительное (незнакомый IP, браузер, время захода, локацию) — немедленно нажмите на **Завершить другие сеансы** и смените пароль.\n\u200C\n4. **Соблюдайте конфиденциальность**\nПожалуйста, никогда не передавайте данные для входа в ваш аккаунт третьим лицам. Если вы заходите на сайт с чужого устройства, по завершении сессии обязательно выходите из своего профиля и не сохраняйте пароли, если это предлагает браузер."

        viewState.setQuestion(questionTitle, questionAnswer)
    }


}