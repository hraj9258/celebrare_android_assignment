package com.hraj9258.celebrareassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.hraj9258.celebrareassignment.navigation.presentation.MainNavHost
import com.hraj9258.celebrareassignment.core.ui.theme.CelebrareAssignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CelebrareAssignmentTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->
                    MainNavHost(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}