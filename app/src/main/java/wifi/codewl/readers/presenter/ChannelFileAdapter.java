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


public class ChannelFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Model> files;
    private AlertDialog dialog;
    private View emptyListMessage;


    public ChannelFileAdapter(Context context, ArrayList<Model> files, View emptyListMessage) {

        this.context = context;
        this.files = files;
        this.emptyListMessage = emptyListMessage;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.progress_dialog_layout);
        builder.setCancelable(false);
        dialog = builder.create();
    }

    @Override
    public int getItemViewType(int position) {
        return files.get(position).getModelType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new ViewHolderFile(LayoutInflater.from(context).inflate(R.layout.file_channel_layout, parent, false));


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (Channel_Fragment.tabLayout.getSelectedTabPosition() == 1)
            dialog.show();


        if (files.size() == 0) {

            emptyListMessage.setVisibility(View.VISIBLE);


        } else {

            emptyListMessage.setVisibility(View.GONE);

        }


        String s = ((Channel_File) files.get(holder.getAdapterPosition())).getUpload_date();


        getFileBackground((ViewHolderFile) holder, ((Channel_File) files.get(holder.getAdapterPosition())).getFile_background());
        ((ViewHolderFile) holder).title.setText(((Channel_File) files.get(holder.getAdapterPosition())).getTitle());
        ((ViewHolderFile) holder).views_count.setText(""+((Channel_File) files.get(holder.getAdapterPosition())).getView_Counter());
        ((ViewHolderFile) holder).upload_date.setText(s.substring(0, s.indexOf(" ")));

        ((ViewHolderFile) holder).menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(context, ((ViewHolderFile) holder).menu);
                menu.inflate(R.menu.file_menu);
                menu.setOnMenuItemClickListener(item -> {

                    switch (item.getItemId()) {


                        case R.id.menu_delete: {

                            dialog.show();


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


    }


    private void getFileBackground(ViewHolderFile holder, String imageName) {

        ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + imageName, response -> {

            holder.file_background.setImageBitmap(response);


            if (Channel_Fragment.tabLayout.getSelectedTabPosition() == 1)
                dialog.dismiss();


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
                    intent.putExtra("id",Integer.parseInt(channelFile.getId()));
                    context.startActivity(intent);
                }
            });

            if (!Channel_Fragment.MyChannel) {

                menu.setVisibility(View.GONE);

            }


        }

        private void addView(){
            String url = MainActivity.SERVER_API_URL + "add_to_history";

            StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {
                Toast.makeText(context,response,Toast.LENGTH_LONG).show();
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> maps = new HashMap<>();
                    Channel_File channelFile = (Channel_File) files.get(getAdapterPosition());

                    maps.put("file_id",String.valueOf(channelFile.getId()));
                    maps.put("user_id", User.getUserReference().getUserId());
                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);
        }
    }


    public void editFromServer(Channel_File file) {


        Intent intent = new Intent(context, EditFile.class);


        intent.putExtra("file", file);
        context.startActivity(intent);


    }


    public void deleteFromServer(String id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "delete",

                response -> {


                    Log.d("myTage", response);
                    dialog.dismiss();


                }, error -> {
            Log.d("myTage", error.toString());

        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                return map;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


    }


    static class ViewHolderProgressFiles extends RecyclerView.ViewHolder {

        public ViewHolderProgressFiles(@NonNull View itemView) {
            super(itemView);
        }
    }

}
