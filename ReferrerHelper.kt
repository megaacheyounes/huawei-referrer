package com.younes.referrer_demo

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.huawei.hms.ads.installreferrer.api.InstallReferrerClient
import com.huawei.hms.ads.installreferrer.api.InstallReferrerStateListener
import timber.log.Timber


object ReferrerHelper {
    private const val TAG = "ReferrerHelper"
    private const val PROVIDER_URI = "content://com.huawei.appmarket.commondata/item/5"
    private const val INDEX_ENTER_AG_TIME = 1
    private const val INDEX_INSTALLED_FINISH_TIME = 2
    private const val INDEX_START_DOWNLOAD_TIME = 3
    private const val INDEX_REFERRER_EX = 5

    fun getInstallReferrer(activity: Activity ): String {

        var cursor: Cursor? = null
        val uri = Uri.parse(PROVIDER_URI)

        val packageName = arrayOf(activity.packageName)
        try {
            cursor = activity.contentResolver.query(uri, null, null, packageName, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val storeVisitTime = cursor.getString(INDEX_ENTER_AG_TIME)
                val installTime = cursor.getString(INDEX_INSTALLED_FINISH_TIME)
                val downloadTime = cursor.getString(INDEX_START_DOWNLOAD_TIME)
                val referrer = cursor.getString(INDEX_REFERRER_EX)
                Log.d(TAG, "referrer=$ referrer")
                return referrer
            } else {
                Log.d(TAG, "cursor is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //Todo: handle the exception.
        } finally {
            cursor?.close()
        }
        return ""
    }

    lateinit var mReferrerClient: InstallReferrerClient

    fun getInstallReferrerUsingSDK(activity: Activity) {
        val mReferrerClient = InstallReferrerClient.newBuilder(activity).setTest(false).build()

    }

    fun onResume() {
        try {
            mReferrerClient.startConnection(installReferrerStateListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun onStop() {
        try {
            mReferrerClient.endConnection()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val installReferrerStateListener = object : InstallReferrerStateListener {
        override fun onInstallReferrerSetupFinished(p0: Int) {
            Timber.d("install ref setup finished: $p0")
            if (p0 == InstallReferrerClient.InstallReferrerResponse.OK) {
                //connect to AppGallery, request referrer
                try {
                    val referrerInformation = mReferrerClient.installReferrer

                    val referrer = referrerInformation.installReferrer
                    val channel = referrerInformation.installChannel
                    val installTimestamp = referrerInformation.installBeginTimestampMillisecond
                    val clickTimestamp = referrerInformation.referrerClickTimestampMillisecond

                } catch (e: Exception) {

                }
            }
        }

        override fun onInstallReferrerServiceDisconnected() {
            Timber.d("install ref failed")
        }
    }


}
