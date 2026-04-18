package kr.android.wishlistapp.data

import kotlinx.coroutines.flow.Flow

class WishRepository (private val wishDao : WishDao) {

    //adding wish
    suspend fun addWish(wish: Wish) { wishDao.addWish(wishEntity = wish) }

    //update wish
    suspend fun updateWish(wish: Wish) { wishDao.updateWish(wishEntity = wish) }

    //delete wish
    suspend fun deleteWish(wish: Wish) { wishDao.deleteWish(wishEntity = wish) }

    //getting all wishes
    fun getAllWishes() : Flow<List<Wish>> = wishDao.getAllWishes()

    //get wish by id
    fun getWishById(id : Long) : Flow<Wish> { return wishDao.getWishById(id) }

}