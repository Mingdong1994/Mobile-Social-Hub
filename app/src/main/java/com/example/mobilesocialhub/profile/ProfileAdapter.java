package com.example.mobilesocialhub.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilesocialhub.R;
import com.example.mobilesocialhub.databinding.ActivityFolderchildBinding;
import com.example.mobilesocialhub.eventcard.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;

import java.util.List;
import java.util.Locale;


public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>  {

    private List<Event> event;
    final String TAG="Click";
    private boolean flag;
    private Context context;


    public ProfileAdapter(List<Event> event) {
        this.event = event;
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder{

        private ActivityFolderchildBinding binding;
        public ProfileViewHolder(@NonNull ActivityFolderchildBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }



    @NonNull
    @Override
    public ProfileAdapter.ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ActivityFolderchildBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.activity_folderchild,parent,false);
        return new ProfileViewHolder(binding);
    }



    @Override
    public void onBindViewHolder(@NonNull final ProfileAdapter.ProfileViewHolder holder, int position) {
        holder.binding.usernamePublished.setText(event.get(position).getUsernamePublished());
        holder.binding.setEvent(event.get(position));
        //here change to the activity name
        setPicture(event.get(position).getActivityName(), holder);

        holder.binding.datePublished.setText(event.get(position).getDatePublished());
//        holder.binding.activity.setImageDrawable(event.get(position).getActivity());
        holder.binding.eventDate.setText(event.get(position).getEventDate());
        holder.binding.eventTime.setText(event.get(position).getEventTime());
        holder.binding.address.setText(event.get(position).getAddress());
        holder.binding.joinImage.setVisibility(View.INVISIBLE);
        holder.binding.elip.setVisibility(View.INVISIBLE);
//        holder.binding.elip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.w(TAG,"Test Click"+String.valueOf(holder.getAdapterPosition()));
//                Intent intent = new Intent(view.getContext(), MainActivity.class);
//                intent.putExtra("address",holder.binding.getEvent().getId());
//                view.getContext().startActivity(intent);
//            }
//        });
        holder.binding.gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(context, "Please check the GPS permission is opened", Toast.LENGTH_LONG).show();
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    //获取当前位置，这里只用到了经纬度
                    String latitude = String.format(Locale.ENGLISH,"%.5f", location.getLatitude());
                    String longitude = String.format(Locale.ENGLISH, "%.5f", location.getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?"
                                    + "saddr="+ latitude+ "," + longitude
                                    + "&daddr="  + holder.binding.address.getText().toString())
                    );
                    intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Cannot get the GPS location", Toast.LENGTH_LONG).show();
                }
            }
        });

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String username = holder.binding.usernamePublished.getText().toString();
        storageRef.child("images/"+ username + "-head.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                try {
                    Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                    Tiny.getInstance().source(uri).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                        @Override
                        public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                            holder.binding.profileImage.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    //"上传失败");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



    }

    @Override
    public int getItemCount() {
        return event.size();
    }



//    public interface OnElipsClick{
//        public void onelipsclick(View view);
//    }

    public void setPicture(String activityName, ProfileAdapter.ProfileViewHolder holder){
        switch (activityName){
            case "sport":
                holder.binding.activity.setImageResource(R.drawable.sport);
                break;

            case "date":
                holder.binding.activity.setImageResource(R.drawable.dating);
                break;

            case "dining":
                holder.binding.activity.setImageResource(R.drawable.restaurant);
                break;
            case "work":
                holder.binding.activity.setImageResource(R.drawable.work);
                break;
            case "coffee":
                holder.binding.activity.setImageResource(R.drawable.coffee);
                break;
            case "shopping":
                holder.binding.activity.setImageResource(R.drawable.shopping);
                break;
        }
    }
}

