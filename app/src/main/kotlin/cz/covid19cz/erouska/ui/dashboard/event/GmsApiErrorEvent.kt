package cz.covid19cz.erouska.ui.dashboard.event

import arch.event.LiveEvent

class GmsApiErrorEvent(val throwable: Throwable) : LiveEvent()