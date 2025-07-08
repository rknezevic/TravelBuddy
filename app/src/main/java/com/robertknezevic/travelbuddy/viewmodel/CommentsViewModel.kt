package com.robertknezevic.travelbuddy.viewmodel
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.robertknezevic.travelbuddy.data.model.Comment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class CommentsViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadComments(cityName: String) {
        _isLoading.value = true

        firebaseFirestore.collection("comments")
            .whereEqualTo("cityName", cityName)
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    Log.e("CommentsViewModel", "Error loading comments", error)
                    return@addSnapshotListener
                }

                val commentsList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Comment::class.java)
                } ?: emptyList()


                _comments.value = commentsList
            }
    }

    fun postComment(cityName: String, commentText: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Log.e("CommentsViewModel", "User not authenticated")
            return
        }

        firebaseFirestore.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "Anonymous"
                    val lastName = document.getString("lastName") ?: ""
                    val fullName = "$name $lastName".trim()

                    val comment = Comment(
                        userId = currentUser.uid,
                        dateCreated = System.currentTimeMillis(),
                        cityName = cityName,
                        createdBy = fullName,
                        text = commentText
                    )

                    firebaseFirestore.collection("comments")
                        .add(comment)
                        .addOnSuccessListener {
                            Log.d("CommentsViewModel", "Comment posted successfully")
                        }
                        .addOnFailureListener { error ->
                            Log.e("CommentsViewModel", "Error posting comment", error)
                        }

                } else {
                    Log.e("CommentsViewModel", "User document not found")
                }
            }
            .addOnFailureListener { error ->
                Log.e("CommentsViewModel", "Error fetching user name", error)
            }
    }

}

