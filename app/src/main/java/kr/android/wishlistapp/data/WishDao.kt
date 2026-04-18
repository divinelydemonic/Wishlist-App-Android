package kr.android.wishlistapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WishDao {

    //adding wish to the table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addWish(wishEntity : Wish) : Long  //returns which row number inserted

    //update wish
    @Update
    abstract suspend fun updateWish(wishEntity: Wish) : Int //returns number of rows affected

    //delete wish
    @Delete
    abstract suspend fun deleteWish(wishEntity: Wish) : Int //returns number of rows deleted

    //load all wishes from the table
    @Query("select * from `wish_table`")
    abstract fun getAllWishes() : Flow<List<Wish>>

    //get a single wish by id
    @Query("select * from `wish_table` where id = :id")
    abstract fun getWishById(id : Long) : Flow<Wish>

}