package com.example.appointmentbooker.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.appointmentbooker.Models.Appointment;
import com.example.appointmentbooker.Models.LoginUserModel;
import com.example.appointmentbooker.Models.SignupUserModel;
import com.example.appointmentbooker.Models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Appointment.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users(last_name TEXT, first_name TEXT, email TEXT PRIMARY KEY, password TEXT, phone TEXT, role INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_image(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, image BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS appointments(start_datetime TEXT PRIMARY KEY, period INTEGER, end_datetime TEXT, title TEXT, booked_email TEXT, service_name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS appointments");
    }


    //Account database manipulation
    public Boolean signup(SignupUserModel user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("last_name", user.getLastName());
        values.put("first_name", user.getFirstName());
        values.put("email", user.getEmail());
        String hashedPass = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
        values.put("password", hashedPass);
        values.put("phone", user.getPhone());
        values.put("role", 1);
        try {
            long result = db.insert("users", null, values);
            return result != -1;
        } catch (Exception e){
            return false;
        }

    }

    public int[] login(LoginUserModel user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int[] resultArray = new int[2];
        resultArray[0] = 0;
        resultArray[1] = 0;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{user.getEmail()});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int passwordColumnIndex = cursor.getColumnIndex("password");
                String hashedPassword = cursor.getString(passwordColumnIndex);
                BCrypt.Result result = BCrypt.verifyer().verify(user.getPassword().toCharArray(), hashedPassword);
                resultArray[0] = result.verified==true?1:0;
                resultArray[1] = cursor.getInt((int)cursor.getColumnIndex("role"));
                return resultArray;
            }
            return resultArray;
        } finally {
            cursor.close();
        }
    }

    public Boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email})) {

            if (cursor.getCount() > 0) {
                return true;
            }
        }
        return false;
    }
    public Boolean checkPictureExists(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM user_image WHERE email = ?", new String[]{email})) {

            if (cursor.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    public User getUserInfo(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        User u = new User();
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                u.setEmail(email);
                u.setFirstName(cursor.getString((int)cursor.getColumnIndex("first_name")));
                u.setLastName(cursor.getString((int)cursor.getColumnIndex("last_name")));
                u.setPhone(cursor.getString((int)cursor.getColumnIndex("phone")));
                u.setPassword("");
                return u;
            }
            return u;
        } finally {
            cursor.close();
        }
    }

    public boolean updateUserInfo(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put("phone", user.getPhone());
        int result = db.update("users", values, "email" + "=?", new String[]{user.getEmail()});
        db.close();
        return result != -1;
    }

    public boolean addProfilePicture(String email, byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("image", image);
        if(checkPictureExists(email)){
            int result = db.update("user_image", values, "email" + "=?", new String[]{email});
            return result != -1;
        }
        long result = db.insert("user_image", null, values);
        db.close();
        return result!= -1;
    }

    public byte[] getProfilePicture(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        byte[] imageByte = null;
        try {
            cursor = db.rawQuery("SELECT * FROM user_image WHERE email = ?", new String[]{email});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                imageByte = cursor.getBlob((int)cursor.getColumnIndex("image"));
                return imageByte;
            }
            return imageByte;
        } finally {
            cursor.close();
        }
    }

    //Appointment
    public Boolean addAppointment(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("start_datetime", appointment.getStartDatetime());
        values.put("period", appointment.getPeriod());
        values.put("end_datetime", appointment.getEndDatetime());
        values.put("title", appointment.getTitle());
        values.put("booked_email", appointment.getBookedEmail());
        values.put("service_name", appointment.getServiceName());

        long result = db.insert("appointments", null, values);
        return result != -1;
    }

    public Boolean bookAppointment(Date startDatetime, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("booked_email", email);
        long result = db.update("appointments", values, "start_datetime", new String[]{startDatetime.toString()});
        return result != -1;
    }


    public List<Appointment> listAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM appointments", new String[]{});
            if (cursor != null) {
                if (cursor.moveToFirst()) do {
                    Appointment appointment = new Appointment();
                    appointment.setStartDatetime(cursor.getString((int)cursor.getColumnIndex("start_datetime")));
                    appointment.setPeriod(cursor.getInt((int)cursor.getColumnIndex("period")));
                    appointment.setEndDatetime(cursor.getString((int)cursor.getColumnIndex("end_datetime")));
                    appointment.setTitle(cursor.getString((int)cursor.getColumnIndex("title")));
                    appointment.setBookedEmail(cursor.getString((int)cursor.getColumnIndex("booked_email")));
                    appointment.setServiceName(cursor.getString((int)cursor.getColumnIndex("service_name")));
                    appointments.add(appointment);
                } while (cursor.moveToNext());
            }
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
        return appointments;
    }

    public List<Appointment> filterAppointment(String col, String data) {
        List<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        db.beginTransaction();
        try {
            cursor = db.rawQuery("SELECT * FROM appointments WHERE ? = ?", new String[]{col, data});
            if(cursor!=null){
                if(cursor.moveToFirst()) {
                    do {
                        Appointment appointment = new Appointment();
                        appointment.setStartDatetime(cursor.getString((int)cursor.getColumnIndex("start_datetime")));
                        appointment.setPeriod(cursor.getInt((int)cursor.getColumnIndex("period")));
                        appointment.setEndDatetime(cursor.getString((int)cursor.getColumnIndex("end_datetime")));
                        appointment.setTitle(cursor.getString((int)cursor.getColumnIndex("title")));
                        appointment.setBookedEmail(cursor.getString((int)cursor.getColumnIndex("booked_email")));
                        appointment.setServiceName(cursor.getString((int)cursor.getColumnIndex("service_name")));
                        appointments.add(appointment);
                    }while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
        return appointments;
    }


}
