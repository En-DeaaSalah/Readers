package wifi.codewl.readers.presenter;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import wifi.codewl.readers.model.home.Proposal;
import wifi.codewl.readers.model.library.RecentHistory;
import wifi.codewl.readers.status.Screen;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.ViewActivity;

public class RecentHistoryAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Model> list;

    public RecentHistoryAdapter(Context context, List<Model> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        Model model = list.get(position);
        if(model instanceof RecentHistory){
            return 0;
        }else {
            return 1;
        }
    }

    public void addFiles(List<Model> list){
        this.list.addAll(list);
    }


    public void getData(){
        String url = MainActivity.SERVER_API_URL + "return_history16";

        StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

            List<Model> list = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RecentHistory recentHistory = new RecentHistory();
                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL+jsonObject.getString("image"), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            recentHistory.setImageFile(response);
                            notifyDataSetChanged();
                        }
                    }, 0, 0,null, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { }
                    });

                    MySingleton.getInstance(context).getRequestQueue().add(imageRequest);


                    recentHistory.setNameFile(jsonObject.getString("Title"));
                    recentHistory.setNameChannel(jsonObject.getString("Name"));
                    recentHistory.setUrlFile(jsonObject.getString("url_file"));
                    recentHistory.setIdFile(jsonObject.getInt("file_id"));
                    recentHistory.setIdHistory(jsonObject.getInt("history_id"));
                    recentHistory.setModelType(0);
                    list.add(recentHistory);
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:{
                return new ViewHolderRecentHistory(LayoutInflater.from(context).inflate(R.layout.item_history,parent,false));
            }
            case 1:{
                return new ViewHolderProgressLibrary(LayoutInflater.from(context).inflate(R.layout.item_progress,parent,false));
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Model model = list.get(position);
        switch (model.getModelType()){
            case 0:{
                RecentHistory recentHistory = (RecentHistory) model;
                ViewHolderRecentHistory viewHolderRecentHistory = (ViewHolderRecentHistory) holder;

                viewHolderRecentHistory.imageView.setImageBitmap(recentHistory.getImageFile());
                viewHolderRecentHistory.nameFile.setText(recentHistory.getNameFile());
                viewHolderRecentHistory.nameChannel.setText(recentHistory.getNameChannel());

            }
        }
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderRecentHistory extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView nameFile,nameChannel;
        private ImageView imageViewMenu;

        private ConstraintLayout constraintLayout;

        public ViewHolderRecentHistory(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewHistoryFile);
            nameFile = itemView.findViewById(R.id.textViewHistoryNameFile);
            nameChannel = itemView.findViewById(R.id.textView23HistoryNameChannel);
            imageViewMenu = itemView.findViewById(R.id.imageButtonMenuRecentHistory);
            constraintLayout = itemView.findViewById(R.id.constraintLayoutRecentHistory);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addView();
                    Intent intent = new Intent(context, ViewActivity.class);
                    intent.putExtra("id",((RecentHistory)list.get(getAdapterPosition())).getIdFile());
                    context.startActivity(intent);
                }
            });

            imageViewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, imageViewMenu);
                    popup.getMenuInflater().inflate(R.menu.item_proposal, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete_file_proposal:{
                                    deleteOneHistory();
                                    break;
                                } case R.id.save_later_file_proposal:{
                                    addViewLater();
                                    break;
                                }
                                case R.id.share_file_proposal:{
                                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);

                                    Model model = list.get(getAdapterPosition());

                                    RecentHistory recentHistory = (RecentHistory) model;

                                    String shareBody = MainActivity.SERVER_IMAGES_URL + recentHistory.getUrlFile(); ;
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

        private void deleteOneHistory(){

            String url = MainActivity.SERVER_API_URL + "delete_history";
            int position = getAdapterPosition();

            StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

                try {

                    System.out.println(response);
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("messages").equalsIgnoreCase("Done")){
                        removeAt(getAdapterPosition());
                    }else {
                        final Snackbar snackbar = Snackbar.make(itemView.getRootView(), jsonObject.getString("messages"), Snackbar.LENGTH_LONG);
                        snackbar.setAction("Close", v1 -> snackbar.dismiss());
                        snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                        double yOffset = Screen.dm.heightPixels*Screen.display1;
                        View snackbarLayout = snackbar.getView();
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, (int)yOffset, 0, 0);
                        snackbarLayout.setLayoutParams(lp);snackbar.show();
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
                    maps.put("id",String.valueOf(((RecentHistory)list.get(position)).getIdHistory()));
                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);
        }

        private void addViewLater(){
            String url = MainActivity.SERVER_API_URL + "view_later";

            StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

                double yOffset = Screen.getScreenHeight()*Screen.display1;

                try {
                    JSONObject jsonObject = new JSONObject(response);
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
                    maps.put("file_id",String.valueOf(((RecentHistory)list.get(getAdapterPosition())).getIdFile()));
                    maps.put("user_id", User.getUserReference().getUserId());
                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);
        }
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size() );
    }

    class ViewHolderProgressLibrary extends RecyclerView.ViewHolder{

        public ViewHolderProgressLibrary(@NonNull View itemView) {
            super(itemView);
        }
    }

}
