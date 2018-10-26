package com.wavesplatform.wallet.v2.ui.home.wallet.assets.token_burn.confirmation

import com.arellomobile.mvp.InjectViewState
import com.wavesplatform.wallet.App
import com.wavesplatform.wallet.R
import com.wavesplatform.wallet.v1.api.NodeManager
import com.wavesplatform.wallet.v1.data.rxjava.RxUtil
import com.wavesplatform.wallet.v2.data.model.remote.request.BurnRequest
import com.wavesplatform.wallet.v2.data.model.remote.response.AssetBalance
import com.wavesplatform.wallet.v2.ui.base.presenter.BasePresenter
import pers.victor.ext.currentTimeMillis
import javax.inject.Inject

@InjectViewState
class TokenBurnConfirmationPresenter @Inject constructor() : BasePresenter<TokenBurnConfirmationView>() {

    private var nodeManager = NodeManager.createInstance(
            App.getAccessManager().getWallet()!!.publicKeyStr)

    var assetBalance: AssetBalance? = null
    var amount: Long? = 0L

    fun burn() {
        val request = BurnRequest(
                assetId = assetBalance!!.assetId!!,
                quantity = amount ?: 0L,
                senderPublicKey = App.getAccessManager().getWallet()!!.publicKeyStr,
                timestamp = currentTimeMillis)
        request.sign(App.getAccessManager().getWallet()!!.privateKey)

        addSubscription(nodeManager.transactionsBroadcast(request)
                .compose(RxUtil.applySchedulersToObservable()).subscribe({ tx ->
                    viewState.onShowBurnSuccess(tx)
                }, { _ ->
                    viewState.onShowError(R.string.transaction_failed)
                }))
    }

}
