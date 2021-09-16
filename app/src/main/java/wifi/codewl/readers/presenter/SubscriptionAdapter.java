package wifi.codewl.readers.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.subscriptions.TitleChannel;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.SubscriptionListActivity;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.View_Holder> {


    ArrayList<Model>ChannelTitle;
    Context context;

    public SubscriptionAdapter(ArrayList<Model> channelTitle, Context context) {
        ChannelTitle=channelTitle;
        this.context=context;
    }


    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_list, parent, false);
        return new View_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {


        ((SubscriptionAdapter.View_Holder) holder).channel_name.setText(((TitleChannel)ChannelTitle.get(position)).getChannel_name());
        ((SubscriptionAdapter.View_Holder) holder).channel_image.setImageBitmap(((TitleChannel)ChannelTitle.get(position)).getProfileImage());




    }

    @Override
    public int getItemCount() {
        return ChannelTitle.size();
    }

    class View_Holder extends RecyclerView.ViewHolder {
        CircularImageView channel_image;
        TextView channel_name;




        public View_Holder(@NonNull View itemView) {
            super(itemView);

            channel_image=itemView.findViewById(R.id.channel_image);
            channel_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.getContext().goTo_People_Channel(Integer.parseInt(((TitleChannel)ChannelTitle.get(getAdapterPosition())).getChannelId()));
                    ((SubscriptionListActivity)context).finish();




                }
            });
            channel_name=itemView.findViewById(R.id.channel_name);
        }
    }

}
