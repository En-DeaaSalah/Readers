package wifi.codewl.readers.presenter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import java.util.List;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.PlayLists;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.show_play_list_files;
import wifi.codewl.readers.view.fragment.channel.Channel_Fragment;
import wifi.codewl.readers.view.fragment.channel.FilePage;

import static wifi.codewl.readers.view.fragment.channel.FilePage.getFiles;


public class playAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<PlayLists> play;
    private View emptyListMessage;


    public playAdapter(Context context, ArrayList<PlayLists> playList, View emptyListMessage) {

        this.context = context;
        this.play = playList;
        this.emptyListMessage = emptyListMessage;


    }

    @Override
    public int getItemViewType(int position) {
        return play.get(position).getModelType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new play_list_holder(LayoutInflater.from(context).inflate(R.layout.play_list_item, parent, false));


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (play.size() == 0) {

            emptyListMessage.setVisibility(View.VISIBLE);


        } else {

            emptyListMessage.setVisibility(View.GONE);

        }


        PlayLists object = ((PlayLists) play.get(position));




        ((play_list_holder) holder).channel_name.setText(object.getChannel_name());

      ((play_list_holder) holder).file_count.setText(""+object.getFile_count());
        ((play_list_holder) holder).play_name.setText(object.getPlayListName());

        getFileBackground((play_list_holder) holder, object.getBackground_image());


/*
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


 */


    }


    private void getFileBackground(play_list_holder holder, String imageName) {

        ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + imageName, response -> {

            holder.play_backgeound.setImageBitmap(response);


        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error -> {


        });
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(imageRequest);

    }


    @Override
    public int getItemCount() {
        return play.size();
    }


    class play_list_holder extends RecyclerView.ViewHolder {


        private ConstraintLayout play_list_root;
        ImageView play_backgeound;
        TextView play_name, file_count, channel_name;
        ImageButton menu;


        public play_list_holder(@NonNull View itemView) {
            super(itemView);

            play_backgeound = itemView.findViewById(R.id.play_backgeound);
            play_name = itemView.findViewById(R.id.play_name);
            file_count = itemView.findViewById(R.id.file_count);
            channel_name = itemView.findViewById(R.id.channel_name);
            menu = itemView.findViewById(R.id.playListMenu);

            play_list_root = itemView.findViewById(R.id.play_list_root);

            play_list_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(context, show_play_list_files.class);

                    /*

                    ArrayList<Model>files=FilePage.getFiles();
                    ArrayList<Model>temp=new ArrayList<>();


                    for (int i = 0; i < files.size(); i++) {

                        play.get(getAdapterPosition()).getPlayListFilesIds()



                    }
                    */



                    intent.putExtra("files_show", play.get(getAdapterPosition()).getPlayListFilesIds());
                    context.startActivity(intent);





                }
            });


            if (!Channel_Fragment.MyChannel) {

                menu.setVisibility(View.GONE);

            }


        }


    }


}
