package com.test.spotontestapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import co.poynt.os.model.Intents;
import co.poynt.os.model.SecondScreenStrings;
import co.poynt.os.services.v1.IPoyntSecondScreenCheckInListener;
import co.poynt.os.services.v1.IPoyntSecondScreenCodeScanListener;
import co.poynt.os.services.v1.IPoyntSecondScreenPhoneEntryListener;
import co.poynt.os.services.v1.IPoyntSecondScreenService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private IPoyntSecondScreenService secondScreenService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            secondScreenService = IPoyntSecondScreenService.Stub.asInterface(iBinder);
            Log.d(TAG, "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: second screen service");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        bindService(Intents.getComponentIntent(Intents.COMPONENT_POYNT_SECOND_SCREEN_SERVICE), connection,
                BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button displayCashBtn = (Button) findViewById(R.id.displayMessageBtn);
        displayCashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cool_background);
                try {
                    secondScreenService.displayMessage("You have won a prize!", bitmap);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        Button checkInScreenButton = (Button) findViewById(R.id.checkInScreenButton);
        checkInScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.cool_background);
                    Bitmap buttonBackground = BitmapFactory.decodeResource(getResources(), R.drawable.green_rounded_button);

                    secondScreenService.showCheckIn("TAP TO CHECK IN!", buttonBackground, background, new IPoyntSecondScreenCheckInListener.Stub() {
                        @Override
                        public void onCheckIn() throws RemoteException {
                            Log.d(TAG, "onCheckIn: ");
                            secondScreenService.scanCode(new IPoyntSecondScreenCodeScanListener.Stub() {
                                @Override
                                public void onCodeScanned(String s) throws RemoteException {

                                }

                                @Override
                                public void onCodeEntryCanceled() throws RemoteException {

                                }
                            });
                        }

                        @Override
                        public void onSecondScreenBusy() throws RemoteException {

                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        Button collectPhone = (Button) findViewById(R.id.collectPhoneBtn);
        collectPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle options = new Bundle();
//                options.putString("PROMPT", "Use phone # to check in");
                options.putString("PROMPT", SecondScreenStrings.COLLECT_PHONE_PROMPT_BECOME_VIP);
                try {
                    secondScreenService.capturePhoneWithOptions(null, null, null, options, new IPoyntSecondScreenPhoneEntryListener.Stub() {
                        @Override
                        public void onPhoneEntered(String s) throws RemoteException {
                            Log.d(TAG, "onPhoneEntered: " + s);
                        }

                        @Override
                        public void onPhoneEntryCanceled() throws RemoteException {
                            Log.d(TAG, "onPhoneEntryCanceled: ");
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "onActivityResult called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
/*        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cool_background);
                try {
                    secondScreenService.displayMessage("You have won a prize!", bitmap);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 1000l);*/

    }
}
