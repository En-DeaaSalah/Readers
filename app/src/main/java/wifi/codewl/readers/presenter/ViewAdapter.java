package wifi.codewl.readers.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.api.InputStreamVolleyRequest;
import wifi.codewl.readers.api.MySingleton;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.file.AddCommentFile;
import wifi.codewl.readers.model.file.AttributesFile;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.file.RelatedFile;
import wifi.codewl.readers.model.file.ViewCommentFile;
import wifi.codewl.readers.model.home.Proposal;
import wifi.codewl.readers.model.progress.ProgressFile;
import wifi.codewl.readers.status.Screen;
import wifi.codewl.readers.view.activity.CommentActivity;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.ViewActivity;

public class ViewAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Model> list;







    private  Animation rotateOpen,rotateClose;


    public ViewAdapter(Context context,List<Model> list){
        this.context = context;
        this.list  = list;
        rotateOpen = AnimationUtils.loadAnimation(context, R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(context, R.anim.rotate_close);
    }


    public void addFiles(List<Model> list,boolean last){
        if(!last){
            this.list.remove(this.list.size()-1);
        }
        this.list.addAll(list);
    }

    public void getDataFile(int idFile, boolean last, ViewActivity.ShowFile showFile){
        String url = MainActivity.SERVER_API_URL + "viewfile";

        StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {
            try {
                JSONArray jsonArray = new JSONArray(response);


                List<Model> list = new ArrayList<>();
                AttributesFile attributesFile = new AttributesFile(false);
                try {

                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    attributesFile.setIdFile(idFile);
                    attributesFile.setTitleFile(jsonObject.getString("Title"));
                    attributesFile.setDateFile(jsonObject.getString("created_at"));
                    attributesFile.setNumberView(jsonObject.getInt("Count_view"));
                    attributesFile.setNameChanel(jsonObject.getString("Name"));
                    attributesFile.setNumberSub(jsonObject.getInt("Count_sub"));
                    attributesFile.setNumberLike(jsonObject.getInt("like"));
                    attributesFile.setNumberDislike(jsonObject.getInt("dislike"));
                    attributesFile.setDescriptionFile(jsonObject.getString("Description"));
                    attributesFile.setIdChannel(jsonObject.getInt("channel_id"));




                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + jsonObject.getString("Personal_image"), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            attributesFile.setImageChannel(response);
                            notifyItemChanged(0);
                        }
                    }, 0, 0,null, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { }
                    });
                    MySingleton.getInstance(context).addToRequestQueue(imageRequest);
                    attributesFile.setStatus(jsonArray.getInt(1));
                    attributesFile.setSubscribed(jsonArray.getInt(2) == 0 ? false : true);
                    attributesFile.setModelType(0);
                    attributesFile.setUrlFile(jsonObject.getString("PDF"));
                    download(MainActivity.SERVER_IMAGES_URL+jsonObject.getString("PDF"),showFile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                list.add(attributesFile);
                ProgressFile progressFile = new ProgressFile();
                progressFile.setModelType(4);
                list.add(progressFile);

                addFiles(list,last);
                notifyDataSetChanged();


                getDataRelatedFiles(idFile,false);
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


                maps.put("file_id",String.valueOf(idFile));

                maps.put("user_id", User.getUserReference().getUserId());
                return maps;
            }
        };

        MySingleton.getInstance(context).getRequestQueue().add(request);
    }

    private void download(String mUrl, ViewActivity.ShowFile showFile){


        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, mUrl,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        if(response == null){return;}

                        //File root = MainActivity.this.getFilesDir();;

                        File root = context.getExternalFilesDir("root");


                        File dir = new File (root.getAbsolutePath() + "/download");

                        dir.mkdirs();
                        File file = new File(dir, "file.pdf");

                        try {
                            FileOutputStream f = new FileOutputStream(file);
                            BufferedOutputStream b = new BufferedOutputStream(f);
                            b.write(response);
                            b.flush();
                            b.close();
                            f.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        showFile.showFile(file);

                    }
                } ,new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
// TODO handle the error
                error.printStackTrace();
            }
        }, null);

        MySingleton.getInstance(context).getRequestQueue().add(request);
    }

    public void getDataRelatedFiles(int idFile, boolean last){

       String url = MainActivity.SERVER_API_URL + "suggestedfiles";

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
                            notifyItemChanged(finalI+1);
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
                    relatedFile.setUrlFile(jsonObject.getString("url_file"));
                    relatedFile.setModelType(1);
                    list.add(relatedFile);
                }


                ProgressFile progressFile = new ProgressFile();
                progressFile.setModelType(4);
                list.add(progressFile);


                addFiles(list,last);
                notifyDataSetChanged();

                getAddComment(idFile,false);



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

                maps.put("id",String.valueOf(idFile));
                return maps;
            }
        };


        MySingleton.getInstance(context).getRequestQueue().add(request);
    }


    public void getAddComment(int idFile,boolean last){

        String url = MainActivity.SERVER_API_URL + "number_of_comments_for_file";

        StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {



                int numberComment = Integer.parseInt(response);

                List<Model> list = new ArrayList<>();
                AddCommentFile addCommentFile = new AddCommentFile();
                addCommentFile.setPersonalImage(User.getUserReference().getChannel().getChannel_profile().getProfileImage());
                addCommentFile.setIdChannel(Integer.parseInt(User.getUserReference().getUserId()));
                addCommentFile.setNumberComment(numberComment);
                addCommentFile.setIdFile(idFile);
                addCommentFile.setModelType(2);
                list.add(addCommentFile);

                ProgressFile progressFile = new ProgressFile();
                progressFile.setModelType(4);
                list.add(progressFile);


                addFiles(list,last);
                notifyDataSetChanged();

                getDataComment(idFile,false);



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> maps = new HashMap<>();
                maps.put("file_id",String.valueOf(idFile));
                return maps;
            }
        };


        MySingleton.getInstance(context).getRequestQueue().add(request);
    }

    public void getDataComment(int idFile,boolean last){

        int size = list.size()-1;

        String url = MainActivity.SERVER_API_URL + "return_all_comments";

        StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

            try {


                JSONArray jsonArray = new JSONArray(response);

                List<Model> list = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {


                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    ViewCommentFile viewCommentFile = new ViewCommentFile();

                    int finalI = i;
                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + jsonObject.getString("Personal_image"), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            viewCommentFile.setPersonalImage(response);
                            notifyItemChanged(size+finalI);
                        }
                    }, 0, 0,null, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { }
                    });

                    MySingleton.getInstance(context).addToRequestQueue(imageRequest);

                    viewCommentFile.setNameUser(jsonObject.getString("name"));
                    viewCommentFile.setText(jsonObject.getString("text"));
                    viewCommentFile.setIdComment(jsonObject.getInt("id"));
                    viewCommentFile.setNumberReply(jsonObject.getInt("replies_count"));
                    viewCommentFile.setDateComment(jsonObject.getString("created_at"));

                    //viewCommentFile.setIdChannel(jsonObject.getInt("idChannel"));

                    viewCommentFile.setModelType(3);
                    list.add(viewCommentFile);
                }


                addFiles(list,last);
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
                maps.put("file_id",String.valueOf(idFile));
                return maps;
            }
        };


        MySingleton.getInstance(context).getRequestQueue().add(request);
    }



    @Override
    public int getItemViewType(int position) {

        Model model = list.get(position);

        if(model instanceof AttributesFile){
            return 0;
        }else if(model instanceof RelatedFile){
            return 1;
        }else if(model instanceof AddCommentFile){
            return 2;
        }else if(model instanceof ViewCommentFile) {
            return 3;
        }else {
            return 4;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case 0:{
                return new ViewHolderAttributes(LayoutInflater.from(context).inflate(R.layout.item_attributes,parent,false));
            }
            case 1:{
                return new ViewHolderRelated(LayoutInflater.from(context).inflate(R.layout.item_related,parent,false));
            }
            case 2:{
                return new ViewHolderAddComment(LayoutInflater.from(context).inflate(R.layout.item_add_comment,parent,false));
            }
            case 3:{
                return new ViewHolderViewComment(LayoutInflater.from(context).inflate(R.layout.item_views_comment,parent,false));
            }
            case 4:{
                return new ViewHolderProgressFile(LayoutInflater.from(context).inflate(R.layout.item_progress,parent,false));
            }
        }

        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Model model = list.get(position);

        switch (model.getModelType()){
            case 0:{
                ViewHolderAttributes viewHolderAttributes = (ViewHolderAttributes) holder;
                AttributesFile attributesFile = (AttributesFile) model;
                boolean statusDescription = attributesFile.isExpand();
                viewHolderAttributes.descriptionView.setVisibility(statusDescription ? View.VISIBLE : View.GONE);
                if (!statusDescription)
                    viewHolderAttributes.titleClick.startAnimation(rotateClose);
                else
                    viewHolderAttributes.titleClick.startAnimation(rotateOpen);

                if(attributesFile.isSubscribed())
                    viewHolderAttributes.subscriptionsNotifications.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                else
                    viewHolderAttributes.subscriptionsNotifications.setImageResource(R.drawable.ic_baseline_notifications_24);


                viewHolderAttributes.nameFile.setText(attributesFile.getTitleFile());
                viewHolderAttributes.dateAndViewFile.setText(attributesFile.getDateFile() + "  View :" + attributesFile.getNumberView());
                viewHolderAttributes.numberLike.setText(String.valueOf(attributesFile.getNumberLike()));
                viewHolderAttributes.numberDislike.setText(String.valueOf(attributesFile.getNumberDislike()));
                viewHolderAttributes.nameChannel.setText(attributesFile.getNameChanel());
                viewHolderAttributes.numberSub.setText(String.valueOf(attributesFile.getNumberSub()) + " Subscription");

                viewHolderAttributes.imageChannelView.setImageBitmap(attributesFile.getImageChannel());

                viewHolderAttributes.dateFile.setText(attributesFile.getDateFile());

                viewHolderAttributes.descriptionFile.setText(attributesFile.getDescriptionFile());

                switch (attributesFile.getStatus()){
                    case 1:{
                        viewHolderAttributes.like.setImageResource(R.drawable.test);
                        break;
                    }
                    case  2:{
                        viewHolderAttributes.dislike.setImageResource(R.drawable.test2);
                        break;
                    }
                }

                if (attributesFile.isSubscribed()) {
                    viewHolderAttributes.subscriptionsNotifications.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                } else {
                    viewHolderAttributes.subscriptionsNotifications.setImageResource(R.drawable.ic_baseline_notifications_24);
                }

                break;
            }
            case 1:{
                ViewHolderRelated viewHolderRelated = (ViewHolderRelated) holder;
                RelatedFile relatedFile= (RelatedFile) model;
                viewHolderRelated.imageFileRelated.setImageBitmap(relatedFile.getImageFile());
                viewHolderRelated.nameFile.setText(relatedFile.getNameFile());
                viewHolderRelated.nameChannel.setText(relatedFile.getNameChannel());
                viewHolderRelated.viewAndDateFile.setText(relatedFile.getDateFile()+ " - "+relatedFile.getNumberViews() +" views");
                break;
            } case 2:{
                ViewHolderAddComment viewHolderAddComment = (ViewHolderAddComment) holder;
                AddCommentFile addCommentFile = (AddCommentFile) model;
                viewHolderAddComment.personalAddComment.setImageBitmap(addCommentFile.getPersonalImage());
                viewHolderAddComment.viewNumberComments.setText("Comment " + addCommentFile.getNumberComment());
                break;
            } case 3:{
                ViewHolderViewComment viewHolderViewComment = (ViewHolderViewComment) holder;
                ViewCommentFile viewCommentFile = (ViewCommentFile) model;
                viewHolderViewComment.nameUserForViewComment.setText(viewCommentFile.getNameUser() + " . " + viewCommentFile.getDateComment());
                viewHolderViewComment.contentText.setText(viewCommentFile.getText());
                viewHolderViewComment.replyButton.setText(viewCommentFile.getNumberReply() + " replies");
                if(viewCommentFile.getPersonalImage()!=null){
                    viewHolderViewComment.imagePersonalViewComment.setImageBitmap(viewCommentFile.getPersonalImage());
                }

                break;
            }case 4:{
                break;
            }
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

    class ViewHolderAttributes extends RecyclerView.ViewHolder{


        private LinearLayout titleFile,descriptionView;

        public TextView  nameFile,dateAndViewFile,numberLike,numberDislike,nameChannel,numberSub,dateFile,descriptionFile;
        public ImageButton imageChannelView;

        public ImageButton like,dislike,share,addViewLater;

        private ImageButton titleClick,subscriptionsNotifications;





        public ViewHolderAttributes(@NonNull View itemView) {
            super(itemView);

            titleFile = itemView.findViewById(R.id.title_file);

            nameFile = itemView.findViewById(R.id.nameFileView);

            dateAndViewFile = itemView.findViewById(R.id.date_and_view);
            numberLike = itemView.findViewById(R.id.numberLike);
            numberDislike = itemView.findViewById(R.id.numberDislike);


            nameChannel = itemView.findViewById(R.id.nameChannelView);
            numberSub = itemView.findViewById(R.id.numberSubView);

            dateFile = itemView.findViewById(R.id.dateFileView);
            descriptionFile = itemView.findViewById(R.id.descriptionFile);

            imageChannelView = itemView.findViewById(R.id.imageChannelView);

            imageChannelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model model = list.get(getAdapterPosition());
                    AttributesFile attributesFile = (AttributesFile) model;

                    MainActivity.getContext().goTo_People_Channel(attributesFile.getIdChannel());
                    ((ViewActivity)context).finish();


                }
            });

            descriptionView = itemView.findViewById(R.id.description_view);

            addViewLater = itemView.findViewById(R.id.imageButtonAddViewLater);
            share = itemView.findViewById(R.id.imageButtonShareViewFile);

            addViewLater.setOnClickListener(v-> {
                    addViewLater();
            });


            share.setOnClickListener(v-> {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    Model model = list.get(getAdapterPosition());
                    AttributesFile attributesFile = (AttributesFile) model;
                    String shareBody = MainActivity.SERVER_IMAGES_URL + attributesFile.getUrlFile();
                    intent.setType("text/plain");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(intent);
            });

            like = itemView.findViewById(R.id.imageButtonLike);

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = MainActivity.SERVER_API_URL + "like";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {

                        try {
                            AttributesFile attributesFile = (AttributesFile)list.get(getAdapterPosition());
                            JSONArray jsonArray = new JSONArray(response);
                            String status = jsonArray.getString(0);
                            String numberLikes = String.valueOf(jsonArray.getInt(1));
                            String numberDislikes = String.valueOf(jsonArray.get(2));
                            if(status.equalsIgnoreCase("true")){
                                like.setImageResource(R.drawable.test);
                                attributesFile.setStatus(1);
                            }else {
                                like.setImageResource(R.drawable.ic_baseline_thumb_up_alt_36);
                            }

                            dislike.setImageResource(R.drawable.ic_baseline_thumb_down_alt_36);
                            numberLike.setText(numberLikes);
                            numberDislike.setText(numberDislikes);
                            attributesFile.setNumberLike(Integer.parseInt(numberLikes));
                            attributesFile.setNumberDislike(Integer.parseInt(numberDislikes));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }, error -> {
                    }){
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String,String> maps = new HashMap<>();
                            maps.put("file_id",String.valueOf(((AttributesFile)list.get(getAdapterPosition())).getIdFile()));
                            maps.put("user_id", User.getUserReference().getUserId());
                            return maps;
                        }
                    };
                    MySingleton.getInstance(context).getRequestQueue().add(stringRequest);
                }

            });

            dislike = itemView.findViewById(R.id.imageButtonDislike);

            dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = MainActivity.SERVER_API_URL + "dislike";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {

                        JSONArray jsonArray = null;
                        try {
                            AttributesFile attributesFile = (AttributesFile)list.get(getAdapterPosition());
                            jsonArray = new JSONArray(response);
                            String status = jsonArray.getString(0);
                            String numberLikes = String.valueOf(jsonArray.getInt(1));
                            String numberDislikes = String.valueOf(jsonArray.get(2));
                            if(status.equalsIgnoreCase("true")){
                                dislike.setImageResource(R.drawable.test2);
                                attributesFile.setStatus(2);
                            }else {
                                dislike.setImageResource(R.drawable.ic_baseline_thumb_down_alt_36);
                            }

                            like.setImageResource(R.drawable.ic_baseline_thumb_up_alt_36);

                            numberLike.setText(numberLikes);
                            numberDislike.setText(numberDislikes);
                            attributesFile.setNumberLike(Integer.parseInt(numberLikes));
                            attributesFile.setNumberDislike(Integer.parseInt(numberDislikes));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }, error -> {
                    }){
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String,String> maps = new HashMap<>();
                            maps.put("file_id",String.valueOf(((AttributesFile)list.get(getAdapterPosition())).getIdFile()));
                            maps.put("user_id", User.getUserReference().getUserId());
                            return maps;
                        }
                    };
                    MySingleton.getInstance(context).getRequestQueue().add(stringRequest);
                }
            });

            titleClick = itemView.findViewById(R.id.title_click);
            subscriptionsNotifications = itemView.findViewById(R.id.subscriptions_notifications);

            titleFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AttributesFile attributesFile = (AttributesFile) list.get(getAdapterPosition());
                    attributesFile.setExpand(!attributesFile.isExpand());
                    notifyItemChanged(0);
                }
            });

            subscriptionsNotifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscriptions();
                }
            });


        }

        private void subscriptions(){
            String url = MainActivity.SERVER_API_URL + "sub";

            StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    AttributesFile attributesFile = (AttributesFile) list.get(getAdapterPosition());


                    if(jsonObject.getString("messages").equalsIgnoreCase("Subscribed")){
                        subscriptionsNotifications.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                        YoYo.with(Techniques.Swing).duration(700).playOn(subscriptionsNotifications);
                        attributesFile.setSubscribed(true);
                        int n = attributesFile.getNumberSub();
                        n++;
                        numberSub.setText(n + " Subscription");
                        attributesFile.setNumberSub(n);

                    }else {
                        subscriptionsNotifications.setImageResource(R.drawable.ic_baseline_notifications_24);
                        YoYo.with(Techniques.Bounce).duration(700).playOn(subscriptionsNotifications);
                        attributesFile.setSubscribed(false);
                        int n = attributesFile.getNumberSub();
                        n--;
                        numberSub.setText(n+ " Subscription");
                        attributesFile.setNumberSub(n);
                    }

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
                    maps.put("channel_id",String.valueOf(((AttributesFile)list.get(getAdapterPosition())).getIdChannel()));
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
                    maps.put("file_id",String.valueOf(((AttributesFile)list.get(getAdapterPosition())).getIdFile()));
                    maps.put("user_id", User.getUserReference().getUserId());
                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);
        }
    }


    public String getTitleFile(){
        Model model = list.get(0);
        AttributesFile attributesFile = (AttributesFile) model;
        return attributesFile.getTitleFile();
    }

    class ViewHolderRelated extends RecyclerView.ViewHolder{

        public ImageView imageFileRelated;
        public TextView nameFile,nameChannel,viewAndDateFile;

        private ImageButton  imageButton;

        private LinearLayout layout;



        public ViewHolderRelated(@NonNull View itemView) {
            super(itemView);

            imageButton = itemView.findViewById(R.id.related_menu);
            imageFileRelated = itemView.findViewById(R.id.imageFileRelated);
            nameFile = itemView.findViewById(R.id.nameFileRelated);
            nameChannel = itemView.findViewById(R.id.nameChannelRelated);
            viewAndDateFile = itemView.findViewById(R.id.viewAndDateFileRelated);
            layout = itemView.findViewById(R.id.layoutRelatedFile);

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
                    maps.put("file_id",String.valueOf(((RelatedFile)list.get(getAdapterPosition())).getIdFile()));
                    maps.put("user_id", User.getUserReference().getUserId());
                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);
        }
    }

    class ViewHolderAddComment extends RecyclerView.ViewHolder{

        public TextView viewNumberComments;
        public EditText writeComment;
        public ImageView personalAddComment;

        public ViewHolderAddComment(@NonNull View itemView) {
            super(itemView);

            viewNumberComments = itemView.findViewById(R.id.viewNumberComments);
            writeComment =itemView.findViewById(R.id.WriteComment);
            personalAddComment = itemView.findViewById(R.id.personalAddComment);

            personalAddComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model model = list.get(getAdapterPosition());
                    AddCommentFile addCommentFile = (AddCommentFile) model;
                    MainActivity.getContext().goTo_People_Channel(addCommentFile.getIdChannel());
                    ((ViewActivity)context).finish();

                }
            });

            writeComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if((event != null && (event.getKeyCode()==KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE){
                        addComment(writeComment.getText().toString());
                        writeComment.setText("");

                    }

                    return false;
                }
            });


        }

        public void addComment(String text){

            String url = MainActivity.SERVER_API_URL + "add_comment";


            int size = list.size()-1;
            StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {



                double yOffset = Screen.getScreenHeight() * Screen.display2;

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equalsIgnoreCase("true")){
                        final Snackbar snackbar = Snackbar.make(itemView.getRootView(), jsonObject.getString("messages"), Snackbar.LENGTH_LONG);
                        snackbar.setAction("Close", v1 -> snackbar.dismiss());
                        snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                        View snackbarLayout = snackbar.getView();
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, (int)yOffset, 0, 0);
                        snackbarLayout.setLayoutParams(lp);
                        snackbar.show();

                        List<Model> list = new ArrayList<>();

                        ViewCommentFile viewCommentFile = new ViewCommentFile();
                        viewCommentFile.setNumberReply(0);
                        viewCommentFile.setText(text);
                        viewCommentFile.setPersonalImage(User.getUserReference().getChannel().getChannel_profile().getProfileImage());
                        viewCommentFile.setNameUser(User.getUserReference().getChannel().getChannel_profile().getChannelName());
                        viewCommentFile.setIdComment(jsonObject.getInt("id"));
                        viewCommentFile.setDateComment(jsonObject.getString("date"));
                        viewCommentFile.setModelType(3);
                        list.add(viewCommentFile);
                        addFileNew(list);
                        notifyItemChanged(size);
                    }else {
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
                    Model model = list.get(getAdapterPosition());
                    AddCommentFile addCommentFile = (AddCommentFile) model;

                    maps.put("user_id",User.getUserReference().getUserId());
                    maps.put("file_id",String.valueOf(addCommentFile.getIdFile()));
                    maps.put("text",text);

                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);

        }
    }

    private void addFileNew(List<Model> list){
        this.list.addAll(list);
    }

    class ViewHolderViewComment extends RecyclerView.ViewHolder{

        private LinearLayout replyLinerLayout;


        public ImageView imagePersonalViewComment;

        public TextView nameUserForViewComment,contentText;
        public Button replyButton;


        public ViewHolderViewComment(@NonNull View itemView) {
            super(itemView);



            imagePersonalViewComment = itemView.findViewById(R.id.imagePersonalViewComment);
            nameUserForViewComment = itemView.findViewById(R.id.nameFileForViewComment);
            contentText = itemView.findViewById(R.id.contentText);
            replyButton = itemView.findViewById(R.id.reply_button);

            imagePersonalViewComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model model = list.get(getAdapterPosition());
                    ViewCommentFile viewCommentFile = (ViewCommentFile) model;
                    MainActivity.getContext().goTo_People_Channel(viewCommentFile.getIdChannel());
                    ((ViewActivity)context).finish();
                }
            });

            replyLinerLayout = itemView.findViewById(R.id.reply_liner_layout);

            replyLinerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentActivity.class);
                    Model model = list.get(getAdapterPosition());
                    ViewCommentFile viewCommentFile = (ViewCommentFile) model;
                    intent.putExtra("idComment",viewCommentFile.getIdComment());
                    context.startActivity(intent);
                }
            });

            replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentActivity.class);
                    Model model = list.get(getAdapterPosition());
                    ViewCommentFile viewCommentFile = (ViewCommentFile) model;
                    intent.putExtra("idComment",viewCommentFile.getIdComment());
                    context.startActivity(intent);
                }
            });

        }
    }

    class ViewHolderProgressFile extends RecyclerView.ViewHolder{

        public ViewHolderProgressFile(@NonNull View itemView) {
            super(itemView);
        }
    }







}
