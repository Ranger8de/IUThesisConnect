package com.dlbcsemse.iuthesisconnect.model

import com.dlbcsemse.iuthesisconnect.model.DashboardUserType

data class Thesis(
    var id: Int, var state: String, var supervisor: Int, var secondSupervisor: Int, var theme: String, var student: Int, var dueDateDay: Int, var dueDateMonth: Int,
    var dueDateYear: Int, var billState: String, var userType: Int
) {
    var thesisId: Int = id
    var thesisState: String = state
    var thesisSupervisor: Int = supervisor
    var thesisSecondSupervisor: Int = secondSupervisor
    var thesisTheme: String = theme
    var thesisStudent: Int = student
    var thesisDueDateDay: Int = dueDateDay
    var thesisDueDateMonth: Int = dueDateMonth
    var thesisDueDateYear: Int = dueDateYear
    var thesisBillState: String = billState

    fun getUserTypeString(): String {
        return when(DashboardUserType.values()[userType]) {
            DashboardUserType.student -> "student"
            DashboardUserType.supervisor -> "supervisor"
        }
    }
}