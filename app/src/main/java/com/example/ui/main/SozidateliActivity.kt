package com.example.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.example.navigation.NavigationGraph
import com.example.ui.theme.SozidateliTheme

class SozidateliActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SozidateliTheme(dynamicColor = false) {

                val navController = rememberNavController()
                Scaffold() { padding ->
                    NavigationGraph(navController, padding)
                }
            }

        }

    }
}