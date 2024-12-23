package com.example.travelmate

import android.os.Parcel
import android.os.Parcelable

data class User(
    val userId: String = "", // ID único del usuario en Firestore
    val username: String = "", // Cambiado a "username" para reflejar Firestore
    val email: String = "", // Correo electrónico del usuario
    var isSelected: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(username) // Guardar el username en lugar de name
        parcel.writeString(email)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User = User(parcel)
        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
    }
}
