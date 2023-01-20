package com.mate.baedalmate.data.di

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mate.baedalmate.domain.model.ReceiveMessageInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class StompModule {
    private val url = "ws://3.35.27.107:8080/ws/chat/websocket"
    lateinit var stompClient: StompClient
    private var compositeDisposable: CompositeDisposable? = null
    var chatMessage = MutableLiveData<ReceiveMessageInfo>()

    fun connectStomp(accessToken: String, roomId: Int) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        stompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);
        resetSubscriptions()
        val disposableLifecycle = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.i("OPENED", "Stomp connection opened!! roomId:${roomId}")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.i("CLOSED", "Stomp connection closed!!")

                    }
                    LifecycleEvent.Type.ERROR -> {
                        Log.i("ERROR", "Stomp connection error!!")
                        Log.e("CONNECT ERROR", lifecycleEvent.exception.toString())
                    }
                    else -> {
                        Log.i("ELSE", lifecycleEvent.message)
                    }
                }
            }
        compositeDisposable?.add(disposableLifecycle)

        // 메세지 받기위해 구독 설정
        val disposableRoomTopic = stompClient.topic("/topic/chat/room/${roomId}")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { topicMessage ->
                val senderId = JSONObject(topicMessage.payload).getInt("senderId")
                val sender = JSONObject(topicMessage.payload).getString("sender")
                val senderImage = JSONObject(topicMessage.payload).getString("senderImage")
                val roomIdRecv = JSONObject(topicMessage.payload).getLong("roomId")
                val message = JSONObject(topicMessage.payload).getString("message")
                chatMessage.postValue(
                    ReceiveMessageInfo(
                        senderId = senderId,
                        sender = sender,
                        senderImage = senderImage,
                        roomId = roomIdRecv,
                        message = message
                    )
                )
                Log.d("message receive", topicMessage.payload)
            }

        compositeDisposable?.add(disposableRoomTopic)

        val headers = listOf(StompHeader("Authorization", "Bearer ${accessToken}")).toMutableList()
        stompClient.connect(headers)
    }

    fun disconnectStomp() {
        stompClient.disconnect()
    }

    private fun resetSubscriptions() {
        compositeDisposable?.dispose()
        compositeDisposable = CompositeDisposable()
    }

    fun sendMessage(roomId: Long, message: String, senderId: Int) {
        // 전송할 값
        val data = JSONObject()
        data.put("roomId", roomId)
        data.put("senderId", senderId)
        data.put("message", message)

        // 전송
        compositeDisposable?.add(stompClient.send("/app/chat/message", data.toString()).subscribe())
    }
}