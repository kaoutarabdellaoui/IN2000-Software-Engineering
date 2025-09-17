package no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.repository

import no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.datasource.MetAlertsDatasource
import no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.models.MetAlerts

/**
 * Repository for fetching MetAlerts data
 */
class MetAlertsRepository() {
    private val datasource = MetAlertsDatasource()

    fun getAlerts(): MetAlerts? {
        return datasource.getMetAlerts()
    }
}
