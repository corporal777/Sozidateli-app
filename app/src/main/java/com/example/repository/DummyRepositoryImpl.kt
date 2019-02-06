package com.example.repository

import com.example.data.models.*
import com.example.util.LOREM
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

class DummyRepositoryImpl
@Inject constructor(
) : DummyRepository {

    private val logos = listOf(
            "https://r.hswstatic.com/w_907/gif/tesla-cat.jpg",
            "https://www.aspca.org/sites/default/files/cat-care_urine-marking_main-image.jpg",
            "https://www.pets4homes.co.uk/images/classifieds/2013/07/16/362464/large/beautiful-two-cats-bengal-x-british-shorthair-51f9b081db1b2.jpg",
            "https://www.animeddirect.co.uk/advice/wp-content/uploads/2016/05/cat-1459522_1920.jpg",
            "https://www.ajc.com/rf/image_large/Pub/p8/AJC/2017/05/30/Images/GettyImages-103131850-RrP1uSpXextUiu1XkjmkXgI-680x383%40AJC.com.jpg"
    )

    private val icons = listOf(
            "https://i.pinimg.com/736x/3c/99/3d/3c993d920b9fe8389063a67cce75fc6f.jpg",
            "https://i.pinimg.com/originals/3b/a2/5e/3ba25e6e74fcd83b508e5a9bf5b8acee.jpg",
            "http://i.kinja-img.com/gawker-media/image/upload/s--gRG2YWja--/efg4piwisx1tcco4byit.png",
            "http://www.britgrace-cat.com/images/pg/Golan_Sabine-Gloria-small.jpg",
            "https://pbs.twimg.com/profile_images/424484505915621376/EOwsjaMZ.png"
    )

    private val names = listOf(
            "Наталья Краснова",
            "Анна Морозова",
            "Владимир Малыч",
            "Николай Иванов",
            "Александра Майер",
            "Георгий Кудрявцев",
            "Николай Копылов",
            "Степан Кулагин",
            "Ирина Капустина",
            "Виктория Давыдова",
            "Наталья Захарова",
            "Нина Дементьева",
            "Ростислав Сафонов",
            "Григорий Захаров",
            "Вадим Горшков",
            "Дмитрий Елисеев",
            "Елизавета Шарапова",
            "Алёна Колобова",
            "Марина Гусева"
    )

    private val avatars = listOf(
            "https://st.kp.yandex.net/images/actor_iphone/iphone360_10143.jpg",
            "https://st.kp.yandex.net/images/actor_iphone/iphone360_11380.jpg",
            "https://st.kp.yandex.net/images/actor_iphone/iphone360_24302.jpg",
            "https://st.kp.yandex.net/images/actor_iphone/iphone360_21444.jpg",
            "https://st.kp.yandex.net/images/actor_iphone/iphone360_3903.jpg"
    )

    private val tags = listOf(
            "Для всех",
            "Технологии",
            "Спорт",
            "Домашнее хоз-во",
            "Череповец"
    )

    private val buildingSchemas = listOf(
            "https://unecon.ru/sites/default/files/1etazh.jpg",
            "https://sapr.ru/archive/sg/2009/1/21/3b.jpg",
            "http://www.kirovssk.ru/upload/iblock/0d6/0d6deeb9d485cf3537456add390f8c14.jpg",
            "https://sport.bmstu.net/uploads/media/branch_page/2016/05/09/02/469e56b0dce650be2396.jpg",
            "https://freelance.ru/img/portfolio/pics/00/1F/A1/2072950.jpg"
    )

    private val coordinates = listOf(
            listOf(55.753705, 37.619918),
            listOf(55.759824, 37.625112),
            listOf(55.741015, 37.628317),
            listOf(55.757374, 37.660706),
            listOf(55.828684, 37.633627)
    )

    private fun getRandomDate(): Long {
        val oneYear = 31536000000
        val now = System.currentTimeMillis()
        return Random.nextLong(now - oneYear, now + oneYear)
    }

    private fun getRandomText(suggestionCount: Int? = null): String {
        val suggestions = LOREM.split(".")

        val max = if (suggestionCount != null) {
            if (suggestionCount >= suggestions.size) suggestions.size - 1
            else suggestionCount
        } else Random.nextInt(1, suggestions.size)

        var text = ""
        for (index in 0..max) {
            text = text.plus(suggestions[index]).plus(". ")
        }
        return text.trim()
    }

    private fun generateEvents(limit: Int, offset: Int): List<Event> {
        return (1..limit).map { index ->
            val dates = arrayOf(getRandomDate(), getRandomDate())
            Event(
                    "${offset + index}",
                    "Российский инвестиционный форум ${offset + index}",
                    logos.random(),
                    getRandomText(),
                    dates.min() ?: 0,
                    dates.max() ?: System.currentTimeMillis(),
                    "Форум $index",
                    Status.values().random(),
                    Place(
                            coordinates.random(),
                            getRandomText(),
                            buildingSchemas.random(),
                            getRandomText()
                    )
            )
        }
    }

    private fun generateSubscriptions(limit: Int, offset: Int): List<Subscription> {
        return (1..limit).map { index ->
            Subscription(
                    "${offset + index}",
                    "Российский инвестиционный форум ${offset + index}",
                    icons.random()
            )
        }
    }

    private fun generateNews(limit: Int, offset: Int): List<News> {
        return (1..limit).map { index ->
            News(
                    "${offset + index}",
                    "Заголовок новсти Российский инвестиционный форум ${offset + index}",
                    getRandomText(),
                    logos.random(),
                    getRandomDate()
            )
        }
    }

    private fun generateDocuments(limit: Int, offset: Int): List<Document> {
        return (1..limit).map { index ->
            Document(
                    "${offset + index}",
                    "РРТ Название презентации ${offset + index}\nв две строки"
            )
        }
    }

    private fun generateSpeakers(limit: Int, offset: Int, onlyFavorite: Boolean = false): List<User> {
        return (1..limit).map { index ->
            User(
                    (offset + index).toString(),
                    names.random(),
                    avatars.random(),
                    getRandomText(),
                    subscribed = if (onlyFavorite) true else Random.nextBoolean()
            )
        }
    }

    private fun generateUser(id: Int): User {
        return User(
                id = "$id",
                name = names.random(),
                image = avatars.random(),
                info = getRandomText(),
                subscribed = Random.nextBoolean()
        )
    }

    private fun generateSpeakers(limit: Int, offset: Int): List<User> {
        return (1..limit).map { index -> generateUser(offset + index) }
    }

    private fun generateUserChats(limit: Int, offset: Int): List<UserChat> {
        return (1..limit).map { index ->
            UserChat(
                    "testChat",
                    generateUser(offset + index),
                    ChatMessage(
                            getRandomText(),
                            "${offset + index}",
                            Date(getRandomDate())
                    )
            )
        }
    }

    private fun generateUserNotifications(limit: Int, offset: Int): List<Notification> {
        return (1..limit).map { index ->
            Notification(
                    "${offset + index}",
                    getRandomText(Random.nextInt(1, 5)),
                    getRandomDate()
            )
        }
    }

    override fun loadTags(): Single<List<String>> {
        return Single.fromCallable { return@fromCallable tags }
    }

    override fun loadSubevent(inSchedule: Boolean): Single<List<Subevent>> {
        return Single.fromCallable {
            return@fromCallable (0..7).map {
                Subevent(it.toString(), "9:00 - 14:00", (0..Random(System.currentTimeMillis()).nextInt(0, 4)).map {
                    tags.random()
                }, Random.nextBoolean(),inSchedule,tags.random())
            }
        }
    }

    override fun loadSearchType(): Single<List<SearchTypeEvent>> {
        return Single.fromCallable {
            return@fromCallable (1..12).map {
                SearchTypeEvent(it.toString(), "Тестовый ${it}")
            }
        }
    }

    override fun loadFavoriteSpeakers(limit: Int, offset: Int): Maybe<PaginationResponse<User>> {
        return Maybe.fromCallable { PaginationResponse(totalCount = null, data = generateSpeakers(limit, offset, true)) }
    }

    override fun loadRecommendations(limit: Int, offset: Int): Maybe<PaginationResponse<Event>> {
        return Maybe.fromCallable { PaginationResponse(totalCount = null, data = generateEvents(limit, offset)) }
    }

    override fun loadSubscriptions(limit: Int, offset: Int): Maybe<PaginationResponse<Subscription>> {
        return Maybe.fromCallable { PaginationResponse(totalCount = null, data = generateSubscriptions(limit, offset)) }
    }

    override fun loadNews(event: String, limit: Int, offset: Int): Maybe<PaginationResponse<News>> {
        return Maybe.fromCallable { PaginationResponse(totalCount = null, data = generateNews(limit, offset)) }
    }

    override fun loadDocuments(event: String, limit: Int, offset: Int): Maybe<PaginationResponse<Document>> {
        return Maybe.fromCallable { PaginationResponse(totalCount = null, data = generateDocuments(limit, offset)) }
    }

    override fun loadEventSpeakers(event: String, limit: Int, offset: Int): Maybe<PaginationResponse<User>> {
        return Maybe.fromCallable { PaginationResponse(totalCount = null, data = generateSpeakers(limit, offset)) }
    }

    override fun loadUserChats(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>> {
        return Maybe.fromCallable { PaginationResponse(totalCount = null, data = generateUserChats(limit, offset)) }
    }

    override fun loadUserNotifications(limit: Int, offset: Int): Maybe<PaginationResponse<Notification>> {
        return Maybe.fromCallable { PaginationResponse(totalCount = null, data = generateUserNotifications(limit, offset)) }
    }

    override fun getEvent() = generateEvents(1, 0).first()

    override fun getUser(id: Int) = generateUser(id)
}