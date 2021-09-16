package wifi.codewl.readers.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.notifications.NotificationsUploadFile;

public class NotificationsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Model> list;

    public NotificationsAdapter(Context context,List<Model> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        Model model = list.get(position);
        if(model instanceof NotificationsUploadFile){
            return 0;
        }else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:{
                return new ViewHolderNotificationsUploadFile(LayoutInflater.from(context).inflate(R.layout.item_upload_file_notifications,parent,false));
            }
            case 1:{
                return new ViewHolderProgressNotifications(LayoutInflater.from(context).inflate(R.layout.item_progress,parent,false));
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderNotificationsUploadFile extends RecyclerView.ViewHolder{

        public ViewHolderNotificationsUploadFile(@NonNull View itemView) {
            super(itemView);
        }
    }

    class ViewHolderProgressNotifications extends RecyclerView.ViewHolder{

        public ViewHolderProgressNotifications(@NonNull View itemView) {
            super(itemView);
        }
    }

}
