package com.example.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instafire.models.Post
import com.example.instafire.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "PostsActivity"
private const val EXTRA_USERNAME = "josh"
open class PostsActivity : AppCompatActivity() {

    private var signedInUser : User? = null
    private lateinit var firestoreDb : FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        //Recycler View Steps
        // 1. Create the layout file representing 1 post
        // 2. create the data source
        posts = mutableListOf()
        // 3. create the adapter (new class, then imported here)
        adapter = PostsAdapter(this, posts)
        // 4. Bind the adapter and layout manager to RV
        var rvPosts = findViewById<RecyclerView>(R.id.rvPosts)
        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)

        val firestoreDb = Firebase.firestore

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)
            }

        var postsReference = firestoreDb.collection("posts")
            .limit(20)
            .orderBy("creation_time", Query.Direction.DESCENDING)

        val intentUsername = intent.getStringExtra(EXTRA_USERNAME)
        if (intentUsername != null) {
              postsReference = postsReference.whereEqualTo("user.username", intentUsername)
            }

        postsReference.addSnapshotListener{ snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exception when querying posts", exception)
                return@addSnapshotListener
            }
            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()
                for (post in postList) {
                    Log.i(TAG, "Post | $post")
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuProfile) {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}