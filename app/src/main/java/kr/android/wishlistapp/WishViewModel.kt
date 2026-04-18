package kr.android.wishlistapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.android.wishlistapp.data.Wish
import kr.android.wishlistapp.data.WishRepository

class WishViewModel(
    private val wishRepository: WishRepository = Graph.wishRepository
) : ViewModel() {

    var wishTitleState by mutableStateOf("")
    var wishDescriptionState by mutableStateOf("")

    //recently deleted for undo action
    private val _recentlyDeletedWish = MutableStateFlow<Wish?>(null)
    val recentlyDeletedWish: StateFlow<Wish?> = _recentlyDeletedWish


    fun onWishTitleChanged(newTitle : String){
        wishTitleState = newTitle
    }

    fun onWishDescriptionChanged(newDescription : String){
        wishDescriptionState = newDescription
    }

    //getting all wishes
    lateinit var getAllWishes : Flow<List<Wish>>
    init {
        viewModelScope.launch {
            getAllWishes = wishRepository.getAllWishes()
        }
    }

    //getting single wish by id
    fun getWishById(id : Long) : Flow<Wish> {
        return wishRepository.getWishById(id)
    }

    //adding wish
    fun addWish(wish: Wish){
        viewModelScope.launch(Dispatchers.IO) {
            wishRepository.addWish(wish = wish)
        }
    }

    //updating wish
    fun updateWish(wish: Wish){
        viewModelScope.launch(Dispatchers.IO) {
            wishRepository.updateWish(wish = wish)
        }
    }

    //temporary storing for deletion
    fun markWishForDeletion(wish: Wish) {
        _recentlyDeletedWish.value = wish
    }

    //confirmed deletion if undo not done
    fun confirmDelete() {
        _recentlyDeletedWish.value?.let { wish ->
            viewModelScope.launch(Dispatchers.IO) {
                wishRepository.deleteWish(wish)
            }
        }
        _recentlyDeletedWish.value = null
    }

    //restoring the deleted wish if undo done
    fun restoreDeletedWish() {
        _recentlyDeletedWish.value = null
    }

    /* //deleting wish (no undo)
    fun deleteWish(wish: Wish){
        viewModelScope.launch(Dispatchers.IO) {
            wishRepository.deleteWish(wish = wish)
        }
    }
     */

}