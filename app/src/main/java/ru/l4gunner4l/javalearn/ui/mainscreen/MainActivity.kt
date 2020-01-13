package ru.l4gunner4l.javalearn.ui.mainscreen

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ru.l4gunner4l.javalearn.R
import ru.l4gunner4l.javalearn.data.FirebaseCallback
import ru.l4gunner4l.javalearn.data.models.User
import ru.l4gunner4l.javalearn.ui.mainscreen.fragments.LessonsFragment
import ru.l4gunner4l.javalearn.ui.mainscreen.fragments.ProfileFragment
import ru.l4gunner4l.javalearn.ui.mainscreen.fragments.ShopFragment
import ru.l4gunner4l.javalearn.ui.signscreens.SignActivity


/**
 * Activity with Bottom NavigationView (Profile, Lessons, Shop).
 * It is host for fragments
 *
 * Экран с нижней навигацией (профилем, уроками, магазином).
 * Является хостом для фрагментов
 */
class MainActivity : AppCompatActivity() {

    private val lessonsFragment: LessonsFragment = LessonsFragment()
    private val shopFragment: ShopFragment = ShopFragment()
    private val profileFragment: ProfileFragment = ProfileFragment()
    private var activeFragment: Fragment = lessonsFragment

    private lateinit var nav: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private var user: User = User("0", "-", "-")
    fun getUser() = user


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        LoadUser().execute()
    }

    fun signOut(view: View){
        val sureAlert = AlertDialog.Builder(this@MainActivity)
        sureAlert.setTitle(getString(R.string.label_exit))
                .setMessage(getString(R.string.text_question_sure_sign_out))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.label_yes_sure)){ dialog, which ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, SignActivity::class.java))
            finish()
        }
                .setNegativeButton(getString(R.string.label_cancel)){ dialog, which ->
            Toast.makeText(applicationContext,
                    getString(R.string.text_you_stayed), Toast.LENGTH_SHORT).show()
        }
        sureAlert.create().show()
    }


    private fun loadUser(firebaseCallback: FirebaseCallback) {
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
                                starsList
                        )
                        Log.i("M_MAIN", "3MainActivity - user=$user")
                        firebaseCallback.onCallback(user)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("M_MAIN", "Failed to read value.", error.toException())
                    }

                })
    }

    private fun endLoading() {
        updateUI()
        findViewById<ProgressBar>(R.id.main_pb_progress).visibility = GONE
        findViewById<ImageView>(R.id.main_iv_splash).visibility = GONE
    }

    private fun updateUI() {
        toolbar.findViewById<TextView>(R.id.tv_title).text = getLevelText(user.level.toString())
        profileFragment.setUserUI(user)
        lessonsFragment.setUserUI(user)
    }

    private fun initViews() {
        initToolbar()

        val fm = supportFragmentManager
        nav = findViewById(R.id.main_nav_view_main)
        nav.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.nav_lessons -> {
                    changeFragment(fm, lessonsFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    changeFragment(fm, profileFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_shop -> {
                    changeFragment(fm, shopFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    changeFragment(fm, lessonsFragment)
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }

        // set primary fragment - lessonsFrag - on the screen
        nav.selectedItemId = R.id.nav_lessons
        fm.beginTransaction().add(R.id.main_fragment_container, profileFragment, "3").hide(profileFragment).commit()
        fm.beginTransaction().add(R.id.main_fragment_container, shopFragment, "2").hide(shopFragment).commit()
        fm.beginTransaction().add(R.id.main_fragment_container, activeFragment, "1").commit()
    }
    private fun initToolbar() {
        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = null
    }


    private fun changeFragment(fm: FragmentManager, fragment: Fragment) {
        fm.beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit()
        activeFragment = fragment
        when (fragment) {
            is LessonsFragment -> {
                toolbar.visibility = VISIBLE
                toolbar.findViewById<TextView>(R.id.tv_title).text = getLevelText(user.level.toString())
                toolbar.findViewById<ImageView>(R.id.iv_settings).visibility = GONE
            }
            is ProfileFragment -> {
                toolbar.visibility = VISIBLE
                toolbar.findViewById<TextView>(R.id.tv_title).text = resources.getString(R.string.label_profile)
                toolbar.findViewById<ImageView>(R.id.iv_settings).visibility = VISIBLE
            }
            is ShopFragment -> {
                toolbar.visibility = GONE
            }
        }
    }


    /**
     * Method for creating colored text with user's level
     * Метод для создания цветного текста с уровнем пользователя
     */
    private fun getLevelText(levelStr: String): SpannableString {
        val spannable = SpannableString("${resources.getString(R.string.label_your_lvl)}: $levelStr")
        val startIndex = spannable.length-levelStr.length
        val endIndex = spannable.length
        spannable.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.colorAccent)),
                startIndex, endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
                RelativeSizeSpan(1.25f),
                startIndex, endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }


    companion object {
        fun createNewInstance(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            // all previous activities will be finished
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    inner class LoadUser : AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void?): Void? {
            loadUser(object : FirebaseCallback {
                override fun onCallback(obj: Any) {
                    endLoading()
                }
            })
            return null
        }

    }

}