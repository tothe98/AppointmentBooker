package com.example.appointmentbooker;

import static android.app.Activity.RESULT_OK;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appointmentbooker.Models.User;
import com.example.appointmentbooker.Utils.DatabaseHelper;
import com.example.appointmentbooker.Utils.ImageHelper;

public class AccountFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String mParam1, mParam2;
    DatabaseHelper databaseHelper;

    EditText emailText, lNameText, fNameText, phoneText;
    Button saveBtn, logoutBtn;

    ImageView imageView;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public AccountFragment() {

    }
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailText = view.findViewById(R.id.editTextEmail);
        lNameText = view.findViewById(R.id.editTextLName);
        fNameText = view.findViewById(R.id.editTextFName);
        phoneText = view.findViewById(R.id.editTextPhone);
        imageView = view.findViewById(R.id.imageView);
        saveBtn = view.findViewById(R.id.buttonSave);
        logoutBtn = view.findViewById(R.id.buttonLogout);
        databaseHelper = new DatabaseHelper(getContext());

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        User user = databaseHelper.getUserInfo(email);
        if(!email.isEmpty()){
            emailText.setText(email);
        }
        if(!user.getFirstName().isEmpty()){
            lNameText.setText(user.getLastName());
            fNameText.setText(user.getFirstName());
            phoneText.setText(user.getPhone());
        }
        loadImageFromDatabase(email);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("isLogged", "false");
                editor.putString("email", "");
                editor.apply();
                Intent i = new Intent(getContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
                getActivity().finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setFirstName(fNameText.getText().toString());
                user.setLastName(lNameText.getText().toString());
                user.setPhone(phoneText.getText().toString());
                boolean updated = databaseHelper.updateUserInfo(user);
                if(updated){
                    Toast.makeText(getContext(), R.string.successful_save_userinfo, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.unsuccessful_save_userinfo, Toast.LENGTH_SHORT).show();
                }
            }
        });



        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Bundle extras = data.getExtras();
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                imageView.setImageBitmap(imageBitmap);
                                boolean uploaded = databaseHelper.addProfilePicture(email, ImageHelper.convertBitmapToByteArray(imageBitmap));
                                if(uploaded){
                                    Toast.makeText(getContext(), R.string.successful_upload_picture, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), R.string.unsuccessful_upload_picture, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        takePicture();
                    }
                });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
    }

    private void loadImageFromDatabase(String email) {
        byte[] imageBytes = databaseHelper.getProfilePicture(email);
        if (imageBytes != null) {
            Bitmap bitmap = ImageHelper.convertByteArrayToBitmap(imageBytes);
            imageView.setImageBitmap(bitmap);
            imageView.setMinimumHeight(500);
            imageView.setMinimumWidth(500);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        }
    }
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            takePicture();
        }
    }





}