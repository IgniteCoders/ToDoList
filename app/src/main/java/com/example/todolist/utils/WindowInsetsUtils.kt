package com.example.todolist.utils

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
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
 * (such as the status bar and navigation bar) using `WindowInsets`.
 *
 * This function modifies the view's layout parameters (specifically the margins)
 * based on the system bars' inset values. It adds the system bars' insets to
 * the current margins of the view, ensuring that the content is not overlapped by
 * the system bars. The margins for the top, right, left, and bottom sides are adjusted.
 *
 * @receiver The view to which the margin adjustments will be applied.
 *           This view should have `MarginLayoutParams` as its layout parameters.
 */
fun View.setWindowInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = systemBars.top + this@setWindowInsets.marginTop
            rightMargin = systemBars.right + this@setWindowInsets.marginRight
            leftMargin = systemBars.left + this@setWindowInsets.marginLeft
            bottomMargin = systemBars.bottom + this@setWindowInsets.marginBottom
        }
        insets
    }
}