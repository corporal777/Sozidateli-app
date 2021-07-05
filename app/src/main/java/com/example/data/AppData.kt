package com.example.data

import android.util.Log
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.data.prefs.AppPrefs
import com.example.util.PHONE_PERSONAL
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class AppData(
        private val appPrefs: AppPrefs
) {

    var token: String? = appPrefs.userToken
        set(value) {
            val changed = field != value
            if (changed) {
                if (value.isNullOrEmpty()) {
                    field = null
                    appPrefs.userToken = null
                    if (!isLoggedOut) {
                        logout()
                    }

                    tokenChangeSubject.onNext(Optional())
                } else if (!isLoggedOut) {
                    field = value
                    appPrefs.userToken = value
                    tokenChangeSubject.onNext(value.asOptional())
                }
            }
        }

    var isStoriesShown: Boolean = appPrefs.isStoriesShown
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                appPrefs.isStoriesShown = value
            }
        }

    var isSubscribedToPush: Boolean = appPrefs.isFCMTokenSent
        set(value) {
            field = value
            appPrefs.isFCMTokenSent = value
        }

    var chatUnreadMessageCount = 0
        set(value) {
            val changed = field != value
            field = value
            if (changed) chatMessageCountSubject.onNext(value)
        }

    var chatRequestsCount = 0
        set(value) {
            val changed = field != value
            field = value
            if (changed) chatRequestsCountSubject.onNext(value)
        }

    var notificationsCount = 0
        set(value) {
            val changed = field != value
            field = if (value < 0) 0 else value
            if (changed) notificationsCountSubject.onNext(value)
        }

    var interests: List<Interest>? = null
    var interestsNew: List<InterestNew>? = null

    private var user: User? = null
    private var newUser: UserDetail? = null

    var isLoggedOut = token.isNullOrEmpty()
        private set

    var hasBaseState = false
    var hasMaxState = false
    var defaultEvent: Int? = null

    val userChangeSubject = BehaviorSubject.createDefault(user.asOptional())
    val userNewChangeSubject = BehaviorSubject.createDefault(newUser.asOptional())
    val tokenChangeSubject = BehaviorSubject.createDefault(token.asOptional())
    val chatMessageCountSubject = BehaviorSubject.createDefault(chatUnreadMessageCount)
    val chatRequestsCountSubject = BehaviorSubject.createDefault(chatRequestsCount)
    val notificationsCountSubject = BehaviorSubject.createDefault(notificationsCount)
    val notificationReadSubject = PublishSubject.create<Pair<Int, Notification.AcceptState>>()
    val userPhoneConfirmedSubject = BehaviorSubject.createDefault(false)
    private var eventFormats: List<NewEventFormat>? = null

    fun setEventFormats(formats: List<NewEventFormat>?) {
        eventFormats = formats
    }

    fun getEventFormats() = eventFormats

    fun setUser(user: User) {
        val changed = this.user != user
        this.user = user
        appPrefs.userId = user.user_id
        if (changed) userChangeSubject.onNext(user.asOptional())
        notificationsCount = user.notification_unread
    }

    fun setUserShort(userShort: UserShort) {
        setUser(userShort.toUser())
    }

    fun setAllUserInfo(user: UserDetail) {
        val changed = this.newUser != user
        this.newUser = user
        appPrefs.userId = user.id
        if (changed) userNewChangeSubject.onNext(newUser.asOptional())
    }

    fun updatePhone(phone: String) {
        this.newUser?.phone?.forEach {
            if (it.type == PHONE_PERSONAL){
                it.value = phone
                it.isConfirmed = true
            }
        }
        userNewChangeSubject.onNext(newUser.asOptional())
    }

    fun checkUserState(data: List<UserProfileFields>?) {
        val base = data?.filter { it.requiredFor?.contains("basic") == true }
        val max = data?.filter { it.requiredFor?.contains("maximum") == true }
        hasBaseState = (base?.filter { it.filled == false }?.size?: 0) == 0
        hasMaxState = (max?.filter { it.filled == false }?.size?: 0) == 0
    }

    fun setUserShortNew(user: UserDetail) {
        val changed = this.newUser != user
        val binds = this.newUser?.binds
        val educationLevelList = this.newUser?.educationLevelList
        val speciality = this.newUser?.speciality
        val academicDegrees = this.newUser?.academicDegrees
        this.newUser = user
        this.newUser?.binds = binds
        this.newUser?.educationLevelList = educationLevelList
        this.newUser?.speciality = speciality
        this.newUser?.academicDegrees = academicDegrees
        appPrefs.userId = user.id
        if (changed) userNewChangeSubject.onNext(newUser.asOptional())
    }

    fun updateWorkExperience(data: WorkExperienceServerModel) {
        this.newUser?.binds?.workExperience?.absent = data.absent
        this.newUser?.binds?.workExperience?.models = data.data
        userNewChangeSubject.onNext(newUser.asOptional())
    }

    fun updateEducationLevel(data: List<EducationLevel>?) {
        this.newUser?.educationLevelList = data
        userNewChangeSubject.onNext(newUser.asOptional())
    }

    fun updateSpeciality(data: List<EducationLevel>?) {
        this.newUser?.speciality = data
        userNewChangeSubject.onNext(newUser.asOptional())
    }

    fun updateUserEducation(data: List<EducationModel>?) {
        this.newUser?.binds?.education = data
    }

    fun updateUserAcademicDegree(data: List<AcademicDegreeModel>?) {
        this.newUser?.binds?.academicDegree = data
    }

    fun updateAcademicDegrees(data: List<EducationLevel>?) {
        this.newUser?.academicDegrees = data
        userNewChangeSubject.onNext(newUser.asOptional())
    }

    fun getUserNew(): UserDetail = newUser
            ?: throw UninitializedPropertyAccessException("\"User\" was queried before being initialized")

    fun getUser(): User = user
            ?: throw UninitializedPropertyAccessException("\"User\" was queried before being initialized")

    fun updateUser(update: User.() -> Unit) {
        userChangeSubject.onNext(getUser().apply(update).asOptional())
    }

    fun updateUserNew(update: UserDetail.() -> Unit) {
        userNewChangeSubject.onNext(getUserNew().apply(update).asOptional())
    }

    fun login(token: String) {
        isLoggedOut = false
        this.token = token
    }

    fun saveId(id: Int?) {
        if (id!= null)
            appPrefs.userId = id
    }

    fun getId(): Int {
        return appPrefs.userId?: 0
    }

    fun logout() {
        isLoggedOut = true
        user = null
        appPrefs.userId = -1
        notificationsCount = 0
        chatRequestsCount = 0
        chatUnreadMessageCount = 0
        userChangeSubject.onNext(Optional(null))
        userPhoneConfirmedSubject.onNext(false)
        token = null
    }
}