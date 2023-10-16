package brian.project.hobbyexplore.personalitytest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentSystemRecommendsHobbyBinding
import brian.project.hobbyexplore.databinding.FragmentWhetherTakeMbtiTestBinding

class SystemRecommendsHobbyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSystemRecommendsHobbyBinding.inflate(inflater)
//        binding.selectButton.setOnClickListener {
//            it.findNavController()
//                .navigate(SystemRecommendsHobbyFragmentDirections.actionSystemRecommendsHobbyFragmentToEnterBudgetFragment())
//        }

        binding.changeButton.setOnClickListener {
            binding.detailImage.setImageResource(R.drawable.baseball)
            binding.detailTitle.text = "棒球"
            binding.detailContent.text = "為一種團體球類運動，由人數最少為9人的兩支隊伍在一個扇形的棒球場進行攻擊與守備。棒球球員分為攻方與守方，攻方球員利用球棒將守方投擲的球擊出，隨後以逆時針方向沿著四個壘位進行跑壘，當成功回到本壘就可得1分；而守方則利用手套將攻方擊出的球接住或透過傳球將攻方球員封殺或觸殺出局。比賽中，兩隊輪流攻守，比賽結束後，得分較高的一隊會勝出；若雙方得分仍相同，則進入延長賽；在延長賽中每一局規則均按照正規賽最後一個半局計算，在該局下半局結束後若分數有差距即結束比賽。  棒球起源於大英帝國本土，後流傳到北美洲；在美國成為國民運動後，分別流傳至中南美洲及亞洲東部；目前在美洲中北部及亞洲東部較為盛行；棒球曾經在1992年至2008年為奧林匹克運動會的正式項目，但之後被從正式項目中移除，自此之後並非每屆舉辦；目前棒球的主要國際賽事是世界棒球經典賽與世界棒球12強賽，在世界多個地區皆有職業棒球聯盟，而美國職棒大聯盟被公認是世界強度最高的職業棒球聯盟。"
        }
        return binding.root
    }
}