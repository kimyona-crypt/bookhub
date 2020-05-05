@file:Suppress("DEPRECATION")

package com.nikita.bookhub.util
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo as NetworkInfo1

class connectionmanager {
fun checkConnectivity(context:Context):Boolean{
val connectivityManager= context.getSystemService(Context.CONNECTIVITY_SERVICE)as ConnectivityManager
val activeNetwork: NetworkInfo1?=connectivityManager.activeNetworkInfo
    if(activeNetwork?.isConnected!=null){
        return activeNetwork.isConnected
    }else
    {
        return false
    }
}
}