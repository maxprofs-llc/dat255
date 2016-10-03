package se.chalmers.cid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import se.chalmers.cid.R;

public class MainActivity extends AppCompatActivity {

	private static final int RC_SIGN_IN = 9001;

	private static boolean persistenceEnabled = false;

	private FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAuth = FirebaseAuth.getInstance();
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
					usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							if (dataSnapshot.exists()) {
								// Show profile/list activity
							} else {
								// Show registration
							}

						}

						@Override
						public void onCancelled(DatabaseError databaseError) {
							Toast.makeText(MainActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					Intent signInIntent = AuthUI.getInstance()
						.createSignInIntentBuilder()
						.setProviders(
							AuthUI.FACEBOOK_PROVIDER,
							AuthUI.GOOGLE_PROVIDER)
						.build();
					startActivityForResult(signInIntent, RC_SIGN_IN);
				}
			}
		};

		// Temporary sign out button for testing purposes
		/*Button signOutButton = (Button) findViewById(R.id.button_sign_out);
		signOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AuthUI.getInstance().signOut(MainActivity.this);
			}
		});*/
	}

	@Override
	protected void onStart() {
		super.onStart();
		mAuth.addAuthStateListener(mAuthListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mAuth.removeAuthStateListener(mAuthListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN && resultCode == RESULT_CANCELED) {
			Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
		}
	}

}
