package brian.project.hobbyexplore.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.TypeConverters
import brian.project.hobbyexplore.data.local.HobbyConverters
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize


@Entity(tableName = "hobby_data_table", primaryKeys = ["id"])
@TypeConverters(HobbyConverters::class)
@Parcelize
data class Hobby(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "price")
    val price: Int,
    @ColumnInfo(name = "place")
    val place: String,
    @ColumnInfo(name = "image")
    val image: String,
) : Parcelable
