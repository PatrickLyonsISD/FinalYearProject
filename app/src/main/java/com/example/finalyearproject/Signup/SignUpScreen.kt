package com.example.finalyearproject.Signup

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.util.UUID

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController,
                 signUpViewModel: SignUpViewModel = viewModel(),
                 bluetoothViewModel: BluetoothViewModel = viewModel()) {

    var showDialog by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    val courses by signUpViewModel.courses.collectAsState()
    val filteredCourses = courses.filter { it.contains(searchText, ignoreCase = true) }

    val signUpMessage by signUpViewModel.signUpMessage.collectAsState()

    // Define colors and shapes
    val skyBlue = Color(0xFF2777B0)
    val estuaryRed = Color(0xFFEB5757)
    val textColor = Color.White
    val backgroundColor = Color(0xFFFAFAFA)
    val textFieldColor = Color(0xFFF0F0F0)
    val buttonShape = RoundedCornerShape(50)

    // Define common text field colors
    val textFieldColors = TextFieldDefaults.textFieldColors(
        containerColor = textFieldColor,
        focusedIndicatorColor = skyBlue,
        unfocusedIndicatorColor = Color.LightGray
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Email TextField
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password TextField
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Name TextField
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Search Course TextField
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Course") },
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Course selection list
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredCourses) { course ->
                CourseItem(
                    courseName = course,
                    isSelected = selectedCourse == course,
                    onCourseSelected = { selectedCourse = it },
                    skyBlue = skyBlue,
                    textColor = textColor,
                    backgroundColor = backgroundColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Button
        /*Button(
            onClick = {
                val uniqueIdentifier = UUID.randomUUID().toString()  // Generate a UUID
                signUpViewModel.signUp(email, password, name, selectedCourse, uniqueIdentifier)
            },
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(containerColor = skyBlue)
        ) {
            Text("Sign Up", color = textColor)
        }*/

        val discoveredDevices by bluetoothViewModel.discoveredDevices.observeAsState(initial = emptyList())

        Button(onClick = {
            bluetoothViewModel.startDiscovery()
            showDialog = true
        }) {
            Text("Discover Bluetooth Devices")
        }

        // Dialog to show discovered Bluetooth devices
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Column {
                    Text("Select a Bluetooth Device", style = MaterialTheme.typography.headlineMedium)

                    LazyColumn {
                        items(discoveredDevices) { device ->
                            Text(
                                text = device.name ?: "Unknown Device",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Handle the device selection
                                        // Example: You might want to store the device's MAC address
                                        showDialog = false
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }

                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }

        Spacer(modifier = Modifier.height(8.dp))

        // Displaying sign-up message
        Text(signUpMessage, color = estuaryRed)

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation to Log In Screen
        Text(
            "Already have an account? Log in",
            color = skyBlue,
            modifier = Modifier.clickable { navController.navigate("login") }
        )
    }


@Composable
fun CourseItem(
    courseName: String,
    isSelected: Boolean,
    onCourseSelected: (String) -> Unit,
    skyBlue: Color,
    textColor: Color,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCourseSelected(courseName) }
            .background(if (isSelected) skyBlue else backgroundColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = courseName,
            color = if (isSelected) textColor else Color.Black,
            modifier = Modifier.padding(8.dp)
        )
    }
}
