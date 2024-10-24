package com.example.lifelineapp

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

/**
 * Highlights the appointment dates on the calendar
 */

class EventHighlighter(private val dates: Collection<CalendarDay>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        // Use a DotSpan to highlight the dates
        view.addSpan(DotSpan(10f, Color.RED)) // Adjust size and color as needed
    }
}
