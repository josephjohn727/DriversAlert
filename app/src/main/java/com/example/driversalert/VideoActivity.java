package com.example.driversalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity implements OnRecyclerItemClickListener {

    private static final String TAG = "VideoActivity";

    private RecyclerView rvVideo;
    private AppCompatTextView tvEmpty;

    List<StorageReference> references = new ArrayList<>();
    VideoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        rvVideo = findViewById(R.id.rv_videos);
        tvEmpty = findViewById(R.id.tv_empty_label);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvVideo.addItemDecoration(new DividerItemDecoration(this, manager.getOrientation()));
        rvVideo.setLayoutManager(manager);
        mAdapter = new VideoAdapter(this);
        rvVideo.setAdapter(mAdapter);

        fileNames();
    }

    private void fileNames() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference videoReference = storageRef.child("videos");
        videoReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                references.addAll(listResult.getItems());
                tvEmpty.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void itemClicked(StorageReference reference) {

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Intent intent = new Intent(getApplicationContext(),PlayVideoActivity.class);
                intent.putExtra("uri_reference",uri);
                startActivity(intent);

            }
        });


    }
//"https://firebasestorage.googleapis.com/v0/b/drivers-alert-51fbe.appspot.com/o/videos%2F + "" + "?alt=media&token=8cd66417-5672-47a1-8e56-bf103e0b79e9"
    //

    //https://firebasestorage.googleapis.com/v0/b/drivers-alert-51fbe.appspot.com/o/videos%2Fcom.example.driversalert.video_url.mp4?alt=media&token=8cd66417-5672-47a1-8e56-bf103e0b79e9
    //https://firebasestorage.googleapis.com/v0/b/drivers-alert-51fbe.appspot.com/o/videos%2F11-12-2019%200%3A9%3A26.mp4?alt=media&token=9f42e651-99e6-471a-8a4d-c7b11ab3bae7
    //https://firebasestorage.googleapis.com/v0/b/drivers-alert-51fbe.appspot.com/o/videos%2F11-12-2019%2013%3A25%3A3.mp4?alt=media&token=6980f11b-df94-46c5-a2c5-b428866a022e
    //https://firebasestorage.googleapis.com/v0/b/drivers-alert-51fbe.appspot.com/o/videos%2F11-12-2019%201%3A52%3A13.mp4?alt=media&token=06fa8572-471c-46cc-b304-1356624c38ee
    class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

        private OnRecyclerItemClickListener listener;

        public VideoAdapter(OnRecyclerItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.single_video, parent, false);
            return new VideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
            holder.bind(references.get(position));
        }

        @Override
        public int getItemCount() {
            return references.size();
        }

        class VideoViewHolder extends RecyclerView.ViewHolder {

            private AppCompatTextView tvViewLabel;
            VideoViewHolder(@NonNull View itemView) {
                super(itemView);
                tvViewLabel = itemView.findViewById(R.id.tv_video_label);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.itemClicked(references.get(getLayoutPosition()));
                    }
                });
            }

            void bind(StorageReference reference) {
                tvViewLabel.setText(reference.getName());
            }
        }


    }


}
