package com.oman.sayakil.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;


public class SharedPreferencesManager {

    private static final String APP_PREFS = "AppPrefsFile";
    private static final String FIRST_NAME_KEY = "firstName";
    private static final String LAST_NAME_KEY = "lastName";
    private static final String DATE_BIRTH_KEY = "dateBirth";
    private static final String PHONE_NUMBER_KEY = "phoneNumber";
    private static final String SECOND_PHONE_NUMBER_KEY = "secndphoneNumber";
    private static final String EMAIL_KEY = "email";
    private static final String CONFORM_EMAIL_KEY = "conformemail";
    private static final String L1_KEY = "l1Address";
    private static final String L2_KEY = "l2Address";
    private static final String ZIPCODE_KEY = "zipcode";
    private static final String PROVINCCE_KEY = "province";
    private static final String GENDER_KEY = "gender";
    private static final String CREDIT_CARD_KEY = "card";
    private static final String EXPIRY_DATE_KEY = "expiry";
    private static final String SECURITY_CODE_KEY = "scode";
    private static final String CARD_HOLDER_NAME_KEY = "cardHolder";

    private static final String WELCOME_KEY = "first_time";


    private SharedPreferences sharedPrefs;
    private static SharedPreferencesManager instance;


    private SharedPreferencesManager(Context context) {
        sharedPrefs = context.getApplicationContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }


    public static synchronized SharedPreferencesManager getInstance(Context context) {

        if (instance == null)
            instance = new SharedPreferencesManager(context);

        return instance;
    }

    public void setFirstName(String firstName) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(FIRST_NAME_KEY, firstName);
        editor.apply();
    }

    public String getFirstName() {
        return sharedPrefs.getString(FIRST_NAME_KEY, "name");
    }

    public void setLastName(String lastName) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(LAST_NAME_KEY, lastName);
        editor.apply();
    }

    public String getLastName() {
        return sharedPrefs.getString(LAST_NAME_KEY, "last");
    }

    public void setDateBirth(String dateBirth) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(DATE_BIRTH_KEY, dateBirth);
        editor.apply();
    }

    public String getDateBirth() {
        return sharedPrefs.getString(DATE_BIRTH_KEY, "12/23");
    }

    public void setPhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PHONE_NUMBER_KEY, phoneNumber);
        editor.apply();
    }

    public String getPhoneNumber() {
        return sharedPrefs.getString(PHONE_NUMBER_KEY, "123");
    }

    public void setSecondPhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(SECOND_PHONE_NUMBER_KEY, phoneNumber);
        editor.apply();
    }

    public String getSecondPhoneNumber() {
        return sharedPrefs.getString(SECOND_PHONE_NUMBER_KEY, "123");
    }

    public void setEmail(String email) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(EMAIL_KEY, email);
        editor.apply();
    }

    public String getEmail() {
        return sharedPrefs.getString(EMAIL_KEY, "abc@gmail.com");
    }

    public void setConformEmail(String conformEmail) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(CONFORM_EMAIL_KEY, conformEmail);
        editor.apply();
    }

    public String getConformEmail() {
        return sharedPrefs.getString(CONFORM_EMAIL_KEY, "abc@gmail.com");
    }

    public void setL1(String l1) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(L1_KEY, l1);
        editor.apply();
    }

    public String getL1() {
        return sharedPrefs.getString(L1_KEY, "this is primary address");
    }

    public void setL2(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(L2_KEY, phoneNumber);
        editor.apply();
    }

    public String getL2() {
        return sharedPrefs.getString(L2_KEY, "this is secondary address");
    }

    public void setZipcode(String  zipcode) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(ZIPCODE_KEY, zipcode);
        editor.apply();
    }

    public String getZipcode() {
        return sharedPrefs.getString(ZIPCODE_KEY, "0");
    }

    public void setProvincce(String provincce) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PROVINCCE_KEY, provincce);
        editor.apply();
    }

    public String getProvince() {
        return sharedPrefs.getString(PROVINCCE_KEY, "ABC");
    }

    public void setGender(int position) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(GENDER_KEY, position);
        editor.apply();
    }

    public int getGender() {
        return sharedPrefs.getInt(GENDER_KEY, 1);
    }


    public void setCreditCard(String number) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(CREDIT_CARD_KEY, number);
        editor.apply();
    }

    public String getCreditCard() {
        return sharedPrefs.getString(CREDIT_CARD_KEY, "1234");
    }

    public void setExpiryDate(String expiryDate) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(EXPIRY_DATE_KEY, expiryDate);
        editor.apply();
    }

    public String getExpiryDate() {
        return sharedPrefs.getString(PROVINCCE_KEY, "11/20");
    }

    public void setSecurityCode(String securityCode) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(SECURITY_CODE_KEY, securityCode);
        editor.apply();
    }

    public String getSecurityCode() {
        return sharedPrefs.getString(SECURITY_CODE_KEY, "123");
    }

    public void setCardHolderName(String cardHolderName) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(CARD_HOLDER_NAME_KEY, cardHolderName);
        editor.apply();
    }

    public String getCardHolderName() {
        return sharedPrefs.getString(CARD_HOLDER_NAME_KEY, "ABC");
    }

    public void setWelcome(boolean isFirstTime) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(WELCOME_KEY, isFirstTime);
        editor.apply();
    }

    public boolean getWelcome() {
        return sharedPrefs.getBoolean(WELCOME_KEY, false);
    }
}
