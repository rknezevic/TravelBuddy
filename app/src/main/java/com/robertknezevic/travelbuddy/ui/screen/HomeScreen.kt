package com.robertknezevic.travelbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.robertknezevic.travelbuddy.data.auth.AuthViewModel
import com.robertknezevic.travelbuddy.data.model.City
import com.robertknezevic.travelbuddy.viewmodel.CityViewModel


@Composable
fun CitySearchScreen(navController: NavController, cityViewModel: CityViewModel = CityViewModel(), authViewModel: AuthViewModel = AuthViewModel()) {
    var query by remember { mutableStateOf("") }
    val cities by cityViewModel.cities.collectAsState(initial = emptyList())
    val errorMessage by cityViewModel.errorMessage.collectAsState()

    Column(modifier = Modifier
        .padding(16.dp)
        .padding(top = 30.dp, bottom = 30.dp)

        .fillMaxSize()) {
Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = query,
                onValueChange = {
                    query = it
                    cityViewModel.updateQuery(it)
                },
                label = { Text("Enter city name") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(cities) { city ->
                CityItem(city) {
                    navController.navigate(
                        "city-detail/${city.name}/${city.country}/${city.population}/${city.latitude}/${city.longitude}"
                    )

                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = {
                    authViewModel.logout()
                    navController.navigate("login")
                }
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun CityItem(city: City, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(color = Color(0xFFDFFFD6), shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = city.name,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${city.country} - Population: ${city.population}",
            color = Color.DarkGray,
            fontSize = 14.sp
        )
    }
}
