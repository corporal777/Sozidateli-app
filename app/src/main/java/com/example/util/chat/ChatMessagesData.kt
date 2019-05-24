package com.example.util.chat

import com.example.data.models.Optional
import com.example.data.models.UserChatMessage
import com.example.data.models.asOptional
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import ru.houseofapps.chat.ChatRepository
import ru.houseofapps.chat.SocketRepository
import ru.houseofapps.chat.models.Message

class ChatMessagesData(
        private val chatId: String,
        private val socketRepository: SocketRepository,
        private val chatRepository: ChatRepository
) {

    private val messageIdSet = mutableSetOf<String>()
    private val messages = mutableListOf<UserChatMessage>()

    private val messageHistoryLoadSubject = PublishSubject.create<Optional<String>>()

    private var isHistoryLoading = false
    private var isHistoryFullyLoad = false

    fun subscribeToChatMessageUpdates(): Flowable<List<UserChatMessage>> {
        return Flowable.create({ emitter ->
            val compositeDisposable = CompositeDisposable()
            emitter.setDisposable(compositeDisposable)

            compositeDisposable += socketRepository.subscribeToMessageInRoom()
                    .subscribe({ message ->
                        if (messageIdSet.add(message._id)) {
                            messages.add(0, createUserChatMessage(message))
                            emitter.onNext(messages)
                        } else {
                            val index = messages.indexOfFirst { it.message._id == message._id }
                            if (index != -1) {
                                messages[index] = createUserChatMessage(message)
                                emitter.onNext(messages)
                            }
                        }
                    }, {
                        emitter.onError(it)
                    })

            compositeDisposable += messageHistoryLoadSubject
                    .flatMapSingle { chatRepository.getMessages(chatId, 20, it.value) }
                    .subscribe({
                        val filtered = it.messages.filter { message -> messageIdSet.add(message._id) }
                                .map { message -> createUserChatMessage(message) }
                        messages.addAll(filtered)
                        emitter.onNext(messages)
                        if (it.totalCount <= messages.size) isHistoryFullyLoad = true
                        isHistoryLoading = false
                    }, {
                        emitter.onError(it)
                        isHistoryLoading = false
                    })

            loadNextMessageHistory()
        }, BackpressureStrategy.DROP)
    }

    fun loadNextMessageHistory() {
        if (isHistoryLoading || isHistoryFullyLoad) return
        isHistoryLoading = true
        messageHistoryLoadSubject.onNext(messages.lastOrNull()?.message?._id.asOptional())
    }

    private fun createUserChatMessage(message: Message): UserChatMessage {
        return UserChatMessage(message, message.isUserMessage(socketRepository.getCurrentUserKey()))
    }
}