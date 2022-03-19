package com.example.parsetagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parsetagram.MainActivity
import com.example.parsetagram.Post
import com.example.parsetagram.PostAdapter
import com.example.parsetagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery

class FeedFragment : Fragment() {

    lateinit var  postsRecyclerView: RecyclerView

    lateinit var  adapter: PostAdapter

    var allPosts:MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This is were we set up our views and click listeners

        postsRecyclerView = view.findViewById(R.id.postRecyclerView)

        // steps to populate RecyclerView
        // 1. Create layout for each row in list (item_post.xml)
        // 2. Create data source for each row (this is the Post class)
        // 3. Create adapter that will bridge and row layout (PostAdapter)
        // 4. Set adapter on RecyclerView
        adapter = PostAdapter(requireContext(), allPosts)
        postsRecyclerView.adapter = adapter

        // 5. Set layout manager on RecyclerView
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        queryPosts() // when ever home feed button gets called log queries
    }



    // Query for all post in our server
    fun queryPosts() {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all the Post objects
        query.include(Post.KEY_USER)
        // Return post in descending order: ie newer post will appear first
        query.addDescendingOrder("createdAt")
        
        // Only return the most recent 20 posts
        
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    // Something has gone wrong
                    Log.e(MainActivity.TAG, "Error fetching posts")
                } else {
                    if(posts != null) {
                        for(post in  posts) {
                            Log.i(
                                MainActivity.TAG, "Post: " + post.getDescription() + ", username: " +
                                        post.getUser()?.username) // print out description
                        }
                    }
                    if (posts != null) {
                        allPosts.addAll(posts)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

        })
    }
    companion object {
        const val TAG = "FeedFragment"
    }
}