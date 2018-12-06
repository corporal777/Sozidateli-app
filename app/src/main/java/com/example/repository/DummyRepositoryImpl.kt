package com.example.repository

import com.example.data.models.*
import com.example.util.LOREM
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
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
            "Александра Майер"
    )

    private val avatars = listOf(
            "https://st.kp.yandex.net/images/actor_iphone/iphone360_10143.jpg",
            "https://st.kp.yandex.net/images/actor_iphone/iphone360_11380.jpg",
            "https://st.kp.yandex.net/images/actor_iphone/iphone360_24302.jpg",
            "https://st.kp.yandex.net/images/actor_iphone/iphone360_21444.jpg",
            "https://st.kp.yandex.net/images/actor_iphone/iphone360_3903.jpg"
    )

    private fun getRandomDate(): Long {
        val oneYear = 31536000000
        val now = System.currentTimeMillis()
        return Random.nextLong(now - oneYear, now + oneYear)
    }

    private fun getRandomLongText(): String {
        val suggestions = LOREM.split(".")
        var text = ""
        for (index in 0..Random.nextInt(1, suggestions.size)) {
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
                    getRandomLongText(),
                    dates.min() ?: 0,
                    dates.max() ?: System.currentTimeMillis(),
                    "Форум $index",
                    Status.values().random()
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
                    getRandomLongText(),
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

    private fun generateUser(id: Int): User {
        return User(
                id = "$id",
                name = names.random(),
                image = avatars.random(),
                info = getRandomLongText(),
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
                            getRandomLongText(),
                            "${offset + index}",
                            Date(getRandomDate())
                    )
            )
        }
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
}