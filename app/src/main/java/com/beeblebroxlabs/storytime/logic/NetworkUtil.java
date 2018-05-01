package com.beeblebroxlabs.storytime.logic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

  private ConnectivityManager connectivityManager;
  private Context context;
  private NetworkInfo networkInfo;

  public NetworkUtil(Context context) {
    this.context = context;
    this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    this.networkInfo = connectivityManager.getActiveNetworkInfo();
  }

  public boolean isWifiConnected(){
    if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
      return true;
    }else{
      return  false;
    }
  }

  public boolean isMobileConnected(){
    if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
      return true;
    }else{
      return  false;
    }
  }

}
