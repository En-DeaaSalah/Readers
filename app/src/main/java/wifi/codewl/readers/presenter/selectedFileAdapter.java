package wifi.codewl.readers.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.transition.ArcMotion;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.Channel;
import wifi.codewl.readers.model.channel.Channel_File;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.play_list_details;
import wifi.codewl.readers.view.fragment.channel.Channel_Fragment;
import wifi.codewl.readers.view.fragment.channel.FilePage;

public class selectedFileAdapter extends RecyclerView.Adapter<selectedFileAdapter.ViewHolder> {

    Activity context;
    ArrayList<Model> AllFile;
    TextView tv_empty;
    boolean isEnable = false;
    boolean isSelectedAll = false;
    ArrayList<Model> SelectedFiles;

    mainViewModel mainViewModel;

    public selectedFileAdapter(Activity context,TextView tv_empty) {
        this.context = context;

        AllFile= FilePage.getFiles();
        this.tv_empty = tv_empty;
        SelectedFiles = new ArrayList<>();

        fetchFilesFromServer();

    }

    public void fetchFilesFromServer() {

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "all_files", response -> {


            try {

                JSONArray jsonArray = new JSONArray(response);


                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.has("check")) {

                        break;
                    }


                    Channel_File file = new Channel_File(jsonObject.getString("id"), jsonObject.getString("Title"), jsonObject.getString("Description")
                            , jsonObject.getString("image"), jsonObject.getString("PDF"), jsonObject.getString("created_at"),jsonObject.getInt("Count_videw"));




                    AllFile.add(file);






                }






            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fetch", e.getMessage());


            }


        }, error -> {
            Log.d("fetch", "error");



        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("id", User.getUserReference().getUserId());
                return map;
            }

        };
        requestQueue.add(request);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_file, parent, false);
        mainViewModel = ViewModelProviders.of((FragmentActivity) context).get(mainViewModel.class);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {




        getFileBackground((selectedFileAdapter.ViewHolder) holder, ((Channel_File) AllFile.get(holder.getAdapterPosition())).getFile_background());
        ((ViewHolder) holder).title_file.setText(((Channel_File) AllFile.get(holder.getAdapterPosition())).getTitle());
        ((ViewHolder) holder).owner_name.setText(User.getUserReference().getChannel().getChannel_profile().getChannelName());
        ((ViewHolder) holder).root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isEnable) {

                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            MenuInflater menuInflater = mode.getMenuInflater();
                            menuInflater.inflate(R.menu.selected_menu, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                            isEnable = true;
                            ClickItem(holder);

                            mainViewModel.getText().observe((LifecycleOwner) context, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    mode.setTitle(String.format("%s selected", s));
                                }
                            });
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {


                            int id = item.getItemId();
                            switch (id) {
                                case R.id.menu_delete: {

                                    for (Model file : SelectedFiles) {

                                        AllFile.remove(file);
                                    }


                                    if (AllFile.size() == 0) {


                                        tv_empty.setVisibility(View.VISIBLE);
                                    }

                                    notifyDataSetChanged();
                                    mode.finish();

                                    break;
                                }
                                case R.id.create_PlayList:{


                                    Intent intent=new Intent(context, play_list_details.class);
                                    intent.putExtra("play_list_file",SelectedFiles);
                                    context.startActivity(intent);
                                    context.finish();



                                    break;
                                }


                                case R.id.menu_file_selected_selectAll: {

                                    if (SelectedFiles.size() == AllFile.size()) {


                                        isSelectedAll = false;
                                        SelectedFiles.clear();

                                    } else {


                                        isSelectedAll = true;
                                        SelectedFiles.clear();

                                        SelectedFiles.addAll(AllFile);

                                    }

                                    mainViewModel.setText(String.valueOf(SelectedFiles.size()));
                                    notifyDataSetChanged();

                                    break;
                                }


                            }


                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {

                            isEnable = false;
                            isSelectedAll = false;
                            SelectedFiles.clear();
                            notifyDataSetChanged();

                        }
                    };


                    ((AppCompatActivity) v.getContext()).startActionMode(callback);

                } else {


                    ClickItem(holder);
                }

                return true;
            }
        });


        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(isEnable){

                    ClickItem(holder);
                }else{

                   // Toast.makeText(context, "You Clicked"+AllFile.get(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            }
        });



        if(isSelectedAll){



            holder.item_check_box.setVisibility(View.VISIBLE);
            holder.root.setBackgroundColor(Color.LTGRAY);
        }else{


            holder.item_check_box.setVisibility(View.GONE);
            holder.root.setBackgroundColor(Color.TRANSPARENT);


        }


    }

    private void ClickItem(ViewHolder holder) {

        Model item = AllFile.get(holder.getAdapterPosition());
        if (holder.item_check_box.getVisibility() == View.GONE) {


            holder.item_check_box.setVisibility(View.VISIBLE);

            holder.root.setBackgroundColor(Color.LTGRAY);

            SelectedFiles.add(item);

        } else {

            holder.item_check_box.setVisibility(View.GONE);
            holder.root.setBackgroundColor(Color.TRANSPARENT);
            SelectedFiles.remove(item);

        }
        mainViewModel.setText(String.valueOf(SelectedFiles.size()));
    }

    private void getFileBackground(selectedFileAdapter.ViewHolder holder, String imageName) {

        ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + imageName, response -> {

            holder.image_file.setImageBitmap(response);


        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {


        });
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(imageRequest);

    }


    @Override
    public int getItemCount() {
        return AllFile.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title_file, owner_name;
        ImageView image_file, item_check_box;
        LinearLayout root;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            title_file = itemView.findViewById(R.id.title_file);
            owner_name = itemView.findViewById(R.id.owner_name);
            image_file = itemView.findViewById(R.id.image_file);
            item_check_box = itemView.findViewById(R.id.item_check_box);
            root = itemView.findViewById(R.id.item_selected);
        }
    }
}
