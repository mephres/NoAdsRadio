package me.kdv.noadsradio.data.network

import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import me.kdv.noadsradio.data.network.model.StationContainerDto
import me.kdv.noadsradio.data.network.model.StationGroupDto

object FBDataBase {

    private var database: FirebaseDatabase
    private var stationInfoReference: DatabaseReference

    init {
        database = Firebase.database
        stationInfoReference = database.getReference()
    }

    fun getStationInfo(
        onComplete: (List<StationGroupDto>) -> Unit,
        onError: ((String) -> Unit)
    ) {
        stationInfoReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val stationData = dataSnapshot.getValue(StationContainerDto::class.java)
                val groupList = stationData?.data ?: emptyList()
                onComplete.invoke(groupList)
            }

            override fun onCancelled(error: DatabaseError) {
                onError.invoke(error.message)
            }
        })
    }
}