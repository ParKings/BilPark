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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.uur.bilpark.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
	private Fragment fragment;
	Polygon nanotamPolygon;
	Marker nanotamMarker;

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
		supportMapFragment = SupportMapFragment.newInstance();

		setContentView(com.example.uur.bilpark.R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
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
		android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
		if (supportMapFragment.isAdded()) {
			supportFragmentManager.beginTransaction().hide(supportMapFragment).commit();
		}
		if (id == R.id.nav_park) {
			// handle the park fragment
			if (!supportMapFragment.isAdded()) {
				supportFragmentManager.beginTransaction().add(R.id.map, supportMapFragment).commit();
			} else {
				supportFragmentManager.beginTransaction().show(supportMapFragment).commit();
			}
		} else if (id == R.id.nav_statistics) {
			fragment = new StatisticsMain();
		} else if (id == R.id.nav_complaints) {
			fragment = new ComplaintsFragment();
		} else if (id == R.id.nav_aboutus) {
			fragment = new AboutUsFragment();
		}

		if (fragment != null) {
			android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

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
		}
		mapClicked = false;
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
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		// gets called once when map is ready to be loaded
		mMap = googleMap;
		mapClicked = false;
		cameraClicked = false;
		polygonClicked = false;

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

		nanotamMarker = mMap.addMarker(
				new MarkerOptions().position(
						new LatLng(39.86685447665023, 32.74732355028391))
						.title("%46").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
		nanotamMarker.setTag("nanotamMarker");
		nanotamMarker.showInfoWindow();

		addPolygon("nanotam");

		mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
			@Override
			public void onPolygonClick(Polygon polygon) {
				polygonClicked = true;
				mapClicked = true;
				Log.i("POLYGON CLICKLENDI", "IF'IN DISI");
				if (polygon.getTag().equals("nanotam")) {
					Log.i("POLYGON CLICKLENDI", "IF'IN ICI");
					nanotamPolygon.remove();
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(39.866855, 32.747324))      // Sets the center of the map to Mountain View
							.zoom(19)                  // Sets the zoom
							.bearing(180f)                // Sets the orientation of the camera to east
							//.tilt(30)                // Sets the tilt of the camera to 30 degrees
							.build();                  // Creates a CameraPosition from the builder
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			}
		});

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getUserLocation();
				polygonClicked = false;
				addPolygon("nanotam" );
			}
		});

		//GETTING USERS LOCATION
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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
					//mMap.addMarker(new MarkerOptions().position(userLocation).title("Marker in Turkey"));
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(userLocation)      // Sets the center of the map to Mountain View
							.zoom(16)                  // Sets the zoom
							.bearing(location.getBearing())                // Sets the orientation of the camera to east
							//.tilt(30)                // Sets the tilt of the camera to 30 degrees
							.build();                  // Creates a CameraPosition from the builder
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
				nanotamMarker.showInfoWindow();
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
				if ( marker.getTag().equals("nanotamMarker") ) {
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(39.866855, 32.747324))      // Sets the center of the map to Mountain View
							.zoom(19)                  // Sets the zoom
							.bearing(180f)                // Sets the orientation of the camera to east
							//.tilt(30)                // Sets the tilt of the camera to 30 degrees
							.build();                  // Creates a CameraPosition from the builder
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					nanotamPolygon.remove();
				}
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

		// Parking slot ground overlay listener
		FirebaseDatabase.getInstance().getReference().child("parkingdata/slots")
				.addChildEventListener(new ChildEventListener() {
					@Override
					public void onChildChanged(DataSnapshot dataSnapshot, String s) {
						// repaint. getParkingSpots();
					}

					@Override
					public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

					@Override
					public void onChildRemoved(DataSnapshot dataSnapshot) {}

					@Override
					public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

					@Override
					public void onCancelled(DatabaseError databaseError) {}
				});
	}
}
