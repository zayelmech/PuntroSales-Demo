package com.imecatro.demosales.ui.sales.list

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.list.model.SalesDateRange
import com.imecatro.demosales.ui.sales.list.views.SalesDateFilterDialog
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SalesDateFilterDialogTest {
    @get:Rule val compose = createComposeRule()
    private val range = SalesDateRange(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12))
    private fun text(id: Int) = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test fun cancelDoesNotApplyDraft() {
        var applied = false
        var dismissed = false
        compose.setContent {
            MaterialTheme { SalesDateFilterDialog(range, { applied = true }, { dismissed = true }) }
        }
        compose.onNodeWithText(text(R.string.sales_date_range)).performClick()
        compose.onNodeWithText(text(R.string.sales_date_cancel)).performClick()
        compose.runOnIdle {
            assertFalse(applied)
            assertTrue(dismissed)
        }
    }

    @Test fun applyRangePreservesBothDates() {
        var selected: SalesDateRange? = null
        compose.setContent {
            MaterialTheme { SalesDateFilterDialog(range, { selected = it }, {}) }
        }
        compose.onNodeWithText(text(R.string.sales_date_range)).performClick()
        compose.onNodeWithText(text(R.string.sales_date_apply)).performClick()
        compose.runOnIdle { assertEquals(range, selected) }
    }

    @Test fun applySingleDateUsesSameStartAndEnd() {
        var selected: SalesDateRange? = null
        compose.setContent {
            MaterialTheme { SalesDateFilterDialog(range, { selected = it }, {}) }
        }
        compose.onNodeWithText(text(R.string.sales_date_single)).performClick()
        compose.onNodeWithText(text(R.string.sales_date_apply)).performClick()
        compose.runOnIdle { assertEquals(SalesDateRange(range.start), selected) }
    }

    @Test fun clearReturnsAllDates() {
        var selected: SalesDateRange? = range
        compose.setContent {
            MaterialTheme { SalesDateFilterDialog(range, { selected = it }, {}) }
        }
        compose.onNodeWithText(text(R.string.sales_date_all)).performClick()
        compose.runOnIdle { assertNull(selected) }
    }

    @Test fun emptyRangeCannotBeApplied() {
        compose.setContent {
            MaterialTheme { SalesDateFilterDialog(null, {}, {}) }
        }
        compose.onNodeWithText(text(R.string.sales_date_range)).performClick()
        compose.onNodeWithText(text(R.string.sales_date_apply)).assertIsNotEnabled()
    }
}
