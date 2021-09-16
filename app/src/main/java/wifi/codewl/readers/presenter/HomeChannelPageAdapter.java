package wifi.codewl.readers.presenter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.User;
import wifi.codewl.readers.model.channel.Channel;
import wifi.codewl.readers.model.channel.Content;
import wifi.codewl.readers.model.channel.Channel_Profile;
import wifi.codewl.readers.status.Screen;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.fragment.channel.Channel_Fragment;


public class HomeChannelPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<Model> items;

    public HomeChannelPageAdapter(List<Model> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



            if (Channel_Fragment.MyChannel)
                return new HeadProfileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_personal_page, parent, false));


                return new HeadProfileViewHolderForPeople(LayoutInflater.from(parent.getContext()).inflate(R.layout.people__personal_page, parent, false));



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {




            if (Channel_Fragment.MyChannel)
                ((HeadProfileViewHolder) holder).setHeadProfileData();

            else {

                ((HeadProfileViewHolderForPeople) holder).setHeadProfileData(((Channel) items.get(position)).getChannel_profile());

            }




    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getModelType();
    }


    static class HeadProfileViewHolderForPeople extends RecyclerView.ViewHolder {

        private ImageView profile_background;
        private CircularImageView profile_image;
        private TextView userName, subscriper_count, is_sub;


        public HeadProfileViewHolderForPeople(@NonNull View itemView) {
            super(itemView);
            profile_background=itemView.findViewById(R.id.profile_people_background);
            profile_image=itemView.findViewById(R.id.profile_people_image);
            userName=itemView.findViewById(R.id.people_name_field);
            subscriper_count=itemView.findViewById(R.id.subscriper_count);
            is_sub=itemView.findViewById(R.id.subscription_label);
            subscriper_count=itemView.findViewById(R.id.subscriper_count);



        }

        @SuppressLint("UseCompatLoadingForDrawables")
        void setHeadProfileData(Channel_Profile headProfile) {

            profile_background.setImageBitmap(headProfile.getProfileBackground());
            profile_image.setImageBitmap(headProfile.getProfileImage());
            userName.setText(headProfile.getChannelName());
            subscriper_count.setText(""+headProfile.getSuc_count());

        }




    }


    static class HeadProfileViewHolder extends RecyclerView.ViewHolder {

        private ImageView profile_background, profile_image;
        private TextView userName, description_text;
        private ImageView edit_channel_name, edit_description;


        HeadProfileViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_background = itemView.findViewById(R.id.profile_background);

            profile_image = itemView.findViewById(R.id.profile_people_image);

            userName = itemView.findViewById(R.id.userName);

            edit_channel_name = itemView.findViewById(R.id.edit_channel_name);

            edit_description = itemView.findViewById(R.id.edit_description);

            description_text = itemView.findViewById(R.id.description_text);

            edit_description.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    EditText editText = new EditText(itemView.getRootView().getContext());

                    editText.setHint("type description here");
                    AlertDialog.Builder dialog = new AlertDialog.Builder(itemView.getRootView().getContext());
                    dialog.setTitle("edit description :)");
                    dialog.setIcon(R.drawable.icon_edit);
                    dialog.setCancelable(false);
                    dialog.setMessage("this description will appear to people into about part in your channel ");

                    dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            StringRequest request = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "EditDescription", response -> {


                                JSONObject jsonObject = null;

                                try {

                                    jsonObject = new JSONObject(response);
                                    final Snackbar snackbar = Snackbar.make(itemView.getRootView(), jsonObject.getString("messages"), Snackbar.LENGTH_LONG);
                                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                                    snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                                    double yOffset = Screen.getScreenHeight() * Screen.display1;
                                    View snackbarLayout = snackbar.getView();
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    lp.setMargins(0, (int)yOffset, 0, 0);
                                    snackbarLayout.setLayoutParams(lp);
                                    snackbar.show();
                                    description_text.setText(editText.getText());
                                    User.getUserReference().getChannel().setDescription_text(editText.getText().toString());


                                } catch (Exception e) {
                                    e.printStackTrace();
                                    final Snackbar snackbar = Snackbar.make(itemView.getRootView(), "error in connection retry again", Snackbar.LENGTH_LONG);
                                    snackbar.setAction("Close", v1 -> snackbar.dismiss());
                                    snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                                    double yOffset = Screen.getScreenHeight() * Screen.display1;
                                    View snackbarLayout = snackbar.getView();
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    lp.setMargins(0, (int)yOffset, 0, 0);
                                    snackbarLayout.setLayoutParams(lp);
                                    snackbar.show();
                                    snackbar.show();

                                }


                            }, error -> {
                                final Snackbar snackbar = Snackbar.make(itemView.getRootView(), "error in connection retry again", Snackbar.LENGTH_LONG);
                                snackbar.setAction("Close", v1 -> snackbar.dismiss());
                                snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                                double yOffset = Screen.getScreenHeight() * Screen.display1;
                                View snackbarLayout = snackbar.getView();
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(0, (int)yOffset, 0, 0);
                                snackbarLayout.setLayoutParams(lp);
                                snackbar.show();
                                snackbar.show();


                            }) {

                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("id", User.getUserReference().getUserId());
                                    map.put("description", editText.getText().toString());


                                    return map;
                                }

                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext());
                            requestQueue.add(request);

                        }
                    });

                    dialog = dialog.setView(editText);
                    dialog.create().show();

                }
            });


            edit_channel_name.setOnClickListener(v -> {


                EditText editText = new EditText(itemView.getRootView().getContext());
                editText.setHint("type name here");


                AlertDialog.Builder dialog = new AlertDialog.Builder(itemView.getRootView().getContext());

                dialog.setTitle("edit channel Name :)");
                dialog.setIcon(R.drawable.icon_edit);
                dialog.setCancelable(false);
                dialog.setMessage("this name will appearing to all people on platform ");

                dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.SERVER_API_URL + "EditName", response -> {


                            JSONObject jsonObject = null;

                            try {

                                jsonObject = new JSONObject(response);
                                final Snackbar snackbar = Snackbar.make(itemView.getRootView(), jsonObject.getString("messages"), Snackbar.LENGTH_LONG);
                                snackbar.setAction("Close", v1 -> snackbar.dismiss());
                                snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                                double yOffset = Screen.getScreenHeight() * Screen.display1;
                                View snackbarLayout = snackbar.getView();
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(0, (int)yOffset, 0, 0);
                                snackbarLayout.setLayoutParams(lp);
                                snackbar.show();
                                snackbar.show();
                                userName.setText(editText.getText());
                                User.getUserReference().setUserName(editText.getText().toString());


                            } catch (Exception e) {
                                e.printStackTrace();
                                final Snackbar snackbar = Snackbar.make(itemView.getRootView(), "error in connection retry again", Snackbar.LENGTH_LONG);
                                snackbar.setAction("Close", v1 -> snackbar.dismiss());
                                snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                                double yOffset = Screen.getScreenHeight() * Screen.display1;
                                View snackbarLayout = snackbar.getView();
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(0, (int)yOffset, 0, 0);
                                snackbarLayout.setLayoutParams(lp);
                                snackbar.show();
                                snackbar.show();

                            }


                        }, error -> {
                            final Snackbar snackbar = Snackbar.make(itemView.getRootView(), "error in connection retry again", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Close", v1 -> snackbar.dismiss());
                            snackbar.setActionTextColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
                            double yOffset = Screen.getScreenHeight() * Screen.display1;
                            View snackbarLayout = snackbar.getView();
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, (int)yOffset, 0, 0);
                            snackbarLayout.setLayoutParams(lp);
                            snackbar.show();
                            snackbar.show();


                        }) {

                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> map = new HashMap<>();
                                map.put("id", User.getUserReference().getUserId());
                                map.put("name", editText.getText().toString());


                                return map;
                            }

                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext());
                        requestQueue.add(request);

                    }
                });

                dialog = dialog.setView(editText);
                dialog.create().show();

            });


        }


        @SuppressLint("UseCompatLoadingForDrawables")
        void setHeadProfileData() {

            if (User.getUserReference().getChannel().getChannel_profile().getProfileImage() != null)
                profile_image.setImageBitmap(User.getUserReference().getChannel().getChannel_profile().getProfileImage());
            if (User.getUserReference().getChannel().getChannel_profile().getProfileBackground() != null)
                profile_background.setImageBitmap(User.getUserReference().getChannel().getChannel_profile().getProfileBackground());

            userName.setText(User.getUserReference().getUserName());
            description_text.setText(User.getUserReference().getChannel().getDescription_text());


        }


    }


    static class ContentViewHolder extends RecyclerView.ViewHolder {


        private ImageView profileContent;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            profileContent = itemView.findViewById(R.id.image);

        }


        void setContentData(Content content) {

            profileContent.setImageResource(content.getImage());


        }
    }


}
