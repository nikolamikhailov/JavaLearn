package ru.l4gunner4l.javalearn.ui.mainscreen.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ru.l4gunner4l.javalearn.R
import ru.l4gunner4l.javalearn.data.models.User
import ru.l4gunner4l.javalearn.ui.settingsscreen.SettingsActivity

class ProfileFragment : Fragment() {

    private lateinit var nameTIL: TextInputLayout
    private lateinit var emailTIL: TextInputLayout
    private lateinit var avatarIV: ImageView

    lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoadData().execute()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        nameTIL = view.findViewById(R.id.profile_til_name)
        emailTIL = view.findViewById(R.id.profile_til_email)
        avatarIV = view.findViewById(R.id.civ_avatar)
        val toolbar = view.findViewById<Toolbar>(R.id.profile_toolbar)
        toolbar.findViewById<ImageView>(R.id.profile_toolbar_iv_settings)
                .setOnClickListener{
                    startActivity(SettingsActivity.createNewInstance(activity!!.baseContext, user))
                }
        nameTIL.isEnabled = false
        emailTIL.isEnabled = false

        return view
    }

    private fun updateUI() {
        if (user.avatarUrl == null) avatarIV.setImageResource(R.drawable.avatar_default)
        else {
            Glide.with(activity!!.baseContext).load(user.avatarUrl).into(avatarIV)
        }
        nameTIL.editText!!.setText(user.name)
        emailTIL.editText!!.setText(user.email)
    }


    inner class LoadData : AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void?): Void? {
            loadData()
            return null
        }
    }

    private fun loadData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("users").child(userId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val starsList = mutableListOf<Int>()
                        for (starsSnapshot in dataSnapshot.child("starsList").children)
                            starsList.add(starsSnapshot.getValue(Int::class.java)!!)

                        user = User(
                                dataSnapshot.child("id").value.toString(),
                                dataSnapshot.child("name").value.toString(),
                                dataSnapshot.child("email").value.toString(),
                                dataSnapshot.child("imageUrl")
                                        .getValue(String::class.java),
                                starsList
                        )
                        updateUI()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("M_MAIN", "Failed to read value.", error.toException())
                    }
                })
    }
}