package com.example.a1first_application


import android.os.Bundle
import android.view.LayoutInflater

import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope


import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a1first_application.databinding.ActivityMainBinding
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main


class MainActivity : AppCompatActivity() {

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

        lifecycleScope.launch {
            binding.progressBar.isVisible = true
            getResultFromGit()
        }

    }

    private suspend fun getResultFromGit() {
        delay(5000)
        val client = HttpClient {
            install(JsonFeature) {
                acceptContentTypes = listOf(
                    ContentType.Text.Plain,
                    ContentType.Application.Json
                )
                serializer = GsonSerializer()
            }
        }

        // тестовый ответ будет десериализован в List<Post>
        val response = client.get<List<Post>>(url)
        println("Десериализация + ${response}")
        client.close()
        setResponseOnMainThread(response)
        binding.progressBar.isInvisible = true

    }


    private suspend fun setResponseOnMainThread(response: List<Post>) {
        withContext(Main) {
            setResponse(response)
            println("Главный поток + $response")
            binding.progressBar.visibility = ProgressBar.INVISIBLE
        }
    }

    private fun setResponse(response: List<Post>) {
        adapter.submitList(response)

    }

}



















