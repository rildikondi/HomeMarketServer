package com.akondi.homemarketserver.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.akondi.homemarketserver.model.Request;
import com.akondi.homemarketserver.model.User;
import com.akondi.homemarketserver.remote.APIService;
import com.akondi.homemarketserver.remote.FCMRetrofitClient;
import com.akondi.homemarketserver.remote.IGeoCoordinates;
import com.akondi.homemarketserver.remote.RetrofitClient;

public class Common {
    public static User currentUser;
    public static Request currentRequest;
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final String MORE_DETAILS = "More details";

    public static final String MAP_URL = "https://maps.googleapis.com/";
    private static final String FCM_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService() {
        return FCMRetrofitClient.getClient(FCM_URL).create(APIService.class);
    }

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "Shipping";
        else
            return "Shipped";
    }

    public static IGeoCoordinates getGeoCodeService() {
        return RetrofitClient.getClient(MAP_URL).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canavas = new Canvas(scaledBitmap);
        canavas.setMatrix(scaleMatrix);
        canavas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }
}
