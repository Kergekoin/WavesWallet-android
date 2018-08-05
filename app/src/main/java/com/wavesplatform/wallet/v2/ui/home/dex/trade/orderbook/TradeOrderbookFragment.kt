package com.wavesplatform.wallet.v2.ui.home.dex.trade.orderbook

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import javax.inject.Inject

import com.arellomobile.mvp.presenter.InjectPresenter

import com.wavesplatform.wallet.v2.ui.base.view.BaseFragment;

import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.wavesplatform.wallet.R
import com.wavesplatform.wallet.v2.data.model.local.OrderbookItem
import com.wavesplatform.wallet.v2.ui.home.dex.trade.buy_and_sell.BuyAndSellBottomSheetFragment
import kotlinx.android.synthetic.main.fragment_trade_orderbook.*
import pers.victor.ext.click
import java.util.ArrayList


class TradeOrderbookFragment : BaseFragment(), TradeOrderbookView {

    @Inject
    @InjectPresenter
    lateinit var presenter: TradeOrderbookPresenter

    @Inject
    lateinit var adapter: TradeOrderbookAdapter

    @ProvidePresenter
    fun providePresenter(): TradeOrderbookPresenter = presenter

    override fun configLayoutRes() = R.layout.fragment_trade_orderbook


    override fun onViewReady(savedInstanceState: Bundle?) {
        recycle_orderbook.layoutManager = LinearLayoutManager(baseActivity)
        recycle_orderbook.adapter = adapter
        recycle_orderbook.isNestedScrollingEnabled = false

        linear_buy.click {
            val dialog = BuyAndSellBottomSheetFragment()
            dialog.show(fragmentManager, dialog::class.java.simpleName)
        }

        linear_sell.click {
            val dialog = BuyAndSellBottomSheetFragment()
            dialog.show(fragmentManager, dialog::class.java.simpleName)
        }

        presenter.loadOrderbook()
    }

    override fun afterSuccessOrderbook(data: ArrayList<OrderbookItem>) {
        adapter.setNewData(data)
    }
}
