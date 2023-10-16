package brian.project.hobbyexplore.personalitytest.mbti

import brian.project.hobbyexplore.R


data class Personality(
    val name: String,
    val image: Int,
    val type: String,
    val description: String
)

enum class Category {
    Analysts,
    Diplomats,
    Sentinels,
    Explorers
}

val personalities = listOf(
    Personality("建築師", R.drawable.intj_0, "INTJ", "富有想像力且有策略的思考者，對於每件事都有計畫。"),
    Personality("邏輯學家", R.drawable.intp_1, "INTP", "創新的發明家，對知識有著不滅的渴望。"),
    Personality("指揮官", R.drawable.entj_2, "ENTJ", "大膽、富有想像力且意志堅強的領導者，總是找到或創造出一條路。"),
    Personality("辯論家", R.drawable.entp_3, "ENTP", "聰明且好奇的思考者，不能抗拒智慧上的挑戰。"),

    Personality("提倡者", R.drawable.infj_4, "INFJ", "安靜且神秘，但非常有啟發性且是不知疲倦的理想主義者。"),
    Personality("調停者", R.drawable.infp_5, "INFP", "富有詩意、善良且無私的人，總是急於助人於好的事業中。"),
    Personality("主人公", R.drawable.enfj_6, "ENFJ", "充滿魅力且具啟發性的領導者，能夠迷住他們的聽眾。"),
    Personality("競選者", R.drawable.enfp_7, "ENFP", "充滿熱情、創造力和社交的自由靈魂，總能找到一個笑的理由。"),

    Personality("物流師", R.drawable.istj_8, "ISTJ", "實際和注重事實的人，他們的可靠性是無可置疑的。"),
    Personality("守衛者", R.drawable.isfj_9, "ISFJ", "非常專心和溫暖的保護者，總是準備捍衛他們所愛的人。"),
    Personality("總經理", R.drawable.estj_10, "ESTJ", "卓越的行政管理者，無人能及的管理事物或人。"),
    Personality("執政官", R.drawable.esfj_11, "ESFJ", "特別關心、社交和受歡迎的人，總是急於幫助他人。"),

    Personality("鑑賞家", R.drawable.istp_12, "ISTP", "大膽和實用的實驗者，是各種工具的大師。"),
    Personality("探險家", R.drawable.isfp_13, "ISFP", "靈活且有魅力的藝術家，總是準備探索和體驗新事物。"),
    Personality("企業家", R.drawable.estp_14, "ESTP", "聰明、充滿活力且非常有洞察力的人，真正喜歡生活在邊緣。"),
    Personality("表演者", R.drawable.esfp_15, "ESFP", "自發性、充滿活力和熱情的人-他們身邊的生活永遠不會無聊。")
)