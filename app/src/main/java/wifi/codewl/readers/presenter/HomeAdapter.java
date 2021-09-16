package wifi.codewl.readers.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.android.volley.toolbox.JsonArrayRequest;
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
import wifi.codewl.readers.model.progress.Progress;
import wifi.codewl.readers.model.progress.ProgressHome;
import wifi.codewl.readers.status.Screen;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.ViewActivity;

public class HomeAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Model> list;



    public HomeAdapter(Context context,List<Model> list){
        this.context = context;
        this.list  = list;
    }

    @Override
    public int getItemViewType(int position) {
        Model model =  list.get(position);
        if (model instanceof Proposal){
            return 0;
        }else {
            return 1;
        }

    }

    public void addFiles(List<Model> list,boolean last){
        if(!last){
            this.list.remove(this.list.size()-1);
        }
        this.list.addAll(list);
    }

    public void update(){
        this.list.clear();
    }

    public void getData(boolean last){

        String url = MainActivity.SERVER_API_URL + "getfiles";

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                List<Model> list = new ArrayList<>();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Proposal proposal = new Proposal(false);
                        proposal.setIdFile(jsonObject.getInt("id"));
                        proposal.setUrlFile(jsonObject.getString("url_file"));
                        proposal.setNameFile(jsonObject.getString("Title"));
                        proposal.setDate(jsonObject.getString("created_at"));
                        proposal.setNumberViews(jsonObject.getInt("Count_view"));
                        proposal.setNameChannel(jsonObject.getString("Name"));
                        proposal.setIdChannel(jsonObject.getInt("id_channel"));

                        ImageRequest imageRequest1 = new ImageRequest(MainActivity.SERVER_IMAGES_URL+jsonObject.getString("image"), new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                proposal.setImageFile(response);
                                notifyDataSetChanged();
                            }
                        }, 0, 0,null, null, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) { }
                        });

                        ImageRequest imageRequest2 = new ImageRequest(MainActivity.SERVER_IMAGES_URL+jsonObject.getString("Personal_image"), new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                proposal.setImageChannel(response);
                                notifyDataSetChanged();
                            }
                        }, 0, 0,null, null, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) { }
                        });

                        MySingleton.getInstance(context).getRequestQueue().add(imageRequest1);
                        MySingleton.getInstance(context).getRequestQueue().add(imageRequest2);
                        proposal.setModelType(0);

                        list.add(proposal);
                    }

                    ProgressHome progressHome = new ProgressHome();
                    progressHome.setModelType(1);
                    list.add(progressHome);


                    addFiles(list,last);
                    notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(context).getRequestQueue().add(jsonArrayRequest);
    }




    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:{
                return new ViewHolderProposal(LayoutInflater.from(context).inflate(R.layout.item_proposal,parent,false));
            }
            case 1:{
                return new ViewHolderProgressHome(LayoutInflater.from(context).inflate(R.layout.item_progress,parent,false));
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Model model =list.get(position);
        if(model.getModelType() == 0){
            Proposal proposal = (Proposal) list.get(position);
            ViewHolderProposal viewHolderProposal = (ViewHolderProposal) holder;

            viewHolderProposal.nameFile.setText(proposal.getNameFile());
            viewHolderProposal.nameChannel.setText(proposal.getNameChannel());
            viewHolderProposal.numberViews.setText(String.valueOf(proposal.getNumberViews()) + " views");
            viewHolderProposal.dateFile.setText(proposal.getDate());

            if(proposal.getImageFile()!=null){
                viewHolderProposal.imageFile.setImageBitmap(proposal.getImageFile());
            }
            if(proposal.getImageChannel()!=null){
                viewHolderProposal.imageChannel.setImageBitmap(proposal.getImageChannel());
            }



        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getData(false);
                }
            }).start();

        }
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

    class ViewHolderProposal extends RecyclerView.ViewHolder{

        public ImageView imageFile;
        public ImageView imageChannel;
        public TextView nameFile,nameChannel,numberViews,dateFile;
        private ImageView imageView;


        public ViewHolderProposal(@NonNull View itemView) {
            super(itemView);

            nameFile = itemView.findViewById(R.id.nameFile);
            imageFile = itemView.findViewById(R.id.imageView);
            imageChannel =itemView.findViewById(R.id.title_click);
            nameChannel = itemView.findViewById(R.id.nameChannel);
            numberViews = itemView.findViewById(R.id.numberViews);
            dateFile = itemView.findViewById(R.id.dateFile);

            //nameChannel = itemView.find
            imageChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model model = list.get(getAdapterPosition());
                    Proposal proposal = (Proposal) model;

                    ((MainActivity)context).goTo_People_Channel(proposal.getIdChannel());
                }
            });
            imageView = itemView.findViewById(R.id.proposal_menu);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, imageView);
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
                                    Proposal proposal = (Proposal) model;

                                    String shareBody = MainActivity.SERVER_IMAGES_URL + proposal.getUrlFile(); ;
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addView();
                    Intent intent = new Intent(context, ViewActivity.class);
                    intent.putExtra("id",((Proposal)list.get(getAdapterPosition())).getIdFile());
                    context.startActivity(intent);
                }
            });

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


                    double yOffset = Screen.getScreenHeight() * Screen.display1;

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
                    maps.put("file_id",String.valueOf(((Proposal)list.get(getAdapterPosition())).getIdFile()));
                    maps.put("user_id", User.getUserReference().getUserId());
                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);
        }
    }

    class ViewHolderProgressHome extends RecyclerView.ViewHolder{

        public ViewHolderProgressHome(@NonNull View itemView) {
            super(itemView);
        }
    }
}
