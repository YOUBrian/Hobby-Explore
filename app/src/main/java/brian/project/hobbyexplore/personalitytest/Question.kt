package brian.project.hobbyexplore.personalitytest

data class Choice(val content: String, var score: Int)

data class Question(val choices: List<Choice>)

val questions = listOf(
    Question(
        listOf(
            Choice("我會先了解別人的想法再下決定。", 0),
            Choice("我不和別人商量就下決定。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我是一個富於想像或憑直覺的人。", 0),
            Choice("我是一個講求精確、講求事實的人。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會根據現有資料及情境的分析，對他人做評斷。", 0),
            Choice("我會先了解他人的需要及價值觀，才對他人做評斷。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會順著他人的意思做出承諾。", 0),
            Choice("我會自己做承諾，並確實加以實踐。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我要有安靜、獨自思考的時間。", 0),
            Choice("我喜歡與他人打成一片。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會運用我熟悉的好方法來完成工作。", 0),
            Choice("我會嘗試運用新的方法來完成工作。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會以合乎邏輯思考及按部就班的分析得到結論。", 0),
            Choice("我會根據過去生活的體驗及訊息來得到結論。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會訂下完成工作的最後期限。", 0),
            Choice("我會擬訂時間表並嚴格遵行。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會與人稍談話題後，再自我思考一番。", 0),
            Choice("我會和他人盡興暢談某事後，再自我思考一番。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會設想各種可能發生的情況。", 0),
            Choice("我只按實際的情況處理問題。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我認為自己是一個擅長思考的人。", 0),
            Choice("我被別人認為是一個敏感的人。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會事前詳細考慮各種可能性，事後反覆思考。", 0),
            Choice("我會蒐集需要的資料，稍作考慮後，作出明確決定。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我擁有內在的思想和情感而不為他人所知。", 0),
            Choice("我會與他人共同分享某些活動或事件。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我喜歡抽象與理論的事物。", 0),
            Choice("我喜歡具體與實際的事物。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會協助別人探索他們自己的感受。", 0),
            Choice("我會協助別人做出合理的決定。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我對問題的答案是保持彈性且可修改的。", 0),
            Choice("我對問題的答案是明確的、可預知的。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我很少表達自己內心的想法及感受。", 0),
            Choice("我能夠自在表達自己內心的想法及感受。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我傾向從大處著眼。", 0),
            Choice("我喜歡從小處著手。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我慣於運用常識，憑著信念來做決定。", 0),
            Choice("我善於運用資料分析的事實來做決定。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會事先詳細計劃。", 0),
            Choice("我會臨時視需要而作計劃。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我喜歡結交新朋友。", 0),
            Choice("我喜歡獨處或與熟識者交往。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我重視概念。", 0),
            Choice("我重視事實。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我相信自已的想法。", 0),
            Choice("我相信經證實的結論。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會盡可能在記事簿記下事情。", 0),
            Choice("我會盡可能少用記事簿記錄事情。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會在團體中詳細地討論新奇、未決定的問題。", 0),
            Choice("我會自己先想出結論，然後再和他人討論。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會擬定詳細計劃，然後確實執行。", 0),
            Choice("我會擬定計劃，但不一定執行。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我是理性的。", 0),
            Choice("我是感性的。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會隨心所欲地做一些事情。", 0),
            Choice("我會事先了解別人期望我做什麼。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我喜歡成為眾人的焦點。", 0),
            Choice("我喜歡退居幕後。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我喜歡自由想像。", 0),
            Choice("我傾向檢視現實情況。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我喜歡體驗感人的情境或事物。", 0),
            Choice("我不和別人商量就下決定。", 0)
        )
    ),
    Question(
        listOf(
            Choice("我會在預定的時間內召開會議。", 0),
            Choice("我會在一切妥善或適當的情況下宣告開會。", 0)
        )
    )
)