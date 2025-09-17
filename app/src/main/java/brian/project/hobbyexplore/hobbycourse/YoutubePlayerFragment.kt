import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentYoutubePlayerBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class YouTubePlayerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentYoutubePlayerBinding.inflate(inflater)

        val args = YouTubePlayerFragmentArgs.fromBundle(requireArguments())
        val videoId = YouTubePlayerFragmentArgs.fromBundle(requireArguments()).course.videoId


        Log.i("sadfeaasfd", "$videoId")
        binding.youtubePlayerView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0F)
//                youTubePlayer.pause()
            }
        })
        return binding.root
    }
}
