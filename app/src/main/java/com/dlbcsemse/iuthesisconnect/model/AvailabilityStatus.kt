package com.dlbcsemse.iuthesisconnect.model

import com.dlbcsemse.iuthesisconnect.R

enum class AvailabilityStatus {
    free, blocked, limited;

    companion object {
        fun getAvailabilityFlag(status: AvailabilityStatus): Int {
            return when (status) {
                free -> {
                    R.drawable.flag_green
                }

                blocked -> {
                    R.drawable.flag_red
                }

                limited -> {
                    R.drawable.flag_yellow
                }
            }
        }

        fun getAvailabilityText(status: AvailabilityStatus): Int {
            return when (status) {
                free -> {
                    R.string.availabilityStatus_free
                }
                blocked -> {
                    R.string.availabilityStatus_blocked
                }
                limited -> {
                    R.string.availabilityStatus_limited
                }
            }
        }
    }
}