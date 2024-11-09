package com.devspace.rickandmorty.detail.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.devspace.rickandmorty.common.RetrofitClient
import com.devspace.rickandmorty.detail.data.CharacterDetailService
import com.devspace.rickandmorty.detail.presentation.ui.CharacterDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CharacterDetailViewModel(private val characterDetailService: CharacterDetailService) :
    ViewModel() {
    private val _uiCharacterDetail = MutableStateFlow(CharacterDetailUiState())
    val uiCharacterDetail: StateFlow<CharacterDetailUiState> = _uiCharacterDetail

    fun clearState() {
        viewModelScope.launch {
            delay(200)
            _uiCharacterDetail.value = CharacterDetailUiState()
        }
    }

    fun fetchCharacterDetail(characterId: Int) {
        _uiCharacterDetail.value = CharacterDetailUiState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = characterDetailService.getCharacterById(characterId)
                if (response.isSuccessful) {
                    _uiCharacterDetail.value = CharacterDetailUiState(character = response.body())
                    Log.d("CharacterDetailScreen", "Success")
                } else {
                    Log.d("CharacterDetailScreen", "Request Error :: ${response.errorBody()}")
                    _uiCharacterDetail.value = CharacterDetailUiState(isError = true, errorMessage = "The request failed")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d("CharacterDetailScreen", ex.message.toString())
                _uiCharacterDetail.value = CharacterDetailUiState(isError = true, errorMessage = ex.message.toString())
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val characterDetailService =
                    RetrofitClient.retrofitInstance.create(CharacterDetailService::class.java)
                return CharacterDetailViewModel(characterDetailService) as T
            }
        }
    }
}