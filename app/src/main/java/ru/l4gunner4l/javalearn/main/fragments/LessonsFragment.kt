package ru.l4gunner4l.javalearn.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.l4gunner4l.javalearn.R
import ru.l4gunner4l.javalearn.main.MainActivity
import ru.l4gunner4l.javalearn.main.adapters.LessonsAdapter
import ru.l4gunner4l.javalearn.main.adapters.LessonsAdapter.ItemClickListener
import ru.l4gunner4l.javalearn.models.User

class LessonsFragment : Fragment(){

    private lateinit var rvLessons: RecyclerView
    private lateinit var rvAdapter: LessonsAdapter
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = (activity as MainActivity).getUser()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lessons, container, false)
        rvLessons = view.findViewById(R.id.rv_lessons)
        rvAdapter = LessonsAdapter(activity!!.baseContext, user.starsList, object : ItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                //openLessonClick(position)
            }

        })
        rvLessons.adapter = rvAdapter
        rvLessons.layoutManager = GridLayoutManager(activity, 2)
        return view
    }

    private fun openLessonClick(position: Int) {
        Toast.makeText(activity, "${position+1}) - stars=${rvAdapter.getItem(position)}", Toast.LENGTH_SHORT).show()
    }

    fun setUserUI(user: User) {
        this.user = user
        updateInfo(user)
    }

    private fun updateInfo(user: User) {
        rvAdapter = LessonsAdapter(activity!!.baseContext, user.starsList, object : ItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                openLessonClick(position)
            }
        })
        rvLessons.adapter = rvAdapter
    }
}