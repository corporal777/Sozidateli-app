package com.examle.domain.repository

interface ChatRepository {

//    //fun loadChatList(limit: Int, offset: Int): Maybe<ApiResponse<ChatListResponse>>
//
//    //fun loadInvitesList(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>
//
//    //fun loadBannedList(limit: Int, offset: Int): Maybe<PaginationResponse<UserChat>>
//
//    //fun startChat(userId: String): Single<ChatStartResponse>
//
//    //fun getChat(chatId: String): Single<UserChat>
//
//    //fun searchUser(limit: Int, offset: Int): Maybe<PaginationResponse<User?>>
//
//    //fun chatAccept(chatId: String): Completable
//
//    //fun chatBan(chatId: String): Completable
//
//    //fun chatUnban(chatId: String): Completable
//
//    //New API
//    fun getChats(map: Map<String, Any>): Maybe<ApiNewResponse<List<ChatModel>>>
//
//    fun getChatById(chatId: String, map: Map<String, Any>): Single<ChatModel>
//
//    fun createChat(body: com.examle.data.bodies.CreateChatBody): Single<CreatedChatModel>
//
//    fun bannedList(map: Map<String, Any>): Maybe<PaginationResponse<UserChat>>
//
//    fun chatBann(body: com.examle.data.bodies.CreateChatBody): Single<BannedUsersModel>
//
//    fun deleteBan(id: Int): Completable
//
//    fun acceptChat(id: Int): Completable
//
//    fun getChatInvitesCount(): Single<ChatInvitesCount>
//
//    fun sendChatMessage(body: RequestBody): Single<MessageModel>
//
//    fun sendChatMessageNew(body: MessageBodyNew): Single<MessageModel>
//
//    fun getChatMessages(map: Map<String, Any>): Single<ApiNewResponse<List<MessageModel>>>
//
//    fun getChatMessagesPagination(map: Map<String, Any>): Maybe<PaginationResponse<MessageModel>>
//
//    fun markMessageAsRead(id: Int): Completable
}