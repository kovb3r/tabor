package com.example.tabor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CampListActivity extends AppCompatActivity {

    private static final String LOG_TAG = CampListActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<Camp> mCampList;
    private CampAdapter mAdapter;

    private FrameLayout redCircle;
    private TextView contentTextView;

    private int gridNumber = 1;
    private int cartItems = 0;
    private int queryLimit = 10;

    private FirebaseFirestore mFirestore;
    private CollectionReference mCamps;
    private boolean viewRow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camp_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }



        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mCampList = new ArrayList<>();
        mAdapter = new CampAdapter(this, mCampList);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mCamps = mFirestore.collection("Camps");

        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReciever, filter);
    }

    BroadcastReceiver powerReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action == null)
                return;

            switch (action){
                case Intent.ACTION_POWER_CONNECTED:
                    queryLimit = 10;
                    queryData();
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    queryLimit = 5;
                    queryData();
                    break;
            }
            queryData();
        }
    };

    private void queryData() {
        mCampList.clear();

        //mCamps.whereEqualTo()...
        mCamps.orderBy("count", Query.Direction.DESCENDING).limit(queryLimit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Camp camp = document.toObject(Camp.class);
                camp.setId(document.getId());
                mCampList.add(camp);
            }

            if (mCampList.isEmpty()) {
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });

    }

    public void deleteCamp(Camp camp) {
        DocumentReference ref = mCamps.document(camp._getId());
        ref.delete()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Item is successfully deleted: " + camp._getId());
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + camp._getId() + " cannot be deleted.", Toast.LENGTH_LONG).show();
                });

        queryData();
        //mNotificationHelper.cancel();
    }


    private void initializeData() {
        String[] campList = getResources().getStringArray(R.array.camp_names);
        String[] campDescription = getResources().getStringArray(R.array.camp_desc);
        String[] campPrice = getResources().getStringArray(R.array.camp_price);;

        TypedArray campImageResources = getResources().obtainTypedArray(R.array.camp_images);
        TypedArray campRate = getResources().obtainTypedArray(R.array.camp_rates);


        for (int i = 0; i < campList.length; i++) {
            mCamps.add(new Camp(
                    campDescription[i],
                    campImageResources.getResourceId(i, 0),
                    campList[i],
                    campPrice[i],
                    campRate.getFloat(i, 0),
                    0
                    ));

        }

        campImageResources.recycle();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.camp_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.settings_button:
                Log.d(LOG_TAG, "Setting clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Cart clicked!");
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG, "View selector clicked!");
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(Camp camp) {
        cartItems = (cartItems + 1);
        if (0 < cartItems) {
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            contentTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        mCamps.document(camp._getId()).update("count", camp.getCount() + 1)
            .addOnFailureListener(fail -> {
                Toast.makeText(this, "Item " + camp._getId() + " cannot be updated.", Toast.LENGTH_LONG).show();
            });
        queryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerReciever);
    }
}