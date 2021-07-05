package com.example.project.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final int PICK_IMAGE = 1;
    CustomAdaptor customAdaptor;
    ListView mListView;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient mGoogleSignInClient;
    String categorySelected;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String selectedItem;

    ArrayList<Integer> images =new ArrayList<>(Arrays.asList(R.drawable.images,R.drawable.images,R.drawable.images));
    ArrayList<String> names =new ArrayList<>(Arrays.asList("Name", "Name", "Name"));
    ArrayList<Item> itemsMain = new ArrayList<>();
    // On App Start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        categorySelected = getIntent().getExtras().getString("categorySelected");
        Log.d("categorySelected",categorySelected);





        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users").child(firebaseUser.getUid()).child("categories").child(categorySelected);

        // Set action bar title
        setTitle(categorySelected);

        // Data change
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Category category = snapshot.getValue(Category.class);
                if(category.getItems() == null){
//                    names = new ArrayList<>();
//                    images = new ArrayList<>();
                      itemsMain = new ArrayList<>();
                }
                else{
                    itemsMain = new ArrayList<>(category.getItems().values());
                }
                customAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Category category = snapshot.getValue(Category.class);
                if(category == null)
                    return;
                if(category.getItems() == null){
                    itemsMain = new ArrayList<>();
                }
                else{
                    itemsMain = new ArrayList<>(category.getItems().values());
                }
                customAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if(signInAccount != null){
//            Toast.makeText(this, "Google Sign Successful", Toast.LENGTH_SHORT).show();
//        }









        //_________________________________________________
        // Showing a bottom sheet on long click on an item
        //_________________________________________________

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_item);
                bottomSheetDialog.setCanceledOnTouchOutside(true);

                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bottom_sheet_item,
                        (ViewGroup) findViewById(R.id.bottomSheetContainer));


                // Setting the text in the bottom sheet
                TextView txtName = sheetView.findViewById(R.id.txtName);
                txtName.setText(itemsMain.get(position).getName());
                selectedItem = itemsMain.get(position).getName();
                TextView quantityBottomSheet = sheetView.findViewById(R.id.quantityBottomSheet);

                databaseReference.child("items").child(selectedItem).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Item item = snapshot.getValue(Item.class);
                        quantityBottomSheet.setText(Integer.toString(item.getQuantity()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//                databaseReference.child("items").child(selectedItem).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Item item = snapshot.getValue(Item.class);
//                        quantityBottomSheet.setText(Integer.toString(item.getQuantity()));
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });



                // Setting the image in the bottom sheet if it is already selected
                if(!itemsMain.get(position).getImagePath().equals("randomPath")) {
                    ImageView imageView = sheetView.findViewById(R.id.imageView);
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(Uri.parse(itemsMain.get(position).getImagePath()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    selectedImage = getResizedBitmap(selectedImage, 400);
                    imageView.setImageBitmap(selectedImage);
                }
                else{
                    ImageView imageView = sheetView.findViewById(R.id.imageView);
                    imageView.setImageResource(R.drawable.default_image);
                }

                sheetView.findViewById(R.id.delete_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog();
                        bottomSheetDialog.dismiss();
                    }
                });

                sheetView.findViewById(R.id.editImageLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditImageDialog();
                        bottomSheetDialog.dismiss();
                    }
                });

                sheetView.findViewById(R.id.editNameLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditNameDialog();
                        bottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareImage(itemsMain.get(position).getImagePath());
                    }
                });

                sheetView.findViewById(R.id.btnPlus).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference itemReference = databaseReference.child("items").child(selectedItem);
                        itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Item currentItem = snapshot.getValue(Item.class);
                                int quantity = currentItem.getQuantity();
                                currentItem.setQuantity(currentItem.getQuantity() + 1);
                                quantityBottomSheet.setText(Integer.toString(quantity + 1));
                                itemReference.setValue(currentItem);
                                }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                sheetView.findViewById(R.id.btnMinus).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    DatabaseReference itemReference = databaseReference.child("items").child(selectedItem);
                    itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Item currentItem = snapshot.getValue(Item.class);

                            if(currentItem.getQuantity() == 1){
                                Toast.makeText(MainActivity.this, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                int quantity = currentItem.getQuantity();
                                currentItem.setQuantity(currentItem.getQuantity() - 1);
                                quantityBottomSheet.setText(Integer.toString(quantity-1));
                                itemReference.setValue(currentItem);
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
                return false;
            }
        });

        customAdaptor = new CustomAdaptor();
        mListView.setAdapter(customAdaptor);
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
                startActivity(new Intent(MainActivity.this, CategoryActivity.class));
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
                startActivity(new Intent(MainActivity.this, NotesMainActivity.class));
                break;
            case R.id.profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // Logout function
    private void logout(){
        FirebaseAuth.getInstance().signOut();
        //signOut();
        // revokeAccess();
        finish();
        startActivity(new Intent(MainActivity.this, Login.class));
    }





    //___________________________________
    // Alert dialog to create a new item
    //___________________________________

    public void btn_showDialogue(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.new_item, null);
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

                String newItemName = txt_inputText.getText().toString().trim();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Category category = snapshot.getValue(Category.class);
                        Item item = new Item(newItemName, "randomPath", 1);
                        Map<String, Item> items = category.getItems();
                        if(items == null)
                            items = new HashMap<>();
                        items.put(item.getName(), item);
                        category.setItems(items);
                        databaseReference.setValue(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                alertDialogue.dismiss();

            }
        });

        alertDialogue.show();
    }










    //______________________
    // Custom Adapter Class
    //______________________

    class CustomAdaptor extends BaseAdapter{

        @Override
        public int getCount() {
            return itemsMain.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item_view, null);
            View listRelativeLayout = view.findViewById(R.id.listRelativeLayout);
            ImageView mImageView = (ImageView) view.findViewById(R.id.imageView);
            TextView mTextView = view.findViewById(R.id.textView);
            TextView quanitityListItem = view.findViewById(R.id.quantityListItem);

            if(itemsMain.get(position).getImagePath().equals("randomPath"))
                mImageView.setImageResource(R.drawable.default_image);
            else
            {
                Uri imageUri = Uri.parse(itemsMain.get(position).getImagePath());
                try {
                    ContentResolver cr = getContentResolver();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    System.out.println("imageUri: "+imageUri);
                    System.out.println("inputStream:" + imageStream.toString());
                    Bitmap selectedImage = BitmapFactory.decodeStream(cr.openInputStream(imageUri));
                    selectedImage = getResizedBitmap(selectedImage, 400);
                    mImageView.setImageBitmap(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mTextView.setText(itemsMain.get(position).getName());
            quanitityListItem.setText(Integer.toString(itemsMain.get(position).getQuantity()));
            return view;
        }

    }








    //_________________________________________
    // Alert dialog when edit name is clicked
    //_________________________________________

    public void showEditNameDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.edit_name, null);

        Button btnConfirm = (Button)view.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button)view.findViewById(R.id.btn_cancel);
        EditText edtTxtNewName = (EditText)view.findViewById(R.id.edtTxt_newName);
        edtTxtNewName.setText(selectedItem);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        alertDialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = edtTxtNewName.getText().toString();
                if(newName.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter a valid name", Toast.LENGTH_SHORT).show();
                }
                else {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Category category = snapshot.getValue(Category.class);
                            Map<String, Item> items = category.getItems();
                            Item newItem = new Item(newName, items.get(selectedItem).imagePath, items.get(selectedItem).getQuantity());
                            items.remove(selectedItem);
                            items.put(newItem.getName(), newItem);
                            category.setItems(items);
                            databaseReference.setValue(category);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Toast.makeText(MainActivity.this, "Name changed to " + newName, Toast.LENGTH_SHORT).show();
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









    //________________________________________
    // Alert dialog when edit image is clicked
    //________________________________________

    public void showEditImageDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.edit_image, null);

        Button btnBrowse= (Button)view.findViewById(R.id.btn_browse);
        Button btnCancel = (Button)view.findViewById(R.id.btn_cancel);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();

        alertDialog.show();

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView ProfileImage;
                Uri imageUri;
                Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                //gallery.setAction(Intent.CATEGORY_OPENABLE);
                gallery.setType("image/*");
                //gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Choose Image"), PICK_IMAGE);
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









    //_____________________________________
    // Alert dialog when delete is clicked
    //_____________________________________

    public void showDeleteDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.delete_item, null);

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
                        Category category = snapshot.getValue(Category.class);
                        Map<String, Item> items = category.getItems();
                        items.remove(selectedItem);
                        category.setItems(items);
                        databaseReference.setValue(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
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





    
    
    // Quantity of the item is changed
    private void quantityChanged(String btnSelected, TextView quantityBottomSheet){
        DatabaseReference itemReference = databaseReference.child("items").child(selectedItem);
        itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Item currentItem = snapshot.getValue(Item.class);

                if(btnSelected.equals("Plus")){
                        currentItem.setQuantity(currentItem.getQuantity() + 1);
                        quantityBottomSheet.setText(currentItem.getQuantity()+1);
                        itemReference.setValue(currentItem);
                }
                else{
                    if(currentItem.getQuantity() == 1){
                        Toast.makeText(MainActivity.this, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        currentItem.setQuantity(currentItem.getQuantity() - 1);
                        quantityBottomSheet.setText(currentItem.getQuantity()-1);
                        itemReference.setValue(currentItem);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    //____________________________________________________________________
    // Code for adding images from gallery and sharing images
    //____________________________________________________________________
        private void shareImage(String imageUri) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            BitmapDrawable drawable;
            Bitmap bitmap;
            try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.parse(imageUri));
                drawable = (BitmapDrawable) Drawable.createFromStream(inputStream, imageUri.toString() );
            } catch (FileNotFoundException e) {
                drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.images);
            }
            bitmap = drawable.getBitmap();
            File file = new File(getExternalCacheDir() + "/" + "Beautiful Picture" + ".png");
            Intent intent;
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
            startActivity(Intent.createChooser(intent, "Share Image Via"));
        }

        public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
            int width = image.getWidth();
            int height = image.getHeight();

            float bitmapRatio = (float)width / (float) height;
            if (bitmapRatio > 1) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
            return Bitmap.createScaledBitmap(image, width, height, true);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            Uri imageUri;
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
                Toast.makeText(this, selectedItem, Toast.LENGTH_LONG);
                imageUri = data.getData();
                DatabaseReference itemReference = databaseReference.child("items").child(selectedItem);
                itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int takeFlags = getIntent().getFlags();
                        getContentResolver().takePersistableUriPermission(imageUri,Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        Item currentItem = snapshot.getValue(Item.class);
                        Item item = new Item(selectedItem,imageUri.toString(), currentItem.getQuantity());
                        itemReference.setValue(item);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

}