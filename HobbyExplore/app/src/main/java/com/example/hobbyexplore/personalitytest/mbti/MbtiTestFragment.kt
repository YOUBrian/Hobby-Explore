package com.example.hobbyexplore.personalitytest.mbti

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.navigation.fragment.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentMbtiTestBinding
import com.example.hobbyexplore.personalitytest.questions

class MbtiTestFragment : Fragment() {
    private lateinit var viewBinding : FragmentMbtiTestBinding
    private var currentQuestionIndex = 0

    lateinit var progressBar: ProgressBar
    lateinit var choiceOneButton: Button
    lateinit var choiceTwoButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewBinding = FragmentMbtiTestBinding.inflate(inflater, container, false)
        dialogDismissed()
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewBinding.buttonChoice1.setOnClickListener { onChoiceButtonClick(it) }
        viewBinding.buttonChoice2.setOnClickListener { onChoiceButtonClick(it) }
        viewBinding.buttonChoice1.tag = 0
        viewBinding.buttonChoice2.tag = 1


        showQuestion(currentQuestionIndex)
    }

    private fun showQuestion(questionIndex: Int) {
        if (questionIndex < questions.size) {
            val question = questions[questionIndex]
            viewBinding.buttonChoice1.text = question.choices[0].content
            viewBinding.buttonChoice2.text = question.choices[1].content


        } else {
            findNavController().navigate(MbtiTestFragmentDirections.actionMbtiTestFragmentToMbtiTestResultFragment())
            dialogDismissed()
        }
    }

    private fun onChoiceButtonClick(view: View) {
        Log.i("Click_Choice", "View ID: ${view.id}, R.id.button_choice1: ${R.id.button_choice1}, R.id.button_choice2: ${R.id.button_choice2}")
        val choiceIndex = when (view.id) {
            viewBinding.buttonChoice1.id -> 0
            viewBinding.buttonChoice2.id -> 1
            else -> -1
        }
        Log.i("Click_Choice", "choiceIndex: $choiceIndex")
        if (choiceIndex != -1) {
            // 用戶選擇了某個選項，您可以在這裡進行相應的處理
            if (currentQuestionIndex < questions.size) {
                val selectedChoice = questions[currentQuestionIndex].choices[choiceIndex]


//                val selectedChoice = questions[currentQuestionIndex].choices[choiceIndex]
                val content = selectedChoice.content
                val score = selectedChoice.score
                buttonTapped(view)
                Log.i("Click_Choice", "selectedChoice: $selectedChoice")
                Log.i("Click_Choice", "content: $content")
                Log.i("Click_Choice", "score: $score")
                // 處理所選選項的內容和分數
                // 這裡可以添加您的邏輯

                // 更新當前問題的索引，準備跳轉到下一題
//            currentQuestionIndex++

                // 顯示下一個問題
                showQuestion(currentQuestionIndex)
            }
        }
    }

    fun buttonTapped(view: View) {
        var personalityTypeText = ""
        var index = 0

        currentQuestionIndex++

        // 沒下一題了，計算是哪一個人格 type，回傳字串
        if (currentQuestionIndex == questions.size) {
            personalityTypeText = computePersonalityType()

            // 求出 personalityTypeText 屬於的 struct 在陣列中的 index
            index = when (personalityTypeText) {
                "INTJ" -> 0
                "INTP" -> 1
                "ENTJ" -> 2
                "ENTP" -> 3
                "INFJ" -> 4
                "INFP" -> 5
                "ENFJ" -> 6
                "ENFP" -> 7
                "ISTJ" -> 8
                "ISFJ" -> 9
                "ESTJ" -> 10
                "ESFJ" -> 11
                "ISTP" -> 12
                "ISFP" -> 13
                "ESTP" -> 14
                "ESFP" -> 15
                else -> -1
            }
        } else if (currentQuestionIndex < questions.size) { // 還有題目
            if (view.tag == 0) {
                questions[currentQuestionIndex - 1].choices[0].score++
                Log.i("Score_Update", "Question: $currentQuestionIndex, Choice: 0, Score: ${questions[currentQuestionIndex - 1].choices[0].score}")
            } else {
                questions[currentQuestionIndex - 1].choices[1].score++
                Log.i("Score_Update", "Question: $currentQuestionIndex, Choice: 1, Score: ${questions[currentQuestionIndex - 1].choices[1].score}")
            }
            showQuestion(currentQuestionIndex) // 顯示下題
        }
    }

    private fun computePersonalityType(): String {
        val iTypeScore =
            questions[0].choices[1].score +
                    questions[4].choices[0].score +
                    questions[8].choices[0].score +
                    questions[12].choices[0].score +
                    questions[16].choices[0].score +
                    questions[20].choices[1].score +
                    questions[24].choices[1].score +
                    questions[28].choices[1].score

        val eTypeScore =
            questions[0].choices[0].score +
                    questions[4].choices[1].score +
                    questions[8].choices[1].score +
                    questions[12].choices[1].score +
                    questions[16].choices[1].score +
                    questions[20].choices[0].score +
                    questions[24].choices[0].score +
                    questions[28].choices[0].score

        val nTypeScore =
            questions[1].choices[0].score +
                    questions[5].choices[1].score +
                    questions[9].choices[0].score +
                    questions[13].choices[0].score +
                    questions[17].choices[0].score +
                    questions[21].choices[0].score +
                    questions[25].choices[1].score +
                    questions[29].choices[0].score

        val sTypeScore =
            questions[1].choices[1].score +
                    questions[5].choices[0].score +
                    questions[9].choices[1].score +
                    questions[13].choices[1].score +
                    questions[17].choices[1].score +
                    questions[21].choices[1].score +
                    questions[25].choices[0].score +
                    questions[29].choices[1].score

        val tTypeScore =
            questions[2].choices[0].score +
                    questions[6].choices[0].score +
                    questions[10].choices[0].score +
                    questions[14].choices[1].score +
                    questions[18].choices[1].score +
                    questions[22].choices[1].score +
                    questions[26].choices[0].score +
                    questions[30].choices[1].score

        val fTypeScore =
            questions[2].choices[1].score +
                    questions[6].choices[1].score +
                    questions[10].choices[1].score +
                    questions[14].choices[0].score +
                    questions[18].choices[0].score +
                    questions[22].choices[0].score +
                    questions[26].choices[1].score +
                    questions[30].choices[0].score

        val pTypeScore =
            questions[3].choices[0].score +
                    questions[7].choices[0].score +
                    questions[11].choices[0].score +
                    questions[15].choices[0].score +
                    questions[19].choices[1].score +
                    questions[23].choices[1].score +
                    questions[27].choices[0].score +
                    questions[31].choices[1].score

        val jTypeScore =
            questions[3].choices[1].score +
                    questions[7].choices[1].score +
                    questions[11].choices[1].score +
                    questions[15].choices[1].score +
                    questions[19].choices[0].score +
                    questions[23].choices[0].score +
                    questions[27].choices[1].score +
                    questions[31].choices[0].score

        var typeString = ""

        val arrTemp1 = arrayOf("I", "E")
        typeString += when {
            iTypeScore > eTypeScore -> "I"
            iTypeScore < eTypeScore -> "E"
            else -> arrTemp1.random()
        }

        val arrTemp2 = arrayOf("N", "S")
        typeString += when {
            nTypeScore > sTypeScore -> "N"
            nTypeScore < sTypeScore -> "S"
            else -> arrTemp2.random()
        }

        val arrTemp3 = arrayOf("T", "F")
        typeString += when {
            tTypeScore > fTypeScore -> "T"
            tTypeScore < fTypeScore -> "F"
            else -> arrTemp3.random()
        }

        val arrTemp4 = arrayOf("P", "J")
        typeString += when {
            pTypeScore > jTypeScore -> "P"
            pTypeScore < jTypeScore -> "J"
            else -> arrTemp4.random()
        }
        Log.i("Click_Choice", "typeString: $typeString")
        Log.i("MBTI_Scores", "P Score: $pTypeScore, J Score: $jTypeScore")
        return typeString

    }

    private fun dialogDismissed() {
        // score歸零
        for (i in questions.indices) {
            questions[i].choices[0].score = 0
            questions[i].choices[1].score = 0
        }
        currentQuestionIndex = 0 // 指標回到第1題
//        progressBar.progress = 0f // 進度條歸零
        showQuestion(currentQuestionIndex) // 顯示問題
    }
}