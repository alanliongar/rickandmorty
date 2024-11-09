package com.devspace.rickandmorty.detail.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.devspace.rickandmorty.detail.model.CharacterDetailDto
import com.devspace.rickandmorty.detail.presentation.CharacterDetailViewModel
import com.devspace.rickandmorty.list.presentation.ui.getDominantColorFromImage
import com.devspace.rickandmorty.list.presentation.ui.readableColor

@Composable
fun CharacterDetailScreen(characterId: String, viewModel: CharacterDetailViewModel) {
    val characterDetail by viewModel.uiCharacterDetail.collectAsState()
    LaunchedEffect(characterId) {
        viewModel.fetchCharacterDetail(characterId.toInt())
    }
    characterDetail?.let { CharacterDetailContent(character = it) }
}

@Composable
fun CharacterDetailContent(character: CharacterDetailDto) {
    val context = LocalContext.current
    val color = remember { mutableStateOf(Color.Transparent) }
    LaunchedEffect(character) {
        color.value = getDominantColorFromImage(context, character.image) ?: Color.Transparent
    }

    Box(
        modifier = Modifier
            .background(color.value)
            .fillMaxSize()
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AdjustableTextSize(character = character, color = color.value)
            Text(
                text = "#${character?.id.toString()?.padStart(3, '0')}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp), color = readableColor(color.value)
            )
            AsyncImage(
                model = character.image,
                contentDescription = "Image of the character ${character.name}",
                modifier = Modifier
                    .height(300.dp)
                    .clip(RoundedCornerShape(64.dp)),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = "Character detail information",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold, color = readableColor(color.value)
            )
            Spacer(modifier = Modifier.height(8.dp))
            CharacterInfo(character = character, color = readableColor(color.value))
        }
    }
}

@Composable
private fun AdjustableTextSize(character: CharacterDetailDto, color: Color) {
    //Essa função só serve pra determinar o tamanho da fonte do nome do personagem
    val maxFontSize = 36f
    val maxLength = 20
    val fontSize = if (character.name.length > maxLength) {
        (maxFontSize * (maxLength.toFloat() / character.name.length)).coerceAtMost(maxFontSize)
    } else {
        maxFontSize
    }
    Text(
        text = character.name,
        fontSize = fontSize.sp,
        color = readableColor(color),
        fontWeight = FontWeight.Bold
    )
}


@Composable
fun CharacterInfo(character: CharacterDetailDto, color: Color) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // permite rolar caso o conteúdo exceda a tela
    ) {
        val fontSize = 20.sp
        val space = 8.dp
        InfoRow(
            label = "Status:", value = if (character.status == "") {
                "[Empty]"
            } else {
                character.status
            }, fontSize = fontSize, color = color
        )
        Spacer(modifier = Modifier.height(space))

        InfoRow(
            label = "Species:", value = if (character.species == "") {
                "[Empty]"
            } else {
                character.species
            }, fontSize = fontSize, color = color
        )
        Spacer(modifier = Modifier.height(space))

        InfoRow(
            label = "Type:", value = if (character.type == "") {
                "[Empty]"
            } else {
                character.type
            }, fontSize = fontSize, color = color
        )
        Spacer(modifier = Modifier.height(space))

        InfoRow(
            label = "Gender:", value = if (character.gender == "") {
                "[Empty]"
            } else {
                character.gender
            }, fontSize = fontSize, color = color
        )
        Spacer(modifier = Modifier.height(space))

        InfoRow(
            label = "Origin:", value = if (character.origin.name == "") {
                "[Empty]"
            } else {
                character.origin.name
            }, fontSize = fontSize, color = color
        )
        Spacer(modifier = Modifier.height(space))

        InfoRow(
            label = "Location:", value = if (character.location.name == "") {
                "[Empty]"
            } else {
                character.location.name
            }, fontSize = fontSize, color = color
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String, fontSize: TextUnit, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp), color = color
        )
        Text(
            text = value,
            fontSize = fontSize,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.Start)
                .wrapContentHeight(Alignment.CenterVertically), color = color
        )
    }
}


