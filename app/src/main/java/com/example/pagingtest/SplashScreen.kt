package com.example.pagingtest

import android.annotation.SuppressLint
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import java.io.Serializable
import java.time.Clock
import java.time.Instant

const val splashRoute = "splashRoute"

@Composable
fun SplashScreen(
    navController: NavController
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val navDestination = navController.graph.findNode(mainRoute)!!
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(splashRoute, true)
            .build()
        navController.navigate(
            navDestination.id,
            bundleOf(
                "lambda" to {
                    val param = MyParcelableData()
                    Toast.makeText(context, "param? ${param.id} ${param.serializableData.data}", Toast.LENGTH_SHORT).show()
                },
            ),
            navOptions
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

    }
}

@SuppressLint("NewApi")
data class TestParam constructor(
    val id: Int,
    val instant: Instant = Instant.now(),
): Serializable


// Serializable 클래스를 포함하는 데이터 클래스
data class MySerializableData(
    val data: String
) : Serializable

// Parcelable 클래스인데 내부에 Serializable 객체를 포함하고 있음
data class MyParcelableData(
    val id: Int = 2,
    val name: String = "testName",
    val serializableData: MySerializableData = MySerializableData("test")  // Serializable 객체 포함
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readSerializable() as MySerializableData  // 이 부분에서 IOException 발생 가능
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeSerializable(serializableData)  // 이 부분에서 IOException 발생 가능
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyParcelableData> {
        override fun createFromParcel(parcel: Parcel): MyParcelableData {
            return MyParcelableData(parcel)
        }

        override fun newArray(size: Int): Array<MyParcelableData?> {
            return arrayOfNulls(size)
        }
    }
}