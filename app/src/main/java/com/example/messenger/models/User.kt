package com.example.messenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String, val id_user: String): Parcelable{
    constructor() : this("",",","", "")
}