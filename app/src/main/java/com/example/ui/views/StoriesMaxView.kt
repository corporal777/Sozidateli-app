package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.example.R

// Костыль. В библиотеки сторисов если кликнуть на следующий сторис то в текущем прогрессбаре становится
// видна вьюшка с айди max_progress у которой нельзя настроить фон. Вот тут его и настраиваю костылем
class StoriesMaxView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(R.drawable.background_stories_progress_primary)
    }
}