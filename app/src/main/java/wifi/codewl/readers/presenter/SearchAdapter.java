package wifi.codewl.readers.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import wifi.codewl.readers.model.file.AttributesFile;
import wifi.codewl.readers.model.file.RelatedFile;
import wifi.codewl.readers.model.home.Proposal;
import wifi.codewl.readers.model.progress.ProgressFile;
import wifi.codewl.readers.model.progress.ProgressHistory;
import wifi.codewl.readers.model.search.SearchChannel;
import wifi.codewl.readers.model.search.SearchFile;
import wifi.codewl.readers.status.Screen;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.SearchActivity;
import wifi.codewl.readers.view.activity.ViewActivity;

public class SearchAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Model> list;

    public SearchAdapter(Context context,List<Model> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        Model model =  list.get(position);

        if(model instanceof SearchChannel)
            return 0;
        else if(model instanceof RelatedFile)
            return 1;
        else
            return 2;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:{
                return new ViewHolderSearchChannel(LayoutInflater.from(context).inflate(R.layout.item_search_channel,parent,false));
            }
            case 1:{
                return new ViewHolderSearchFile(LayoutInflater.from(context).inflate(R.layout.item_related,parent,false));
            }
            case 2:{
                return new ViewHolderProgressSearch(LayoutInflater.from(context).inflate(R.layout.item_progress,parent,false));
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Model model = list.get(position);
        switch (model.getModelType()){
            case 0:{
                ViewHolderSearchChannel viewHolderSearchChannel = (ViewHolderSearchChannel) holder;
                SearchChannel searchChannel = (SearchChannel) list.get(position);
                if(searchChannel.getImageChannel()!=null){
                    viewHolderSearchChannel.imageChannelSearch.setImageBitmap(searchChannel.getImageChannel());
                }
                viewHolderSearchChannel.textChannelSearch.setText(searchChannel.getNameChannel());
                viewHolderSearchChannel.numberSubAndNumberFiles.setText(searchChannel.getNumberSub() + " Subscription");
                viewHolderSearchChannel.numberFilesForChannel.setText(searchChannel.getNumberFiles() + " files");
                break;
            } case 1:{
                ViewHolderSearchFile viewHolderSearchFile = (ViewHolderSearchFile) holder;
                RelatedFile relatedFile= (RelatedFile) model;
                viewHolderSearchFile.imageFileRelated.setImageBitmap(relatedFile.getImageFile());
                viewHolderSearchFile.nameFile.setText(relatedFile.getNameFile());
                viewHolderSearchFile.nameChannel.setText(relatedFile.getNameChannel());
                viewHolderSearchFile.viewAndDateFile.setText(relatedFile.getDateFile()+ " - "+relatedFile.getNumberViews() +" views");
                break;
            }
        }

    }

    public void addFiles(List<Model> list){
        this.list.addAll(list);
    }


    public void getData(String query){
        addProgressHistory();
        getDataForChannel(query);
    }

    private void addProgressHistory(){
        List<Model> temp = new ArrayList<>();
        ProgressHistory progressHistory = new ProgressHistory();
        progressHistory.setModelType(2);
        temp.add(progressHistory);
        addFiles(temp);
    }

    public void getDataForChannel(String query){

        String url = MainActivity.SERVER_API_URL + "search_for_channel";

        int size = getItemCount()-1;

        StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

            try {

                JSONArray jsonArray = new JSONArray(response);

                List<Model> list = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {


                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SearchChannel searchChannel = new SearchChannel();
                    int finalI = i;
                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + jsonObject.getString("Personal_image"), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            searchChannel.setImageChannel(response);
                            notifyItemChanged(size+finalI);
                        }
                    }, 0, 0,null, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { }
                    });

                    MySingleton.getInstance(context).addToRequestQueue(imageRequest);

                    searchChannel.setNameChannel(jsonObject.getString("Name"));
                    searchChannel.setNumberSub(jsonObject.getInt("Count_sub"));
                    searchChannel.setNumberFiles(jsonObject.getInt("files_count"));
                    searchChannel.setIdChannel(jsonObject.getInt("id"));

                    searchChannel.setModelType(0);
                    list.add(searchChannel);
                }
                ProgressHistory progressHistory = new ProgressHistory();
                progressHistory.setModelType(2);
                list.add(progressHistory);

                removeAt(0);
                addFiles(list);
                notifyDataSetChanged();

                getDataForFiles(query);

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
                maps.put("key_word",query);
                return maps;
            }
        };


        MySingleton.getInstance(context).getRequestQueue().add(request);

    }


    public void getDataForFiles(String query){

        String url = MainActivity.SERVER_API_URL + "search_for_file";

        int size = getItemCount()-1;

        StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

            try {

                JSONArray jsonArray = new JSONArray(response);

                List<Model> list = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {


                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RelatedFile relatedFile = new RelatedFile();
                    int finalI = i;
                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + jsonObject.getString("image"), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            relatedFile.setImageFile(response);
                            notifyItemChanged(size+finalI);
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
                    relatedFile.setIdFile(jsonObject.getInt("file_id"));
                    relatedFile.setUrlFile(jsonObject.getString("url_file"));
                    relatedFile.setModelType(1);
                    list.add(relatedFile);
                }


                removeAt(getItemCount()-1);
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
                maps.put("key_word",query);
                return maps;
            }
        };


        MySingleton.getInstance(context).getRequestQueue().add(request);
    }


    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size() );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderSearchFile extends RecyclerView.ViewHolder{
        public ImageView imageFileRelated;
        public TextView nameFile,nameChannel,viewAndDateFile;

        public ImageButton imageButton;

        private LinearLayout layout;

        public ViewHolderSearchFile(@NonNull View itemView) {
            super(itemView);

            imageFileRelated = itemView.findViewById(R.id.imageFileRelated);
            nameFile = itemView.findViewById(R.id.nameFileRelated);
            nameChannel = itemView.findViewById(R.id.nameChannelRelated);
            viewAndDateFile = itemView.findViewById(R.id.viewAndDateFileRelated);
            layout = itemView.findViewById(R.id.layoutRelatedFile);


            imageButton = itemView.findViewById(R.id.related_menu);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, imageButton);
                    popup.getMenuInflater().inflate(R.menu.item_proposal, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete_file_proposal:{
                                    removeAt(getAdapterPosition());
                                    break;
                                } case R.id.save_later_file_proposal:{
                                    addViewLater();
                                    break;
                                }
                                case R.id.share_file_proposal:{
                                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                                    Model model = list.get(getAdapterPosition());
                                    RelatedFile relatedFile = (RelatedFile) model;
                                    String shareBody = MainActivity.SERVER_IMAGES_URL +relatedFile.getUrlFile();
                                    intent.setType("text/plain");
                                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                    context.startActivity(intent);
                                }
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });


            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addView();
                    Intent intent = new Intent(context, ViewActivity.class);
                    intent.putExtra("id",((RelatedFile)list.get(getAdapterPosition())).getIdFile());
                    context.startActivity(intent);
                }
            });


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
                    Proposal proposal = (Proposal) list.get(getAdapterPosition());

                    maps.put("file_id",String.valueOf(proposal.getIdFile()));
                    maps.put("user_id", User.getUserReference().getUserId());
                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);
        }
        private void addViewLater(){
            String url = MainActivity.SERVER_API_URL + "view_later";

            StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    double yOffset = Screen.getScreenHeight() * Screen.display2;;
                    final Snackbar snackbar = Snackbar.make(itemView.getRootView(), jsonObject.getString("messages"), Snackbar.LENGTH_LONG);
                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                    snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                    View snackbarLayout = snackbar.getView();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, (int)yOffset, 0, 0);
                    snackbarLayout.setLayoutParams(lp);
                    snackbar.show();

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
        }
    }

    class ViewHolderSearchChannel extends RecyclerView.ViewHolder{


        public ImageView imageChannelSearch;
        public TextView textChannelSearch,numberSubAndNumberFiles,numberFilesForChannel;

        private ImageButton imageButton;

        private LinearLayout ckjdkhvj;

        public ViewHolderSearchChannel(@NonNull View itemView) {
            super(itemView);

            imageChannelSearch = itemView.findViewById(R.id.imageChannelSearch);
            textChannelSearch = itemView.findViewById(R.id.textChannelSearch);
            numberSubAndNumberFiles = itemView.findViewById(R.id.numberSubAndNumberFiles);
            numberFilesForChannel = itemView.findViewById(R.id.numberFilesForChannel);
            ckjdkhvj = itemView.findViewById(R.id.ckjdkhvj);

            ckjdkhvj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model model = list.get(getAdapterPosition());
                    SearchChannel searchChannel = (SearchChannel) model;

                    MainActivity.getContext().goTo_People_Channel(searchChannel.getIdChannel());
                    ((ViewActivity)context).finish();
                }
            });





            imageButton = itemView.findViewById(R.id.search_channel_menu);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, imageButton);
                    popup.getMenuInflater().inflate(R.menu.item_search_channel, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete_channel:{
                                    removeAt(getAdapterPosition());
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

    class ViewHolderProgressSearch extends RecyclerView.ViewHolder{

        public ViewHolderProgressSearch(@NonNull View itemView) {
            super(itemView);
        }
    }

}
