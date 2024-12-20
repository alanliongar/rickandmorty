package com.devspace.rickandmorty.list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.devspace.rickandmorty.CharacterApplication
import com.devspace.rickandmorty.list.data.CharacterListRepository
import com.devspace.rickandmorty.list.presentation.ui.CharacterListUiState
import com.devspace.rickandmorty.list.presentation.ui.CharacterUiData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CharacterListViewModel(
    private val repository: CharacterListRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiCharacterListUiState = MutableStateFlow(CharacterListUiState())
    val uiCharacterListUiState: StateFlow<CharacterListUiState> = _uiCharacterListUiState

    fun getFavoriteCharacters() {
        val favoriteCharacters =
            _uiCharacterListUiState.value.charactersList.filter { it.isFavorite }
        _uiCharacterListUiState.value = CharacterListUiState(charactersList = favoriteCharacters)
    }

    fun getAllCharacters() {
        viewModelScope.launch(dispatcher) {
            val characters = repository.getAllCharacters()
            val charactersUiDataList: List<CharacterUiData> = characters.map { character ->
                CharacterUiData(
                    id = character.id,
                    name = character.name,
                    image = character.image,
                    specie = character.specie,
                    isFavorite = character.isFavorite
                )
            }
            _uiCharacterListUiState.value =
                CharacterListUiState(charactersList = charactersUiDataList)
        }
    }

    fun fetchFilteredCharacterList(name: String? = null, species: String? = null) {
        _uiCharacterListUiState.value = CharacterListUiState(isLoading = true)
        viewModelScope.launch(dispatcher) {
            val result = repository.getFilteredCharacters(name = name, specie = species)
            if (result.isSuccess) {
                val characters = result.getOrNull() ?: emptyList()
                val charactersUiDataList: List<CharacterUiData> = characters.map { character ->
                    CharacterUiData(
                        id = character.id,
                        name = character.name,
                        image = character.image,
                        specie = character.specie,
                        isFavorite = character.isFavorite
                    )
                }
                delay(2000) //coloquei esse delay só pra vcs olharem o loading kkkkkkkkkk
                _uiCharacterListUiState.value =
                    CharacterListUiState(charactersList = charactersUiDataList)
            } else {
                _uiCharacterListUiState.value = CharacterListUiState(isError = true)
            }
        }
    }

    suspend fun updateCharacterFavoriteStatus(character: CharacterUiData) {
        repository.updateCharacterFavorite(character)
        val updatedCharacter = repository.getUpdatedFavoriteCharacter(character)
        _uiCharacterListUiState.value = _uiCharacterListUiState.value.copy(
            charactersList = _uiCharacterListUiState.value.charactersList.map { currentCharacter ->
                if (currentCharacter.id == updatedCharacter.id) {
                    updatedCharacter
                } else {
                    currentCharacter
                }
            }
        )
    }

    init {
        fetchFilteredCharacterList()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return CharacterListViewModel(
                    repository = (application as CharacterApplication).repository
                ) as T
            }
        }
    }
}
