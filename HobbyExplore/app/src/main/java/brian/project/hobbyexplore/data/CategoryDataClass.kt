package brian.project.hobbyexplore.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Introduce(
    val image : String,
    val title : String,
    val content : String,
    val name : String
): Parcelable
{
    constructor() : this( "", "","", "")
}
@Parcelize
data class Appliance(
    val id : String,
    val image : String,
    val title : String,
    val price : String,
    val content : String
): Parcelable
{
    constructor() : this("","", "", "","")
}
@Parcelize
data class Course(
    val videoId : String,
    val title : String,
    val image : String
): Parcelable
{
    constructor() : this("", "","")
}
@Parcelize
data class Place(
    val title: String,
    val image: String,
    val latitude: Double,
    val longitude: Double
): Parcelable
{
    constructor() : this("", "",25.038476250873885, 121.53237057262506)
}


