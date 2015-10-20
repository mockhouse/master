package com.quick.mockscoop;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;




import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;








import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.widget.ShareDialog;



import com.quick.connector.WebConnector;
import com.quick.global.Globals;

import android.support.v4.app.FragmentActivity;
//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Login extends Activity implements RequestReceiver {


	Context context;
	String regId;
	public static String fb_id;
	public static String name;
	public static String imageURI;
	SharedPreferences sharedpreferences =null;	
	    private CallbackManager callbackManager;
        Map<String, String> userDetails = new HashMap<>();

	    WebConnector connector = WebConnector.getInstance();


	    public static String printKeyHash(Activity context) {
	    	PackageInfo packageInfo;
	    	String key = null;
	    	try {
	    		//getting application package name, as defined in manifest
	    		String packageName = "com.aasqr.carpool";
int a;
	    		//Retriving package info
	    		packageInfo = context.getPackageManager().getPackageInfo(packageName,
	    				PackageManager.GET_SIGNATURES);
	    		
	    		Log.e("Package Name=", context.getApplicationContext().getPackageName());
	    		
	    		for (Signature signature : packageInfo.signatures) {
	    			MessageDigest md = MessageDigest.getInstance("SHA");
	    			md.update(signature.toByteArray());
	    			key = new String(Base64.encode(md.digest(), 0));
	    		
	    			// String key = new String(Base64.encodeBytes(md.digest()));
	    			Log.e("Key Hash=", key);
	    		}
	    	} catch (NameNotFoundException e1) {
	    		Log.e("Name not found", e1.toString());
	    	}
	    	catch (NoSuchAlgorithmException e) {
	    		Log.e("No such an algorithm", e.toString());
	    	} catch (Exception e) {
	    		Log.e("Exception", e.toString());
	    	}

	    	return key;
	    }
	    public void click(View v)
	    {
	    	Log.d("aman"," Key ="+printKeyHash(this));
	    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginButton loginButton=null;
		 FacebookSdk.sdkInitialize(this.getApplicationContext());
		  //	Log.d("amanjot"," Key ="+printKeyHash(this));
	        callbackManager = CallbackManager.Factory.create();
	 //       LoginManager.getInstance().logInWithReadPermissions(
	   //     	    this,
	     //   	    Arrays.asList("email"));
	        LoginManager.getInstance().registerCallback(callbackManager,
	                new FacebookCallback<LoginResult>() {
	                    @Override
	                    public void onSuccess(LoginResult loginResult) {
	              Log.d("aman","We are here");
	             
					       Profile profile = Profile.getCurrentProfile();
					       fb_id=profile.getId();
					       name=profile.getFirstName();
	                    	AccessToken at=AccessToken.getCurrentAccessToken();
	                    	try {
	                    		Globals.imageURI=profile.getProfilePictureUri(100, 100).toString();
	                    		Globals.realName=name;
	                    		Globals.fb_id=fb_id;
	                    	userDetails.put("fb_id",fb_id);
	                    	
	                    	userDetails.put("username",name);
	                    	sharedpreferences= getSharedPreferences("users", Context.MODE_PRIVATE);
	                    	String  unique_name=sharedpreferences.getString(fb_id, "not found");
	                    	if(unique_name.equals("not found"))
								connector.registerUser(Login.this, Login.this, userDetails);
	                    	else
	                    	{
	                    		Globals.userName=unique_name;
	                    		
	                    		  Intent intent = new Intent(Login.this, HomePage.class);
	                    		  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	   					          startActivity(intent);
	   					
	   					       finish();
	                    	}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                 
	                  
	                    }

	                    @Override
	                    public void onCancel() {
	                    
	                    }

	                    @Override
	                    public void onError(FacebookException exception) {
	                    
	                    }

	              
	                });
	        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
		setContentView(R.layout.activity_login);
	}

	
	  @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        callbackManager.onActivityResult(requestCode, resultCode, data);
	    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//if (id == R.id.action_settings) {
			//return true;
	//	}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void receiveResponse(Object response) {
		// TODO Auto-generated method stub
		 Editor editor = sharedpreferences.edit();
         editor.putString(fb_id,(String) response);
         editor.commit();
 		Globals.userName=(String) response;
 		
         Intent intent = new Intent(Login.this, HomePage.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	     startActivity(intent);
	     finish();

		
	}
}
