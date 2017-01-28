package info.benallen.jeeves.jeevescontroller;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maartie0 on 28/01/2017.
 */

class BlueToothHandler implements RangeNotifier,MonitorNotifier {
    private final String TAG = "BlueToothHandler";
    private BeaconManager mBeaconManager;
    private Map<Integer,Float> mIdToDistance = new HashMap<Integer, Float>();
    private Context context;

    BlueToothHandler(Context context,MainActivity mainActivity){
        mBeaconManager = BeaconManager.getInstanceForApplication(context);
        // Detect the URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        mBeaconManager.bind(mainActivity);
        this.context = context;
    }

    public BeaconManager getmBeaconManager() {
        return mBeaconManager;
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.d(TAG,"5");
        for (Beacon beacon: beacons) {
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                // This is a Eddystone-URL frame
                String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                Log.d(TAG, "I see a beacon transmitting a url: " + url + " approximately " + beacon.getDistance() + " meters away.");
                updateMap(beacon);
            }
        }
        Log.d(TAG,getCurrentData().toString());
        Log.d(TAG,"6");
    }

    private void updateMap(Beacon beacon){
        if(mIdToDistance.containsKey(beacon.getId1().toInt())){
            mIdToDistance.remove(beacon.getId1().toInt());
        }
        NumberFormat formatter = new DecimalFormat("#0.0000");
        mIdToDistance.put(beacon.getId1().toInt(), (float)beacon.getDistance());
    }

    public List<BeaconData> getCurrentData(){
        List<BeaconData> beaconDatas = new ArrayList<>();
        for (int key : mIdToDistance.keySet()){
            BeaconData beaconData = new BeaconData(key,mIdToDistance.get(key));
            beaconDatas.add(beaconData);
        }
        return beaconDatas;
    }


    @Override
    public void didEnterRegion(Region region) {

    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
