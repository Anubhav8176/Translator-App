package com.example.translatorapp.presentation

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.translatorapp.ui.theme.TranslatorAppTheme
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale


@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val speechText = remember { mutableStateOf("Hello there!!") }

    val context = LocalContext.current

    val translatedText = remember { mutableStateOf("") }

    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK){
            val data = it.data
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            speechText.value = result?.get(0)?:"No voice detected!:("
        }else{
            speechText.value = "Voice can't be heard:("
        }
    }

    val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.HINDI)
        .build()

    val englishHindiTranslator = Translation.getClient(options)

    var conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()

    englishHindiTranslator.downloadModelIfNeeded(conditions)
        .addOnSuccessListener {
            Toast.makeText(context, "Resource downloaded!!", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Resource not downloaded, connect to wifi!!", Toast.LENGTH_SHORT).show()
        }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Go on then, say something")
            speechLauncher.launch(intent)
        }) {
            Text(
                text = "Start Speaking!!",
                fontSize = 18.sp
            )
        }

        Spacer(modifier=modifier.height(10.dp))

        Text(
            text = speechText.value,
            fontSize = 18.sp
        )

        Spacer(modifier = modifier.height(15.dp))

        Button(onClick = {
            englishHindiTranslator.translate(speechText.value)
                .addOnSuccessListener {
                    translatedText.value = it
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Can't translate ${it.message}!!", Toast.LENGTH_SHORT).show()
                }
        }) {
            Text(
                text = "Translate to Hindi",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = modifier.height(10.dp))

        Text(
            text = translatedText.value,
            fontSize = 18.sp
        )

    }
}


@Preview
@Composable
fun MainScreenPreview(){
    TranslatorAppTheme {
        MainScreen(Modifier.fillMaxSize())
    }
}