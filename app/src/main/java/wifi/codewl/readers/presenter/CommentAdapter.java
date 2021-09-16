package wifi.codewl.readers.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import wifi.codewl.readers.model.comment.AddReplyComment;
import wifi.codewl.readers.model.comment.ReplyComment;
import wifi.codewl.readers.model.file.AddCommentFile;
import wifi.codewl.readers.model.file.ViewCommentFile;
import wifi.codewl.readers.model.progress.ProgressComment;
import wifi.codewl.readers.status.Screen;
import wifi.codewl.readers.view.activity.CommentActivity;
import wifi.codewl.readers.view.activity.MainActivity;

public class CommentAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Model> list;


    public CommentAdapter(Context context, List<Model> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        Model model = list.get(position);
       // return  model.getModelType();
        if (model instanceof AddReplyComment){
            return 0;
        }else if(model instanceof ReplyComment){
            return 1;
        }else {
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:{
                return new ViewHolderAddReplyComment(LayoutInflater.from(context).inflate(R.layout.item_add_reply_comment,parent,false));
            }
            case 1:{
                return new ViewHolderReplyComment(LayoutInflater.from(context).inflate(R.layout.item_reply_comment,parent,false));
            }
            case 2:{
                return new ViewHolderProgressComment(LayoutInflater.from(context).inflate(R.layout.item_progress,parent,false));
            }
        }
        return null;
    }


    public void addFiles(List<Model> list,boolean last){
        if(!last){
            this.list.remove(this.list.size()-1);
        }
        this.list.addAll(list);
    }
    public void getData(int idComment){


        String url = MainActivity.SERVER_API_URL + "return_comment";

        StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

            try {


                JSONArray jsonArray = new JSONArray(response);

                List<Model> list = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {


                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    AddReplyComment addReplyComment = new AddReplyComment();

                    ViewCommentFile viewCommentFile = new ViewCommentFile();

                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + jsonObject.getString("Personal_image"), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            viewCommentFile.setPersonalImage(response);
                            notifyItemChanged(0);
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

                    addReplyComment.setViewCommentFile(viewCommentFile);

                    addReplyComment.setUserPersonalImage(User.getUserReference().getChannel().getChannel_profile().getProfileImage());
                    addReplyComment.setModelType(0);
                    list.add(addReplyComment);

                }

                ProgressComment progressComment = new ProgressComment();
                progressComment.setModelType(3);
                list.add(progressComment);

                addFiles(list,true);
                notifyDataSetChanged();

                getDataForReplyComment(idComment,false);


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

                maps.put("id",String.valueOf(idComment));
                return maps;
            }
        };


        MySingleton.getInstance(context).getRequestQueue().add(request);

    }


    public void getDataForReplyComment(int idComment,boolean last){

        String url = MainActivity.SERVER_API_URL + "return_all_replies";

        StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {

            try {


                JSONArray jsonArray = new JSONArray(response);

                List<Model> list = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {


                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    ReplyComment replyComment = new ReplyComment();


                    int finalI = i;
                    ImageRequest imageRequest = new ImageRequest(MainActivity.SERVER_IMAGES_URL + jsonObject.getString("Personal_image"), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            replyComment.setPersonalImage(response);
                            notifyItemChanged(finalI +1);
                        }
                    }, 0, 0,null, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { }
                    });

                    MySingleton.getInstance(context).addToRequestQueue(imageRequest);

                    replyComment.setNameUser(jsonObject.getString("name"));
                    replyComment.setText(jsonObject.getString("text"));
                    replyComment.setDateComment(jsonObject.getString("created_at"));
                    replyComment.setModelType(1);
                    list.add(replyComment);

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

                maps.put("comment_id",String.valueOf(idComment));
                return maps;
            }
        };


        MySingleton.getInstance(context).getRequestQueue().add(request);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Model model = list.get(position);

        switch (model.getModelType()){
            case 0:{
                ViewHolderAddReplyComment viewHolderAddReplyComment = (ViewHolderAddReplyComment) holder;
                AddReplyComment addReplyComment = (AddReplyComment) model;
                viewHolderAddReplyComment.imagePersonalViewComment.setImageBitmap(addReplyComment.getViewCommentFile().getPersonalImage());
                viewHolderAddReplyComment.nameMainComment.setText(addReplyComment.getViewCommentFile().getNameUser() + " . " + addReplyComment.getViewCommentFile().getDateComment());
                viewHolderAddReplyComment.contentMainText.setText(addReplyComment.getViewCommentFile().getText());

                viewHolderAddReplyComment.imageUserViewComment.setImageBitmap(addReplyComment.getUserPersonalImage());

                break;
            } case 1:{
                ViewHolderReplyComment viewHolderReplyComment = (ViewHolderReplyComment) holder;
                ReplyComment replyComment = (ReplyComment) model;
                if(replyComment.getPersonalImage()!=null){
                    viewHolderReplyComment.imagePersonalViewComment.setImageBitmap(replyComment.getPersonalImage());
                }
                viewHolderReplyComment.nameMainComment.setText(replyComment.getNameUser() + " . "+ replyComment.getDateComment());
                viewHolderReplyComment.contentMainText.setText(replyComment.getText());

                break;
            }
        }
    }

    public void clearForReply(){
        Model model = list.get(0);
        list.clear();
        list.add(model);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderAddReplyComment extends RecyclerView.ViewHolder{
        public ImageView imagePersonalViewComment;
        public TextView nameMainComment,contentMainText;

        public ImageView imageUserViewComment;

        private EditText writeComment;
        public ViewHolderAddReplyComment(@NonNull View itemView) {
            super(itemView);

            imagePersonalViewComment = itemView.findViewById(R.id.personalForReplyComment);
            nameMainComment = itemView.findViewById(R.id.nameFileForReplyComment);
            contentMainText = itemView.findViewById(R.id.contentTextForReplay);

            imageUserViewComment = itemView.findViewById(R.id.userImageForReplyComment);

            writeComment = itemView.findViewById(R.id.writeCommentForReply);

            writeComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if((event != null && (event.getKeyCode()==KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE){
                        addReplyComment(writeComment.getText().toString());
                        writeComment.setText("");

                    }

                    return false;
                }
            });

        }

        public void addReplyComment(String text){

            String url = MainActivity.SERVER_API_URL + "add_reply";

            StringRequest request = new StringRequest(Request.Method.POST, url, response ->  {



                double yOffset = Screen.dm.heightPixels * Screen.display2;

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

                        AddReplyComment addReplyComment = (AddReplyComment) list.get(getAdapterPosition());

                        clearForReply();
                        getDataForReplyComment(addReplyComment.getViewCommentFile().getIdComment(),true);

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
                    AddReplyComment addReplyComment = (AddReplyComment) model;

                    maps.put("user_id",User.getUserReference().getUserId());
                    maps.put("comment_id",String.valueOf(addReplyComment.getViewCommentFile().getIdComment()));
                    maps.put("text",text);

                    return maps;
                }
            };


            MySingleton.getInstance(context).getRequestQueue().add(request);

        }
    }


    class ViewHolderReplyComment extends RecyclerView.ViewHolder{

        public ImageView imagePersonalViewComment;
        public TextView nameMainComment,contentMainText;

        public ViewHolderReplyComment(@NonNull View itemView) {
            super(itemView);
            imagePersonalViewComment = itemView.findViewById(R.id.RPersonalImageFoReplay);
            nameMainComment = itemView.findViewById(R.id.RnameFileForReplay);
            contentMainText = itemView.findViewById(R.id.RcontentText);
        }
    }
    class ViewHolderProgressComment extends RecyclerView.ViewHolder{

        public ViewHolderProgressComment(@NonNull View itemView) {
            super(itemView);
        }
    }

}
