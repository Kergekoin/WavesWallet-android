package com.wavesplatform.wallet.v2.ui.home.history.details

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.wavesplatform.wallet.R
import com.wavesplatform.wallet.v2.ui.base.view.BaseBottomSheetDialogFragment
import com.wavesplatform.wallet.v2.ui.home.history.TestObject
import com.wavesplatform.wallet.v2.ui.home.profile.address_book.AddressBookActivity
import com.wavesplatform.wallet.v2.ui.home.profile.address_book.AddressTestObject
import com.wavesplatform.wallet.v2.ui.home.profile.address_book.add.AddAddressActivity
import com.wavesplatform.wallet.v2.util.copyToClipboard
import com.wavesplatform.wallet.v2.util.launchActivity
import com.wavesplatform.wallet.v2.util.notNull
import io.github.kbiakov.codeview.CodeView
import io.github.kbiakov.codeview.highlight.ColorTheme
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.SyntaxColors
import kotlinx.android.synthetic.main.fragment_history_bottom_sheet_bottom_btns.view.*
import pers.victor.ext.*
import pyxis.uzuki.live.richutilskt.utils.runDelayed
import java.util.*
import kotlin.collections.ArrayList


class HistoryDetailsBottomSheetFragment : BaseBottomSheetDialogFragment() {

    var selectedItemPosition: Int = 0
    var selectedItem: TestObject? = null
    var allItems: List<TestObject>? = ArrayList()
    var historyType: String? = ""
    var viewPager: ViewPager? = null
    var rooView: View? = null
    var inflater: LayoutInflater? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        this.inflater = inflater

        rooView = inflater.inflate(R.layout.history_details_bottom_sheet_dialog, container, false)

        setupHistoryViewPager(rooView!!)

        rooView?.findViewById<ImageView>(R.id.image_icon_go_to_preview)?.click {
            viewPager?.currentItem = viewPager?.currentItem!! - 1

            checkStepIconState()
        }
        rooView?.findViewById<ImageView>(R.id.image_icon_go_to_next)?.click {
            viewPager?.currentItem = viewPager?.currentItem!! + 1

            checkStepIconState()
        }

        return rooView
    }

    private fun copyToClipboard(view: TextView, text: Int) {
        view.text = getString(R.string.common_copied)
        view.setTextColor(findColor(R.color.success400))
        view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_18_success_400, 0, 0, 0);
        runDelayed(1500, {
            this.context.notNull {
                view.text = getString(text)
                view.setTextColor(findColor(R.color.black))
                view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_copy_18_black, 0, 0, 0);
            }
        })
    }

    private fun setupView(type: HistoryTypeEnum) {
        val historyContainer = rooView?.findViewById<LinearLayout>(R.id.main_container)
        val bottomBtns = inflater?.inflate(R.layout.fragment_history_bottom_sheet_bottom_btns, historyContainer, false)
        val baseInfoLayout = inflater?.inflate(R.layout.fragment_history_bottom_sheet_base_info_layout, historyContainer, false)

        /**Base info to all type**/
        val freeValue = baseInfoLayout?.findViewById<TextView>(R.id.text_free)
        val confirmation = baseInfoLayout?.findViewById<TextView>(R.id.text_confirmations)
        val block = baseInfoLayout?.findViewById<TextView>(R.id.text_block)
        val timeStamp = baseInfoLayout?.findViewById<TextView>(R.id.text_timestamp)
        val status = baseInfoLayout?.findViewById<TextView>(R.id.text_status)


        /** Add click to copy buttons **/
        bottomBtns?.container_copy_tx_id?.click {
            copyToClipboard(bottomBtns.text_copy_tx_id, R.string.history_details_copy_tx_id)
        }
        bottomBtns?.container_copy_all_data?.click {
            copyToClipboard(bottomBtns.text_copy_all_data, R.string.history_details_copy_all_data)
        }



        historyContainer?.removeAllViews()

        when (type) {
            HistoryTypeEnum.RECEIVE, HistoryTypeEnum.CLOSELEASE, HistoryTypeEnum.INCOMINGLEASE, HistoryTypeEnum.MASSRECEIVED -> {
                val receiveView = inflater?.inflate(R.layout.fragment_bottom_sheet_receive_layout, historyContainer, false)
                val receivedFrom = receiveView?.findViewById<TextView>(R.id.text_received_from)
                val imageAddAddressSubmit = receiveView?.findViewById<AppCompatImageView>(R.id.image_add_address_submit)
                val textReceivedFrom = receiveView?.findViewById<AppCompatTextView>(R.id.text_received_from)

                imageAddAddressSubmit?.click {
                    launchActivity<AddAddressActivity>(AddressBookActivity.REQUEST_ADD_ADDRESS) {
                        putExtra(AddressBookActivity.BUNDLE_TYPE, AddressBookActivity.SCREEN_TYPE_EDITABLE)
                        putExtra(AddressBookActivity.BUNDLE_ADDRESS_ITEM, AddressTestObject(textReceivedFrom?.text.toString(), ""))
                    }
                }


                historyContainer?.addView(receiveView)
                historyContainer?.addView(baseInfoLayout)
            }
            HistoryTypeEnum.SEND -> {
                val sendView = inflater?.inflate(R.layout.fragment_bottom_sheet_send_layout, historyContainer, false)
                val resendBtn = inflater?.inflate(R.layout.resen_btn, historyContainer, false)
                val sentToName = sendView?.findViewById<TextView>(R.id.text_sent_to_name)
                val sentAddress = sendView?.findViewById<TextView>(R.id.text_sent_address)

                historyContainer?.addView(sendView)
                historyContainer?.addView(baseInfoLayout)
                historyContainer?.addView(resendBtn)
            }
            HistoryTypeEnum.DATA -> {
                val dataView = inflater?.inflate(R.layout.fragment_bottom_sheet_data_layout, historyContainer, false)
                val codeView = dataView?.findViewById<CodeView>(R.id.code_view)
                val imageCopyData = dataView?.findViewById<AppCompatImageView>(R.id.image_copy_data)

                val customTheme = ColorThemeData(SyntaxColors(android.R.color.transparent, R.color.submit300, android.R.color.transparent, android.R.color.transparent, R.color.basic700,
                        android.R.color.transparent, R.color.basic700, android.R.color.transparent, android.R.color.transparent, android.R.color.transparent, android.R.color.transparent),
                        R.color.basic50, android.R.color.transparent, android.R.color.transparent, R.color.basic50)

                val code = "{\n" +
                        "\t\"key\" : \"test long\",\n" +
                        "\t\"type\" : \"integer\",\n" +
                        "\t\"value\" : 1001\n" +
                        "}, {\n" +
                        "\t\"key\" : \"test true\",\n" +
                        "\t\"type\" : \"boolean\",\n" +
                        "\t\"value\" : true\n" +
                        "}, {\n" +
                        "\t\"key\" : \"test false\",\n" +
                        "\t\"type\" : \"boolean\",\n" +
                        "\"value\" : true\n" +
                        "}"
                codeView?.setCode(code);
                codeView?.getOptions()?.withTheme(customTheme)
                imageCopyData?.click {
                    it.copyToClipboard(code)
                }
                historyContainer?.addView(dataView)
                historyContainer?.addView(baseInfoLayout)
            }
            HistoryTypeEnum.STARTLEASE -> {
                val startLeaseView = inflater?.inflate(R.layout.fragment_bottom_sheet_start_lease_layout, historyContainer, false)
                val cancelLeasingBtn = inflater?.inflate(R.layout.cancel_leasing_btn, historyContainer, false)
                val leasingTo = startLeaseView?.findViewById<TextView>(R.id.text_leasing_to)

                historyContainer?.addView(startLeaseView)
                historyContainer?.addView(baseInfoLayout)
                historyContainer?.addView(cancelLeasingBtn)
            }
            HistoryTypeEnum.EXCHANGE -> {
                val exchangeView = inflater?.inflate(R.layout.fragment_bottom_sheet_exchange_layout, historyContainer, false)
                val btcPrice = exchangeView?.findViewById<TextView>(R.id.text_btc_price)

                historyContainer?.addView(exchangeView)
                historyContainer?.addView(baseInfoLayout)
            }
            HistoryTypeEnum.SELFTRANS -> {
                val selfTransfer = inflater?.inflate(R.layout.fragment_bottom_sheet_self_trans_layout, historyContainer, false)

                historyContainer?.addView(selfTransfer)
                historyContainer?.addView(baseInfoLayout)
            }
            HistoryTypeEnum.TOKENREIS, HistoryTypeEnum.TOKENGEN, HistoryTypeEnum.TOKENBURN -> {
                val tokenGenView = inflater?.inflate(R.layout.fragment_bottom_sheet_token_gen_layout, historyContainer, false)
                val recipientName = tokenGenView?.findViewById<TextView>(R.id.text_recipient_name)
                val tokenGenStatus = tokenGenView?.findViewById<TextView>(R.id.text_token_gen_status)

                historyContainer?.addView(tokenGenView)
                historyContainer?.addView(baseInfoLayout)
            }
            HistoryTypeEnum.ALIAS -> {
                historyContainer?.addView(baseInfoLayout)
            }
            HistoryTypeEnum.MASSTRANSFER -> {
                val tokenGenView = inflater?.inflate(R.layout.fragment_bottom_sheet_token_gen_layout, null, false)
                val addressContainer = tokenGenView?.findViewById<LinearLayout>(R.id.container_address)
                val showMoreAddress = tokenGenView?.findViewById<TextView>(R.id.text_show_more_address)
                val button = inflater?.inflate(R.layout.resen_btn, null, false)

                val address: MutableList<AddressModel> = ArrayList()

                for (i in 0..15) {
                    address.add(AddressModel("96AFUzFKebbwmJulY6evx9GrfYBkmn8LcUL0", Random().nextDouble()))
                }

                for (i in 0..2) {
                    val addressModel = address[i]

                    val addressView = inflater?.inflate(R.layout.address_layout, null, false)
                    val leasingToHint = addressView?.findViewById<TextView>(R.id.text_leasing_to_hint)
                    val textLeasingTo = addressView?.findViewById<TextView>(R.id.text_leasing_to)

                    leasingToHint?.text = addressModel.address
                    textLeasingTo?.text = addressModel?.value.toString()

                    addressContainer?.addView(addressView)
                }

                if (address.size > 3) {
                    showMoreAddress?.visiable()
                    showMoreAddress?.text = getString(R.string.history_details_show_all, (address.size - 3).toString())
                }

                showMoreAddress?.click {
                    showMoreAddress.gone()

                    for (i in 3 until address.size) {
                        val addressModel = address[i]

                        val addressView = inflater?.inflate(R.layout.address_layout, null, false)
                        val leasingToHint = addressView?.findViewById<TextView>(R.id.text_leasing_to_hint)
                        val textLeasingTo = addressView?.findViewById<TextView>(R.id.text_leasing_to)

                        leasingToHint?.text = addressModel?.address
                        textLeasingTo?.text = addressModel?.value.toString()

                        addressContainer?.addView(addressView)
                    }
                }

                historyContainer?.addView(tokenGenView)
                historyContainer?.addView(baseInfoLayout)
                historyContainer?.addView(button)
            }
            else -> {

            }
        }
        historyContainer?.addView(bottomBtns)
    }

    private fun setupHistoryViewPager(view: View) {
        viewPager = view.findViewById(R.id.viewpager_history_item)
        val historyDetailsAdapter = HistoryDetailsAdapter(activity!!, allItems!!, historyType)
        viewPager?.adapter = historyDetailsAdapter
        viewPager?.currentItem = selectedItemPosition

        val enumList = HistoryTypeEnum.values()

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val historyItem = historyDetailsAdapter.mData[position]

                setupView(enumList[Random().nextInt(enumList.size)])
//                setupView(HistoryTypeEnum.MASSTRANSFER)

                checkStepIconState()
            }
        })

        checkStepIconState()

        setupView(HistoryTypeEnum.DATA)
//        setupView(enumList[Random().nextInt(enumList.size)])
    }

    private fun checkStepIconState() {
        if (viewPager?.currentItem == 0 && viewPager?.adapter?.count == 1) {
            rooView?.findViewById<ImageView>(R.id.image_icon_go_to_preview)?.alpha = 0.5F
            rooView?.findViewById<ImageView>(R.id.image_icon_go_to_next)?.alpha = 0.5F
        } else if (viewPager?.currentItem == 0) {
            rooView?.findViewById<ImageView>(R.id.image_icon_go_to_preview)?.alpha = 0.5F
            rooView?.findViewById<ImageView>(R.id.image_icon_go_to_next)?.alpha = 1.0F
        } else if (viewPager?.currentItem == viewPager?.adapter?.count!! - 1) {
            rooView?.findViewById<ImageView>(R.id.image_icon_go_to_preview)?.alpha = 1.0F
            rooView?.findViewById<ImageView>(R.id.image_icon_go_to_next)?.alpha = 0.5F
        } else {
            rooView?.findViewById<ImageView>(R.id.image_icon_go_to_preview)?.alpha = 1.0F
            rooView?.findViewById<ImageView>(R.id.image_icon_go_to_next)?.alpha = 1.0F
        }
    }
}