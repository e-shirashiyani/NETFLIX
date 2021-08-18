package com.example.netflix.utilities;

import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BindingAdapter {

    @androidx.databinding.BindingAdapter("android:imageURL")
    public static void setImageURL(ImageView imageView,String URl){
        try {
            imageView.setAlpha(0f);
            Picasso.get().load(URl).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    imageView.animate().setDuration(300).alpha(1f).start();
                }

                @Override
                public void onError(Exception e) {

                }
            });

        }catch (Exception e){

        }
    }

}
