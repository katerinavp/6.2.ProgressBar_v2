package com.example.a1first_application

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a1first_application.databinding.ActivityMainBinding
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.EmptyCoroutineContext


class MainActivity : AppCompatActivity() {

    private lateinit var job: CompletableJob // спосо
    private lateinit var progressBar: ProgressBar
    private lateinit var adapterPost: AdapterPost
    private val url = "https://raw.githubusercontent.com/katerinavp/GSON/master/posts.json"

    lateinit var adapter: AdapterPost

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterPost()
        binding.recyclerView.adapter = adapter
        progressBar = binding.progressBar

        CoroutineScope(IO).launch {
            progressBar.visibility = ProgressBar.VISIBLE
            getResultFromGit()
        }

    }

    private suspend fun getResultFromGit() {
        delay(5000)
        val client = HttpClient {
            install(JsonFeature) {
                // объясним чуть позже
                acceptContentTypes = listOf(
                        ContentType.Text.Plain,
                        ContentType.Application.Json
                )
                serializer = GsonSerializer()
            }
        }
        with(CoroutineScope(EmptyCoroutineContext))
        {
            launch {
                // тестовый ответ будет десериализован в List<Post>
                val response = client.get<List<Post>>(url)
                println("Десериализация + ${response}")
                client.close()
                setResponseOnMainThread(response)
            }
        }
    }

    private suspend fun setResponseOnMainThread(response: List<Post>) {
        withContext(Main) {
            setResponse(response)
            println("Главный поток + $response")
            progressBar.visibility = ProgressBar.INVISIBLE
        }
    }

    private fun setResponse(response: List<Post>) {
        adapter.submitList(response)

    }

}



















