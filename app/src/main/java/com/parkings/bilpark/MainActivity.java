package com.parkings.bilpark;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This is the main "Park" activity; the starting point of our application.
 * Includes a map and related API tools for geolocating and putting markers.
 *
 * All overridden and/or implemented methods' functionality should be checked
 * from the superclass's/interface's JavaDocs.
 *
 * @author Ugur
 * @version Initial release
 */
public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

	SupportMapFragment supportMapFragment; // if the class extended Activity then we would use MapFragment
	GoogleMap mMap;
	ActionBar actionBar;
	LocationManager locationManager;
	LocationListener locationListener;
	LatLng userLocation;
	Location lastKnownLocation;
	boolean mapClicked;
	boolean cameraClicked;
	boolean polygonClicked;
	//boolean nanotamClicked;
	//boolean mescidClicked;
	//boolean unamClicked;
	boolean firstPush;
	boolean secondPush;
	private Fragment fragment, actionButton;
	private NavigationView navigationView;
	private Polygon nanotamPolygon;
	private Marker nanotamMarker;
	private Polygon mescidPolygon;
	private Marker mescidMarker;
	private Polygon unamPolygon;
	private Marker unamMarker;
	Location parkLocation;
	boolean userParked = false;
	private Marker userMarker;
	LatLng test;
	private ServerUtil serverUtil;
	private HashMap<LatLng, GroundOverlay> redDots = new HashMap<>();
	LatLng keyf;
	private BitmapDrawable bitmapdraw2;
	private Bitmap b2;
	private Bitmap smallMarker2;

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == 1) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MapsInitializer.initialize(getApplicationContext());
		serverUtil = ServerUtil.getInstance();
		supportMapFragment = SupportMapFragment.newInstance();

		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionButton = new ActionButtonFragment();

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		supportMapFragment.getMapAsync(this);
		// the two lines below are VERY VERY VERY IMPORTANT as they ensure that the app launches with the park option selected
		onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_park));
		navigationView.getMenu().getItem(0).setChecked(true);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else if (polygonClicked) {
			getUserLocation();
			addPolygon("nanotam");
			polygonClicked = false;
		} else if(fragment instanceof StatisticsMain && fragment.getActivity().getSupportFragmentManager().findFragmentById(R.id.statistics) instanceof StatisticsFragment) {
			onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_park));
			navigationView.getMenu().getItem(0).setChecked(true);
		} else if (fragment == null) {
			finish();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		fragment = null;
		int id = item.getItemId();
		FragmentManager supportFragmentManager = getSupportFragmentManager();
		if (supportMapFragment.isAdded()) {
			supportFragmentManager.beginTransaction().hide(supportMapFragment).commit();
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction;
		if (id == R.id.nav_park) {
			// handle the park fragment
			if (!supportMapFragment.isAdded()) {
				supportFragmentManager.beginTransaction().add(R.id.map, supportMapFragment).commit();
			} else {
				supportFragmentManager.beginTransaction().show(supportMapFragment).commit();
			}
			if (fragment != null) {
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.remove(fragment);
				fragmentTransaction.commit();
			}
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.floating_action_button, actionButton);
			fragmentTransaction.commit();
		} else {
			if (id == R.id.nav_statistics) {
				fragment = new StatisticsMain();
			} else if (id == R.id.nav_complaints) {
				fragment = new ComplaintsFragment();
			} else if (id == R.id.nav_aboutus) {
				fragment = new AboutUsFragment();
			}
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.remove(actionButton);
			fragmentTransaction.commit();
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.map, fragment);
			fragmentTransaction.commit();
		}

        /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void parked( LatLng center ) {
		LatLngBounds dotBounds = new LatLngBounds(
				new LatLng(center.latitude - 0.000012, center.longitude - 0.000012 ),       // South west corner
				new LatLng(center.latitude + 0.000012, center.longitude + 0.000012 ));      // North east corner

		GroundOverlayOptions dot = new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromResource(R.raw.reddot))
				.positionFromBounds(dotBounds)
				.transparency(0f);

		GroundOverlay dotOverlay = mMap.addGroundOverlay(dot);
		redDots.put( center, dotOverlay);
	}

	/**
	 * Refreshes the user location data
	 */
	public void getUserLocation() {
		Log.i("KONUM", "ISTENDI");
		//mMap.clear();
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
					1 );
		}

		lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
		if (lastKnownLocation != null) {
			userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
			//mMap.addMarker(new MarkerOptions().position(userLocation).title("Marker in Turkey"));
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(userLocation)      // Sets the center of the map to Mountain View
					.zoom(16)                  // Sets the zoom
					.bearing(lastKnownLocation.getBearing())                // Sets the orientation of the camera to east
					//.tilt(30)                // Sets the tilt of the camera to 30 degrees
					.build();                  // Creates a CameraPosition from the builder
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			if ( userMarker != null ) {
				userMarker.remove();
			}
			userMarker = mMap.addMarker(new MarkerOptions().
					position(userLocation).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_navigation_black_48dp)));
		}
		mapClicked = false;
	}

	public void polyTest( LatLng one, LatLng two, LatLng three, LatLng four ) {
		mMap.addPolygon(new PolygonOptions()
				.add(one, two, three, four )
				.strokeColor(Color.BLACK)
				.fillColor(Color.GRAY)
				.strokeWidth(10) );
	}

	public void addPolygon( String tag ) {
		if ( tag.equals("nanotam") ) {
			nanotamPolygon = mMap.addPolygon(new PolygonOptions()
					.add(
							new LatLng(39.86643115675040, 32.74708114564418),
							new LatLng(39.86654052536382, 32.74778019636869),
							new LatLng(39.86715298637697, 32.74763870984316),
							new LatLng(39.86722426824893, 32.74693094193936))
					.strokeColor(Color.BLACK)
					.fillColor(Color.GRAY)
					.strokeWidth(10)
					.clickable(true));
			nanotamPolygon.setTag("nanotam");
			nanotamMarker.showInfoWindow();
		}
		/**
		else if ( tag.equals("mescid") && !mescidClicked ) {
			mescidPolygon = mMap.addPolygon(new PolygonOptions()
					.add(
							new LatLng(39.867656589928885,32.75062870234251),
							new LatLng(39.867765699255635,32.75253340601921),
							new LatLng(39.8659684612521,32.75245092809201),
							new LatLng(39.86596331445483,32.750481851398945))
					.strokeColor(Color.BLACK)
					.fillColor(Color.GRAY)
					.strokeWidth(10)
					.clickable(true));
			mescidPolygon.setTag("mescid");
			mescidMarker.showInfoWindow();
		}
		else if ( tag.equals("unam") && !unamClicked ) {
			unamPolygon = mMap.addPolygon(new PolygonOptions()
					.add(
							new LatLng(39.86917870082838,32.747862339019775),
							new LatLng(39.86910253175345,32.74663086980581),
							new LatLng(39.86933850121406,32.7466020360589),
							new LatLng(39.86947720044192,32.74790424853563))
					.strokeColor(Color.BLACK)
					.fillColor(Color.GRAY)
					.strokeWidth(10)
					.clickable(true));
			unamPolygon.setTag("unam");
			unamMarker.showInfoWindow();
		}
		 **/
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		// gets called once when map is ready to be loaded
		mMap = googleMap;
		mapClicked = false;
		cameraClicked = false;
		polygonClicked = false;
		secondPush = false;
		firstPush = false;
		//nanotamClicked = false;
		//mescidClicked = false;
		//unamClicked = false;

		mMap.setLatLngBoundsForCameraTarget( new LatLngBounds(
				new LatLng(39.864870, 32.746315),
				new LatLng(39.872084, 32.752667)));
		//mMap.setMaxZoomPreference(4f);
		mMap.setMinZoomPreference(16f);
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(39.868593, 32.748719))      // Sets the center of the map to Mountain View
				.zoom(16)                  // Sets the zoom
				.bearing(180f)                // Sets the orientation of the camera to east
				//.tilt(30)                // Sets the tilt of the camera to 30 degrees
				.build();                  // Creates a CameraPosition from the builder
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.quantum_ic_keyboard_arrow_down_white_36);
		Bitmap b = bitmapdraw.getBitmap();
		Bitmap smallMarker = Bitmap.createScaledBitmap(b, 1, 1, false);

		LatLngBounds nanotamBounds = new LatLngBounds(
				new LatLng(39.866421, 32.746917),       // South west corner
				new LatLng(39.867235, 32.747752));      // North east corner

		final GroundOverlayOptions nanotam = new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromResource(R.raw.nanotam))
				.positionFromBounds(nanotamBounds)
				.transparency(0f);

		GroundOverlay nanotamOverlay = mMap.addGroundOverlay(nanotam);

		LatLngBounds mescidBounds = new LatLngBounds(
				new LatLng(39.865979, 32.750582),       // South west corner
				new LatLng(39.86771912194634,32.752494513988495));      // North east corner

		final GroundOverlayOptions mescid = new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromResource(R.raw.mescid))
				.positionFromBounds(mescidBounds)
				.transparency(0f);

		GroundOverlay mescidOverlay = mMap.addGroundOverlay(mescid);

		LatLngBounds unamBounds = new LatLngBounds(
				new LatLng(39.86911642746938,32.74663891643286),       // South west corner
				new LatLng(39.8694617608386,32.74786803871393));      // North east corner

		final GroundOverlayOptions unam = new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromResource(R.raw.unam))
				.positionFromBounds(unamBounds)
				.transparency(0f);

		GroundOverlay unamOverlay = mMap.addGroundOverlay(unam);


		nanotamMarker = mMap.addMarker(
				new MarkerOptions().position(
						new LatLng(39.86685447665023, 32.74732355028391))
						.title("Loading...").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
		nanotamMarker.setTag("nanotamMarker");
		nanotamMarker.showInfoWindow();

		/**
		mescidMarker = mMap.addMarker(
				new MarkerOptions().position(
						new LatLng(39.867077329724296,32.751618437469))
						.title("Loading...").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
		mescidMarker.setTag("mescidMarker");
		mescidMarker.showInfoWindow();

		unamMarker = mMap.addMarker(
				new MarkerOptions().position(
						new LatLng(39.86931148185135,32.747369818389416))
						.title("Loading...").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
		unamMarker.setTag("unamMarker");
		unamMarker.showInfoWindow();
		 **/

		addPolygon("nanotam");
		//addPolygon("mescid" );
		//addPolygon( "unam" );

		mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
			@Override
			public void onPolygonClick(Polygon polygon) {
				polygonClicked = true;
				mapClicked = true;
				firstPush = false;
				Log.i("POLYGON CLICKLENDI", "IF'IN DISI");
				if (polygon.getTag().equals("nanotam")) {
					Log.i("POLYGON CLICKLENDI", "IF'IN ICI");
					//nanotamClicked = true;
					nanotamPolygon.remove();
					nanotamMarker.hideInfoWindow();
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(39.866855, 32.747324))      // Sets the center of the map to Mountain View
							.zoom(19)                  // Sets the zoom
							.bearing(180f)                // Sets the orientation of the camera to east
							//.tilt(30)                // Sets the tilt of the camera to 30 degrees
							.build();                  // Creates a CameraPosition from the builder
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
				/**
				else if (polygon.getTag().equals("mescid")) {
					Log.i("POLYGON CLICKLENDI", "IF'IN ICI");
					//mescidClicked = true;
					mescidPolygon.remove();
					mescidMarker.hideInfoWindow();
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(39.867077329724296,32.751618437469))      // Sets the center of the map to Mountain View
							.zoom(18)                  // Sets the zoom
							.bearing(180f)                // Sets the orientation of the camera to east
							//.tilt(30)                // Sets the tilt of the camera to 30 degrees
							.build();                  // Creates a CameraPosition from the builder
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
				else if (polygon.getTag().equals("unam")) {
					Log.i("POLYGON CLICKLENDI", "IF'IN ICI");
					//unamClicked = true;
					unamPolygon.remove();
					unamMarker.hideInfoWindow();
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(39.86931148185135,32.747369818389416))      // Sets the center of the map to Mountain View
							.zoom(19)                  // Sets the zoom
							.bearing(180f)                // Sets the orientation of the camera to east
							//.tilt(30)                // Sets the tilt of the camera to 30 degrees
							.build();                  // Creates a CameraPosition from the builder
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
				 **/
			}
		});

		//GETTING USERS LOCATION
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		/*
		int height = 300;
		int width = 300;
		bitmapdraw2 = (BitmapDrawable)getResources().getDrawable(R.drawable.baseline_navigation_black_48dp);
		b2 = bitmapdraw2.getBitmap();
		smallMarker2  = Bitmap.createScaledBitmap(b, width, height, false); */

		//mMap.getUiSettings().setZoomGesturesEnabled(false);
		//LatLng turkey = new LatLng(39.868435, 32.748919);
		//mMap.addMarker( new MarkerOptions().position(turkey).title("Marker in Turkey"));
		//mMap.moveCamera(CameraUpdateFactory.newLatLng(turkey));
		//mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkey, (float) 16.5 ));
		locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (!mapClicked) {
					Log.i("TAYF", ".");
					//mMap.clear();
					userLocation = new LatLng(location.getLatitude(), location.getLongitude());

					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(userLocation)      // Sets the center of the map to Mountain View
							.zoom(16)                  // Sets the zoom
							.bearing(location.getBearing())                // Sets the orientation of the camera to east
							//.tilt(30)                // Sets the tilt of the camera to 30 degrees
							.build();                  // Creates a CameraPosition from the builder
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					if ( userMarker != null ) {
						userMarker.remove();
						userMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
					}
					mapClicked = false;
				}
			}

			@Override
			public void onStatusChanged(String s, int i, Bundle bundle) {

			}

			@Override
			public void onProviderEnabled(String s) {

			}

			@Override
			public void onProviderDisabled(String s) {

			}
		};

		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				Log.i("MAP", latLng.toString());
				parkLocation = new Location("");
				parkLocation.setLatitude(latLng.latitude);
				parkLocation.setLongitude(latLng.longitude);
				Log.d("TEST", "" +  parkLocation.getLatitude() + "\n" + parkLocation.getLongitude() );
				//FOR TEST
				/*
				test = latLng;
				keyf = serverUtil.park(test);
				if ( keyf == null ) {
					Toast.makeText( MainActivity.this, "batırdın", Toast.LENGTH_SHORT).show();
				}
				else {
					parked(keyf);
				} */
				//FOR TEST
				mapClicked = true;
			}
		});

		mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
			@Override
			public void onCameraMoveStarted(int i) {
				Log.i("CAMERA", "CLICKLENDI");
				mapClicked = true;
			}
		});

		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				Log.i("MARKER", "CLICKLENDI");
				polygonClicked = true;
				mapClicked = true;
				firstPush = false;
				if ( marker.getTag().equals("nanotamMarker") ) {
					//nanotamClicked = true;
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(39.866855, 32.747324))      // Sets the center of the map to Mountain View
							.zoom(19)                  // Sets the zoom
							.bearing(180f)                // Sets the orientation of the camera to east
							//.tilt(30)                // Sets the tilt of the camera to 30 degrees
							.build();                  // Creates a CameraPosition from the builder
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					nanotamMarker.hideInfoWindow();
					nanotamPolygon.remove();
				}
				/**
				else if ( marker.getTag().equals("mescid") ) {
					mescidClicked = true;
					mescidPolygon = mMap.addPolygon(new PolygonOptions()
							.add(
									new LatLng(39.867656589928885,32.75062870234251),
									new LatLng(39.867765699255635,32.75253340601921),
									new LatLng(39.8659684612521,32.75245092809201),
									new LatLng(39.86596331445483,32.750481851398945))
							.strokeColor(Color.BLACK)
							.fillColor(Color.GRAY)
							.strokeWidth(10)
							.clickable(true));
					mescidPolygon.setTag("mescid");
					mescidMarker.showInfoWindow();
				}
				else if ( marker.getTag().equals("unam") ) {
					unamClicked = true;
					unamPolygon = mMap.addPolygon(new PolygonOptions()
							.add(
									new LatLng(39.86917870082838,32.747862339019775),
									new LatLng(39.86910253175345,32.74663086980581),
									new LatLng(39.86933850121406,32.7466020360589),
									new LatLng(39.86947720044192,32.74790424853563))
							.strokeColor(Color.BLACK)
							.fillColor(Color.GRAY)
							.strokeWidth(10)
							.clickable(true));
					unamPolygon.setTag("unam");
					unamMarker.showInfoWindow();
				}
				 **/
				return true;
			}
		});

		//for older versions
		if (Build.VERSION.SDK_INT < 23 && checkPermission("location", android.os.Process.myPid(),
				android.os.Process.myUid()) != PackageManager.PERMISSION_GRANTED) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
		} else {
			//for newer versions
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
			} else {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
			}
		}
		/*
		ParkingRow "Row = new ParkingRow(36, new LatLng[] {new LatLng(39.867141, 32.747056),
				new LatLng(39.866530, 32.747152),
				new LatLng(39.866542, 32.747316),
				new LatLng(39.867104, 32.747219)});
			39.867069, 32.747314
			39.866980, 32.747340
			39.867003, 32.747375
			39.867100, 32.747359
		 */ // The following numbers ARE NOT CORRECT; for test only.

		/*
		mMap.addPolygon(new PolygonOptions()
				.add(new LatLng(39.867142692959746,32.74706404656172),
						new LatLng(39.86657861133413,32.74714753031731),
						new LatLng(39.86654387075395,32.74722263216973),
						new LatLng(39.867100232597345,32.74715155363083))
				.strokeColor(Color.BLACK)
				.fillColor(Color.GRAY)
				.strokeWidth(10) ); */

		 /**
		Log.d("TEST","TEST");

		for ( ParkingSpot ps: testRow.parkingSpots ) {
			Log.d("TEST\n\n", ps.toString());
		} */
		Log.d("TEST COUNTER", ""+ParkingSpot.counter);
		/*
		for (Map.Entry overlay : (ParkingSpot.dots).entrySet()) {
			GroundOverlay dot = mMap.addGroundOverlay((GroundOverlayOptions) overlay.getValue());
			Log.d("TEST", "LATITUDE: " + ((LatLng) overlay.getKey()).latitude + "\n" +
					"LONGITUDE: " + ((LatLng) overlay.getKey()).longitude + "\n");
		}
		*/

		/*
		for ( LatLng[] latLngs: ParkingSpot.polytest ) {
			polyTest(latLngs[0], latLngs[1], latLngs[2], latLngs[3]);
		} */

		// Parking slot ground overlay listener
		FirebaseDatabase.getInstance().getReference().child("parkingdata/lots")
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onCancelled(DatabaseError databaseError) {}

					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						ArrayList<ParkingSpot> slots = serverUtil.getParkingSpots();
						Toast.makeText(MainActivity.this,"DATA DEGISTI " + slots.size(), Toast.LENGTH_SHORT).show();
						nanotamMarker.setTitle( String.format("%.2f", 100 * ( dataSnapshot.child("occupiedSlots").getValue(Integer.class) + 0.0 ) / dataSnapshot.child("totalSlots").getValue(Integer.class)) + "%");
						for ( int i = 0; i < slots.size(); i++ ) {
							//IF SOMEBODY PARKS
/*							Toast.makeText(MainActivity.this,"LEL", Toast.LENGTH_SHORT).show();
							Log.d( "DEBUG DATA LISTENER: ", "\n" + "ITERATION: " + i + "\n"
									+ slotsClone.get(i).isParked() + "\n" + slots.get(i).isParked()); */
							if ( slots.get(i).isParked() ) {
								parked( slots.get(i).getCenter() );
							}
							//IF SOMEBODY UNPARKS
							else if ( !slots.get(i).isParked() ) {

								if (redDots.get( slots.get(i).getCenter() ) != null) {
									redDots.get(slots.get(i).getCenter()).remove();
									redDots.remove(slots.get(i).getCenter());
								}
							}
						}
					}
				});

		MapsInitializer.initialize(getApplicationContext());
		serverUtil = ServerUtil.getInstance();
	}
}