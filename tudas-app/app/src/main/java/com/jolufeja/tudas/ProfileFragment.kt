package com.jolufeja.tudas

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jolufeja.authentication.UserAuthenticationService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get


class ProfileFragment(
    private val authenticationService: UserAuthenticationService
) : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        val profileFragment = ProfileFragment(get())

        val userName = view.findViewById<TextView>(R.id.userName).let {
            runBlocking {
                it.text = authenticationService.authentication.await().user.name
            }
        }

        // Friends Button
        var profileImage: ImageView = view.findViewById<View>(R.id.profileImage) as ImageView

        // Friends Button
        var friendsButton: Button = view.findViewById<View>(R.id.friendsButton) as Button

        // change mail Button
        var changeEmailButton: Button = view.findViewById<View>(R.id.changeEmail) as Button

        // change password Button
        var changePasswordButton: Button = view.findViewById<View>(R.id.changePassword) as Button

        // notification Button
        var notificationButton: Button = view.findViewById<View>(R.id.notificationButton) as Button

        // log out Button
        var logOutButton: Button = view.findViewById<View>(R.id.logOutButton) as Button

        // test notifications Button
        var testNotificationsButton: Button = view.findViewById<View>(R.id.testNotificationsButton) as Button


        profileImage.setOnClickListener{
            //upload profile picture
            //profileImage.setImageResource()
        }

        //opens FriendsSettingsFragment when clicked, but no layout yet
        friendsButton.setOnClickListener{
            val friendsSettingsFragment = FriendsSettingsFragment()
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(
                ((view as ViewGroup).parent as View).id,
                friendsSettingsFragment
            )
            transaction.addToBackStack("friends_list")
            transaction.commit()
        }

        //opens ChangeEmailFragment when clicked
        changeEmailButton.setOnClickListener{
            val changeEmailFragment = ChangeEmailFragment(get(), get())
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(
                ((view as ViewGroup).parent as View).id,
                changeEmailFragment
            )
            transaction.addToBackStack("change_email")
            transaction.commit()
        }
        //opens ChangePasswordFragment when clicked
        changePasswordButton.setOnClickListener{
            val changePasswordFragment = ChangePasswordFragment()
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(
                ((view as ViewGroup).parent as View).id,
                changePasswordFragment
            )
            transaction.addToBackStack("change_password")
            transaction.commit()
        }

        //opens notification menu
        notificationButton.setOnClickListener{
            val notificationSettingsFragment = NotificationSettingsFragment()
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(
                ((view as ViewGroup).parent as View).id,
                notificationSettingsFragment
            )
            transaction.addToBackStack("notification")
            transaction.commit()
        }

        logOutButton.setOnClickListener {
            lifecycleScope.launch {
                authenticationService.logout()
                findNavController().navigate(R.id.nav_graph_unauthenticated)
            }
        }

        testNotificationsButton.setOnClickListener {
            if ((activity as MainActivity).notificationsAllowed) {
                (activity as MainActivity).sendNotification(
                    "Test Notification",
                    "Click me to open TUDAS"
                )
            }
        }

    }
}