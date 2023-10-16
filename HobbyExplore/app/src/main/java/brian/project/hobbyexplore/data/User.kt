package brian.project.hobbyexplore.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import brian.project.hobbyexplore.data.local.HobbyConverters
import kotlinx.parcelize.Parcelize

@Entity(tableName = "user_data")
@TypeConverters(HobbyConverters::class)
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "user_name")
    val name: String,
    @ColumnInfo(name = "user_email")
    val email: String,
    @ColumnInfo(name = "user_hobby")
    val hobby: List<String>,
) : Parcelable
