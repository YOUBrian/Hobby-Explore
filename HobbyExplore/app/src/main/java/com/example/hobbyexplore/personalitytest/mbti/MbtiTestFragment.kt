package com.example.hobbyexplore.personalitytest.mbti

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentMbtiTestBinding
import com.example.hobbyexplore.personalitytest.questions

class MbtiTestFragment : Fragment() {
    private lateinit var viewBinding : FragmentMbtiTestBinding
    private var currentQuestionIndex = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMbtiTestBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化按鈕
        viewBinding.buttonChoice1.setOnClickListener { onChoiceButtonClick(it) }
        viewBinding.buttonChoice2.setOnClickListener { onChoiceButtonClick(it) }
        // 添加更多按鈕的初始化，根據需要重複這個模式

        // 顯示第一個問題
        showQuestion(currentQuestionIndex)
    }

    private fun showQuestion(questionIndex: Int) {
        if (questionIndex < questions.size) {
            val question = questions[questionIndex]
            viewBinding.buttonChoice1.text = question.choices[0].content
            viewBinding.buttonChoice2.text = question.choices[1].content
            // 添加更多按鈕的更新，根據需要重複這個模式
        } else {
            findNavController().navigate(MbtiTestFragmentDirections.actionMbtiTestFragmentToMbtiTestResultFragment())
        }
    }

    private fun onChoiceButtonClick(view: View) {
        val choiceIndex = when (view.id) {
            R.id.button_choice1 -> 0
            R.id.button_choice2 -> 1
            // 添加更多選項的處理，根據需要重複這個模式

            else -> -1 // 如果未知的 Button 被點擊，可以處理錯誤情況
        }
        Log.i("Click_Choice", "choiceIndex: $choiceIndex")
        if (choiceIndex != -1) {
            // 用戶選擇了某個選項，您可以在這裡進行相應的處理
            val selectedChoice = questions[currentQuestionIndex].choices[choiceIndex]
            val content = selectedChoice.content
            val score = selectedChoice.score

            Log.i("Click_Choice", "selectedChoice: $selectedChoice")
            Log.i("Click_Choice", "content: $content")
            Log.i("Click_Choice", "score: $score")
            // 處理所選選項的內容和分數
            // 這裡可以添加您的邏輯

            // 更新當前問題的索引，準備跳轉到下一題
            currentQuestionIndex++

            // 顯示下一個問題
            showQuestion(currentQuestionIndex)
        }
    }
}