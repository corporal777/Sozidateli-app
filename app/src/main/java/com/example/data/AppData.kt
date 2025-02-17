package com.example.data

import com.example.app.BuildConfig
import com.example.data.models.*
import com.example.data.models.Optional
import com.example.data.prefs.AppPrefs
import com.example.util.AUTH_TOKEN_INVALID
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class AppData(private val appPrefs: AppPrefs) {

    var updateTime: Long = appPrefs.updateTime
        set(value) {
            field = value
            appPrefs.updateTime = value
        }

    fun getUpdateMinutes(): Long {
        return if (updateTime <= 0) updateTime
        else TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - updateTime)
    }

    fun isTimeToUpdate(): Boolean {
        val isTimeToUpdate: Boolean
        if (updateTime <= 0) isTimeToUpdate = true
        else {
            val seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - updateTime)
            if (BuildConfig.DEBUG && (seconds in 1..600)) isTimeToUpdate = false
            else if (!BuildConfig.DEBUG && (seconds in 1..172800)) isTimeToUpdate = false
            else isTimeToUpdate = true
        }
        return isTimeToUpdate
    }


    var deviceId: String? = appPrefs.uniqueDeviceId
        set(value) {
            if (field.isNullOrEmpty()) {
                field = value
                appPrefs.uniqueDeviceId = value
            }
        }


    var attemptsOfChangePassword: Int = appPrefs.attemptsOfChangePassword
        set(value) {
            if (field <= 3) {
                field = value
                appPrefs.attemptsOfChangePassword = value
            }
        }


    var token: String? = appPrefs.userToken
        set(value) {
            val changed = field != value
            if (changed) {
                if (value.isNullOrEmpty()) {
                    field = null
                    appPrefs.userToken = null
                    if (!isLoggedOut) logout()
                    tokenChangeSubject.onNext(Optional())
                } else if (value == AUTH_TOKEN_INVALID) {
                    field = null
                    appPrefs.userToken = null
                } else if (!isLoggedOut) {
                    field = value
                    appPrefs.userToken = value
                    tokenChangeSubject.onNext(value.asOptional())
                }
            }
        }

    var tempToken: String? = appPrefs.temporaryToken
        set(value) {
            val changed = field != value
            if (changed) {
                field = value
                appPrefs.temporaryToken = value
            }
        }

    var tempUserId : Int = appPrefs.temporaryUserId
        set(value) {
            val changed = field != value
            if (changed) {
                field = value
                appPrefs.temporaryUserId = value
            }
        }

    var savedEventId : String? = null

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


    private var newUser: UserDetail? = null
    private var newChatMessage: MessageModel? = null

    var isLoggedOut = token.isNullOrEmpty()
        private set

    var hasBaseState = false
    var hasMaxState = false

    var isNeedShowWelcome = false
    var isNeedUpdateApp = false

    val userChangeSubject = BehaviorSubject.createDefault(newUser.asOptional())
    val tokenChangeSubject = BehaviorSubject.createDefault(token.asOptional())
    val chatMessageCountSubject = BehaviorSubject.createDefault(chatUnreadMessageCount)
    val chatUnreadMessageSubject = BehaviorSubject.createDefault(newChatMessage.asOptional())
    val chatRequestsCountSubject = BehaviorSubject.createDefault(chatRequestsCount)

    private var eventFormats: List<NewEventFormat>? = null
    var interests: List<InterestNew>? = null
    var supportQuestions: List<SupportData> = emptyList()
    val filterRegionsList = arrayListOf<SearchRegion>()
    val organizationsActiveEvents = arrayListOf<OrganizationNew>()
    val educationLevels = arrayListOf<EducationLevel>()
    val academicDegrees = arrayListOf<EducationLevel>()
    val specialities = arrayListOf<EducationLevel>()

    //new notifications subjects
    val notificationReadSubject = PublishSubject.create<Notification>()
    val notificationsCountSubject = BehaviorSubject.createDefault(notificationsCount)

    private var notificationsTypes = NotificationsTypesModel(0, 0, 0, 0, 0)
    private var notificationsInvites = NotificationInviteModel(0, 0, 0)
    private val notificationsTypesSubject = BehaviorSubject.createDefault(notificationsTypes.asOptional())
    private val notificationsInvitesSubject = BehaviorSubject.createDefault(notificationsInvites.asOptional())

    fun setEventFormats(formats: List<NewEventFormat>?) {
        eventFormats = formats
    }
    fun getEventFormats() = eventFormats

    fun setNewChatMessage(message: MessageModel?) {
        this.newChatMessage = message
        chatUnreadMessageSubject.onNext(newChatMessage.asOptional())
    }

    fun setNotificationsTypes(types: NotificationsTypesModel) {
        this.notificationsTypes = types
        notificationsTypesSubject.onNext(notificationsTypes.asOptional())
    }

    fun setNotificationsInvites(invites: NotificationInviteModel) {
        this.notificationsInvites = invites
        notificationsInvitesSubject.onNext(notificationsInvites.asOptional())
    }

    fun setNotificationRead(notification: Notification) {
        notificationReadSubject.onNext(notification)
    }

    fun checkUserState(data: List<UserProfileFields>?) {
        val base = data?.filter { it.requiredFor?.contains("basic") == true }
        val max = data?.filter { it.requiredFor?.contains("maximum") == true }
        hasBaseState = (base?.filter { it.filled == false }?.size ?: 0) == 0
        hasMaxState = (max?.filter { it.filled == false }?.size ?: 0) == 0
    }

    fun getUser(): UserDetail = newUser
        ?: throw UninitializedPropertyAccessException("\"User\" was queried before being initialized")

    fun setFullUserInfo(user: UserDetail) {
        val changed = this.newUser != user
        this.newUser = user
        appPrefs.userId = user.id
        if (changed) userChangeSubject.onNext(newUser.asOptional())
    }

    fun setShortUserInfo(user: UserDetail) {
        val changed = this.newUser != user
        val binds = this.newUser?.binds
        this.newUser = user
        this.newUser?.binds = binds
        appPrefs.userId = user.id
        if (changed) userChangeSubject.onNext(newUser.asOptional())
    }

    fun updateWorkExperience(data: WorkExperienceServerModel) {
        this.newUser?.binds?.workExperience?.absent = data.absent
        this.newUser?.binds?.workExperience?.models = data.data
        userChangeSubject.onNext(newUser.asOptional())
    }

    fun updateUserEducation(data: List<EducationModel>?) {
        this.newUser?.binds?.education = data
    }

    fun updateUserAcademicDegree(data: List<AcademicDegreeModel>?) {
        this.newUser?.binds?.academicDegree = data
    }

    fun updateUser(update: UserDetail.() -> Unit) {
        userChangeSubject.onNext(getUser().apply(update).asOptional())
    }

    fun updateUserFiles(newFile: FileModel) {
        this.newUser?.binds?.recommendationFile?.forEach { file ->
            if (file.id == newFile.id) {
                file.name = newFile.name
                file.showInProfile = newFile.showInProfile
            }
        }
    }


    fun saveId(id: Int?) { if (id != null) appPrefs.userId = id }

    fun getId(): Int = appPrefs.userId

    fun getTempId(): Int = appPrefs.temporaryUserId

    fun login(token: String) {
        this.attemptsOfChangePassword = 3
        isLoggedOut = false
        this.token = token
    }

    fun logout() {
        isLoggedOut = true
        newUser = null
        appPrefs.userId = -1
        notificationsCount = 0
        chatRequestsCount = 0
        chatUnreadMessageCount = 0
        token = null
    }

    fun logoutInvalidation(){
        isLoggedOut = true
        newUser = null
        appPrefs.userId = -1
        notificationsCount = 0
        chatRequestsCount = 0
        chatUnreadMessageCount = 0
        token = AUTH_TOKEN_INVALID
    }

    fun isCurrentUser(id: String): Boolean = newUser?.id.toString() == id

    fun isTemporaryUser() = token.isNullOrEmpty()

    fun getStateValue(): String {
        return if (hasMaxState && hasBaseState) "Максимальный" else "Минимальный"
    }

    fun updateFilesWithAdd(newFile: FileModel): FileModel {
        val userFiles = mutableListOf<FileModel>()
        userFiles.addAll(getUser().binds?.recommendationFile ?: mutableListOf())
        userFiles.add(newFile)
        this.newUser?.binds?.recommendationFile = userFiles

        var userFilesCount = getUser().filesCount
        userFilesCount += 1
        getUser().filesCount = userFilesCount
        return newFile
    }

    fun updateFilesWithDelete(newFile: FileModel): FileModel {
        val userFiles = mutableListOf<FileModel>()
        userFiles.addAll(getUser().binds?.recommendationFile ?: mutableListOf())
        val file = userFiles.find { x -> x.id == newFile.id }
        if (file != null) userFiles.remove(file)
        this.newUser?.binds?.recommendationFile = userFiles

        var userFilesCount = getUser().filesCount
        if (userFilesCount > 0) {
            userFilesCount -= 1
            getUser().filesCount = userFilesCount
        }
        return newFile
    }


    fun isUserEmailConfirmed() : Boolean {
        val email = getUser().email
        if (email == null) return false
        else if(email.value.isNullOrEmpty()) return false
        else if (email.isConfirmed == false) return false
        else return true
    }
}