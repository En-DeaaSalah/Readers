package wifi.codewl.readers.presenter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import wifi.codewl.readers.api.MySingleton;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.Channel_File;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.home.Proposal;
import wifi.codewl.readers.model.library.RecentHistory;
import wifi.codewl.readers.view.activity.EditFile;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.ViewActivity;
import wifi.codewl.readers.view.fragment.channel.Channel_Fragment;


public class show_files_playlist_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private  ArrayList<Channel_File> files;
    ArrayList<Integer> ids;


    public void fetchFilesFromServer() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);


        files = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "all_files", response -> {


            try {

                JSONArray jsonArray = new JSONArray(response);


                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.has("check")) {

                        break;

                    }


                    Channel_File file = new Channel_File(jsonObject.getString("id"), jsonObject.getString("Title"), jsonObject.getString("Description")
                            , jsonObject.getString("image"), jsonObject.getString("PDF"), jsonObject.getString("created_at"), jsonObject.getInt("Count_view"));


                    for (int j = 0; j < ids.size(); j++) {


                        if (Integer.parseInt(file.getId()) == ids.get(j)) {

                            files.add(file);
                            notifyDataSetChanged();
                        }


                    }


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

    public show_files_playlist_adapter(Context context, ArrayList<Integer> files) {

        this.context = context;
        this.ids = files;

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new ViewHolderFile(LayoutInflater.from(context).inflate(R.layout.file_channel_layout, parent, false));


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        String s = ((Channel_File) files.get(holder.getAdapterPosition())).getUpload_date();


        getFileBackground((ViewHolderFile) holder, ((Channel_File) files.get(holder.getAdapterPosition())).getFile_background());
        ((ViewHolderFile) holder).title.setText(((Channel_File) files.get(holder.getAdapterPosition())).getTitle());
        ((ViewHolderFile) holder).upload_date.setText(s.substring(0, s.indexOf(" ")));


        /*
        ((ViewHolderFile) holder).menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(context, ((ViewHolderFile) holder).menu);
                menu.inflate(R.menu.file_menu);
                menu.setOnMenuItemClickListener(item -> {

                    switch (item.getItemId()) {


                        case R.id.menu_delete: {



                            deleteFromServer(((Channel_File) files.get(holder.getAdapterPosition())).getId());
                            files.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeRemoved(holder.getAdapterPosition(), 1);
                            notifyDataSetChanged();
                            if (files.size() == 0) {

                                emptyListMessage.setVisibility(View.VISIBLE);


                            }


                            break;
                        }
                        case R.id.menu_edit: {
                            editFromServer(((Channel_File) files.get(holder.getAdapterPosition())));
                            notifyItemChanged(holder.getAdapterPosition());
                            notifyItemRangeChanged(holder.getAdapterPosition(), 1);
                            notifyDataSetChanged();
                            break;


                        }
                    }

                    return true;
                });
                menu.show();
            }
        });

         */


    }


    private void getFileBackground(ViewHolderFile holder, String imageName) {

        ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + imageName, response -> {

            holder.file_background.setImageBitmap(response);


        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {


        });
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(imageRequest);

    }


    @Override
    public int getItemCount() {
        return files.size();

    }


    class ViewHolderFile extends RecyclerView.ViewHolder {


        private ConstraintLayout layoutChannelFile;
        ImageView file_background;
        TextView title, upload_date, views_count;
        ImageButton menu;


        public ViewHolderFile(@NonNull View itemView) {
            super(itemView);

            file_background = itemView.findViewById(R.id.file_backgeound);
            title = itemView.findViewById(R.id.file_title);
            upload_date = itemView.findViewById(R.id.create_date);
            views_count = itemView.findViewById(R.id.view_counter);
            menu = itemView.findViewById(R.id.file_channel_menu);

            layoutChannelFile = itemView.findViewById(R.id.layoutChannelFile);

            layoutChannelFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addView();
                    Intent intent = new Intent(context, ViewActivity.class);
                    Model model = files.get(getAdapterPosition());
                    Channel_File channelFile = (Channel_File) model;
                    intent.putExtra("id", Integer.parseInt(channelFile.getId()));
                    context.startActivity(intent);
                }
            });

            if (!Channel_Fragment.MyChannel) {

                menu.setVisibility(View.GONE);

            }


        }

        private void addView() {
            String url = MainActivity.SERVER_API_URL + "add_to_history";

            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> maps = new HashMap<>();
                    Channel_File channelFile = (Channel_File) files.get(getAdapterPosition());

                    maps.put("file_id", String.valueOf(channelFile.getId()));
                    maps.put("user_id", User.getUserReference().getUserId());
                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);
        }
    }


}
