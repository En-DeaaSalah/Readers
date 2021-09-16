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
import wifi.codewl.readers.model.library.History;
import wifi.codewl.readers.model.library.RecentHistory;
import wifi.codewl.readers.model.progress.ProgressFile;
import wifi.codewl.readers.status.Screen;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.ViewActivity;

public class HistoryAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Model> list;

    public HistoryAdapter(Context context,List<Model> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        Model model = list.get(position);
        if(model instanceof History){
            return 0;
        }else {
            return 1;
        }
    }

    public void addFiles(List<Model> list){
        this.list.addAll(list);
    }

    public void getDataRelatedFiles(){

        String url = MainActivity.SERVER_API_URL + "return_history_all";

        StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

            try {


                JSONArray jsonArray = new JSONArray(response);

                List<Model> list = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {


                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    History history = new History();
                    int finalI = i;
                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + jsonObject.getString("image"), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            history.setImageFile(response);
                            notifyItemChanged(finalI);
                        }
                    }, 0, 0,null, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { }
                    });

                    MySingleton.getInstance(context).addToRequestQueue(imageRequest);
                    history.setNameFile(jsonObject.getString("Title"));
                    history.setNameChannel(jsonObject.getString("Name"));
                    history.setDateFile(jsonObject.getString("created_at"));
                    history.setNumberViews(jsonObject.getInt("Count_view"));
                    history.setIdHistory(jsonObject.getInt("history_id"));
                    history.setIdFile(jsonObject.getInt("file_id"));
                    history.setUrlFile(jsonObject.getString("url_file"));

                    history.setModelType(0);
                    list.add(history);
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
                return new ViewHolderHistory(LayoutInflater.from(context).inflate(R.layout.item_related,parent,false));
            }
            case 1:{
                return new ViewHolderProgressHistory(LayoutInflater.from(context).inflate(R.layout.item_progress,parent,false));
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Model model = list.get(position);
        switch (model.getModelType()){
            case 0:{
                History relatedFile= (History) model;

                ViewHolderHistory viewHolderHistory = (ViewHolderHistory) holder;
                viewHolderHistory.imageFileRelated.setImageBitmap(relatedFile.getImageFile());
                viewHolderHistory.nameFile.setText(relatedFile.getNameFile());
                viewHolderHistory.nameChannel.setText(relatedFile.getNameChannel());
                viewHolderHistory.viewAndDateFile.setText(relatedFile.getDateFile()+ " - "+relatedFile.getNumberViews() +" views");
                break;
            } case 1:{

                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolderHistory extends RecyclerView.ViewHolder{
        public ImageView imageFileRelated;
        public TextView nameFile,nameChannel,viewAndDateFile;

        private ImageButton imageButton;

        private LinearLayout layout;

        public ViewHolderHistory(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.related_menu);
            imageFileRelated = itemView.findViewById(R.id.imageFileRelated);
            nameFile = itemView.findViewById(R.id.nameFileRelated);
            nameChannel = itemView.findViewById(R.id.nameChannelRelated);
            viewAndDateFile = itemView.findViewById(R.id.viewAndDateFileRelated);
            layout = itemView.findViewById(R.id.layoutRelatedFile);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addView();
                    Intent intent = new Intent(context, ViewActivity.class);
                    intent.putExtra("id",((History)list.get(getAdapterPosition())).getIdFile());
                    context.startActivity(intent);
                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    PopupMenu popup = new PopupMenu(context, imageButton);
                    popup.getMenuInflater().inflate(R.menu.item_proposal, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        public boolean onMenuItemClick(MenuItem item) {

                            int position = getAdapterPosition();

                            System.out.println("**************************************************************");
                            System.out.println(position);
                            System.out.println("*****************************************************************");

                            if(position == -1){
                                return  false;
                            }

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
                                    History history = (History) model;
                                    String shareBody = MainActivity.SERVER_IMAGES_URL + history.getUrlFile();
                                    intent.setType("text/plain");
                                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                    context.startActivity(intent);
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
                        double yOffset = Screen.dm.heightPixels*Screen.display2;
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
                    maps.put("id",String.valueOf(((History)list.get(position)).getIdHistory()));
                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);
        }

        private void addView(){
            String url = MainActivity.SERVER_API_URL + "add_to_history";

            StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> maps = new HashMap<>();
                    History history = (History) list.get(getAdapterPosition());

                    maps.put("file_id",String.valueOf(history.getIdFile()));
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
                    double yOffset = Screen.getScreenHeight() * Screen.display2;
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
                    maps.put("file_id",String.valueOf(((History)list.get(getAdapterPosition())).getIdFile()));
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

    class ViewHolderProgressHistory extends RecyclerView.ViewHolder{

        public ViewHolderProgressHistory(@NonNull View itemView) {
            super(itemView);
        }
    }

}
