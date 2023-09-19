import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentYoutubePlayerBinding

class YouTubePlayerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentYoutubePlayerBinding.inflate(inflater)
        return binding.root
    }
}
