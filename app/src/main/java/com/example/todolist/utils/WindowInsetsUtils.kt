package com.example.todolist.utils

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams

/**
 * Sets the padding of the root view to account for the system bars' insets
 * for this [ComponentActivity] using `WindowInsets`.
 *
 * To set it up with the default behaviour, call this method in your Activity's onCreate method:
 * ```
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         ...
 *         setContentView(binding.root)
 *         setWindowInsets(binding.root)
 *         ...
 *     }
 * ```
 * The default behaviour adjusts the left, top, and right padding of the root view
 * based on the system bars' inset values. The bottom padding is set to 0.
 *
 * @param rootView The root view to which the padding adjustments will be applied.
 *                 This view should be the main container in the activity's layout.
 */
fun ComponentActivity.setWindowInsets(rootView: View) {
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
        insets
    }
}

/**
 * Adjusts the margins of the view to account for the system bars' insets
 * (such as the status bar and navigation bar) using `WindowInsets` and apply custom margins.
 *
 * This function listens for window insets and updates the layout parameters of the view,
 * ensuring that the view content is correctly positioned within the window and does not overlap
 * with system bars.
 *
 * Example usage:
 * ```
 * view.setWindowInsets(Insets.of(0, 0, 10, 10))
 * ```
 * This would add an additional margin of 10px on the right and the bottom, in addition to the system bar insets.
 *
 * @param marginInsets Additional margins to be added on top of the system bars' insets.
 *                     The default value is [Insets.of(0, 0, 0, 0)], meaning no extra margin.
 *                     You can customize the top, right, bottom, and left margins.
 *
 * @receiver The view to which the margin adjustments will be applied.
 *           This view should have `MarginLayoutParams` as its layout parameters.
 */
fun View.setWindowInsets(marginInsets: Insets = Insets.of(0, 0, 0, 0)) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = systemBars.top + marginInsets.top
            rightMargin = systemBars.right + marginInsets.right
            leftMargin = systemBars.left + marginInsets.left
            bottomMargin = systemBars.bottom + marginInsets.bottom
        }
        insets
    }
}