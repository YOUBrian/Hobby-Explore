import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentYoutubePlayerBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class YouTubePlayerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentYoutubePlayerBinding.inflate(inflater)

        var youTubePlayerView: YouTubePlayerView = binding.youtubePlayerView
        viewLifecycleOwner.lifecycle.addObserver(binding.youtubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object: AbstractYouTubePlayerListener() {

            override fun onReady(youTubePlayer: YouTubePlayer) {
//                val videoId: String = bundle.youtubeId;
//                youTubePlayer.loadVideo(videoId, 0F);
            }
        })
        return binding.root
    }
}
