package infinity.to.loop.betteryoutube

import android.content.Intent
import android.net.Uri
import net.openid.appauth.RedirectUriReceiverActivity


class AuthActivity : RedirectUriReceiverActivity() {

    override fun getIntent(): Intent {
        return super.getIntent().setData(Uri.parse(super.getIntent().dataString!!.replaceFirst("#".toRegex(), "?")))
    }
}