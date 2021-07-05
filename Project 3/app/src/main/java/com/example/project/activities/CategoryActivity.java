package com.example.project.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.project.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity{

    private DrawerLayout drawer;
    private CustomAdaptorMain customAdaptorMain;
    private ListView mainListView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String categorySelected;

    ArrayList<String> names_main = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(firebaseUser.getUid());


        // Set action bar title
        setTitle("Dashboard");


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass user = snapshot.getValue(UserHelperClass.class);
                names_main = new ArrayList<String>(user.getCategories().keySet());
                names_main.remove("Dummy Category");
                customAdaptorMain.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass user = snapshot.getValue(UserHelperClass.class);
                names_main = new ArrayList<String>(user.getCategories().keySet());
                names_main.remove("Dummy Category");
                customAdaptorMain.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mainListView = (ListView) findViewById(R.id.listViewMain);

        customAdaptorMain = new CustomAdaptorMain();
        mainListView.setAdapter(customAdaptorMain);






        // On clicking the category
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categorySelected = names_main.get(i);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissions();
                }
            }
        });








        // On long press of category
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CategoryActivity.this);
                bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_category);
                bottomSheetDialog.setCanceledOnTouchOutside(true);


                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bottom_sheet_category,
                        (ViewGroup) findViewById(R.id.bottomSheetContainerCategory));

                TextView categoryName = (TextView) sheetView.findViewById(R.id.categoryName);
                categoryName.setText(names_main.get(position));

                        sheetView.findViewById(R.id.delete_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog(names_main.get(position));
                        bottomSheetDialog.dismiss();
                    }
                });

                sheetView.findViewById(R.id.editNameLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditNameDialog(names_main.get(position));
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
                return true;
            }
        });
    }


    // Navigation bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.dashboard:
                startActivity(new Intent(CategoryActivity.this, CategoryActivity.class));
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.toDo:
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.delaroystudios.alarmreminder");
                if(launchIntent != null)
                    startActivity(launchIntent);
                else
                    Toast.makeText(this, "Launch intent is null", Toast.LENGTH_SHORT).show();
                break;
            case R.id.notes:
                startActivity(new Intent(CategoryActivity.this, NotesMainActivity.class));
                break;
            case R.id.profile:
                startActivity(new Intent(CategoryActivity.this, ProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Logout Implementation
    private void logout(){
        FirebaseAuth.getInstance().signOut();
        //signOut();
        // revokeAccess();
        finish();
        startActivity(new Intent(CategoryActivity.this, Login.class));
    }














    // Dialog when add category is clicked
    public void btn_showDialogueMain(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(CategoryActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.new_category, null);
        final EditText txt_inputText = (EditText) mView.findViewById(R.id.category_input);
        Button btn_cancel = (Button) mView.findViewById(R.id.btn_cancel);
        Button btn_create = (Button) mView.findViewById(R.id.btn_create);

        alert.setView(mView);

        final AlertDialog alertDialogue = alert.create();
        alertDialogue.setCanceledOnTouchOutside(false);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogue.dismiss();
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newCategoryName = txt_inputText.getText().toString().trim();
                alertDialogue.dismiss();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Category newCategory = new Category(newCategoryName, null);
                        UserHelperClass user = snapshot.getValue(UserHelperClass.class);

                        Map<String, Category> categories = user.getCategories();
                        categories.put(newCategory.getName(), newCategory);

                        user.setCategories(categories);

                        databaseReference.setValue(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        alertDialogue.show();
    }


    //Custom Adaptor for the category
    class CustomAdaptorMain extends BaseAdapter {

        @Override
        public int getCount() {
            return names_main.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.category_item_view, null);
            TextView mTextView = view.findViewById(R.id.textViewMain);
            View layout = (View) view.findViewById(R.id.categoryLayout);
            if(position % 5 == 4){
//                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor("#FDC830"), Color.parseColor("#F37335")});
                layout.setBackgroundResource(R.drawable.list_view_style_1);

            }
            else if(position % 5 == 1){
//                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor("#B3FFAB"), Color.parseColor("#12FFF7")});
                layout.setBackgroundResource(R.drawable.list_view_style_2);
            }
            else if(position % 5 == 2){
//                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor("#4776E6"), Color.parseColor("#8E54E9")});
                layout.setBackgroundResource(R.drawable.list_view_style_3);
            }
            else if(position % 5 == 3){
//                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor("#1488CC"), Color.parseColor("#2B32B2")});
                layout.setBackgroundResource(R.drawable.list_view_style_4);
            }
            else if(position % 5 == 0){
//                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor("#1488CC"), Color.parseColor("#2B32B2")});
                layout.setBackgroundResource(R.drawable.list_view_style_5);
            }
            mTextView.setText(names_main.get(position));
            return view;
        }
    }




    // Alert Dialog when Edit Name is clicked
    public void showEditNameDialog(String categorySelected) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.edit_name, null);

        Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        EditText edtTxtNewName = (EditText) view.findViewById(R.id.edtTxt_newName);
        edtTxtNewName.setText(categorySelected);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        alertDialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = edtTxtNewName.getText().toString();
                if(newName.isEmpty()){
                    Toast.makeText(CategoryActivity.this, "Enter a valid name", Toast.LENGTH_SHORT).show();
                }
                else {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserHelperClass user = snapshot.getValue(UserHelperClass.class);
                            Map<String, Category> categories = user.getCategories();
                            Category newCategory = new Category(newName, categories.get(categorySelected).getItems());
                            categories.remove(categorySelected);
                            categories.put(newCategory.getName(), newCategory);
                            user.setCategories(categories);
                            databaseReference.setValue(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Toast.makeText(CategoryActivity.this, "Name changed to " + newName, Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }










    // Alert Dialog when Delete is clicked
    public void showDeleteDialog(String categorySelected) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.delete_category, null);

        Button btnConfirm = (Button)view.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button)view.findViewById(R.id.btn_cancel);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();

        alertDialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserHelperClass user = snapshot.getValue(UserHelperClass.class);
                        Map<String, Category> categories = user.getCategories();
                        categories.remove(categorySelected);
                        user.setCategories(categories);
                        databaseReference.setValue(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




                Toast.makeText(CategoryActivity.this, categorySelected + " Deleted", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }



    // Checking for permissions
    private void checkPermissions(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);

        }
        else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("categorySelected", categorySelected);
            startActivity(intent);
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1052: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission was granted", Toast.LENGTH_SHORT).show();
                    // permission was granted.
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("categorySelected", categorySelected);
                    startActivity(intent);

                } 
                else {

                    Toast.makeText(this, "Cannot proceed without permissions", Toast.LENGTH_SHORT).show();
                    // Permission denied - Show a message to inform the user that this app only works
                    // with these permissions granted

                }
                return;
            }

        }
    }
}
