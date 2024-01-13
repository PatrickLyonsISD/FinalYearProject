package com.example.finalyearproject.Signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SignUpViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("students")

    private val _courses = MutableStateFlow(listOf("Course 1", "Course 2", "Course 3")) // Example courses
    val courses: StateFlow<List<String>> = _courses

    private val _signUpStatus = MutableStateFlow(false)
    val signUpStatus: StateFlow<Boolean> = _signUpStatus

    private val _signUpMessage = MutableStateFlow("")
    val signUpMessage: StateFlow<String> = _signUpMessage

    fun signUp(email: String, password: String, name: String, course: String, uniqueIdentifier: String, deviceName: String) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userData = hashMapOf(
                            "email" to email,
                            "name" to name,
                            "course" to course,
                            "deviceName" to deviceName
                        )

                        // Store user data under the uniqueIdentifier in Firebase
                        db.child(uniqueIdentifier).setValue(userData).addOnCompleteListener { dbTask ->
                            handleDatabaseTaskCompletion(dbTask)
                        }
                    } else {
                        _signUpStatus.value = false
                        _signUpMessage.value = "Registration failed: ${task.exception?.message}"
                    }
                }
            } catch (e: Exception) {
                _signUpStatus.value = false
                _signUpMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun handleDatabaseTaskCompletion(dbTask: Task<Void>) {
        if (dbTask.isSuccessful) {
            _signUpStatus.value = true
            _signUpMessage.value = "Registration successful"
        } else {
            _signUpStatus.value = false
            _signUpMessage.value = "Failed to save data to database: ${dbTask.exception?.message}"
        }
    }
}
