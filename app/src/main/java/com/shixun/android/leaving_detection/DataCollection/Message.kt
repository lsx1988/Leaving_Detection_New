package com.shixun.android.leaving_detection.DataCollection

/**
 * Created by shixunliu on 22/4/17.
 * The message sending between fragment or activity by using EventBus
 */

class Message(var message: String?,
              var pressure: Double?,
              var magnetic: Double?,
              var temperature: Double?,
              var wifi: Double?,
              var isWalking: Boolean) {
}
