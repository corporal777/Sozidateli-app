package com.example.repository

import com.example.data.models.Event
import com.example.data.models.Status
import com.example.data.models.Subscription
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import javax.inject.Inject

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

    private fun generateEvents(limit: Int, offset: Int): List<Event> {
        return (1..limit).map { index ->
            Event(
                    "${offset + index}",
                    "Российский инвестиционный форум ${offset + index}",
                    logos.random(),
                    0,
                    0,
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

    override fun loadRecommendations(limit: Int, offset: Int): Maybe<PaginationResponse<Event>> {
        return Maybe.fromCallable { PaginationResponse(totalCount = null, data = generateEvents(limit, offset)) }
    }

    override fun loadSubscriptions(limit: Int, offset: Int): Maybe<PaginationResponse<Subscription>> {
        return Maybe.fromCallable { PaginationResponse(totalCount = null, data = generateSubscriptions(limit, offset)) }
    }
}