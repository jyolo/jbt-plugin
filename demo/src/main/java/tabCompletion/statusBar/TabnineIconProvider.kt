package tabCompletion.statusBar

import tabCompletion.binary.requests.config.CloudConnectionHealthStatus
import tabCompletion.general.ServiceLevel
import tabCompletion.general.StaticConfig
import tabCompletion.general.getSubscriptionType
import javax.swing.Icon

object TabnineIconProvider {
    @JvmStatic
    fun getIcon(
        serviceLevel: ServiceLevel?,
        isLoggedIn: Boolean?,
        cloudConnectionHealthStatus: CloudConnectionHealthStatus,
        isForcedRegistration: Boolean?
    ): Icon {
        if (isForcedRegistration == null || isLoggedIn == null || serviceLevel == null || (
            isForcedRegistration &&
                !isLoggedIn
            )
        ) {
            return StaticConfig.getIconAndName()
        }

        return getSubscriptionType(serviceLevel).getTabnineLogo(cloudConnectionHealthStatus)
    }
}
