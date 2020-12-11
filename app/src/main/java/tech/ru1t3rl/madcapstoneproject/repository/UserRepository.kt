package tech.ru1t3rl.madcapstoneproject.repository

import android.util.Log
import com.google.firebase.database.*
import tech.ru1t3rl.madcapstoneproject.model.User
import java.lang.Exception
import kotlin.collections.ArrayList

object UserRepository {
    private var mValueDataListener: ValueEventListener? = null
    private var mUserList: ArrayList<User> = ArrayList()

    private fun getDatabaseRef(): DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("User")
    }

    // Get All Users from the database and keep the list updated
    init {
        getDatabaseRef()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val data: ArrayList<User> = ArrayList()
                    for (userData: DataSnapshot in snapshot.children) {
                        try {
                            data.add(User(userData))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mUserList = data
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("UserModel", p0.message)
            }
        })
    }

    // Find the user in the snapshot based on it's id
    fun getUser(id: String) : User? {
        if(mUserList.isNullOrEmpty())
            getAllUsers()

        for(user in mUserList){
            if(user.id == id)
                return user
        }

        return User(null)
    }

    /**
     * Returns the id of the new user
     * @param user a new user which will be added to the database
     * @return the id of new user
     */
    fun addUser(user: User): String {
        val newUser = getDatabaseRef()!!.child("").push()

        user.id = newUser.key.toString()
        newUser.child("username").setValue(user.username)
        newUser.child("totalScore").setValue(0)
        newUser.child("totalTime").setValue(0)
        newUser.child("totalDistance").setValue("0.0")
        newUser.child("runs").setValue(arrayListOf<String>())
        newUser.child("private").setValue(user.private)
        newUser.child("averageSpeed").setValue("0.0")

        return user.id
    }


    fun updateUser(user: User) {
        val updatedUser = getDatabaseRef()!!.child(user.id)

        user.id = updatedUser.key.toString()
        updatedUser.child("username").setValue(user.username)
        updatedUser.child("totalScore").setValue(user.totalScore)
        updatedUser.child("totalTime").setValue(user.totalTime)
        updatedUser.child("totalDistance").setValue(user.totalDistance)
        updatedUser.child("runs").setValue(user.runs)
        updatedUser.child("private").setValue(user.private)
        updatedUser.child("averageSpeed").setValue(user.averageSpeed)
    }

    // Get all users from the database
    fun getAllUsers(): ArrayList<User> {
        if (mValueDataListener != null) {
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }
        mValueDataListener = null
        
        mValueDataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val data: ArrayList<User> = ArrayList()
                    for (userData: DataSnapshot in snapshot.children) {
                        try {
                            data.add(User(userData))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mUserList = data
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i("UserModel", p0.message)
            }
        }
        getDatabaseRef()?.addValueEventListener(mValueDataListener as ValueEventListener)

        return mUserList
    }
}