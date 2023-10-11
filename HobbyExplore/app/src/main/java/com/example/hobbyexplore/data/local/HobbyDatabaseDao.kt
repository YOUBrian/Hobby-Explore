package com.example.hobbyexplore.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hobbyexplore.data.Hobby
import com.example.hobbyexplore.data.User

@Dao
interface HobbyDatabaseDao {

    /**簡易新增所有資料的方法 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun  //預設萬一執行出錯怎麼辦，REPLACE為覆蓋
            insertData(myData: User)

    /**複雜(?)新增所有資料的方法 */
    @Query("INSERT INTO user_data (id,user_name,user_email,user_hobby) VALUES(:id,:name,:email,:hobby)")
    fun insertData(
        id: Int,
        name: String,
        email: String,
        hobby: List<String>,
    )
    /**撈取全部資料 */
    @Query("SELECT * FROM user_data")
    fun displayAll(): MutableList<User>

    /**撈取某個名字的相關資料 */
    @Query("SELECT * FROM user_data WHERE user_name = :name")
    fun findDataByName(name: String): MutableList<User>

    /**簡易更新資料的方法 */
    @Update
    fun updateData(myData: User)

    /**複雜(?)更新資料的方法 */
    @Query("UPDATE user_data SET user_name = :name,user_email=:email,user_hobby=:hobby WHERE id = :id")
    fun updateData(
        id: Int,
        name: String,
        email: String,
        hobby: List<String>,
    )
    /**簡單刪除資料的方法 */
    @Delete
    fun deleteData(myData: User)

    /**複雜(?)刪除資料的方法 */
    @Query("DELETE  FROM user_data WHERE id = :id")
    fun deleteData(id: Int)
}