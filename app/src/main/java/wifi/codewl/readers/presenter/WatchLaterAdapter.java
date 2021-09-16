package wifi.codewl.readers.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.api.MySingleton;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.file.RelatedFile;
import wifi.codewl.readers.model.progress.ProgressFile;
import wifi.codewl.readers.status.Screen;
import wifi.codewl.readers.view.activity.MainActivity;

public class WatchLaterAdapter extends RecyclerView.Adapter<WatchLaterAdapter.ViewHolderWatchLater> {

    private Context context;
    private List<Model> list;
    public WatchLaterAdapter(Context context,List<Model> list){
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolderWatchLater onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderWatchLater(LayoutInflater.from(context).inflate(R.layout.item_related,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderWatchLater holder, int position) {
        Model model = list.get(position);

        RelatedFile relatedFile= (RelatedFile) model;
        System.out.println("*********************************************");
        System.out.println(relatedFile.getNameFile());
        System.out.println(relatedFile.getNameChannel());

        holder.imageFileRelated.setImageBitmap(relatedFile.getImageFile());
        holder.nameFile.setText(relatedFile.getNameFile());
        holder.nameChannel.setText(relatedFile.getNameChannel());
        holder.viewAndDateFile.setText(relatedFile.getDateFile()+ " - "+relatedFile.getNumberViews() +" views");
    }

    private void addFiles(List<Model> list){
        this.list.addAll(list);
    }

    public void getData(){

        String url = MainActivity.SERVER_API_URL + "all_view_later";

        StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

            try {

                System.out.println(response);
                JSONArray jsonArray = new JSONArray(response);

                List<Model> list = new ArrayList<>();
                System.out.println(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RelatedFile relatedFile = new RelatedFile();
                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + jsonObject.getString("image"), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            relatedFile.setImageFile(response);
                            notifyDataSetChanged();
                        }
                    }, 0, 0,null, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { }
                    });
                    MySingleton.getInstance(context).addToRequestQueue(imageRequest);
                    relatedFile.setNameFile(jsonObject.getString("Title"));
                    relatedFile.setNameChannel(jsonObject.getString("Name"));
                    relatedFile.setDateFile(jsonObject.getString("created_at"));
                    relatedFile.setNumberViews(jsonObject.getInt("Count_view"));
                    relatedFile.setIdFile(jsonObject.getInt("id"));
                    relatedFile.setModelType(0);
                    list.add(relatedFile);
                }

                addFiles(list);
                notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> maps = new HashMap<>();
                maps.put("user_id", User.getUserReference().getUserId());
                return maps;
            }
        };


        MySingleton.getInstance(context).getRequestQueue().add(request);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size() );
    }


    class ViewHolderWatchLater extends RecyclerView.ViewHolder{

        public ImageView imageFileRelated;
        public TextView nameFile,nameChannel,viewAndDateFile;

        private ImageButton imageButton;


        public ViewHolderWatchLater(@NonNull View itemView) {
            super(itemView);

            imageButton = itemView.findViewById(R.id.related_menu);
            imageFileRelated = itemView.findViewById(R.id.imageFileRelated);
            nameFile = itemView.findViewById(R.id.nameFileRelated);
            nameChannel = itemView.findViewById(R.id.nameChannelRelated);
            viewAndDateFile = itemView.findViewById(R.id.viewAndDateFileRelated);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, imageButton);
                    popup.getMenuInflater().inflate(R.menu.related_file, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.menu_delete_watch_later:{
                                    String url = MainActivity.SERVER_API_URL + "delete_view_later";

                                    StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

                                        try {

                                            System.out.println(response);
                                            JSONObject jsonObject = new JSONObject(response);
                                            if(jsonObject.getString("messages").equalsIgnoreCase("Done")){
                                                removeAt(getAdapterPosition());
                                                double yOffset = Screen.getScreenHeight() * Screen.display2;
                                                final Snackbar snackbar = Snackbar.make(itemView.getRootView(), jsonObject.getString("messages"), Snackbar.LENGTH_LONG);
                                                snackbar.setAction("Close", v1 -> snackbar.dismiss());
                                                snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                                                View snackbarLayout = snackbar.getView();
                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                lp.setMargins(0, (int)yOffset, 0, 0);
                                                snackbarLayout.setLayoutParams(lp);
                                                snackbar.show();
                                            }else {
                                                double yOffset = Screen.getScreenHeight() * Screen.display2;
                                                final Snackbar snackbar = Snackbar.make(itemView.getRootView(), jsonObject.getString("messages"), Snackbar.LENGTH_LONG);
                                                snackbar.setAction("Close", v1 -> snackbar.dismiss());
                                                snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                                                View snackbarLayout = snackbar.getView();
                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                lp.setMargins(0, (int)yOffset, 0, 0);
                                                snackbarLayout.setLayoutParams(lp);
                                                snackbar.show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }){
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        protected Map<String, String> getParams() {
                                            Map<String,String> maps = new HashMap<>();
                                            maps.put("file_id",String.valueOf(((RelatedFile)list.get(getAdapterPosition())).getIdFile()));
                                            maps.put("user_id", User.getUserReference().getUserId());
                                            return maps;
                                        }
                                    };


                                    MySingleton.getInstance(context).getRequestQueue().add(request);
                                    break;
                                } case R.id.menu_share_watch_later:{
                                    break;
                                }
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }



    }


}
