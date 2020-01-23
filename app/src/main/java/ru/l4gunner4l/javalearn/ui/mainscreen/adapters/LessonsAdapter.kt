package ru.l4gunner4l.javalearn.ui.mainscreen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ru.l4gunner4l.javalearn.R
import ru.l4gunner4l.javalearn.utils.Utils

data class LessonsAdapter(
        val context: Context,
        val starsList: List<Int>,
        val lessonsNames: List<String>
) : RecyclerView.Adapter<LessonsAdapter.ViewHolder>() {

    lateinit var clickListener: ItemClickListener

    // inflates the cell layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card_view_lesson, parent, false)
        return ViewHolder(view)
    }

    // binds the data in each cell
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lessonNumber = position+1
        val lessonStars: Int
        if (lessonNumber <= starsList.size) {
            lessonStars = starsList[position]
            holder.bgIV.setImageDrawable(context.getDrawable(R.drawable.bg_card_lesson_orange))
        } else {
            lessonStars = 0
            holder.bgIV.setImageDrawable(context.getDrawable(R.drawable.bg_card_lesson_grey))
        }
        holder.numberTV.text = lessonNumber.toString()
        holder.failsRB.rating = lessonStars.toFloat()

    }

    // total number of cells
    override fun getItemCount() = 10

    fun setOnItemClickListener(function: ItemClickListener){ this.clickListener = function }

    // convenience method for getting data at click position
    fun getStarsCount(lessonIndex: Int) = starsList[lessonIndex]

    fun getLessonName(lessonIndex: Int) = lessonsNames[lessonIndex]


    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder
        internal constructor(itemView: View)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var numberTV: TextView = itemView.findViewById(R.id.item_tv_lesson_number)
        var bgIV: ImageView = itemView.findViewById(R.id.item_iv_lesson_bg)
        var failsRB: RatingBar = itemView.findViewById(R.id.item_rating_lesson)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (adapterPosition+1 <= starsList.size){
                clickListener.onItemClick(view, adapterPosition)
            }
            else Utils.showToast(context,
                 context.resources.getString(R.string.text_error_not_available_lesson), Toast.LENGTH_SHORT)
        }
    }


    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}