package com.devspace.rickandmorty.detail.data

import com.devspace.rickandmorty.detail.model.CharacterDetailDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CharacterDetailService {
    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): Response<CharacterDetailDto>
}