/*
package wifi.codewl.readers.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Content;
import wifi.codewl.readers.model.channel.HeaderProfile;
import wifi.codewl.readers.model.Item;


public class HomePageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<Item> items;

    public HomePageAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {

            return new HeadProfileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_personal_page, parent, false));


        } else {

            return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_personal, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == 1) {

            ((HeadProfileViewHolder) holder).setHeadProfileData((HeaderProfile) items.get(position).getItem());


        } else {

            ((ContentViewHolder) holder).setContentData((Content) items.get(position).getItem());

        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getItemType();
    }

    static class HeadProfileViewHolder extends RecyclerView.ViewHolder {

        private ImageView profile_background, profile_image;
        private TextView userName;


        HeadProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_background = itemView.findViewById(R.id.profile_background);
            profile_image = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userName);
        }


        void setHeadProfileData(HeaderProfile headProfile) {
            profile_background.setImageURI(headProfile.getProfileBackground());
            profile_image.setImageURI(headProfile.getProfileImage());
            userName.setText(headProfile.getUserName());


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


 */