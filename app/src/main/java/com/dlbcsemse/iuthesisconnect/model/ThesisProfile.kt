package com.dlbcsemse.iuthesisconnect.model

import com.dlbcsemse.iuthesisconnect.DashboardUserType

data class ThesisProfile(var state: String, var supervisor: String, var secondSupervisor: String, var theme: String, var student: String, var dueDateDay: Int, var dueDateMonth: Int,
                         var dueDateYear: Int, var bill: String, var billState: String, var userType: Int
) {
    var thesisState: String = state
    var thesisSupervisor: String = supervisor
    var thesisSecondSupervisor: String = secondSupervisor
    var thesisTheme: String = theme
    var thesisStudent: String = student
    var thesisDueDateDay: Int = dueDateDay
    var thesisDueDateMonth: Int = dueDateMonth
    var thesisDueDateYear: Int = dueDateYear
    var thesisBill: String = bill
    var thesisBillState: String = billState

    fun getUserTypeString(): String {
        return when(DashboardUserType.values()[userType]) {
            DashboardUserType.student -> "student"
            DashboardUserType.supervisor -> "supervisor"
        }
    }
}