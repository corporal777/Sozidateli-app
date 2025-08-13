package com.examle.domain.model.event

import android.os.Parcelable
import com.examle.domain.model.FavoriteModel
import com.examle.domain.model.organization.OrganizationModel
import com.examle.domain.model.user.SpeakerModel
import kotlinx.parcelize.Parcelize

data class EventModel(
    val id: Int,
    val name: String,
    val description : String? = null,
    val holdingDate: String = "",
    val requestDate: String? = null,
    val backgroundColor: String? = null,
    val status: String? = null,
    val actionStatus : EventActionStatus,
    val isStatusActionAvailable : Boolean,
    val isHasFormResult : Boolean = false,
    val state: EventStateModel? = null,
    val image: String? = null,
    val address: String? = null,
    val userRegistration: UserRegistrationModel? = null,
    val userRegistrationState: EventRegistrationModel? = null,
    val eventRegistrationState: EventRegistrationModel? = null,
    val organization: OrganizationModel? = null,
    val eventSpeakers : List<SpeakerModel> = emptyList(),
    val userFavorite: FavoriteModel? = null,
    val activities : List<EventActivityModel> = emptyList()
)


//enum class EventStatus {
//    CANCELED,
//    PREPARING,
//    AWAITING,
//    PENDING,
//    APPROVED,
//    DECLINED,
//    BANNED,
//    REGISTRATION,
//    REGISTRATION_FINISHED,
//    RUNNING,
//    FINISHED,
//    CONFERENCE_ENDS,
//    IN_ARCHIVE
//}
