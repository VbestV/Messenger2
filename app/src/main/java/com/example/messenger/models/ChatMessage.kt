package com.example.messenger.models

import java.util.Date


class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long){
    constructor(): this("","","","",-1)
}