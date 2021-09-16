package wifi.codewl.readers.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.subscriptions.TitleChannel;
import wifi.codewl.readers.view.activity.MainActivity;

public class TitleChannelAdapter extends RecyclerView.Adapter<TitleChannelAdapter.ViewHolderTitleChannel> {

    private Context context;
    private List<Model> list;

    public TitleChannelAdapter(Context context,List<Model> list){
        this.context =context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolderTitleChannel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderTitleChannel(LayoutInflater.from(context).inflate(R.layout.item_title_channel,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTitleChannel holder, int position) {


        ((ViewHolderTitleChannel) holder).channel_name.setText(((TitleChannel)list.get(position)).getChannel_name());
        ((ViewHolderTitleChannel) holder).channel_image.setImageBitmap(((TitleChannel)list.get(position)).getProfileImage());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderTitleChannel extends RecyclerView.ViewHolder{
     CircularImageView channel_image;
     TextView channel_name;

        public ViewHolderTitleChannel(@NonNull View itemView) {
            super(itemView);

            channel_image=itemView.findViewById(R.id.channel_image);
            channel_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.getContext().goTo_People_Channel(Integer.parseInt(((TitleChannel)list.get(getAdapterPosition())).getChannelId()));
                }
            });
            channel_name=itemView.findViewById(R.id.channel_name);


        }
    }
}
